/* 
 * $Header: /root/honeycomb/honeycomb/src/com.sun.honeycomb.common.cli.md5/MD5OutputStream.java,v 1.2 2004/08/13 09:55:00 jmw Exp $
 *
 * MD5OutputStream, a subclass of FilterOutputStream implementing MD5
 * functionality on a stream.
 *
 * written Santeri Paavolainen, Helsinki Finland 1996
 * (c) Santeri Paavolainen, Helsinki Finland 1996
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * 
 * See http://www.cs.hut.fi/~santtu/java/ for more information on this
 * and the MD5 class.  
 *
 * $Log: MD5OutputStream.java,v $
 * Revision 1.2  2004/08/13 09:55:00  jmw
 * Tracker ID's are required to commit.
 *
 * Tracker:1984
 * Status: Open[ ] Closed[ ] Pending[x]
 * Summary:Merged QA back into the mainline (Finally)
 * Unit Test:
 * Who Will Review:Mike is testing it too.
 *
 * Revision 1.1.2.1  2004/06/02 21:15:37  rjw
 * Tracker ID's are required to commit.
 *
 * Tracker:1352
 * Status: Open[ ] Closed[ ] Pending[x]
 * Summary:
 * The ability to change the admin password is in. Sadly, it only changes the
 * new properties and eerything else is still using the old properties, so
 * the change doesn't actually ever appear visibily to the end user. That's the
 * next check-in.
 * Unit Test:
 * Who Will Review:
 *
 * Revision 1.2  2002/03/16 01:46:39  broccol
 * Moved the MD5 classes into the md5 package to make 1.4 javac happy
 *
 * Revision 1.1  1999/08/05 22:07:04  broccol
 * Added support for the MD5 classes.
 *
 * Revision 1.2  1996/04/15 07:28:09  santtu
 * Added GPL statemets, and RSA derivate stametemetsnnts.
 *
 * Revision 1.1  1996/01/09 10:21:07  santtu
 * Initial revision
 *
 *
 */

package com.sun.honeycomb.common.md5;

import java.io.*;

/**
 * MD5OutputStream is a subclass of FilterOutputStream adding MD5
 * hashing of the read output.
 *
 * @version	$Revision: 1.2 $
 * @author	Santeri Paavolainen <santtu@cs.hut.fi>
 */

public class MD5OutputStream extends FilterOutputStream {
    /**
     * MD5 context
     */
    private MD5	md5;

    /**
     * Creates MD5OutputStream
     * @param out	The output stream
     */

    public MD5OutputStream (OutputStream out) {
	super(out);

	md5 = new MD5();
    }

    /**
     * Writes a byte. 
     *
     * @see java.lang.FilterOutputStream
     */

    public void write (int b) throws IOException {
	out.write(b);
	md5.Update((byte) b);
    }

    /**
     * Writes a sub array of bytes.
     *
     * @see java.lang.FilterOutputStream
     */

    public void write (byte b[], int off, int len) throws IOException {
	out.write(b, off, len);
	md5.Update(b, off, len);
    }

    /**
     * Returns array of bytes representing hash of the stream as finalized
     * for the current state.
     * @see MD5.Final()
     */

    public byte[] hash () {
	return md5.Final();
    }
}

