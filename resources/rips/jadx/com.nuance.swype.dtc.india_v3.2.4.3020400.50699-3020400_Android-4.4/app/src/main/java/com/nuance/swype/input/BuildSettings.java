package com.nuance.swype.input;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class BuildSettings {
    public static final String BUILDSETTINGS_FILE = "buildsettings.dat";
    public static final String SWYPE_SWIB = "SWIB";
    private Map<String, String> properties = new HashMap();
    private String swib;

    private void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public String getProperty(String name) {
        return this.properties.get(name);
    }

    public static BuildSettings createFromString(String rawText) {
        BufferedReader bufReader = new BufferedReader(new StringReader(rawText), HardKeyboardManager.META_CTRL_ON);
        try {
            BuildSettings settings = new BuildSettings();
            while (true) {
                String line = bufReader.readLine();
                if (line != null) {
                    int posOfEqual = line.indexOf(61);
                    if (posOfEqual >= 0) {
                        String name = line.substring(0, posOfEqual).trim();
                        String value = line.substring(posOfEqual + 1).trim();
                        settings.addProperty(name, value);
                    }
                } else {
                    try {
                        bufReader.close();
                        return settings;
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        } catch (IOException e2) {
            try {
                bufReader.close();
                return null;
            } catch (IOException e3) {
                return null;
            }
        } catch (Throwable th) {
            try {
                bufReader.close();
                throw th;
            } catch (IOException e4) {
                return null;
            }
        }
    }

    public static BuildSettings createFromDigest(byte[] digest) {
        byte[] message = EncryptUtils.legacyDecrypt(digest, License.PUBLIC_KEY_MODULUS, License.PUBLIC_KEY_EXPONENT);
        if (message == null) {
            return null;
        }
        try {
            return createFromString(new String(message, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("BuildSettings", "UnsupportedEncodingException", e);
            return null;
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String key : this.properties.keySet()) {
            buf.append(key).append('=').append(this.properties.get(key)).append('\n');
        }
        return buf.toString();
    }

    public String getSWIB() {
        if (this.swib == null) {
            this.swib = getProperty("SWIB");
        }
        return this.swib;
    }

    public static BuildSettings getBuildSettings(Context context) {
        ByteArrayOutputStream digest;
        AssetManager am = context.getResources().getAssets();
        InputStream is = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream digest2 = null;
        try {
            is = am.open(BUILDSETTINGS_FILE);
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
        } catch (Throwable th) {
            th = th;
        }
        if (is == null) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
            return null;
        }
        BufferedInputStream bis2 = new BufferedInputStream(is, 8192);
        try {
            digest = new ByteArrayOutputStream();
        } catch (FileNotFoundException e4) {
            bis = bis2;
        } catch (IOException e5) {
            bis = bis2;
        } catch (Throwable th2) {
            th = th2;
            bis = bis2;
        }
        try {
            for (int readByte = bis2.read(); readByte >= 0; readByte = bis2.read()) {
                digest.write(readByte);
            }
            BuildSettings createFromDigest = createFromDigest(digest.toByteArray());
            try {
                digest.close();
                bis2.close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException e6) {
            }
            return createFromDigest;
        } catch (FileNotFoundException e7) {
            digest2 = digest;
            bis = bis2;
            if (digest2 != null) {
                try {
                    digest2.close();
                } catch (IOException e8) {
                }
            }
            if (bis != null) {
                bis.close();
            }
            if (is != null) {
                is.close();
            }
            return null;
        } catch (IOException e9) {
            digest2 = digest;
            bis = bis2;
            if (digest2 != null) {
                try {
                    digest2.close();
                } catch (IOException e10) {
                }
            }
            if (bis != null) {
                bis.close();
            }
            if (is != null) {
                is.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            digest2 = digest;
            bis = bis2;
            if (digest2 != null) {
                try {
                    digest2.close();
                } catch (IOException e11) {
                    throw th;
                }
            }
            if (bis != null) {
                bis.close();
            }
            if (is != null) {
                is.close();
            }
            throw th;
        }
    }
}
