#!/bin/sh
#
# $Id: regressiontest.sh 10853 2007-05-19 02:50:20Z bberndt $
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

# Only edit this script in the repository
# under honeycomb/tools
#
# usage: regression cluster [svnroot version]
#
# example: regressiontest.sh dev331 /export/home/sarahg/svn/build/automated-build-2005-09-14-1548 automated-build-2005-09-14-1548
#
# Notes:
#
# This script must be able to find an ssh key to access the cluster.
# It will look in the workspace if possible, and copy it to a tmp location
# with the right perms.
#
# The cheat node must have the /utils/ENV file configured correctly
#

trap cleanup_ssh 0 2 15

ROOT=`dirname $0`
if [ "$ROOT" = "." ]; then
    ROOT=`pwd`
fi

. $ROOT/common.sh

print_usage() {
    echo
    echo "usage: cluster [client | svnroot] [version]"
    echo
    echo "  cluster     - remote system to be installed, e.g. dev321"
    echo "  client      - remote client on which to run tests, e.g. cl8"
    echo "  svnroot     - defaults to ../.. (relative to script working dir)"
    echo "  version     - build version tag, defaults to username_date"
    echo
    echo "  If no client is specified, tests are run from local machine, "
    echo "  using the hctest code from the svnroot. Client hostname are  "
    echo "  assumed to start with 'cl' string."  
    echo
}


CLUSTER=$1
SVNROOT=$2
CLIENT=$2
VERSION=$3
REMOTE_CLIENT=0

if [ -z "$CLUSTER" ]; then
    print_usage
    exit 1
fi

# if no svnroot given, use the one in common.sh
if [ -z "$SVNROOT" ]; then
    SVNROOT=$DEFAULTSVNROOT
else
    # check if this is a svnroot, or client hostname
    replace=`echo "$CLIENT" | sed s/\^cl/XXX/`
    if [ "$CLIENT" != "$replace" ]; then
        REMOTE_CLIENT=1
    fi
fi

if [ $REMOTE_CLIENT -eq 0 -a ! -d $SVNROOT ]; then
    echo "directory $SVNROOT does not exist"
    print_usage
    exit 1
fi

# if no version specified, use the current username and date
if [ -z "$VERSION" ]; then
    VERSION="`echo $USER`_`date +%F-%H%M`"
fi

configure_ssh $SVNROOT

# be sure data vip is up
ping_cheat $CLUSTER-data

if [ $REMOTE_CLIENT -eq 1 ]; then

    # lauch tests on remote client machine
    ping_cheat $CLIENT
    HCTESTBIN="/opt/test/bin"
    print_date "Starting tests on $CLLIENT against cluster $CLUSTER for version $VERSION at"

    run_ssh_cmd root@$CLIENT "\
    export PATH=\$PATH:/usr/lib/java/bin;\
    $HCTESTBIN/runtest -ctx cluster=$CLUSTER:build=$VERSION:explore:include=regression"

else 

    # lauch tests on local machine
    HCTESTBIN=$SVNROOT/build/hctest/dist/bin
    print_date "Starting tests from dir $SVNROOT on cluster $CLUSTER for version $VERSION at"

    run $HCTESTBIN/runtest -ctx cluster=$CLUSTER:build=$VERSION:explore:include=regression

fi

sleep 10

# This script is just a wrapper to run the regression tests; see twiki
# for info on how to run other tests, including an Audit of results.

print_date "Finished with testrun at"
