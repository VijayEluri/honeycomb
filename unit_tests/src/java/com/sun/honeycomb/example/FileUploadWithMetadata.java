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



package com.sun.honeycomb.example;

import com.sun.honeycomb.client.NameValueObjectArchive;
import com.sun.honeycomb.client.ObjectIdentifier;
import com.sun.honeycomb.client.caches.NameValueRecord;
import com.sun.honeycomb.client.caches.SystemRecord;
import com.sun.honeycomb.common.ArchiveException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class FileUploadWithMetadata {

    private static NameValueObjectArchive archive;

    public static void main(String[] args)
        throws ArchiveException, IOException {

        String host = args[0];
        archive = new NameValueObjectArchive(host);

        String path = args[1];
        ObjectIdentifier oid = uploadFile(path);

        NameValueRecord record = archive.retrieveMetadata(oid);
        System.out.println("got record: " + record);

        downloadFile(oid, path + "-" + oid.toString());
    }

    public static ObjectIdentifier uploadFile(String path)
        throws ArchiveException, IOException {

        File file = new File(path);
        String fileName = file.getName();
        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(fileName);

        NameValueRecord metadata = new NameValueRecord();
        metadata.put("file-name", fileName);
        metadata.put("mime-type", mimeType);

        FileChannel channel = new FileInputStream(file).getChannel();
        SystemRecord record = archive.storeObject(channel, metadata);

        return record.getIdentifier();
    }

    public static void downloadFile(ObjectIdentifier oid, String path)
        throws ArchiveException, IOException {

        FileChannel channel = new FileOutputStream(path).getChannel();
        archive.retrieveObject(oid, channel);
    }
}
