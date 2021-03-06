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



package com.sun.honeycomb.config;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.io.FileInputStream;
import com.sun.honeycomb.cm.NodeMgr;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class ClusterProperties
    extends Properties {

    private static ClusterProperties singleton = null;

    public static final String CONFIG_FILE = "/config/emulator.config";
    private static final Logger LOG = Logger.getLogger(ClusterProperties.class.getName());

    public static synchronized ClusterProperties getInstance() {
        if (singleton == null) {
            singleton = new ClusterProperties();
            try {
                singleton.init();
            } catch (IOException e) {
                LOG.log(Level.SEVERE,
                        "Failed to get the honeycomb simulator properties ["+
                        e.getMessage()+"]",
                        e);
                singleton = null;
            }
        }
        return(singleton);
    }

    private ClusterProperties() {
        super();
    }

    private void init()
        throws IOException {
        FileInputStream input = null;

        try {
            input = new FileInputStream(NodeMgr.getInstance().getEmulatorRoot()+CONFIG_FILE);
            load(input);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public boolean isDefined(String prop) {
        return(getProperty(prop) != null);
    }

    public boolean isDefaulted(String prop) {
        return(true);
    }

    public void setPendingProperty(String name, String value) {
        setProperty(name, value);
    }

    public void commitPendingChanges() {

    }


    /**
     * Returns the given property name, if found, as an int base type. If
     * the property name is not found Integer.MIN_VALUE is returned
     */
    public int getPropertyAsInt(String name) throws NumberFormatException {
        return getPropertyAsInt(name, Integer.MIN_VALUE);
    }

    /**
     * As {@link getPropertyAsInt} except if the property is not found, the
     * specified defaultValue will be returned
     */
    public int getPropertyAsInt (String name, int defaultValue)
        throws NumberFormatException {
        int num = defaultValue;
    String prop = getProperty (name);
    if (prop == null)
        return num;
    else
        return Integer.parseInt(prop);
    }


    /**
     * As {@link getPropertyAsLong} except if the property is not found, the
     * specified defaultValue will be returned
     */
    public long getPropertyAsLong (String name, long defaultValue)
        throws NumberFormatException {
        long num = defaultValue;
    String prop = getProperty (name);
    if (prop == null)
        return num;
    else
        return Long.parseLong(prop);
    }

    /**
     * Returns the given properties value as a boolean. If the value is the
     * string "true" (ignoring case) then Boolean.TRUE will be returned, else
     * Boolean.FALSE will be returned
     */
    public boolean getPropertyAsBoolean (String name) {
        return getPropertyAsBoolean (name, false);
    }


    /**
     * As {@link getPropertyAsBoolean} except if the property specificed by
     * the name parameter is not defined, the defaultValue parameter is
     * returned
     */
    public boolean getPropertyAsBoolean (String name, boolean defaultValue) {
        boolean bool = defaultValue;

        String value = getProperty (name);

        if (name == null) {
            bool = defaultValue;
        }
        else {
            bool = Boolean.valueOf (value).booleanValue();
        }

        return bool;
    }

    // No property change callbacks in the emulator, so this is a no-op
    public void addPropertyListener (PropertyChangeListener l) {}
}
