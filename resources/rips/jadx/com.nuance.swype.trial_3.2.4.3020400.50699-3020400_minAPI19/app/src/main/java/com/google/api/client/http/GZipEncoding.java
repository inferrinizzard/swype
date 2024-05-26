package com.google.api.client.http;

import com.google.api.client.util.StreamingContent;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/* loaded from: classes.dex */
public class GZipEncoding implements HttpEncoding {
    @Override // com.google.api.client.http.HttpEncoding
    public String getName() {
        return "gzip";
    }

    @Override // com.google.api.client.http.HttpEncoding
    public void encode(StreamingContent content, OutputStream out) throws IOException {
        OutputStream out2 = new BufferedOutputStream(out) { // from class: com.google.api.client.http.GZipEncoding.1
            @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                try {
                    flush();
                } catch (IOException e) {
                }
            }
        };
        GZIPOutputStream zipper = new GZIPOutputStream(out2);
        content.writeTo(zipper);
        zipper.close();
    }
}
