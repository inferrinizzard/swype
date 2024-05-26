package com.nuance.swype.input;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.provider.Settings;
import com.nuance.android.compat.BuildCompat;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.LogManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class License {
    public static final String DEVICE_STATUS_DISABLED = "DISABLED";
    public static final String DEVICE_STATUS_ENABLED = "ENABLED";
    public static final String LICENSE_FILE = "license.dat";
    private static final int LICENSE_INVALID = 0;
    private static final int LICENSE_UNKNOWN = 2;
    private static final int LICENSE_VALID = 1;
    private static final int MAX_DELAYED_PROPERTY_CHECK = 100;
    public static final String PUBLIC_KEY_EXPONENT = "10001";
    public static final String PUBLIC_KEY_MODULUS = "b62a557144ca38d42e69cb430a08d629066fc687713ec7c11c2825369a48b97756d7eb958d7b4eef9786a2467bccfa4dae228709b12efcfadaf8fea6dd149df77db523fd41758a0ca7de1d6765936960b6bcaaaac8029d9214983a19298318dfe4289f1dc8978ee49a158ec3fdf44fd44617444576d288c0c9289a7bcec6f6c5";
    public static final String SWYPE_SWIB = "SWIB";
    private boolean isLicenseCheckComplete;
    private boolean isLicenseValid;
    private Map<String, String> properties = new HashMap();
    private String swib;
    private static final LogManager.Log log = LogManager.getLog("License");
    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_SERIAL = "device_serial";
    public static final String DEVICE_ANDROID_ID = "device_android_id";
    private static final String[] DEVICE_PROPERTIES = {"android.os.Build.ID", "android.os.Build.PRODUCT", "android.os.Build.DEVICE", "android.os.Build.BOARD", "android.os.Build.BRAND", "android.os.Build.MODEL", DEVICE_ID, DEVICE_SERIAL, DEVICE_ANDROID_ID};
    public static final String DEVICE_STATUS = "DEVICE_STATUS";
    private static final String[] OTHER_KNOWN_PROPERTIES = {"LICENSE_EXPIRATION", "LICENSE_CHECK_THRESHOLD", "SWIB", DEVICE_STATUS};
    private static int delayed_property_check_count = 0;

    private void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public String getProperty(String name) {
        return this.properties.get(name);
    }

    public static License createFromString(String rawText) {
        BufferedReader bufReader = new BufferedReader(new StringReader(rawText), HardKeyboardManager.META_CTRL_ON);
        try {
            License license = new License();
            while (true) {
                String line = bufReader.readLine();
                if (line != null) {
                    int posOfEqual = line.indexOf(61);
                    if (posOfEqual >= 0) {
                        String name = line.substring(0, posOfEqual).trim();
                        String value = line.substring(posOfEqual + 1).trim();
                        license.addProperty(name, value);
                    }
                } else {
                    try {
                        bufReader.close();
                        return license;
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

    public static License createFromDigest(byte[] digest) {
        byte[] message = EncryptUtils.legacyDecrypt(digest, PUBLIC_KEY_MODULUS, PUBLIC_KEY_EXPONENT);
        if (message == null) {
            return null;
        }
        try {
            return createFromString(new String(message, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.e("UnsupportedEncodingException: UTF-8");
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

    public boolean isValid(Context context) {
        if (!this.isLicenseCheckComplete) {
            validate(context);
        }
        return !IMEApplication.from(context).getBuildInfo().isLicensingOn() || this.isLicenseValid;
    }

    public boolean isDisabled() {
        return this.properties.containsKey(DEVICE_STATUS) && this.properties.get(DEVICE_STATUS).equals(DEVICE_STATUS_DISABLED);
    }

    public String getSWIB() {
        return this.swib;
    }

    public void validate(Context context) {
        this.isLicenseCheckComplete = false;
        this.swib = this.properties.get("SWIB");
        log.i("SWIB:" + this.swib);
        for (String propertyName : this.properties.keySet()) {
            if (isValidProperty(context, propertyName, this.properties.get(propertyName)) == 0) {
                if (canPropertyCheckDelayed(propertyName) && delayed_property_check_count < 100) {
                    if (isInAirplaneMode(context)) {
                        this.isLicenseValid = true;
                        return;
                    } else {
                        delayed_property_check_count++;
                        this.isLicenseValid = true;
                        return;
                    }
                }
                setLicenseCheckCompleted(false);
                return;
            }
        }
        setLicenseCheckCompleted(true);
    }

    private int isValidProperty(Context context, String propertyName, String propertyValue) {
        if ("SWIB".equals(propertyName)) {
            BuildInfo info = IMEApplication.from(context).getBuildInfo();
            return ((info.isDevBuild() || !(propertyValue == null || BuildInfo.DEFAULT_SWIB.equals(propertyValue))) && info.getSwib().equals(propertyValue)) ? 1 : 0;
        }
        for (String prop : DEVICE_PROPERTIES) {
            if (propertyName.equals(prop)) {
                String deviceProperty = getDeviceProperty(context, propertyName);
                return (deviceProperty == null || !propertyValue.equals(deviceProperty)) ? 0 : 1;
            }
        }
        for (String prop2 : OTHER_KNOWN_PROPERTIES) {
            if (propertyName.equals(prop2)) {
                return 2;
            }
        }
        return 0;
    }

    private String getDeviceProperty(Context context, String name) {
        if (name.equals("android.os.Build.ID")) {
            return Build.ID;
        }
        if (name.equals("android.os.Build.PRODUCT")) {
            return Build.PRODUCT;
        }
        if (name.equals("android.os.Build.DEVICE")) {
            return Build.DEVICE;
        }
        if (name.equals("android.os.Build.BOARD")) {
            return Build.BOARD;
        }
        if (name.equals("android.os.Build.BRAND")) {
            return Build.BRAND;
        }
        if (name.equals("android.os.Build.MODEL")) {
            return Build.MODEL;
        }
        if (name.equals(DEVICE_ID)) {
            return null;
        }
        if (name.equals(DEVICE_SERIAL)) {
            return BuildCompat.getSerial();
        }
        if (name.equals(DEVICE_ANDROID_ID)) {
            return "android_id";
        }
        if (name.equals("SWIB")) {
            return IMEApplication.from(context).getBuildInfo().getSwib();
        }
        return null;
    }

    private boolean canPropertyCheckDelayed(String property) {
        return property.equals(DEVICE_ID);
    }

    public static String bytesToStr(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : data) {
            hexString.append(toHex(b & 255));
        }
        return hexString.toString();
    }

    public static byte[] strToBytes(String data) {
        byte[] result = new byte[data.length() / 2];
        for (int i = 0; i < data.length(); i += 2) {
            try {
                int high = Integer.parseInt(data.substring(i, i + 2), 16) & 255;
                result[i / 2] = (byte) high;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return result;
    }

    public static String toHex(int d) {
        String hex = Integer.toHexString(d);
        if (d < 16) {
            return "0" + hex;
        }
        return hex;
    }

    public static String md5(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                String b = Integer.toHexString(messageDigest[i] & 255);
                if ((messageDigest[i] & 255) < 16) {
                    b = "0" + b;
                }
                hexString.append(b);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.e("Error getting the md5!!", e);
            return "";
        }
    }

    private boolean isInAirplaneMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    private void setLicenseCheckCompleted(boolean isValid) {
        this.isLicenseValid = isValid;
        this.isLicenseCheckComplete = true;
    }

    public static License getLicense(Context context) {
        AssetManager am = context.getResources().getAssets();
        Closeable resource = null;
        try {
            try {
                InputStream is = am.open(LICENSE_FILE);
                if (is == null) {
                    return null;
                }
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream digest = new ByteArrayOutputStream();
                for (int readByte = bis.read(); readByte >= 0; readByte = bis.read()) {
                    digest.write(readByte);
                }
                if (digest.size() == 0) {
                    throw new IOException("digest is empty");
                }
                License currentLicense = createFromDigest(digest.toByteArray());
                if (currentLicense != null) {
                    currentLicense.validate(context);
                }
                log.d("getLicense() return currentLicense");
                bis.close();
                return currentLicense;
            } catch (Throwable th) {
                if (0 != 0) {
                    resource.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e) {
            log.d("License file not found");
            return null;
        } catch (IOException ex) {
            log.e("Unable to read license file: " + ex.getMessage());
            log.d("getLicense() return null");
            return null;
        }
    }
}
