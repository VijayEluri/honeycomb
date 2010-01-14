#! /bin/sh

#
# $Id: start_with_hadb.sh 7967 2006-04-20 01:17:55Z sarahg $
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

CLASSPATH=\
"$DIR/lib/bsh-2.0b2.jar":\
"$DIR/lib/honeycomb-common.jar":\
"$DIR/lib/servlet-4.2.19.jar":\
"$DIR/lib/jetty-4.2.20.jar":\
"$DIR/lib/concurrent.jar":\
"$DIR/lib/honeycomb-emulator.jar":\
"$DIR/lib/jug.jar":\
"$DIR/lib/honeycomb-hadb.jar":\
"$DIR/lib/honeycomb-server.jar":\
"$DIR/lib/derby-10.1.1.0.jar":\
"$DIR/lib/hadbjdbc4.jar"

#HADB-based emulator always enables assertions
java -ea -Xms64m -classpath "$CLASSPATH" -Demulator.root="$DIR" -Dmd.cache.path="$DIR/lib/md_caches" -Djava.library.path="$DIR/lib" -Dmd.hadb.jdbc.url=jdbc:sun:hadb:localhost:15005 -Djava.util.logging.config.file="$DIR/config/logger.cfg" -Duid.lib.path=emulator com.sun.honeycomb.cm.NodeMgr

if (test $? -ne 0) then
  echo "A problem was encountered in starting/running the advanced emulator"
fi
