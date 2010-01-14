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



package com.sun.honeycomb.hctest.task;

import com.sun.honeycomb.test.*;
import com.sun.honeycomb.test.util.*;

/**
 *  Thread for handling a retrieve on a remote client.
 */
public class RetrieveMDTask extends Task {

    public int client;
    public String oid;
    public String dataVIP;

    public RetrieveMDTask(int client, String oid) {
        super();
        this.client = client;
        this.oid = oid;
        this.dataVIP = null;
    }

    public RetrieveMDTask(int client, String oid, boolean noisy, String dataVIP) {
        super();
        this.client = client;
        this.oid = oid;
        this.noisy = noisy;
        this.dataVIP = dataVIP;
    }

    public void run() {
        try {
            result = testBed.getMetadata(client, oid, dataVIP);
            if (noisy  &&  result.thrown != null  &&  result.thrown.size() > 0) {
                for (int i=0; i<result.thrown.size(); i++) {
                    Throwable t = (Throwable) result.thrown.get(i);
                    Log.INFO("RetrieveTask CmdResult.thrown[" + i + "]:\n" + 
                                                        Log.stackTrace(t));
                }
            }
            if (result == null)
                thrown.add(new HoneycombTestException("result is null [?]"));
        } catch (Exception e) {
            if (noisy)
                Log.INFO("retrieve:\n" + Log.stackTrace(e));
            thrown.add(e);
        }
        done = true;
    }
}
