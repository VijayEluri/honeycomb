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



package com.sun.honeycomb.hctest.hadb.schemas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.sun.honeycomb.hctest.hadb.Utils;

public class OfotoSchema implements MetadataSchema {

	public OfotoSchema () {
		m_types.add(Utils.STRING_TYPE);
		m_types.add(Utils.DOUBLE_TYPE);
		m_types.add(Utils.LONG_TYPE);
		for (int i = 0; i < m_string_fields.length; i++) {
			m_fieldToType.put(m_string_fields[i],Utils.STRING_TYPE);
		}
		for (int i = 0; i < m_double_fields.length; i++) {
			m_fieldToType.put(m_double_fields[i],Utils.DOUBLE_TYPE);
		}
		for (int i = 0; i < m_long_fields.length; i++) {
			m_fieldToType.put(m_long_fields[i],Utils.LONG_TYPE);
		}
	}
	
	public String getName() {
		return m_name;
	}

	public Collection getFields() {
		return m_fieldToType.keySet();
	}

	public Collection getTypes() {
		return m_types;
	}

	public String getFieldType(String field) {
		if (m_fieldToType.containsKey(field)) {
			return (String)m_fieldToType.get(field);
		} else {
			return null;
		}
	}
	
	public String [] getLegalValues (String field) {
		String [] values = m_emptyArray;
		if (field.startsWith("dir")) {
			values = new String[m_dirLegalValues.length];
			for (int i = 0; i < m_dirLegalValues.length; i++) {
				values[i]=field + m_dirLegalValues[i];
			}
		}
		
		if (field.startsWith("fname")) {
			values = new String[m_fileLegalValues.length];
			for (int i = 0; i < m_fileLegalValues.length; i++) {
				values[i]=field + m_fileLegalValues[i];
			}
		}
		
		return values;
	}
	
	private static final String [] m_string_fields = {"dir1","dir2",
		"dir3", "dir4", "dir5", "dir6", "fname"			
	};
	
	private static final String [] m_dirLegalValues = { "aDir", "bDir", "cDir", "dDir","eDir",
		   											 "fDir", "gDir"};
	
	private static final String [] m_fileLegalValues = {"aFile", "Bfile", "cFile", "dfile", "5File",
        												 "6File", "7file"};
	
	private static final String [] m_emptyArray = {};
	
	private static final String m_name = "Ofoto MetadataSchema";
	private static final ArrayList m_types = new ArrayList();
	
	private static final String [] m_double_fields = {};
	
	private static final String [] m_long_fields = {};
	private static final HashMap m_fieldToType = new HashMap();
}
