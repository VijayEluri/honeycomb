#!/bin/sh
#
# $Id: healing_test.sh 11940 2008-03-21 19:17:17Z dm155201 $
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
#
# For cleanest test, run on empty cluster. 
#
# Test stores X objects using EMI tools,
# then takes a snapshot (must have whitebox pkg!),
# disables a disk and waits for healing to complete,
# verifies against snapshot, and
# does read-back verificatio with EMI tools.
# Then it reenables the disk, waits for heal-back,
# and repeats verification.
#
# TODO: Test needs to wipe the disk before reenabling!
#
# Test duration ~30 min for small data set, ~24 hours for large.
#

if [ "$#" != "2" ]; then
  echo "Usage: $0 <cluster> <dataSet>"
  echo "DataSet choices: SmallSet_SmallFiles, SmallSet_LargeFiles, LargeSet_SmallFiles, FullCluster_LargeFiles"
  exit -1
fi

CLUSTER=$1
DATASET=$2

ROOT=`dirname $0`
if [ "$ROOT" = "." ]; then
    ROOT=`pwd`
fi

# Load functions
source ${ROOT}/healing_base.sh

check_prerequisites $CLUSTER

# output files
PWD=`pwd`
STOREFILE=`mktemp -p $PWD stores.XXXXXX` # will contain stored OIDs
RETRIEVEFILE_1=`mktemp -p $PWD retrieves_1.XXXXXX` # after healing
QUERYFILE_1=`mktemp -p $PWD queries_1.XXXXXX`
RETRIEVEFILE_2=`mktemp -p $PWD retrieves_2.XXXXXX` # after heal-back
QUERYFILE_2=`mktemp -p $PWD queries_2.XXXXXX`

### MAIN ###

# ensure correct datadoctor settings
#
init_dd_cycle

# store data on the cluster
#
generate_data_set 

# take live snapshot
#
SNAPSHOT_OUT=`mktemp -p $PWD snapshot.XXXXXX`
echo "[`date`] Taking live snapshot... Output file ${SNAPSHOT_OUT}"
ssh root@${CLUSTER}-cheat "/bin/yes | /opt/test/bin/snapshot.sh delete $CLUSTER $SNAPSHOT" >${SNAPSHOT_OUT} 2>&1
ssh root@${CLUSTER}-cheat "/bin/yes | /opt/test/bin/snapshot.sh save $CLUSTER $SNAPSHOT live" >>${SNAPSHOT_OUT} 2>&1

# this number is not used, only for logging
#
get_heal_time
LASTHEALTIME="$HEALTIME"
echo "[`date`] Previous healing cycle completed at $LASTHEALTIME"

# Kill a disk and wait for healing to complete
#
select_victim
echo "[`date`] Disabling disk $NODE:$DISK"
run_cli_command "hwcfg -F -D DISK-${NODE}:${DISK}"

echo "[`date`] Waiting for healing cycle to complete"
wait_to_heal

# Do verification: snapshot, retrieve, query
#
verify_snapshot
# XXX Not sure if this conditional ever executes
if [ "$?" != "0" ]; then
  echo "[`date`] HACK: Waiting for 2nd healing cycle to complete..."
  wait_to_heal
  verify_snapshot
  if [ "$?" != "0" ]; then
    echo "[`date`] ERROR: Snapshot verification still fails after 2nd healing cycle."
    echo "EXITING..."
    exit -1
  fi
fi

do_emi_retrieve $CLUSTER $EMI_THREADS $STOREFILE ${RETRIEVEFILE_1}

do_emi_query $CLUSTER $EMI_THREADS $STOREFILE ${QUERYFILE_1}
 
# Reenable disk and wait for heal-back
#
# TODO: Wipe disk before reenabling for a more realistic test (wipe.sh -o <disk> is broken now!)
# Or maybe do rm -rf of fragment files on that disk
#
echo "[`date`] Reenabling disk $NODE:$DISK"
run_cli_command "hwcfg -F -E DISK-${NODE}:${DISK}"

echo "[`date`] Waiting for heal-back cycle to complete"
wait_to_heal

# Do verification again: snapshot, retrieve, query
#
verify_snapshot
if [ "$?" != "0" ]; then
  echo "[`date`] HACK: Waiting for 2nd heal-back cycle to complete..."
  wait_to_heal
  verify_snapshot
  if [ "$?" != "0" ]; then
    echo "[`date`] ERROR: Snapshot verification still fails after 2nd healing cycle."
    echo "EXITING..."
    exit -1
  fi
fi

do_emi_retrieve $CLUSTER $EMI_THREADS $STOREFILE ${RETRIEVEFILE_2}

do_emi_query $CLUSTER $EMI_THREADS $STOREFILE ${QUERYFILE_2}

echo "ALL DONE"
exit 0

