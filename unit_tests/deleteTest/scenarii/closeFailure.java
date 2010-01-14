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



import com.sun.honeycomb.delete.Constants;
import com.sun.honeycomb.oa.OAClient;
import com.sun.honeycomb.oa.FragmentFaultEvent;
import com.sun.honeycomb.oa.FragmentFault;
import com.sun.honeycomb.oa.daal.nfs.FaultyNfsDAAL;

new bsh.Interpreter().source(System.getProperty("deletefmk.script.dir") +
                             File.separatorChar + "utils" + File.separatorChar +
                             "faultUtils.java", this.namespace);

ECHO("Tests that if enough closes fail, we abort the store and return failure");

OAClient oac = OAClient.getInstance();
chunksize = Constants.MAX_CHUNK_SIZE = oac.blockSize;
storesize = chunksize * 3 + 1;

void failFrags(OAClient oac, int storesize, boolean expectFailure) {
    oac.setMaxChunkSize(Constants.MAX_CHUNK_SIZE);

    ECHO("--> Store multiChunk object w/ chunkSize = blockSize");
    if (!expectFailure) {
    ECHO("Not expecting failure on store.");
        STORE(1, storesize, true);
        ECHO("Store succeeded.");
    DEREF(1,0);
        ECHO("Verify w/ retrieve");
        RETRIEVE(1, true);
    } else {
    ECHO("Expecting failure on store.");
        STORE(1, storesize, false);
    ECHO("Store failed correctly.");
    }
}

/**********************************************************************/
ECHO("--> Fault on 1 frag");
Collection faults = addFragmentFaultAllChunks("uncaught",
                                              FragmentFaultEvent.STORE,
                                              FaultyNfsDAAL.CLOSE,
                                              FragmentFault.IO_ERROR,
                                              1,
                                              4,
                                              OAClient.OBJECT_TYPE_DATA);

failFrags(oac, storesize, false);
assertTriggered(faults);
resetFaults(FaultyNfsDAAL.CLOSE.toString());

/**********************************************************************/
ECHO("--> Fault on 2 frags");
faults.addAll(addFragmentFaultAllChunks("uncaught",
    FragmentFaultEvent.STORE,
    FaultyNfsDAAL.CLOSE,
    FragmentFault.IO_ERROR,
    2,
    4,
    OAClient.OBJECT_TYPE_DATA));

failFrags(oac, storesize, false);
assertTriggered(faults);
resetFaults(FaultyNfsDAAL.CLOSE.toString());

/**********************************************************************/
ECHO("--> Fault on 3 frags");
faults.addAll(addFragmentFaultAllChunks("uncaught",
    FragmentFaultEvent.STORE,
    FaultyNfsDAAL.CLOSE,
    FragmentFault.IO_ERROR,
    3,
    4,
    OAClient.OBJECT_TYPE_DATA));

failFrags(oac, storesize, true);
assertMinTriggered(faults, 3);
resetFaults(FaultyNfsDAAL.CLOSE.toString());

/**********************************************************************/
ECHO("--> Store multiChunk object w/ chunkSize = blockSize and all closes fail...");

addFragmentFaultAllChunksAllFragments("uncaught",
    FragmentFaultEvent.STORE,
    FaultyNfsDAAL.CLOSE,
    FragmentFault.IO_ERROR,
    4,
    OAClient.OBJECT_TYPE_DATA);

STORE(1, storesize, false);
ECHO("As expected, store failed b/c all closes failed");

ECHO("Success.");
