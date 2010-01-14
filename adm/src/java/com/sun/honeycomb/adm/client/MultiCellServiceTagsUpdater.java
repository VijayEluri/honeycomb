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


package com.sun.honeycomb.adm.client;

import com.sun.honeycomb.admin.mgmt.client.HCCell;
import com.sun.honeycomb.admin.mgmt.client.HCServiceTagCellData;
import com.sun.honeycomb.admin.mgmt.client.HCServiceTags;
import java.math.BigInteger;

import com.sun.honeycomb.mgmt.common.MgmtException;

/**
 * This class is used to update the service tag data for a given cell
 * on each cell in the system.  This is necessary to keep the silo_info.xml
 * cell data consistent across all cells.   
 */
public class MultiCellServiceTagsUpdater implements MultiCellOp 
{
    HCCell updateAdapter;
    MgmtException mgmtException;
    int result = 0;
    Object cookie;
    
    public MultiCellServiceTagsUpdater(HCCell updateAdapter) {
        this.updateAdapter = updateAdapter;
    }

    /**
     * Make a call to HCCell.updateAllServiceTagData() to update
     * the service tag data 
     */
    public void run() {
        StatusCallback callback = new StatusCallback();
        ServiceTagsCookie cookie = getCookie();
        HCServiceTags data = cookie.getData();
        BigInteger updateRegistry = cookie.updateRegistry() == true ? 
            BigInteger.ONE : BigInteger.ZERO;
        try {
            BigInteger retCode = updateAdapter.updateAllServiceTagData(
                    callback, data, updateRegistry);
            result = retCode.intValue();
        } catch (MgmtException ex) {
            mgmtException = ex;
        }
    }
    
    public int getResult() {
        return result;
    }

    public int waitForPartialResult() {
        return 0;
    }

    public MgmtException getMgmtException() {
        return mgmtException;
    }

    public void setCookie(Object cookie) {
        if (cookie instanceof ServiceTagsCookie)
            this.cookie = cookie;
        else
            throw new IllegalArgumentException(
                    "Cookie must be of type ServiceTagsCookie");
    }
    
    public ServiceTagsCookie getCookie() {
        return (ServiceTagsCookie)cookie;
    }
}
