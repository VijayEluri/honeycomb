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



public class RWLock {

    private boolean writeLockGranted;
    private int nbReadLockGranted;

    private int nbPendingWriteRequests;
    
    public RWLock() {
        writeLockGranted = false;
        nbReadLockGranted = 0;
        nbPendingWriteRequests = 0;
    }
    
    public synchronized void getWriteLock() {
        if ( (nbReadLockGranted == 0)
             && (!writeLockGranted) ) {
            writeLockGranted = true;
            return;
        }

        ++nbPendingWriteRequests;

        while ( (nbReadLockGranted > 0)
                || (writeLockGranted) ) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        --nbPendingWriteRequests;
        writeLockGranted = true;
    }   
    
    public synchronized void getReadLock() {
        if ( (!writeLockGranted)
             && (nbPendingWriteRequests == 0) ) {
            ++nbReadLockGranted;
            return;
        }
        
        while ( (writeLockGranted)
                || (nbPendingWriteRequests > 0) ) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        ++nbReadLockGranted;
    }
    
    public synchronized void releaseLock() {
        if (writeLockGranted) {
            writeLockGranted = false;
        } else {
            --nbReadLockGranted;
        }
        
        notifyAll();
    }
}
