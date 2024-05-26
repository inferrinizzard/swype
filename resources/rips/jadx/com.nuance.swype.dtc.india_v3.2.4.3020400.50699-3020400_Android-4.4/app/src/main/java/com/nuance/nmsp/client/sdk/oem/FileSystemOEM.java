package com.nuance.nmsp.client.sdk.oem;

import android.os.Environment;
import com.nuance.nmsp.client.sdk.common.oem.api.FileSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class FileSystemOEM implements FileSystem {
    private FileSystem.FileMode a;
    private File b = null;
    private FileInputStream c = null;
    private FileOutputStream d = null;

    static {
        LogFactory.getLog(NetworkSystemOEM.class);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public void close() {
        if (this.c != null) {
            try {
                this.c.close();
            } catch (IOException e) {
            }
        }
        if (this.d != null) {
            try {
                this.d.close();
            } catch (IOException e2) {
            }
        }
        this.c = null;
        this.d = null;
        this.b = null;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public boolean exist() {
        return this.b != null && this.b.exists();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public boolean open(String str, FileSystem.FileMode fileMode) {
        this.a = fileMode;
        this.b = new File(Environment.getDataDirectory(), str);
        boolean z = fileMode == FileSystem.FileMode.READ ? !this.b.exists() : false;
        if (z) {
            this.b = null;
        }
        return !z;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public int read(byte[] bArr, int i, int i2) {
        if (this.b == null) {
            return -1;
        }
        if (this.c == null) {
            try {
                this.c = new FileInputStream(this.b);
            } catch (FileNotFoundException e) {
                this.b = null;
                return -1;
            }
        }
        try {
            return this.c.read(bArr, i, i2);
        } catch (Exception e2) {
            try {
                this.c.close();
            } catch (IOException e3) {
            }
            this.c = null;
            this.b = null;
            return -1;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public boolean remove() {
        if (this.b == null) {
            return false;
        }
        if (this.c != null) {
            try {
                this.c.close();
            } catch (IOException e) {
            }
        }
        if (this.d != null) {
            try {
                this.d.close();
            } catch (IOException e2) {
            }
        }
        this.c = null;
        this.d = null;
        File file = this.b;
        this.b = null;
        return file.delete();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public boolean rename(String str) {
        if (this.b == null) {
            return false;
        }
        return this.b.renameTo(new File(str));
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public long size() {
        if (this.b == null) {
            return -1L;
        }
        return this.b.length();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public long skip(long j) {
        if (this.b != null && this.c != null) {
            try {
                return this.c.skip(j);
            } catch (IOException e) {
            }
        }
        return 0L;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.FileSystem
    public int write(byte[] bArr, int i, int i2) {
        if (this.b == null) {
            return -1;
        }
        if (this.d == null) {
            try {
                this.d = new FileOutputStream(this.b, this.a == FileSystem.FileMode.APPEND);
            } catch (FileNotFoundException e) {
                this.b = null;
                return -1;
            }
        }
        try {
            this.d.write(bArr, i, i2);
            return i2;
        } catch (IOException e2) {
            try {
                this.d.close();
            } catch (IOException e3) {
            }
            this.d = null;
            this.b = null;
            return -1;
        }
    }
}
