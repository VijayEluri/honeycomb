#!/usr/bin/ksh
#
# $Id: rwprobe.sh 10845 2007-05-19 02:31:46Z bberndt $
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
# rwprobe.sh - display top read/write bytes by process.
#
# This is measuring reads and writes at the application level. This matches
# read and write system calls.
#
# USAGE:        rwprobe.sh [-cC] [-j|-Z] [-n name] [-p pid]
#                          [-t top] [interval [count]]
# 
#               rwprobe.sh              # default output, 5 second samples
#
#               -C              # clear the screen
#               -c              # print counts
#               -j              # print project ID
#               -Z              # print zone ID
#               -n name         # this process name only
#               -p PID          # this PID only
#               -t top          # print top number only
#       eg,
#               rwprobe.sh 1            # 1 second samples
#               rwprobe.sh -t 10        # print top 10 only
#               rwprobe.sh -n bash      # monitor processes named "bash"
#               rwprobe.sh 5 12         # print 12 x 5 second samples
#
# FIELDS:
#               ZONE            Zone ID
#               PROJ            Project ID
#               UID             User ID
#               PID             Process ID
#               PPID            Parent Process ID
#               CMD             Process name
#               D               Direction, Read or Write
#               BYTES           Total bytes during sample
#               app_r           total reads during sample, Kbytes
#               app_w           total writes during sample, Kbytes
##

##############################
# --- Process Arguments ---
#

### default variables
opt_name=0; opt_pid=0; opt_clear=0; opt_proj=0; opt_zone=0
opt_def=1; opt_bytes=1; filter=0; pname=.; pid=0
opt_top=0; opt_count=0; interval=5; count=-1; top=0

### process options
while getopts Cchn:p:jt:Z name
do
        case $name in
        C)      opt_clear=1 ;;
        c)      opt_count=1; opt_bytes=0 ;;
        n)      opt_name=1; pname=$OPTARG ;;
        p)      opt_pid=1; pid=$OPTARG ;;
        j)      opt_proj=1; opt_def=0 ;;
        t)      opt_top=1; top=$OPTARG ;;
        Z)      opt_zone=1; opt_def=0 ;;
        h|?)    cat <<-END >&2
                USAGE: rwprobe.sh [-cC] [-j|-Z] [-n name] [-p pid]
                                  [-t top] [interval [count]]
 
                                -C        # clear the screen
                                -c        # print counts
                                -j        # print project ID
                                -Z        # print zone ID
                                -n name   # this process name only
                                -p PID    # this PID only
                                -t top    # print top number only
                   eg,
                        rwprobe.sh          # default output, 5 second samples
                        rwprobe.sh 1        # 1 second samples
                        rwprobe.sh -t 10    # print top 10 only
                        rwprobe.sh -n bash  # monitor processes named "bash"
                        rwprobe.sh -C 5 12  # print 12 x 5 second samples
		END
                exit 1
        esac
done

shift $(( $OPTIND - 1 ))

### option logic
if [[ "$1" > 0 ]]; then
        interval=$1; shift
fi
if [[ "$1" > 0 ]]; then
        count=$1; shift
fi
if (( opt_proj && opt_zone )); then
        opt_proj=0
fi
if (( opt_name || opt_pid )); then
        filter=1
fi
if (( opt_clear )); then
        clearstr=`clear`
else
        clearstr=.
fi



#################################
# --- Main Program, DTrace ---
#
/usr/sbin/dtrace -n '
 /*
  * Command line arguments
  */
 inline int OPT_def     = '$opt_def';
 inline int OPT_proj    = '$opt_proj';
 inline int OPT_zone    = '$opt_zone';
 inline int OPT_clear   = '$opt_clear';
 inline int OPT_bytes   = '$opt_bytes';
 inline int OPT_count   = '$opt_count';
 inline int OPT_name    = '$opt_name';
 inline int OPT_pid     = '$opt_pid';
 inline int OPT_top     = '$opt_top';
 inline int INTERVAL    = '$interval';
 inline int COUNTER     = '$count';
 inline int FILTER      = '$filter';
 inline int TOP         = '$top';
 inline int PID         = '$pid';
 inline string NAME     = "'$pname'";
 inline string CLEAR    = "'$clearstr'";
 
 #pragma D option quiet

 /*
  * Print header
  */
 dtrace:::BEGIN 
 {
        /* starting values */
        counts = COUNTER;
        secs = INTERVAL;
        app_r = 0;
        app_w = 0;

        printf("Tracing... Please wait.\n");
 }

 /*
  * Check event is being traced
  */
 sysinfo:::readch,
 sysinfo:::writech
 /pid != $pid/
 { 
        /* default is to trace unless filtering, */
        this->ok = FILTER ? 0 : 1;

        /* check each filter, */
        (OPT_name == 1 && NAME == execname)? this->ok = 1 : 1;
        (OPT_pid == 1 && PID == pid) ? this->ok = 1 : 1;
 }

 /*
  * Increment tallys
  */
 sysinfo:::readch
 /this->ok/
 {
        app_r += arg0;
 }
 sysinfo:::writech
 /this->ok/
 {
        app_w += arg0;
 }

 /*
  * Process event
  */
 sysinfo:::readch,
 sysinfo:::writech
 /this->ok/
 {
        /* choose statistic to track */
        this->value = OPT_bytes ? arg0 : 1;
        
        /*
         * Save details
         */
        OPT_def ? @out[uid, pid, ppid, execname,
            probename == "readch" ? "R" : "W"] = sum(this->value) : 1;
        OPT_proj ? @out[curpsinfo->pr_projid, pid, ppid, execname,
            probename == "readch" ? "R" : "W"] = sum(this->value) : 1;
        OPT_zone ? @out[curpsinfo->pr_zoneid, pid, ppid, execname,
            probename == "readch" ? "R" : "W"] = sum(this->value) : 1;

        this->ok = 0;
 }

 /*
  * Timer
  */
 profile:::tick-1sec
 {
        secs--;
 }

 /*
  * Print Report
  */
 profile:::tick-1sec
 /secs == 0/
 {
        /* fetch 1 min load average */
        this->load1a  = `hp_avenrun[0] / 65536;
        this->load1b  = ((`hp_avenrun[0] % 65536) * 100) / 65536;

        /* convert counters to Kbytes */
        app_r /= 1024;
        app_w /= 1024;

        /* print status */
        OPT_clear ? printf("%s", CLEAR) : 1;
        printf("%Y,  load: %d.%02d,  app_r: %6d KB,  app_w: %6d KB\n\n",
            walltimestamp, this->load1a, this->load1b, app_r, app_w);

        /* print headers */
        OPT_def  ? printf("  UID ") : 1;
        OPT_proj ? printf(" PROJ ") : 1;
        OPT_zone ? printf(" ZONE ") : 1;
        printf("%6s %6s %-16s %1s",
            "PID", "PPID", "CMD", "D");
        OPT_bytes ? printf(" %16s\n", "BYTES") : 1;
        OPT_count ? printf(" %16s\n", "COUNT") : 1;

        /* truncate to top lines if needed */
        OPT_top ? trunc(@out, TOP) : 1;

        /* print data */
        printa("%5d %6d %6d %-16s %1s %16@d\n", @out);
        printf("\n");

        /* clear data */
        trunc(@out);
        app_r = 0;
        app_w = 0;
        secs = INTERVAL;
        counts--;
 }

 /*
  * End of program
  */
 profile:::tick-1sec
 /counts == 0/
 {
        exit(0);
 }

 /*
  * Cleanup for Ctrl-C
  */
 dtrace:::END
 {
        trunc(@out);
 }
'
