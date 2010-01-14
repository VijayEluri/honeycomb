#! /bin/sh
#
# $Id: convert_to_hadbquery.sh 10857 2007-05-19 03:01:32Z bberndt $
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

DIR=`cd \`dirname $0\`/..; pwd`

CLASSPATH="$DIR/lib/bsh-2.0b2.jar":"$DIR/lib/honeycomb-common.jar":"$DIR/lib/servlet-4.2.19.jar":"$DIR/lib/jetty-4.2.20.jar":"$DIR/lib/concurrent.jar":"$DIR/lib/honeycomb-emulator.jar":"$DIR/lib/jug.jar":"$DIR/lib/md_caches/derby-cache.jar":"$DIR/lib/derby-10.1.1.0.jar"

java  -Demulator.root=$DIR -Dmd.cache.path=$DIR/lib/md_caches -Dfscache.class=com.sun.honeycomb.fscache.DerbyFileCache -Duid.lib.path=emulator -classpath $CLASSPATH QueryConvert "$@"

