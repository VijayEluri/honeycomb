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



package com.sun.honeycomb.emd.cache;

import java.util.HashMap;
import java.util.Set;
import com.sun.honeycomb.common.CacheRecord;
import com.sun.honeycomb.coding.Encoder;
import com.sun.honeycomb.coding.Decoder;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ExtendedCacheEntry 
    extends HashMap
    implements CacheRecord {
    
    public ExtendedCacheEntry() {
    }

    public ExtendedCacheEntry(DataInputStream input) 
        throws IOException {
        int length = input.readInt();
        
        for (int i=0; i<length; i++) {
            put(input.readUTF(),
                input.readUTF());
        }
    }

    public String getCacheID() {
        return(CacheInterface.EXTENDED_CACHE);
    }
    
    public void encode(Encoder encoder) {
        Set keySet = keySet();
        String[] keys = new String[keySet.size()];
        keySet.toArray(keys);

        encoder.encodeInt(keys.length);
        for (int i=0; i<keys.length; i++) {
            encoder.encodeString(keys[i]);
            encoder.encodeString(get(keys[i]).toString());
        }
    }

    public void decode(Decoder decoder) {
        int length = decoder.decodeInt();
        
        for (int i=0; i<length; i++) {
            put(decoder.decodeString(),
                decoder.decodeString());
        }
    }

    public void send(DataOutputStream output)
        throws IOException {
        Set keySet = keySet();
        String[] keys = new String[keySet.size()];
        keySet.toArray(keys);
        
        output.writeInt(keys.length);
        for (int i=0; i<keys.length; i++) {
            output.writeUTF(keys[i]);
            output.writeUTF(get(keys[i]).toString());
        }
    }
}
