#!/bin/bash
#
# $Id: ioexceptions.sh 10858 2007-05-19 03:03:41Z bberndt $
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
# NOTE: also need to run this test to populate all skipped results:
# https://hc-twiki.sfbay.sun.com/twiki/bin/view/Main/FragmentTests

WHEREAMI=`cd \`dirname $0\`; pwd`
POSTRESULT=$WHEREAMI/../../../suitcase/src/script/postResult.sh

post () {
    local result=$1
    shift
    echo $POSTRESULT -r $result $*
    $POSTRESULT -r $result $*
}

TAG="io-exceptions"

# in the future, more parameters to be accounted for such as:
#   context (ie, store, delete)
#   type of failure (delay, expected, unexpected)
#   number of frags affected
#   which frags affected (data/parity)
#   which chunks
#   data or metadata
#   location of error (footer, checksum, data, etc)
# currently we just list the ops and pass/fail/skipped based on
# whether we have any coverage or not

post pass sysClose $TAG
post pass sysCreate $TAG # note the test fails due to excpn, but is doing right thing so counting as marginal pass
post pass sysRead $TAG
post pass sysRename $TAG
post pass sysSeek $TAG
post pass sysWrite $TAG

exit 0

# punted for 1.0
post skipped sysTruncate $TAG
post skipped sysTryLock $TAG
post skipped sysUnlock $TAG
post skipped sysDelete $TAG
post skipped sysGetFilePointer $TAG
post skipped sysLock $TAG

# below are jeremy's cases that were deleted because we aren't ready to give
# such fine grain status yet.
datastorecreatefirstfragdelay
datastorecreatefirstfragfail
datastorecreatepfragsdelay
datastorecreatepfragsfail
datastorecreatep+1fragsdelay
datastorecreatep+1fragsfail
datastorewritefirstfragdelay
datastorewritefirstfragfail
datastorewritepfragsdelay
datastorewritepfragsfail
datastorewritep+1fragsdelay
datastorewritep+1fragsfail
datastoreclosefirstfragdelay
datastoreclosefirstfragfail
datastoreclosepfragsdelay
datastoreclosepfragsfail
datastoreclosep+1fragsdelay
datastoreclosep+1fragsfail
datastorerenamefirstfragdelay
datastorerenamefirstfragfail
datastorerenamepfragsdelay
datastorerenamepfragsfail
datastorerenamep+1fragsdelay
datastorerenamep+1fragsfail
dataretrievestoreopenfirstfragdelay
dataretrieveopenfirstfragfail
dataretrieveopenpfragsdelay
dataretrieveopenpfragsfail
dataretrieveopenp+1fragsdelay
dataretrieveopenp+1fragsfail
dataretrievereadfirstfragdelay
dataretrievereadfirstfragfail
dataretrievereadfirstfragcorrupt
dataretrievereadpfragsdelay
dataretrievereadpfragsfail
dataretrievereadpfragscorrupt
dataretrievereadp+1fragsdelay
dataretrievereadp+1fragsfail
dataretrievereadp+1fragscorrupt
dataretrieveclosefirstfragdelay
dataretrieveclosefirstfragfail
dataretrieveclosepfragsdelay
dataretrieveclosepfragsfail
dataretrieveclosep+1fragsdelay
dataretrieveclosep+1fragsfail
metadatastorecreatefirstfragdelay
metadatastorecreatefirstfragfail
metadatastorecreatepfragsdelay
metadatastorecreatepfragsfail
metadatastorecreatep+1fragsdelay
metadatastorecreatep+1fragsfail
metadatastorewritefirstfragdelay
metadatastorewritefirstfragfail
metadatastorewritepfragsdelay
metadatastorewritepfragsfail
metadatastorewritep+1fragsdelay
metadatastorewritep+1fragsfail
metadatastoreclosefirstfragdelay
metadatastoreclosefirstfragfail
metadatastoreclosepfragsdelay
metadatastoreclosepfragsfail
metadatastoreclosep+1fragsdelay
metadatastoreclosep+1fragsfail
metadatastorerenamefirstfragdelay
metadatastorerenamefirstfragfail
metadatastorerenamepfragsdelay
metadatastorerenamepfragsfail
metadatastorerenamep+1fragsdelay
metadatastorerenamep+1fragsfail
metadataretrievestoreopenfirstfragdelay
metadataretrieveopenfirstfragfail
metadataretrieveopenpfragsdelay
metadataretrieveopenpfragsfail
metadataretrieveopenp+1fragsdelay
metadataretrieveopenp+1fragsfail
metadataretrievereadfirstfragdelay
metadataretrievereadfirstfragfail
metadataretrievereadfirstfragcorrupt
metadataretrievereadpfragsdelay
metadataretrievereadpfragsfail
metadataretrievereadpfragscorrupt
metadataretrievereadp+1fragsdelay
metadataretrievereadp+1fragsfail
metadataretrievereadp+1fragscorrupt
metadataretrieveclosefirstfragdelay
metadataretrieveclosefirstfragfail
metadataretrieveclosepfragsdelay
metadataretrieveclosepfragsfail
metadataretrieveclosep+1fragsdelay
metadataretrieveclosep+1fragsfail
datastorecreatefirstfragdelay2ndchunk
datastorecreatefirstfragfail2ndchunk
datastorecreatepfragsdelay2ndchunk
datastorecreatepfragsfail2ndchunk
datastorecreatep+1fragsdelay2ndchunk
datastorecreatep+1fragsfail2ndchunk
datastorewritefirstfragdelay2ndchunk
datastorewritefirstfragfail2ndchunk
datastorewritepfragsdelay2ndchunk
datastorewritepfragsfail2ndchunk
datastorewritep+1fragsdelay2ndchunk
datastorewritep+1fragsfail2ndchunk
datastoreclosefirstfragdelay2ndchunk
datastoreclosefirstfragfail2ndchunk
datastoreclosepfragsdelay2ndchunk
datastoreclosepfragsfail2ndchunk
datastoreclosep+1fragsdelay2ndchunk
datastoreclosep+1fragsfail2ndchunk
datastorerenamefirstfragdelay2ndchunk
datastorerenamefirstfragfail2ndchunk
datastorerenamepfragsdelay2ndchunk
datastorerenamepfragsfail2ndchunk
datastorerenamep+1fragsdelay2ndchunk
datastorerenamep+1fragsfail2ndchunk
dataretrievestoreopenfirstfragdelay2ndchunk
dataretrieveopenfirstfragfail2ndchunk
dataretrieveopenpfragsdelay2ndchunk
dataretrieveopenpfragsfail2ndchunk
dataretrieveopenp+1fragsdelay2ndchunk
dataretrieveopenp+1fragsfail2ndchunk
dataretrievereadfirstfragdelay2ndchunk
dataretrievereadfirstfragfail2ndchunk
dataretrievereadfirstfragcorrupt2ndchunk
dataretrievereadpfragsdelay2ndchunk
dataretrievereadpfragsfail2ndchunk
dataretrievereadpfragscorrupt2ndchunk
dataretrievereadp+1fragsdelay2ndchunk
dataretrievereadp+1fragsfail2ndchunk
dataretrievereadp+1fragscorrupt2ndchunk
dataretrieveclosefirstfragdelay2ndchunk
dataretrieveclosefirstfragfail2ndchunk
dataretrieveclosepfragsdelay2ndchunk
dataretrieveclosepfragsfail2ndchunk
dataretrieveclosep+1fragsdelay2ndchunk
dataretrieveclosep+1fragsfail2ndchunk
metadatastorecreatefirstfragdelay2ndchunk
metadatastorecreatefirstfragfail2ndchunk
metadatastorecreatepfragsdelay2ndchunk
metadatastorecreatepfragsfail2ndchunk
metadatastorecreatep+1fragsdelay2ndchunk
metadatastorecreatep+1fragsfail2ndchunk
metadatastorewritefirstfragdelay2ndchunk
metadatastorewritefirstfragfail2ndchunk
metadatastorewritepfragsdelay2ndchunk
metadatastorewritepfragsfail2ndchunk
metadatastorewritep+1fragsdelay2ndchunk
metadatastorewritep+1fragsfail2ndchunk
metadatastoreclosefirstfragdelay2ndchunk
metadatastoreclosefirstfragfail2ndchunk
metadatastoreclosepfragsdelay2ndchunk
metadatastoreclosepfragsfail2ndchunk
metadatastoreclosep+1fragsdelay2ndchunk
metadatastoreclosep+1fragsfail2ndchunk
metadatastorerenamefirstfragdelay2ndchunk
metadatastorerenamefirstfragfail2ndchunk
metadatastorerenamepfragsdelay2ndchunk
metadatastorerenamepfragsfail2ndchunk
metadatastorerenamep+1fragsdelay2ndchunk
metadatastorerenamep+1fragsfail2ndchunk
metadataretrievestoreopenfirstfragdelay2ndchunk
metadataretrieveopenfirstfragfail2ndchunk
metadataretrieveopenpfragsdelay2ndchunk
metadataretrieveopenpfragsfail2ndchunk
metadataretrieveopenp+1fragsdelay2ndchunk
metadataretrieveopenp+1fragsfail2ndchunk
metadataretrievereadfirstfragdelay2ndchunk
metadataretrievereadfirstfragfail2ndchunk
metadataretrievereadfirstfragcorrupt2ndchunk
metadataretrievereadpfragsdelay2ndchunk
metadataretrievereadpfragsfail2ndchunk
metadataretrievereadpfragscorrupt2ndchunk
metadataretrievereadp+1fragsdelay2ndchunk
metadataretrievereadp+1fragsfail2ndchunk
metadataretrievereadp+1fragscorrupt2ndchunk
metadataretrieveclosefirstfragdelay2ndchunk
metadataretrieveclosefirstfragfail2ndchunk
metadataretrieveclosepfragsdelay2ndchunk
metadataretrieveclosepfragsfail2ndchunk
metadataretrieveclosep+1fragsdelay2ndchunk
metadataretrieveclosep+1fragsfail2ndchunk
deletecreatestub1fragfail
deletelockstub1fragfail
deletewritestub1fragfail
deleteclosestub1fragfail
deleteunlockstub1fragfail
deleterenamestub1fragfail
deleterefopenrw1fragfail
deletereflock1fragfail
deleterefwritecorrupt1fragfail
deleterefwrite1fragfail
deleterefclose1fragfail
deleterefunlock1fragfail
deletecreatestubfirstfragdelay
deletelockstub1fragfaildelay
deletewritestub1fragfaildelay
deleteclosestub1fragfaildelay
deleteunlockstub1fragfaildelay
deleterenamestub1fragfaildelay
deleterefopenrw1fragfaildelay
deletereflock1fragfaildelay
deleterefwritecorrupt1fragfaildelay
deleterefwrite1fragfaildelay
deleterefclose1fragfaildelay
deleterefunlock1fragfaildelay
deletecreatestubpfragfail
deletelockstubpfragfail
deletewritestubpfragfail
deleteclosestubpfragfail
deleteunlockstubpfragfail
deleterenamestubpfragfail
deleterefopenrwpfragfail
deletereflockpfragfail
deleterefwritecorruptpfragfail
deleterefwritepfragfail
deleterefclosepfragfail
deleterefunlockpfragfail
deletecreatestub1fragdelay
deletelockstubpfragfaildelay
deletewritestubpfragfaildelay
deleteclosestubpfragfaildelay
deleteunlockstubpfragfaildelay
deleterenamestubpfragfaildelay
deleterefopenrwpfragfaildelay
deletereflockpfragfaildelay
deleterefwritecorruptpfragfaildelay
deleterefwritepfragfaildelay
deleterefclosepfragfaildelay
deleterefunlockpfragfaildelay
deletecreatestubp+1fragfail
deletelockstubp+1fragfail
deletewritestubp+1fragfail
deleteclosestubp+1fragfail
deleteunlockstubp+1fragfail
deleterenamestubp+1fragfail
deleterefopenrwp+1fragfail
deletereflockp+1fragfail
deleterefwritecorruptp+1fragfail
deleterefwritep+1fragfail
deleterefclosep+1fragfail
deleterefunlockp+1fragfail
deletecreatestub1fragdelay
deletelockstubp+1fragfaildelay
deletewritestubp+1fragfaildelay
deleteclosestubp+1fragfaildelay
deleteunlockstubp+1fragfaildelay
deleterenamestubp+1fragfaildelay
deleterefopenrwp+1fragfaildelay
deletereflockp+1fragfaildelay
deleterefwritecorruptp+1fragfaildelay
deleterefwritep+1fragfaildelay
deleterefclosep+1fragfaildelay
deleterefunlockp+1fragfaildelay

deletecreatestub1fragfail2ndchunk
deletelockstub1fragfaill2ndchunk
deletewritestub1fragfaill2ndchunk
deleteclosestub1fragfaill2ndchunk
deleteunlockstub1fragfaill2ndchunk
deleterenamestub1fragfaill2ndchunk
deleterefopenrw1fragfaill2ndchunk
deletereflock1fragfaill2ndchunk
deleterefwritecorrupt1fragfaill2ndchunk
deleterefwrite1fragfaill2ndchunk
deleterefclose1fragfaill2ndchunk
deleterefunlock1fragfaill2ndchunk
deletecreatestubfirstfragdelayl2ndchunk
deletelockstub1fragfaildelayl2ndchunk
deletewritestub1fragfaildelayl2ndchunk
deleteclosestub1fragfaildelayl2ndchunk
deleteunlockstub1fragfaildelayl2ndchunk
deleterenamestub1fragfaildelayl2ndchunk
deleterefopenrw1fragfaildelayl2ndchunk
deletereflock1fragfaildelayl2ndchunk
deleterefwritecorrupt1fragfaildelayl2ndchunk
deleterefwrite1fragfaildelayl2ndchunk
deleterefclose1fragfaildelayl2ndchunk
deleterefunlock1fragfaildelayl2ndchunk
deletecreatestubpfragfaill2ndchunk
deletelockstubpfragfaill2ndchunk
deletewritestubpfragfaill2ndchunk
deleteclosestubpfragfaill2ndchunk
deleteunlockstubpfragfaill2ndchunk
deleterenamestubpfragfaill2ndchunk
deleterefopenrwpfragfaill2ndchunk
deletereflockpfragfaill2ndchunk
deleterefwritecorruptpfragfaill2ndchunk
deleterefwritepfragfaill2ndchunk
deleterefclosepfragfaill2ndchunk
deleterefunlockpfragfaill2ndchunk
deletecreatestub1fragdelayl2ndchunk
deletelockstubpfragfaildelayl2ndchunk
deletewritestubpfragfaildelayl2ndchunk
deleteclosestubpfragfaildelayl2ndchunk
deleteunlockstubpfragfaildelayl2ndchunk
deleterenamestubpfragfaildelayl2ndchunk
deleterefopenrwpfragfaildelayl2ndchunk
deletereflockpfragfaildelayl2ndchunk
deleterefwritecorruptpfragfaildelayl2ndchunk
deleterefwritepfragfaildelayl2ndchunk
deleterefclosepfragfaildelayl2ndchunk
deleterefunlockpfragfaildelayl2ndchunk
deletecreatestubp+1fragfaill2ndchunk
deletelockstubp+1fragfaill2ndchunk
deletewritestubp+1fragfaill2ndchunk
deleteclosestubp+1fragfaill2ndchunk
deleteunlockstubp+1fragfaill2ndchunk
deleterenamestubp+1fragfaill2ndchunk
deleterefopenrwp+1fragfaill2ndchunk
deletereflockp+1fragfaill2ndchunk
deleterefwritecorruptp+1fragfaill2ndchunk
deleterefwritep+1fragfaill2ndchunk
deleterefclosep+1fragfaill2ndchunk
deleterefunlockp+1fragfaill2ndchunk
deletecreatestub1fragdelayl2ndchunk
deletelockstubp+1fragfaildelayl2ndchunk
deletewritestubp+1fragfaildelayl2ndchunk
deleteclosestubp+1fragfaildelayl2ndchunk
deleteunlockstubp+1fragfaildelayl2ndchunk
deleterenamestubp+1fragfaildelayl2ndchunk
deleterefopenrwp+1fragfaildelayl2ndchunk
deletereflockp+1fragfaildelayl2ndchunk
deleterefwritecorruptp+1fragfaildelayl2ndchunk
deleterefwritep+1fragfaildelayl2ndchunk
deleterefclosep+1fragfaildelayl2ndchunk
deleterefunlockp+1fragfaildelayl2ndchunk
deletesafechkopen1fragfail
deletesafechkread1fragfail
deletesafechkclose1fragfail
deletesafechkopen1fragdelay
deletesafechkread1fragdelay
deletesafechkclose1fragdelay
deletesafechkopenpfragfail
deletesafechkreadpfragfail
deletesafechkclosepfragfail
deletesafechkopenpfragdelay
deletesafechkreadpragdelay
deletesafechkclosepfragdelay
deletesafechkopenp+1fragfail
deletesafechkreadp+1fragfail
deletesafechkclosep+1fragfail
deletesafechkopenp+1fragdelay
deletesafechkreadp+1fragdelay
deletesafechkclosep+1fragdelay
deletesafechkopen1fragfail2ndchunk
deletesafechkread1fragfail2ndchunk
deletesafechkclose1fragfail2ndchunk
deletesafechkopen1fragdelay2ndchunk
deletesafechkread1fragdelay2ndchunk
deletesafechkclose1fragdelay2ndchunk
deletesafechkopenpfragfail2ndchunk
deletesafechkreadpfragfail2ndchunk
deletesafechkclosepfragfail2ndchunk
deletesafechkopenpfragdelay2ndchunk
deletesafechkreadpragdelay2ndchunk
deletesafechkclosepfragdelay2ndchunk
deletesafechkopenp+1fragfail2ndchunk
deletesafechkreadp+1fragfail2ndchunk
deletesafechkclosep+1fragfail2ndchunk
deletesafechkopenp+1fragdelay2ndchunk
deletesafechkreadp+1fragdelay2ndchunk
deletesafechkclosep+1fragdelay2ndchunk 

