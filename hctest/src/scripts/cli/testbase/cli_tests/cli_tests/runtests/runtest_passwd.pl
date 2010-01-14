#!/usr/local/bin/perl
#
# $Id: runtest_passwd.pl 10858 2007-05-19 03:03:41Z bberndt $
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
# Man for perl-expect
# http://search.cpan.org/~rgiersig/Expect-1.15/Expect.pod
#
#
#
use Expect;

$runDir = $ARGV[0];

push (@INC, $runDir);

do 'data_def.pi' || die "Can not load file $runDir/data_def.pi";

#$testArgs="cmddef: df -l| blah1 | blah2|cmddef: df -k| bleh1 | bleh2";

%cmd_arr = ();

sub initTestData {
    &reportError("Test data is not defined", 1 ) if (! defined $testArgs);
    my @testArgArr = split("cmddef:", $testArgs);
    foreach $argStr (@testArgArr) {
        print $argStr."\n";
        next if ($argStr =~ /^\s*$/);
        my @patternArr = split('\|', $argStr);
        my @cmd;
        for (my $i = 1;$i < $#patternArr + 1;$i++) {
            print $patternArr[$i]."\n";
            next if ($patternArr[$i] =~ /^\s*$/);
            $cmd[$i - 1] = $patternArr[$i];
        }
        $cmd_arr{$patternArr[0]} = \@cmd;
    }
}

&initTestData;

foreach $key (keys %cmd_arr) {
    print "Key: $key\n";
    foreach $ptr (@{$cmd_arr{$key}}) {
        print "Patt: $ptr\n";
    }
}


sub reportFail {
    &reportError("", "TEST FAIL\n");
}

sub reportPass {
    print "Test PASS\n";
}

$failedTests = 0;

sub reportError {
    my ($arr, $msg, $errCode) = @_;
    if ($errCode == 1) {
        print "FATAL ERROR: $msg\n";
        exit 1;
    } else {
        $failedTests += 1;
        print "$msg\n";
    }
}

sub runtests {
    my ($exp) = @_;

    print "------------ STARTING TEST RUN-----------------\n";
    $timeout = $testTimeout;
    $timeout = 60 if (! defined $testTimeout);    
    $exp->log_user(0);
    # $exp->log_stdout(0);
    $exp->debug(0);

    foreach $key (sort keys %cmd_arr) {

 
   

        my $str = "\$exp->expect($timeout,
                                [
                                timeout, \\&reportError, ('No patterns found. TEST FAIL.')
                                ]
                                ";
        $resArr = $cmd_arr{$key};        
        foreach $pattern (@$resArr) {
            
            $str = $str.",
                        [
                         $pattern 
                        ]", 
        }
        $str = $str.");";
        print "------------------------ Test: $key Start -----------------------\n";
        $exp->send("\r");
        $exp->log_stdout(0);
        $exp->send("$key\r");
        sleep($timeout);
        print "$str\n";
        eval $str;
        print "------------------------ Test: $key Done ------------------------\n";
        print "Acc: ".$exp->clear_accum();
 
    }
    print "------------ TESTS DONE-----------------\n";


}



sub cmdchk {
    my ($parentHost, $parentHostPwd, $parentHostPrompt, $testedHost, $testedHostPrompt) = @_;

    print "\n------------ LOGIN -----------------\n";
    $timeout = $loginTimeout;
    $timeout = 60 if (! defined $loginTimeout);
 # $exp->log_user(0);
  #  $exp->log_stdout(0);
    my $exp = Expect->spawn("ssh", $parentHost)
        or die "Cannot spawn ssh to $parentHost $!\n";

    # $exp->debug(3);
    $exp->debug(0);
    
    $exp->expect($timeout,
                 [
                  -re, ".*want to continue connecting.*" =>
                  sub {
                      my $exp = shift;
                      $exp->send("yes\n");
                      exp_continue;}
                  ],
                 [
                  -re, "s password:" => sub { print "Got pwd request. Continue..."; }
                  ],
                 [
                  timeout, \&reportError, ("No answer from host $parentHost", 1)
                  ]
                 );
    $exp->send("$parentHostPwd\r");
    $exp->send("\r");
    $exp->send("\r");
    sleep($timeout);
    $exp->send("\r");
    $exp->send("\r");
    $exp->expect($timeout,
                 [
                  "$parentHostPrompt" => sub { print "Got prompt. Continue..."; } 
                  ],
                 [
                  timeout, \&reportError, ("No prompt from host $parentHost", 1)
                  ]
                 );
   # $exp->send("ssh $testedHost\r");
   # $exp->send("\r");
   # $exp->send("\r");
   # sleep($timeout);
   ## $exp->send("\r");
   # $exp->send("\r");
   # $exp->expect($timeout,
               #  [
                 # -re, "$testedHostPrompt" => sub { print "Login done. Ready to run tests..."}
               #   ],
               #  [
               #   timeout, \&reportError, ("No prompt from tested host $testedHost", 1)
               #   ]
               #  );
 
   # print "\n------------ LOGIN DONE-----------------\n";

    &runtests($exp);

}

&cmdchk($masterHost, $masterPwd, $masterPrompt, $testedHost, $testedPrompt);

exit 1 if ($failedTests != 0);

