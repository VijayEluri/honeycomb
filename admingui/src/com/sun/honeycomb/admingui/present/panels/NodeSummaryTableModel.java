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



/*
 * ContentsTableModel.java
 *
 * Created on December 22, 2005, 2:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.sun.honeycomb.admingui.present.panels;

import com.sun.nws.mozart.ui.BaseTableModel;
import com.sun.nws.mozart.ui.BaseTableModel.TableColumn;
import com.sun.nws.mozart.ui.utility.GuiResources;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Random;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dp1272245
 */
public class NodeSummaryTableModel extends BaseTableModel {

    // Would like to use enum here, but can't because you can't have "base" enum
    // and refer in the base class to enums in the derived class.
    
    public static final int ID = 0;
    public static final int STATUS = 1;
    public static final int PERCENT_USED = 2; 
    public static final int BIOS_SMDC_VER = 3;
    public static final int FRU_ID = 4;
    
    /** 
     * Creates a new instance of ContentsTableModel
     */
    public NodeSummaryTableModel() {
       super(new TableColumn[] { 
                new TableColumn(ID, new Integer(70), 
                    new Integer(80), new Integer(70), false, 
                    GuiResources.getGuiString("summaryTable.id")),
//                new TableColumn(STATUS,  new Integer(105), false,
                new TableColumn(STATUS,  new Integer(90), 
                    new Integer(100), new Integer(90), false, 
                    GuiResources.getGuiString("summaryTable.status")),
                new TableColumn(PERCENT_USED,  new Integer(120), false, 
                    GuiResources.getGuiString("summaryTable.percentUsed")),
                new TableColumn(BIOS_SMDC_VER,  new Integer(150), false, 
                    GuiResources.getGuiString(
                                    "summaryTable.biosAndsmdcVersion")),
                new TableColumn(FRU_ID, new Integer(300), false, 
                    GuiResources.getGuiString("summaryTable.fruId"))});
    } 
    
    /**
     * Populates the table with the summary of entries contained in the
     * directory represented by the passed in node from the tree.  If the
     * node is a file, the table will be empty.
     */
    public void populate(Object modelData) {

    }
}
