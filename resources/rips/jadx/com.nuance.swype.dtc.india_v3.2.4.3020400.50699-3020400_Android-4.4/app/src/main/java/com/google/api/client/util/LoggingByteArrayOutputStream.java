package com.google.api.client.util;

import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public final class LoggingByteArrayOutputStream extends ByteArrayOutputStream {
    private int bytesWritten;
    private boolean closed;
    private final Logger logger;
    private final Level loggingLevel;
    private final int maximumBytesToLog;

    public LoggingByteArrayOutputStream(Logger logger, Level loggingLevel, int maximumBytesToLog) {
        this.logger = (Logger) com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(logger);
        this.loggingLevel = (Level) com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(loggingLevel);
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(maximumBytesToLog >= 0);
        this.maximumBytesToLog = maximumBytesToLog;
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public final synchronized void write(int b) {
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(!this.closed);
        this.bytesWritten++;
        if (this.count < this.maximumBytesToLog) {
            super.write(b);
        }
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream
    public final synchronized void write(byte[] b, int off, int len) {
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(!this.closed);
        this.bytesWritten += len;
        if (this.count < this.maximumBytesToLog) {
            int end = this.count + len;
            if (end > this.maximumBytesToLog) {
                len += this.maximumBytesToLog - end;
            }
            super.write(b, off, len);
        }
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public final synchronized void close() throws IOException {
        if (!this.closed) {
            if (this.bytesWritten != 0) {
                StringBuilder buf = new StringBuilder("Total: ");
                appendBytes(buf, this.bytesWritten);
                if (this.count != 0 && this.count < this.bytesWritten) {
                    buf.append(" (logging first ");
                    appendBytes(buf, this.count);
                    buf.append(")");
                }
                this.logger.config(buf.toString());
                if (this.count != 0) {
                    this.logger.log(this.loggingLevel, toString("UTF-8").replaceAll("[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F]", XMLResultsHandler.SEP_SPACE));
                }
            }
            this.closed = true;
        }
    }

    private static void appendBytes(StringBuilder buf, int x) {
        if (x == 1) {
            buf.append("1 byte");
        } else {
            buf.append(NumberFormat.getInstance().format(x)).append(" bytes");
        }
    }
}
