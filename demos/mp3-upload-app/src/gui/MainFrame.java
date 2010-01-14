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



package gui;

/**
 *
 * @author  sarnoud
 */
public abstract class MainFrame extends javax.swing.JFrame {
    
    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        UploadProgress = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        artistField = new javax.swing.JTextField();
        albumField = new javax.swing.JTextField();
        titleField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        statusField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        speedField = new javax.swing.JTextField();
        MainMenu = new javax.swing.JMenuBar();
        MenuTools = new javax.swing.JMenu();
        MenuConfigure = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        MenuQuit = new javax.swing.JMenuItem();
        MenuUpload = new javax.swing.JMenu();
        MenuSingleFile = new javax.swing.JMenuItem();
        MenuDirectory = new javax.swing.JMenuItem();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Honeycomb mp3 upload application");
        setFont(new java.awt.Font("Bitstream Charter", 0, 12));
        setLocationRelativeTo(albumField);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jLabel1.setText("Upload progress : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        UploadProgress.setPreferredSize(new java.awt.Dimension(200, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        getContentPane().add(UploadProgress, gridBagConstraints);

        jLabel2.setText("Artist : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabel3.setText("Album : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel4.setText("Title : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(jLabel4, gridBagConstraints);

        artistField.setColumns(40);
        artistField.setEditable(false);
        artistField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                artistFieldActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        getContentPane().add(artistField, gridBagConstraints);

        albumField.setColumns(40);
        albumField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        getContentPane().add(albumField, gridBagConstraints);

        titleField.setColumns(40);
        titleField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(titleField, gridBagConstraints);

        jLabel5.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(jLabel5, gridBagConstraints);

        statusField.setColumns(40);
        statusField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(statusField, gridBagConstraints);

        jLabel6.setText("Speed (kB/s) :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(jLabel6, gridBagConstraints);

        speedField.setColumns(15);
        speedField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        getContentPane().add(speedField, gridBagConstraints);

        MenuTools.setText("Tools");
        MenuConfigure.setText("Configure");
        MenuConfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuConfigureActionPerformed(evt);
            }
        });

        MenuTools.add(MenuConfigure);

        MenuTools.add(jSeparator1);

        MenuQuit.setText("Quit");
        MenuQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuQuitActionPerformed(evt);
            }
        });

        MenuTools.add(MenuQuit);

        MainMenu.add(MenuTools);

        MenuUpload.setText("Upload");
        MenuSingleFile.setText("Individual files");
        MenuSingleFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuSingleFileActionPerformed(evt);
            }
        });

        MenuUpload.add(MenuSingleFile);

        MenuDirectory.setText("Whole directory");
        MenuDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuDirectoryActionPerformed(evt);
            }
        });

        MenuUpload.add(MenuDirectory);

        MainMenu.add(MenuUpload);

        setJMenuBar(MainMenu);

        pack();
    }//GEN-END:initComponents

    private void MenuConfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuConfigureActionPerformed
        // Add your handling code here:
        configure();
    }//GEN-LAST:event_MenuConfigureActionPerformed

    private void MenuQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuQuitActionPerformed
        // Add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_MenuQuitActionPerformed

    private void MenuDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuDirectoryActionPerformed
        // Add your handling code here:
        uploadDirectory();
    }//GEN-LAST:event_MenuDirectoryActionPerformed

    private void MenuSingleFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuSingleFileActionPerformed
        // Add your handling code here:
        uploadFiles();
    }//GEN-LAST:event_MenuSingleFileActionPerformed

    private void artistFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_artistFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_artistFieldActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    protected abstract void configure();    
    
    protected abstract void uploadFiles();
    
    protected abstract void uploadDirectory();
    
    public void setMetadata(java.lang.String artist, java.lang.String album, java.lang.String title) {
        artistField.setText(artist);
        albumField.setText(album);
        titleField.setText(title);
    }
    
    public void setProgress(int value) {
        UploadProgress.setValue(value);
    }
    
    public void setStatus(java.lang.String status) {
        statusField.setText(status);
    }
    
    public void setSpeed(double value) {
        java.text.DecimalFormat format = new java.text.DecimalFormat();
        format.setMaximumFractionDigits(2);
        speedField.setText(format.format(value));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar MainMenu;
    private javax.swing.JMenuItem MenuConfigure;
    private javax.swing.JMenuItem MenuDirectory;
    private javax.swing.JMenuItem MenuQuit;
    private javax.swing.JMenuItem MenuSingleFile;
    private javax.swing.JMenu MenuTools;
    private javax.swing.JMenu MenuUpload;
    private javax.swing.JProgressBar UploadProgress;
    private javax.swing.JTextField albumField;
    private javax.swing.JTextField artistField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField speedField;
    private javax.swing.JTextField statusField;
    private javax.swing.JTextField titleField;
    // End of variables declaration//GEN-END:variables
    
}
