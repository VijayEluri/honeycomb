#!/bin/bash
#
# $Id: stanford_store_test.sh 11561 2007-10-02 14:53:57Z hs154345 $
#
# Copyright � 2008, Sun Microsystems, Inc.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#
#   # Redistributions of source code must retain the above copyright
# notice, this list of conditions and the following disclaimer.
#
#   # Redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution.
#
#   # Neither the name of Sun Microsystems, Inc. nor the names of its
# contributors may be used to endorse or promote products derived from
# this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
# IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
# TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
# PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
# OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



#
# Usage: ./stanford_perf_test_analysis.sh <logdir>
# <logdir> the directory of output
##

###################################################################
# PARAMETERS TO CONFIGURE

# cluster 
CLUSTER=dev331
 
# clients to be used for Ingest in the test
STRCLIENTS="cl84 cl85"

# number of threads per STR client 
STRPROCS="2"

STR_TIME=100 #minutes - time for STR tests

# END PARAMETERS TO CONFIGURE
########################################################################

# Arguments to the script

# log directory
LOGDIR=$1

let STORETIME=$STR_TIME*60

TMPARR1=($STRCLIENTS)
NUMSTRCLIENTS=${#TMPARR1[@]}
NUMSTRCLPROCS="${NUMSTRCLIENTS}x${STRPROCS}"

STRSUMFILE=${LOGDIR}/${CLUSTER}.${NUMSTRCLPROCS}.Store-Analysis.summary

# expected number of arguments
NUM_ARGS=1

_cmm_verifier () {
  OUTFILE="${LOGDIR}/status/cmm.`date +%m%d-%H%M`"
  echo
  echo -n "CMM VERIFIER [$OUTFILE]: "
  ssh ${CLUSTER}-cheat /opt/test/bin/cmm_verifier $NODES 1 1 yes quorum >$OUTFILE 2>&1
  grep "CLUSTER STATE" $OUTFILE |tail -1
}

# Get a measure of cluster fullness

_hadb_fullness () {
  OUTFILE=$1

  MSG="Count of objects in HADB:"
  if [ ! -e hadb_fullness ]; then
    # T98C20F is the system.object_ctime table
    echo "select count(*) from t_system;" >hadb_fullness.in
  fi
  scp -P 2001 hadb_fullness.in root@${CLUSTER}-admin:hadb_fullness
  COUNT=`ssh root@${CLUSTER}-admin -p 2001 /opt/SUNWhadb/4/bin/clusql -nointeractive localhost:15005 system+superduper -command=hadb_fullness |tail -2 |tr -d "[:space:]"`
  echo "$MSG $COUNT"  # to the main log
  echo >>$OUTFILE
  echo "[`date`] $MSG $COUNT" >>$OUTFILE

  MSG="[`date`] HADB deviceinfo:"
  echo >>$OUTFILE
  echo $MSG >>$OUTFILE
  echo admin | ssh root@${CLUSTER}-admin -p 2001 /opt/SUNWhadb/4/bin/hadbm deviceinfo honeycomb >> $OUTFILE

  MSG="[`date`] HADB resourceinfo:"
  echo >>$OUTFILE
  echo $MSG >>$OUTFILE
  echo admin |ssh root@${CLUSTER}-admin -p 2001 /opt/SUNWhadb/4/bin/hadbm resourceinfo honeycomb >>$OUTFILE
}

_cluster_fullness () {
  OUTFILE="${LOGDIR}/status/full.`date +%m%d-%H%M`"
  echo "CLUSTER FULLNESS [$OUTFILE]:" >>$OUTFILE

  echo -n "Disk fullness: " >>$OUTFILE
  ssh admin@${CLUSTER}-admin df -h >>$OUTFILE
  cat $OUTFILE  # to the main log
  echo >>$OUTFILE

  FULLNESS=`ssh admin@${CLUSTER}-admin df -h | awk '{print $8}' | sed -e 's/%//g'`

  echo "Physical disk fullness:" >>$OUTFILE
  ssh admin@${CLUSTER}-admin df -p >>$OUTFILE
  echo >>$OUTFILE

 _hadb_fullness $OUTFILE

}

_run_store() {

  echo "CLUSTER: ${CLUSTER}"
  echo "STR CLIENTS: ${STRCLIENTS}"
  echo "THREADS PER STR CLIENT: ${STRPROCS}"
  echo "Store for ${STORETIME} sec"

  echo [`date`] CREATING LOG DIRECTORIES ON ALL CLIENTS
  for CLIENT in $STRCLIENTS; do
      echo "Creating ${LOGDIR} and status directory on ${CLIENT}"
      ssh $CLIENT "mkdir -p ${LOGDIR}/status; mkdir -p ${LOGDIR}/Store"
  done
  
#  _cmm_verifier
  _cluster_fullness

  echo [`date`]  Run Store Test
  for CLIENT in $STRCLIENTS; do
    echo [`date`] Launching test on ${CLIENT}
    ssh $CLIENT "cd /opt/performance; source /etc/profile; ./stanford_local_test_analysis.sh Store ${CLUSTER} ${STRPROCS} ${STORETIME} ${LOGDIR}" &
  done
  wait 

  echo [`date`] Store Test Done

  _cluster_fullness

  for CLIENT in $STRCLIENTS; do
    echo [`date`] Launching analysis on ${CLIENT}
    ssh $CLIENT "cd /opt/performance; source /etc/profile; ./stanford_local_test_analysis.sh AnalyzeStore ${CLUSTER} ${STRPROCS} ${STORETIME} ${LOGDIR}" >>${STRSUMFILE} &
  done
  wait
  echo [`date`] Store Analysis Done

}

_print_usage () {
  echo "Usage: ./stanford_perf_test_analysis.sh <logdir> "
}

##########   MAIN    ###############


# Check arguments

if [ $# -ne $NUM_ARGS ]; then  
    _print_usage
    exit 1
fi
    
 # check schema

ssh admin@${CLUSTER}-admin mdconfig -l | egrep -i 'DOuuid' >/dev/null
if [ $? -ne 0 ]; then
    echo "Wrong Schema! Load Stanford Schema before preceding!"
    exit 1
fi

_run_store

echo [`date`] ALL DONE  

