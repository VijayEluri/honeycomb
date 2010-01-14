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



package com.sun.honeycomb.cm.node_mgr;

/**
 * A very simple Timeout class. Note that this class relies on the system
 * clock and any substantial changes to the clock between calls to arm() and
 * hasRemaining() or hasExpired() will cause unreliable results.
 *<br><br>
 For instance,
 *
 * <code>
 * Timeout t = new Timeout (60*1000);
 * t.arm();
 * doSomething();
 * if (t.hasExpired()) {
 *   throw new RuntimeException ("timeout expired");
 * }
 * </code>
 *
 * will throw if the system clock is advanced 2 hours while executing
 * doSomething().
 */
class Timeout {

    private long timeout;
    private int  base;
    private boolean enabled;

   protected Timeout(int base) {
       this.base = base;
       arm();
   }

    protected void arm() {
        timeout = System.currentTimeMillis() + base;
        enabled = true;
    }


    protected void armIfDisabled() {
        if (!enabled) {
            arm();
        }
    }

    protected void disable() {
        enabled = false;
    }

   protected boolean isDisabled() {
       return !enabled;
   }
   
    protected void arm(int base) {
        this.base = base;
        timeout = System.currentTimeMillis() + base;
    }

    protected long remainingTime() {
        long delta = timeout - System.currentTimeMillis();
        if (delta < 0) {
            return 0;
        }
        return delta;
    }

    protected boolean hasExpired() {
        return (enabled && (remainingTime() == 0));
    }
}
