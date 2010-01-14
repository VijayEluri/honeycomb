#!/usr/bin/bash
#
# $Id: load_test.sh 10846 2007-05-19 02:34:13Z bberndt $
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

i=500


while [ $i -gt 0 ]; do
    echo "";
    echo "Iteration $i";
    echo "------------";
    echo "";
    let i=i-1;
    run_example.sh GetHCCell;
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c expansion
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c wipe
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c reboot
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c powerOff
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellInfoCmd -c joinSilo
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c setEncryptedPasswd newPasswd
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c setPublicKey newPublicKey
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c addCell 99.88.77.66
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh HCCellCmd -c delCell 3
    if [ $? -ne 0 ]; then
        exit 1
    fi
    run_example.sh GetHCCellInfo;
    if [ $? -ne 0 ]; then
        exit 1
    fi
done


