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



package com.sun.honeycomb.admingui.present.panels;

import com.sun.honeycomb.admingui.client.AdminApi;
import com.sun.honeycomb.admingui.client.Cell;
import com.sun.honeycomb.admingui.client.CellProps;
import com.sun.honeycomb.admingui.present.HelpFileMapping;
import com.sun.honeycomb.admingui.present.ObjectFactory;
import com.sun.nws.mozart.ui.ButtonPanel;
import com.sun.nws.mozart.ui.ContentPanel;
import com.sun.nws.mozart.ui.ExplorerItem;
import com.sun.nws.mozart.ui.exceptions.HostException;
import com.sun.nws.mozart.ui.exceptions.UIException;
import com.sun.nws.mozart.ui.swingextensions.SizeToFitLayout;
import com.sun.nws.mozart.ui.utility.AsyncQueue;
import com.sun.nws.mozart.ui.utility.GuiResources;
import com.sun.nws.mozart.ui.utility.SwingWorker;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author  Kevin A. Roll T201692
 */
public class PnlBrowseWebDAV extends javax.swing.JPanel 
    implements ContentPanel, HyperlinkListener {
    
    private ExplorerItem explorerItem;
    private String baseURL;
    
    private static final Logger LOGGER = 
        Logger.getLogger(PnlBrowseWebDAV.class.getName());
    
    /** Creates new form PnlBrowseWebDAV */
    public PnlBrowseWebDAV() {
        initComponents();
        
        try {
            AdminApi hostConn = ObjectFactory.getHostConnection();
            Cell[] cells = hostConn.getCells(); 
            CellProps cellProps = hostConn.getCellProps(cells[0].getID());
            baseURL = "http://" + cellProps.dataIP + ":8080";
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, 
                "Exception configuring WebDAV browser", e);
        }
        
        textPane.addHyperlinkListener(this);
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane2 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        textPane.setEditable(false);
        jScrollPane2.setViewportView(textPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void setExplorerItem(ExplorerItem explorerItem) {
        this.explorerItem = explorerItem;
    }

    public ExplorerItem getExplorerItem() {
        return explorerItem;
    }

    public String getTitle() {
        return GuiResources.getGuiString(
            "explorer.silo.config.metadata.view.browse");
    }
    public String getPageKey() {
        return HelpFileMapping.BROWSEFILESYSTEMVIEW;
    }
    public void loadValues() throws UIException, HostException {
        readPage(baseURL+"/webdav/");
    }

    public void saveValues() throws UIException, HostException {
    }

    public ButtonPanel getButtonPanel() {
        return null;
    }

    public JPanel getJPanel() {
        return this;
    }

    public int getAnchor() {
        return SizeToFitLayout.ANCHOR_CENTER;
    }

    public int getFillType() {
        return SizeToFitLayout.FILL_BOTH;
    }

    private void readPage(final String url) {
        
        AsyncQueue.getInstance().execute(this, 
            new SwingWorker() {
                public Object construct() {
                    try {
                        textPane.setDocument(new HTMLDocument());
                        textPane.setPage(new URL(url));
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING,
                            "Exception fetching web page", ex);
                    }
                    return null;
                }
            }
        );
    }
    
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            readPage(baseURL+e.getDescription());
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
    
}
