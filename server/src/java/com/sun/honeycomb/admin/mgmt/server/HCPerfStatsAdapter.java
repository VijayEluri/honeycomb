/*
 * Copyright � 2008, Sun Microsystems, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *    * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *    * Neither the name of Sun Microsystems, Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



package com.sun.honeycomb.admin.mgmt.server;

import com.sun.honeycomb.alert.AlertApi;
import com.sun.honeycomb.alert.AlertException;
import com.sun.honeycomb.alert.AlerterServerIntf;
import com.sun.honeycomb.cm.ServiceManager;
import com.sun.honeycomb.cm.node_mgr.NodeMgrService;
import com.sun.honeycomb.cm.node_mgr.Node;
import com.sun.honeycomb.cm.ManagedServiceException;
import com.sun.honeycomb.common.AlertConstants;
import com.sun.honeycomb.common.BandwidthStatsAccumulator;
import com.sun.honeycomb.common.CliConstants;
import com.sun.honeycomb.common.ConfigPropertyNames;
import com.sun.honeycomb.common.PerfStats;
import com.sun.honeycomb.common.StatsAccumulator;
import com.sun.honeycomb.config.ClusterProperties;
import com.sun.honeycomb.disks.Disk;
import com.sun.honeycomb.diskmonitor.DiskProxy;
import com.sun.honeycomb.emd.MetadataClient;
import com.sun.honeycomb.emd.common.EMDException;
import com.sun.honeycomb.mgmt.common.MgmtException;
import com.sun.honeycomb.protocol.server.ProtocolProxy;
		
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
		

public class HCPerfStatsAdapter 
    extends HCPerfStatsAdapterBase {

    private static transient final Logger logger = 
         Logger.getLogger(HCPerfStatsAdapter.class.getName());
    
    private static final long ONE_SECOND = 1000;   // in milleseconds
    private static final long KILOBYTE = 1024;	  
    private static int perfNodeStatsMaxIndex = 0;


    public synchronized void initCounters() {
	
        Node[] nodes = getNodes();
        int size = nodes.length;
	perfNodeStatsMaxIndex = size;
	lastStoreStats = new BandwidthStatsAccumulator[size];
	lastStoreMDStats = new BandwidthStatsAccumulator[size];
	lastStoreBothStats = new BandwidthStatsAccumulator[size];
	lastStoreMDSideStats = new StatsAccumulator[size];

	lastRetrieveStats = new BandwidthStatsAccumulator[size];
	lastRetrieveMDStats = new BandwidthStatsAccumulator[size];

	lastQueryStats = new StatsAccumulator[size];
	lastDeleteStats = new StatsAccumulator[size];
	
	lastSchemaStats = new StatsAccumulator[size];

	lastPutWebDAVStats = new BandwidthStatsAccumulator[size];
	lastGetWebDAVStats = new BandwidthStatsAccumulator[size];
    }
    
    public BigInteger reset(BigInteger dummy) throws MgmtException {
        initCounters();
        return BigInteger.valueOf(0);
    }
    
    private NodeMgrService.Proxy getNodeMgrProxy (int nodeid) {
        Object obj = ServiceManager.proxyFor (nodeid);
        if (! (obj instanceof NodeMgrService.Proxy)) {

            throw new RuntimeException (
                "Unable to acquire to node manager proxy.");
        }
        return ((NodeMgrService.Proxy) obj);
    }


    private DiskProxy getDiskMonitorProxy (int nodeid) {
        Object obj = ServiceManager.proxyFor (nodeid, "DiskMonitor");
        if (! (obj instanceof DiskProxy)) {

            throw new RuntimeException (
                "Unable to acquire to disk monitor proxy.");
        }
        return ((DiskProxy) obj);
    }


    private NodeMgrService.Proxy getNodeMgrProxy () {
        return getNodeMgrProxy (ServiceManager.LOCAL_NODE);
    }

    private DiskProxy getDiskMonitorProxy () {
        return getDiskMonitorProxy (ServiceManager.LOCAL_NODE);
    }



    public Node[] getNodes() throws RuntimeException {
        Node[] nodes = null;

        nodes = getNodeMgrProxy().getNodes();
        return nodes;
    }

    /*
     * Get Perf Stats for a Given Node
     * Returns an Array of Strings with formatted output
     */ 
    protected synchronized void generateNodePerfStats( )
        throws MgmtException {
	PerfStats nodePerfStats = fetchNodePerfStats();
	publishStatistics(nodePerfStats);
    }
    
    protected PerfStats fetchNodePerfStats()
        throws MgmtException {
	
        Hashtable obMap;

        int mapIdx; 
        int isIdx;
 
        /** Get NodeMgr Proxy */

        Node[] nodes = getNodeMgrProxy().getNodes();
	int numNodes = ClusterProperties.getInstance()
	    .getPropertyAsInt(ConfigPropertyNames.PROP_NUM_NODES);
        for (isIdx=0, mapIdx=0; mapIdx< numNodes; mapIdx++) {
            if (nodes[mapIdx].nodeId() == _nodeId) {
		isIdx = 1;
                break;
            }
        }
        if (isIdx == 0) {
            //
            // Internationalize here
            //
            throw new MgmtException("Invalid Node ID Specified.");
        }
	if (nodes[mapIdx].isAlive() == false) {
	    throw new MgmtException("Performance Statistics for NODE-"+_nodeId
	    	+ " are not available.\nThe node is down.");
        }
	
	
	if (areDataServicesAvailable() == false) {
	    throw new MgmtException("Performance statistics are currently not available.\n"
		+ "Data services are offline.");
	}

        /** Get Alert Perf Stats for all the nodes in the cluster */
        AlerterServerIntf alertApi;

        try {
	    alertApi = getAlerterServerApi();
	    if (alertApi == null) {
		//
		// Internationalize here
		//
		throw new MgmtException("Cannot get alert server API."); 
	    }
            obMap = getAlertPerfStats(_nodeId, alertApi);
	    if (obMap.size() == 0) {
		throw new MgmtException(
		    "Performance statistics for NODE-"
		    + _nodeId + " are not available at this time.");
	    }
		
            return getNodePerfStats(obMap, nodes,_nodeId);
	} catch (MgmtException me) {
	    throw me;
	} catch (Exception e) {
	    //
	    // Internationalize here
	    //
	    logger.log(Level.SEVERE, "Internal error generating performance numbers.", e);
	    throw new MgmtException(
		"Internal error, failed to fetch performance Statistics for NODE-"
		+ _nodeId + ".");
	}
    }
    
    protected synchronized void generateCellPerfStats() 
        throws MgmtException {
	PerfStats cellPerfStats = fetchCellPerfStats();
	publishStatistics(cellPerfStats);
    }
    
    /*
     * Get Perf Stats for a Given Node
     */ 
    private PerfStats fetchCellPerfStats() 
        throws MgmtException {
	
	if (areDataServicesAvailable() == false) {
	    throw new MgmtException("Performance statistics are currently not available.\n"
		+ "Data services are offline.");
	}
	
        /** Get Alert Perf Stats for all the nodes in the cluster */
        // we get an array of hashtable i.e. Hashtable(key=Alert Prop, value = AlertObject)
        AlerterServerIntf alertApi;
        Hashtable[] alertObjMap = null;

	/*
	 * Aggregate Perf Stats for all the nodes in the cluster
	 */    
	PerfStats cellPerfStats = new PerfStats();
	
        try {
	    alertApi = getAlerterServerApi();
	    if (alertApi == null) {

		throw new RuntimeException("Cannot get alert server API"); 
	    }

	    /** Get NodeMgr Proxy */

	    Node[] nodes = getNodeMgrProxy().getNodes();
	    if (nodes == null || nodes.length == 0) {
		throw new RuntimeException("Node lookup failed.");
	    }
	
            Hashtable[] obMap = getAlertPerfStats(alertApi);
	    boolean statsAvailable = false;
	    for (int i=0 ; i<nodes.length; i++) {

	        if (!nodes[i].isAlive()) {
	            continue;
		}
		
		if (obMap[i].size() == 0) {
		    // No stats for this node where retrieved
		    continue;
		}
		statsAvailable = true;

		int nodeId = nodes[i].nodeId();
		PerfStats perfStats = getNodePerfStats(obMap[i], nodes, nodeId);
            
                cellPerfStats.addStoreBytesProcessed(perfStats.getStoreBytesProcessed());
		cellPerfStats.addStoreExecTime(perfStats.getStoreExecTime());
		cellPerfStats.addStoreOps(perfStats.getStoreOps());
		
		cellPerfStats.addStoreMDBytesProcessed(perfStats.getStoreMDBytesProcessed());
		cellPerfStats.addStoreMDExecTime(perfStats.getStoreMDExecTime());
		cellPerfStats.addStoreMDOps(perfStats.getStoreMDOps());
		
		cellPerfStats.addStoreMDSideExecTime(perfStats.getStoreMDSideExecTime());
		cellPerfStats.addStoreMDSideOps(perfStats.getStoreMDSideOps());
		
		cellPerfStats.addStoreBothBytesProcessed(perfStats.getStoreBothBytesProcessed());
		cellPerfStats.addStoreBothExecTime(perfStats.getStoreBothExecTime());
		cellPerfStats.addStoreBothOps(perfStats.getStoreBothOps());
		
		cellPerfStats.addRetrieveBytesProcessed(perfStats.getRetrieveBytesProcessed());
		cellPerfStats.addRetrieveExecTime(perfStats.getRetrieveExecTime());
		cellPerfStats.addRetrieveOps(perfStats.getRetrieveOps());
		
		cellPerfStats.addRetrieveMDBytesProcessed(perfStats.getRetrieveMDBytesProcessed());
		cellPerfStats.addRetrieveMDExecTime(perfStats.getRetrieveMDExecTime());
		cellPerfStats.addRetrieveMDOps(perfStats.getRetrieveMDOps());
		
		cellPerfStats.addQueryExecTime(perfStats.getQueryExecTime());
		cellPerfStats.addQueryOps(perfStats.getQueryOps());
		
		cellPerfStats.addDeleteExecTime(perfStats.getDeleteExecTime());
		cellPerfStats.addDeleteOps(perfStats.getDeleteOps());
		
		cellPerfStats.addSchemaExecTime(perfStats.getSchemaExecTime());
		cellPerfStats.addSchemaOps(perfStats.getSchemaOps());

		cellPerfStats.addWebDAVGetBytesProcessed(perfStats.getWebDAVGetBytesProcessed());
		cellPerfStats.addWebDAVGetExecTime(perfStats.getWebDAVGetExecTime());
		cellPerfStats.addWebDAVGetOps(perfStats.getWebDAVGetOps());
		
		cellPerfStats.addWebDAVPutBytesProcessed(perfStats.getWebDAVPutBytesProcessed());
		cellPerfStats.addWebDAVPutExecTime(perfStats.getWebDAVPutExecTime());
		cellPerfStats.addWebDAVPutOps(perfStats.getWebDAVPutOps());

                cellPerfStats.addSystemLoad(perfStats.getSystemLoad());
                cellPerfStats.addLoad1M(perfStats.getLoad1M());
                cellPerfStats.addLoad5M(perfStats.getLoad5M());
                cellPerfStats.addLoad15M(perfStats.getLoad15M());
                cellPerfStats.addMemUsagePercent(perfStats.getMemUsagePercent());
                cellPerfStats.addFreeMem(perfStats.getFreeMem());
                cellPerfStats.addTotalMem(perfStats.getTotalMem());
                cellPerfStats.addDiskUsed(perfStats.getDiskUsed());
                cellPerfStats.addDiskSize(perfStats.getDiskSize());
	    }
	    if (statsAvailable == false) {
		throw new MgmtException("Performance statistics are currently not available.");
	    }
	} catch(MgmtException me) {
	    throw me;
	} catch (Exception e) {
	    //
	    // Internationalize here
	    //
	    logger.log(Level.SEVERE, "Internal error generating performance numbers.", e);
	    throw new MgmtException("Unable to fetch performance numbers for the specified cell.");
	}
	return cellPerfStats;
    }
    
    /**
     * @param obMap the alert tree object map
     * @param nodes
     * @param nodeId the node to retrieve stats for
     */
    private PerfStats getNodePerfStats(Hashtable obMap, Node[] nodes, int nodeId )
        throws AlertException, MgmtException {
        int mapIdx; 
        int isIdx;

        float systemLoad = 0;
        float load1M = 0;
        float load5M = 0;
        float load15M = 0;
        float freeMem = 0;
        float totalMem = 0;
        long diskUsed = 0;
        long diskSize = 0;
      
        for (isIdx=0, mapIdx=0; mapIdx<nodes.length; mapIdx++) {
            if (nodes[mapIdx].nodeId() == nodeId) {
                isIdx = 1;
                break;
            }
        }
        
        if (isIdx == 0) {  
            throw new MgmtException("Unable to fetch performance statistics for NODE-"
		+ nodeId + ".");
        }

        PerfStats perfStats = new PerfStats(); 
        AlertApi.AlertObject alertObj; 

	// We retrieve the stored performance values for each performance property
	// using the origional class that the stats were written out to
	// the alert tree to reconsistitute it.  Calculate the delta between
	// the current and last value and store them in perfStats
	
	BandwidthStatsAccumulator bStats = null;
	StatsAccumulator stats = null;
	
	if (lastStoreStats == null) {
	    initCounters();
	}
	
	if (mapIdx > perfNodeStatsMaxIndex) {
	    logger.warning("Performance: mapIdx = " + mapIdx + "  Only allocated space for " + perfNodeStatsMaxIndex + " nodes.  Reiniting counters.");
	    initCounters();
	}
	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_STORE_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastStoreStats[mapIdx] != null) {
		perfStats.setStoreBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), 
			lastStoreStats[mapIdx].getTotalBytesProcessed()));
		perfStats.setStoreExecTime(
		    calculateDelta(bStats.getTotalExecTime(), 
			lastStoreStats[mapIdx].getTotalExecTime()));
		perfStats.setStoreOps(
		    calculateDelta(bStats.getTotalOps(), 
			lastStoreStats[mapIdx].getTotalOps()));
	    }
	    lastStoreStats[mapIdx] = bStats;
	}
	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_STORE_MD_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastStoreMDStats[mapIdx] != null) {
		perfStats.setStoreMDBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), 
			lastStoreMDStats[mapIdx].getTotalBytesProcessed()));
		perfStats.setStoreMDExecTime(
		    calculateDelta(bStats.getTotalExecTime(), 
			lastStoreMDStats[mapIdx].getTotalExecTime()));
		perfStats.setStoreMDOps(
		    calculateDelta(bStats.getTotalOps(), 
			lastStoreMDStats[mapIdx].getTotalOps()));
	    }
	    lastStoreMDStats[mapIdx] = bStats;
	}
	
	alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_STORE_MD_SIDE_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    stats = new StatsAccumulator(alertObj.getPropertyValueString());
	    if (lastStoreMDSideStats[mapIdx] != null) {
		perfStats.setStoreMDSideExecTime(
		    calculateDelta(stats.getTotalExecTime(), 
			lastStoreMDSideStats[mapIdx].getTotalExecTime()));
		perfStats.setStoreMDSideOps(
		    calculateDelta(stats.getTotalOps(), 
			lastStoreMDSideStats[mapIdx].getTotalOps()));
	    }
	    lastStoreMDSideStats[mapIdx] = stats;
	}
	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_STORE_BOTH_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastStoreBothStats[mapIdx] != null) {
		perfStats.setStoreBothBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), lastStoreBothStats[mapIdx].getTotalBytesProcessed()));
		perfStats.setStoreBothExecTime(
		    calculateDelta(bStats.getTotalExecTime(), lastStoreBothStats[mapIdx].getTotalExecTime()));
		perfStats.setStoreBothOps(
		    calculateDelta(bStats.getTotalOps(), lastStoreBothStats[mapIdx].getTotalOps()));
	    }
	    lastStoreBothStats[mapIdx] = bStats;
	}
	
        /* Retrieve Stats */ 
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_RETRIEVE_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastRetrieveStats[mapIdx] != null) {
		perfStats.setRetrieveBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), 
			lastRetrieveStats[mapIdx].getTotalBytesProcessed()));
		perfStats.setRetrieveExecTime(
		    calculateDelta(bStats.getTotalExecTime(), 
			lastRetrieveStats[mapIdx].getTotalExecTime()));
		perfStats.setRetrieveOps(
		    calculateDelta(bStats.getTotalOps(), 
			lastRetrieveStats[mapIdx].getTotalOps()));
	    }
	    lastRetrieveStats[mapIdx] = bStats;
	}
	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_RETRIEVE_MD_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastRetrieveMDStats[mapIdx] != null) {
		perfStats.setRetrieveMDBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), 
			lastRetrieveMDStats[mapIdx].getTotalBytesProcessed()));
		if (bStats.getTotalExecTime() != 0)
		perfStats.setRetrieveMDExecTime(
		    calculateDelta(bStats.getTotalExecTime(), 
			lastRetrieveMDStats[mapIdx].getTotalExecTime()));
		perfStats.setRetrieveMDOps(
		    calculateDelta(bStats.getTotalOps(), 
			lastRetrieveMDStats[mapIdx].getTotalOps()));
	    }
	    lastRetrieveMDStats[mapIdx] = bStats;
	}
		 
	/* Query and Delete Stats */	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_QUERY_TIME_BRANCH_LOOKUP_KEY)); 
	if (alertObj != null) {
	    stats = new StatsAccumulator(alertObj.getPropertyValueString());
	    if (lastQueryStats[mapIdx] != null) {
		perfStats.setQueryExecTime(
		    calculateDelta(stats.getTotalExecTime(), lastQueryStats[mapIdx].getTotalExecTime()));
		perfStats.setQueryOps(
		    calculateDelta(stats.getTotalOps(), lastQueryStats[mapIdx].getTotalOps()));
	    }
	    lastQueryStats[mapIdx] = stats;
	}	
	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_DELETE_TIME_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    stats = new StatsAccumulator(alertObj.getPropertyValueString());
	    if (lastDeleteStats[mapIdx] != null) {
		perfStats.setDeleteExecTime(
		    calculateDelta(stats.getTotalExecTime(), lastDeleteStats[mapIdx].getTotalExecTime()));
		perfStats.setDeleteOps(
		    calculateDelta(stats.getTotalOps(), lastDeleteStats[mapIdx].getTotalOps()));
	    }
	    lastDeleteStats[mapIdx] = stats;
	}
	
	/* Schema Stats */	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_GET_SCHEMA_TIME_BRANCH_LOOKUP_KEY)); 
	if (alertObj != null) {
	    stats = new StatsAccumulator(alertObj.getPropertyValueString());
	    if (lastSchemaStats[mapIdx] != null) {
		perfStats.setSchemaExecTime(
		    calculateDelta(stats.getTotalExecTime(), lastSchemaStats[mapIdx].getTotalExecTime()));
		perfStats.setSchemaOps(
		    calculateDelta(stats.getTotalOps(), lastSchemaStats[mapIdx].getTotalOps()));
	    }
	    lastSchemaStats[mapIdx] = stats;
	}	

        /* webdav put and get Stats */
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_WEBDAV_PUT_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastPutWebDAVStats[mapIdx] != null) {
		perfStats.setWebDAVPutBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), lastPutWebDAVStats[mapIdx].getTotalBytesProcessed()));
		perfStats.setWebDAVPutExecTime(
		    calculateDelta(bStats.getTotalExecTime(), lastPutWebDAVStats[mapIdx].getTotalExecTime()));
		perfStats.setWebDAVPutOps(
		    calculateDelta(bStats.getTotalOps(), lastPutWebDAVStats[mapIdx].getTotalOps()));
	    }
	    lastPutWebDAVStats[mapIdx] = bStats;
	}
	
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.PERF_WEBDAV_GET_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    bStats = new BandwidthStatsAccumulator(alertObj.getPropertyValueString());
	    if (lastGetWebDAVStats[mapIdx] != null) {
		perfStats.setWebDAVGetBytesProcessed(
		    calculateDelta(bStats.getTotalBytesProcessed(), lastGetWebDAVStats[mapIdx].getTotalBytesProcessed()));
		perfStats.setWebDAVGetExecTime(
		    calculateDelta(bStats.getTotalExecTime(), lastGetWebDAVStats[mapIdx].getTotalExecTime()));
		perfStats.setWebDAVGetOps(
		    calculateDelta(bStats.getTotalOps(), lastGetWebDAVStats[mapIdx].getTotalOps()));
	    }
	    lastGetWebDAVStats[mapIdx] = bStats;
	}

        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.LOAD_STATS_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    String systemLoadStr = alertObj.getPropertyValueString();
	    String token = null;
	    try {
		StringTokenizer st = new StringTokenizer(systemLoadStr, ",");
		if (st.countTokens() <=1) {
		    systemLoad = 0; 
		} else {
		    token = st.nextToken().trim();
		    systemLoad = Float.valueOf(token).floatValue();
		    token = st.nextToken().trim();
		    load1M = Float.valueOf(token).floatValue();
		    token = st.nextToken().trim();
		    load5M = Float.valueOf(token).floatValue();
		    token = st.nextToken().trim();
		    load15M = Float.valueOf(token).floatValue();
		}
	    } catch (NoSuchElementException e) {
		throw new NoSuchElementException("cannot parse stats o/p" +e.getMessage());
	    }
	    catch (NumberFormatException nfe) {
		logger.log(Level.SEVERE, "System Stats: Failed to convert " + token + " to float value.", nfe);
	    }
	}
        alertObj = (AlertApi.AlertObject) obMap.get(
	    getNodeAlertKey(nodeId, AlertConstants.MEMORY_STATS_BRANCH_LOOKUP_KEY));
	if (alertObj != null) {
	    String memStr = alertObj.getPropertyValueString();
	    String token = null;
	    try {
		StringTokenizer st = new StringTokenizer(memStr, ",");
		if (st.countTokens() <=1) {
		    freeMem = 0; totalMem = 0; 
		} else {
		    token = st.nextToken().trim();
		    float notUsed = Float.valueOf(token).floatValue();
		    token = st.nextToken().trim();
		    freeMem = Float.valueOf(token).floatValue(); 
		    token = st.nextToken().trim();
		    totalMem = Float.valueOf(token).floatValue();
		}      
	    } catch (NoSuchElementException e) {
		throw new NoSuchElementException("cannot parse stats o/p" +e.getMessage());
	    }catch (NumberFormatException nfe) {
		logger.severe("Mem Stats: Failed to convert " + token + " to float value.");
	    }
	}

        /* Get all the disks for a given node */

        DiskProxy proxy = getDiskMonitorProxy();
        com.sun.honeycomb.disks.Disk cdisks[] = null;

        if (proxy != null) {
            cdisks = proxy.getDisks(nodeId);
        } else {
            throw new RuntimeException("Can't get disk proxy.");
        }

	long a = 0;
        for (int d=0;d<cdisks.length; d++) {
            if (cdisks[d].getStatus()==Disk.ENABLED) {
	        diskUsed += cdisks[d].getUsedSize();
	        diskSize += cdisks[d].getDiskSize(); 
		a += cdisks[d].getAvailableSize();
            } 
        }
        
        /*
         * System Metrics - Format String is as follows: 
         * Memory Stats "0, freeMemory, TotalMemory, 0, time"
         * System Load Stats "currentSystemLoad, 1MinSample, 15MinSample, 0, time"
         */ 
	if (freeMem > 0 && totalMem > 0) {
	    //perfStats.setMemUsagePercent(100-((100 * freeMem)/totalMem));  
	    perfStats.setFreeMem(freeMem);  
	    perfStats.setTotalMem(totalMem);  
	} 
	if (systemLoad > 0) {
	    perfStats.setSystemLoad(systemLoad);  
	}
        if (load1M > 0) {
            perfStats.setLoad1M(load1M);
        } 
        if (load5M > 0) {
            perfStats.setLoad5M(load5M);
        } 
        if (load15M > 0) {
            perfStats.setLoad15M(load15M);
        } 
        if (diskUsed > 0) {
            perfStats.setDiskUsed(diskUsed);
        } 
        if (diskSize > 0) {
            perfStats.setDiskSize(diskSize);
        } 
        return perfStats;
    }
    
    /**
     * Get the full alert lookup key.  This routine adds AlertConstants.ROOT_BRANCH_KEY
     * + "." + nodeId + "."  to the beginning of the passed in alertSuffixKey.
     * @return String the fully qualified alert lookup key
     */
    private String getNodeAlertKey(int nodeId, String alertSuffixKey) {
	return new StringBuffer(AlertConstants.ROOT_BRANCH_KEY)
	    .append(".").append(nodeId)
	    .append(".").append(alertSuffixKey).toString();
    }

    private Hashtable getAlertPerfStats(int nodeId, AlerterServerIntf alertApi)
        throws AlertException, ManagedServiceException {
        Hashtable alertObjMap;

        alertObjMap = alertApi.getNodeAlertProperties(nodeId, alertPerfStatProps);
        if (alertObjMap == null) {
            throw new AlertException ("Engine not initialized");
        }
        return alertObjMap; 
    }
    
    
    /**
     * Take the passed in statistics and move them to there global
     * variables so that they may be transferred to the client.
     */
    private synchronized void publishStatistics(PerfStats perfStats) 
    {
	// Strings and BigInteger are the only 2 data types currently available 
	// for sending data to the UI layer. We therefore convert doubles to 
	// Strings
	//
	// Pass the execute time to allow clients to compute average 
	// response time.  
	//
	// Interval here is the polling interval.  Which is technically wrong
	// as it should represent the actual time period between two polling
	// intervals.  
	
	// Design Issue: 
	//
	// If 2 users are monitoring performance we have problems.
	// Since delta are computed based on cli polls the delta's are 
	// going to be wrong for both users.  User 1 specified time period
	// of 5 sec.  User 2 starts poll process right after User 1 and
	// indicates a polling time of 10 secs.  Now all calculations are
	// going to be with a interval value of 10.  Furthermore since we
	// have 2 users polling now the delta they get are for the values
	// between the sample of the last poll.  Not for the time interval
	// they specified.  Thus the performance numbers are going to be
	// almost useless if 2+ users do performance monitoring at the same 
	// time.
	//
	// The solution to this is to push all calculations to the clients.
	// This is where it belongs. (Post 1.1)
	//
	// 
	try {
            //
            // store only
            //  
	    _storeOnly.setOps(BigInteger.valueOf(perfStats.getStoreOps())); 
            _storeOnly.setOpSec(Double.toString(
		divide(perfStats.getStoreOps(), _interval))); 
            _storeOnly.setKbSec(Double.toString(
		divide(divide(perfStats.getStoreBytesProcessed(), KILOBYTE), (long)_interval)));
	    _storeOnly.setExecTime(BigInteger.valueOf(perfStats.getStoreExecTime()));
	    
	    //
	    // store MD
	    //
	    _storeMd.setOps(BigInteger.valueOf(perfStats.getStoreMDOps())); 
            _storeMd.setOpSec(Double.toString(
		divide(perfStats.getStoreMDOps(), _interval))); 
            _storeMd.setKbSec(Double.toString(
		divide(divide(perfStats.getStoreMDBytesProcessed(), KILOBYTE), (long)_interval)));
            _storeMd.setExecTime(BigInteger.valueOf(perfStats.getStoreMDExecTime()));
	    
	    //
	    // store MD
	    //
	    _storeMdSide.setOps(BigInteger.valueOf(perfStats.getStoreMDSideOps())); 
            _storeMdSide.setOpSec(Double.toString(
		divide(perfStats.getStoreMDSideOps(), _interval))); 
            _storeMdSide.setKbSec(null);
            _storeMdSide.setExecTime(BigInteger.valueOf(perfStats.getStoreMDSideExecTime()));
	    
	    //
	    // store both
	    //
	    _storeBoth.setOps(BigInteger.valueOf(perfStats.getStoreBothOps())); 
            _storeBoth.setOpSec(Double.toString(
		divide(perfStats.getStoreBothOps(), _interval))); 
            _storeBoth.setKbSec(Double.toString(
		divide(divide(perfStats.getStoreBothBytesProcessed(), KILOBYTE), (long)_interval)));
            _storeBoth.setExecTime(BigInteger.valueOf(perfStats.getStoreBothExecTime()));
	    
	    //
	    // retrieve
	    //
	    _retrieveOnly.setOps(BigInteger.valueOf(perfStats.getRetrieveOps())); 
            _retrieveOnly.setOpSec(Double.toString(
		divide(perfStats.getRetrieveOps(), _interval)));
            _retrieveOnly.setKbSec(Double.toString(
		divide(divide(perfStats.getRetrieveBytesProcessed(), KILOBYTE), (long)_interval)));
            _retrieveOnly.setExecTime(BigInteger.valueOf(perfStats.getRetrieveExecTime()));
	    
	    
	    //
	    // retrieve MD
	    //
	    _retrieveMd.setOps(BigInteger.valueOf(perfStats.getRetrieveMDOps())); 
            _retrieveMd.setOpSec(Double.toString(
		divide(perfStats.getRetrieveMDOps(), _interval))); 
            _retrieveMd.setKbSec(Double.toString(
		divide(divide(perfStats.getRetrieveMDBytesProcessed(), KILOBYTE), (long)_interval)));
            _retrieveMd.setExecTime(BigInteger.valueOf(perfStats.getRetrieveMDExecTime()));
	    
	    //
	    // query
	    //
	    _query.setOps(BigInteger.valueOf(perfStats.getQueryOps())); 
            _query.setOpSec(Double.toString(
		divide(perfStats.getQueryOps(), _interval))); 
            _query.setKbSec(null);     // N/A
            _query.setExecTime(BigInteger.valueOf(perfStats.getQueryExecTime()));
	    
	    //
	    // delete
	    //
	    _delete.setOps(BigInteger.valueOf(perfStats.getDeleteOps())); 
            _delete.setOpSec(Double.toString(
		divide(perfStats.getDeleteOps(), _interval))); 
            _delete.setKbSec(null);    // N/A
	    _delete.setExecTime(BigInteger.valueOf(perfStats.getDeleteExecTime()));
	    
	    //
	    // schema
	    //
	    _schema.setOps(BigInteger.valueOf(perfStats.getSchemaOps())); 
            _schema.setOpSec(Double.toString(
		divide(perfStats.getSchemaOps(), _interval))); 
            _schema.setKbSec(null);     // N/A
            _schema.setExecTime(BigInteger.valueOf(perfStats.getSchemaExecTime()));

            //
            // webdav put
            //
	    _webdavPut.setOps(BigInteger.valueOf(perfStats.getWebDAVPutOps())); 
            _webdavPut.setOpSec(Double.toString(
		divide(perfStats.getWebDAVPutOps(), _interval))); 
            _webdavPut.setKbSec(Double.toString(
		divide(divide(perfStats.getWebDAVPutBytesProcessed(), KILOBYTE), (long)_interval)));
            _webdavPut.setExecTime(BigInteger.valueOf(perfStats.getWebDAVPutExecTime()));
	    
            //
            // webdav get
            //
	    _webdavGet.setOps(BigInteger.valueOf(perfStats.getWebDAVGetOps())); 
            _webdavGet.setOpSec(Double.toString(
		divide(perfStats.getWebDAVGetOps(), _interval))); 
            _webdavGet.setKbSec(Double.toString(
		divide(divide(perfStats.getWebDAVGetBytesProcessed(), KILOBYTE), (long)_interval)));
            _webdavGet.setExecTime(BigInteger.valueOf(perfStats.getWebDAVGetExecTime()));
	    
            //
            // load
            //
            _loadOneMinute= Float.toString(perfStats.getLoad1M());
            _loadFiveMinute= Float.toString(perfStats.getLoad5M());
            _loadFifteenMinute= Float.toString(perfStats.getLoad15M()); 
	    
            // 
            // memory 
            //
            /*
              memUsagePercent = perfStats.getMemUsagePercent();
              freeMem = perfStats.getFreeMem();
              totalMem = perfStats.getTotalMem();
              Float.toString(freeMem);
              Float.toString(totalMem);
              Float.toString(((totalMem-freeMem)*100)/totalMem); 
            */
            // 
            // Disk Usage is in MBytes
	    long diskUsed = perfStats.getDiskUsed();
            _usedMb= BigInteger.valueOf(diskUsed);

	    // Disk Size is in MBytes
	    long diskSize = perfStats.getDiskSize();
            _totalMb=BigInteger.valueOf(diskSize);
	    _usePercent = Double.toString(divide(diskUsed, diskSize) * 100);

        }  catch (NumberFormatException nfe) {
	    logger.log(Level.SEVERE, "Parsing exception: ", nfe); 
        } 
    }
      
    private Hashtable[] getAlertPerfStats(AlerterServerIntf alertApi) 
        throws ManagedServiceException {
        Hashtable[] alertObjMap;

        /* Get NodeMgr Proxy */

        Node[] nodes = getNodeMgrProxy().getNodes();

        /*
         * Array of Hashtables
         * alertObjMap[nodes = 1..16] = Hashtable<Alert Property String, AlertObject>
         */
	try {
	    alertObjMap = alertApi.getClusterAlertProperties(nodes, alertPerfStatProps);
	    if (alertObjMap == null) {
		throw new AlertException ("Engine not initialized");
	    } 
	}
	catch (Exception e) {
	    logger.log(Level.SEVERE, "Failed to fetch performance properties from alert tree.", e);
	    throw new ManagedServiceException("Failed to fetch performance properties.");
	}
        return alertObjMap; 
    }
    private AlerterServerIntf getAlerterServerApi() {
        return AlerterServerIntf.Proxy.getServiceAPI();
    }
    
    /**
     * Check to see if the Data Services are available by checking
     * to see whether we have a quorum
     * @return boolean true if we have a quorum and data services are online or
     * will be up shortly, false otherwise
     */
    private boolean areDataServicesAvailable() 
    throws MgmtException {
	NodeMgrService.Proxy nodeProxy = null;
	try {
	    nodeProxy = ServiceManager.proxyFor(ServiceManager.LOCAL_NODE);
	} catch (Exception ignore) {
	    //
	}
	return nodeProxy != null && nodeProxy.hasQuorum();
    }


    /**
     * Calculate the delta of 2 performance samples.
     */
    protected long calculateDelta(long newValue, long oldValue) {
	
	if (newValue == oldValue)
	    return 0;
	
	if (newValue > oldValue)
	    return newValue - oldValue;
	
	// Counter has wrapped.
	return Long.MAX_VALUE - oldValue + newValue;
    }
    
    protected double divide(long value, long divisor) {
	if (divisor == 0)
	    return 0;
	return (double)value/divisor;
    }
    
    protected double divide(long value, double divisor) {
	if (divisor == 0)
	    return 0;
	return value/divisor;
    }
    
    protected double divide(double value, double divisor) {
	if (divisor == 0)
	    return 0;
	return value/divisor;
    }
}
