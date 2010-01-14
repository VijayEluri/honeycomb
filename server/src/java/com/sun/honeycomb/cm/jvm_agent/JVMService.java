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



package com.sun.honeycomb.cm.jvm_agent;

import com.sun.honeycomb.cm.ManagedService;
import com.sun.honeycomb.cm.ManagedServiceException;
import com.sun.honeycomb.alert.AlertType;
import com.sun.honeycomb.alert.AlertComponent;
import com.sun.honeycomb.alert.AlertException;

/**
 * Public definition of the JVM managed service.
 * It is an interface that extends ManagedService and
 * exports its own public API and proxy object.
 */
public interface JVMService
    extends ManagedService.RemoteInvocation, ManagedService {
    
    /**
     * set the log level for this JVM; optionally for only the class
     * hierarchy "className"
     */
    void setLogLevel(String level, String className)
        throws ManagedServiceException;
    
    /*
     * JVM proxy object
     */
    public class Proxy extends ManagedService.ProxyObject {
        int  threadCount;
        long freeMemory;
        long maxMemory;
        long totalMemory;

        public int threadCount() {
            return threadCount;
        }
        public long freeMemory() {
            return freeMemory;
        }
        public long maxMemory() {
            return maxMemory;
        }
        public long totalMemory() {
            return totalMemory;
        }

        /*
         * Alert API
         */
        public int getNbChildren() {
            return 4;
        }

        public AlertProperty getPropertyChild(int index) 
            throws AlertException {
            AlertProperty prop = null;
            if (index == 0) {
                prop = new AlertProperty("threadCount", AlertType.INT);
            } else if (index == 1) {
                prop = new AlertProperty("freeMemory", AlertType.LONG);
            } else if (index == 2) {
                prop = new AlertProperty("maxMemory", AlertType.LONG);
            } else if (index == 3) {
                prop = new AlertProperty("totalMemory", AlertType.LONG);
            } else {
                throw new AlertException("index " + index + " does not exist");
            }
            return prop;
        }
        public int getPropertyValueInt(String property) 
            throws AlertException {
            if (property.equals("threadCount")) {
                return (threadCount);
            } else {
                throw new AlertException("property " + property +
                                         " does not exist");
            }
        }
        public long getPropertyValueLong(String property) 
            throws AlertException {
            if (property.equals("freeMemory")) {
                return freeMemory;
            } else if (property.equals("maxMemory")) {
                return maxMemory;
            } else if (property.equals("totalMemory")) {
                return totalMemory;
            } else {
                throw new AlertException("property " + property +
                                         " does not exist");
            }
        }
    }
}
