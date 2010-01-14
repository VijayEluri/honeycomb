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


package com.sun.honeycomb.hctest.rmi.spsrv.common;

import com.sun.honeycomb.hctest.CmdResult;
import com.sun.honeycomb.hctest.util.*;

import com.sun.honeycomb.test.util.*;

//import com.sun.honeycomb.admin.FRU;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;

public interface SPSrvIF extends Remote {

    public String nodeStatus(int node) throws RemoteException;

    public String spStatus() throws RemoteException;

    public CmdResult checkNodes(boolean comprehensive) throws RemoteException;

    public void shutdownNodeServers() throws RemoteException;

    public void logMsg(String msg) throws RemoteException;
    public void logMsg(String msg, int node) throws RemoteException;

    public HClusterConfig getConfig() throws RemoteException;

    public HClusterState getClusterState(ClusterTestContext c) 
                                                        throws RemoteException;

    public HOidInfo getOidInfo(String oid, boolean thisOnly)
                                                        throws RemoteException;

    public void deleteFragments(List fragments) throws RemoteException;

    public void restoreFragments(List fragments) throws RemoteException;

/*
    public void corruptFragments(List fragments, int nbytes, byte mask)
                                                        throws RemoteException;
*/

    public void injectServiceProblem(int node, String svc_name, String action)
                                                        throws RemoteException;

    public CmdResult rebootNode(int node, boolean fast) throws RemoteException;

    public void disableNode(int nodeid) throws RemoteException;

    public void disableDisk(int nodeid, String disk) throws RemoteException;

    //public void disableFRU(FRU fru) throws RemoteException;
}
