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




import java.util.Map;
import java.io.IOException;

import com.sun.honeycomb.client.SystemRecord;
import com.sun.honeycomb.client.NameValueRecord;
import com.sun.honeycomb.client.NameValueObjectArchive;
import com.sun.honeycomb.client.ObjectIdentifier;

import com.sun.honeycomb.common.ArchiveException;


/** This class demonstrates how to add a metadata record to an existing 
 * object on a StorageTek 5800 server
 */
public class AddMetadata
{

    /** Add a metadata record to an existing  object on a StorageTek 5800 server
     */
    public static SystemRecord addMetadata(String address, String oid, Map metadata)
    throws ArchiveException, IOException {

        NameValueObjectArchive archive = Util.getNameValueObjectArchive(address);
        NameValueRecord r = archive.createRecord();
        r.putAll(metadata);
        return archive.storeMetadata(new ObjectIdentifier(oid), r);
    }


    public static void main(String [] argv) {
        try {
            CommandLine commandline = new CommandLine(AddMetadata.class, 2, true);
            commandline.acceptFlag("m", true, true);
            if (commandline.parse(argv) && !commandline.helpMode()){
                String server = commandline.getOrderedArg(0);
                String oid = commandline.getOrderedArg(1);
                Map metadata = commandline.getNameValuePairs("m", "=");
                SystemRecord sr = addMetadata(server, oid, metadata);
                System.out.println(sr.getObjectIdentifier());
            } else {
		if (!commandline.helpMode()) {
                    System.exit(1);
		}
            } 
        } catch (Exception e) {
            System.out.println("Operation failed " + e);
            System.exit(1);
        }
    }
}


/* $Id: AddMetadata.java 11208 2007-07-13 14:28:19Z bberndt $*/
