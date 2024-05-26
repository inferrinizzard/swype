package com.nuance.connect.util;

import android.util.Base64;
import com.nuance.connect.util.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

/* loaded from: classes.dex */
public class Data {
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.OEM, Data.class.getSimpleName());

    public static String serializeObject(Object obj) {
        String str = null;
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            str = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
            byteArrayOutputStream.close();
            return str;
        } catch (IOException e) {
            log.e("serializeObject error: " + e.getMessage());
            return str;
        }
    }

    public static Object unserializeObject(String str) {
        ObjectInputStream objectInputStream;
        Object obj = null;
        if (str != null) {
            ObjectInputStream objectInputStream2 = null;
            try {
                try {
                    byte[] decode = Base64.decode(str, 0);
                    if (decode != null && decode.length != 0) {
                        objectInputStream = new ObjectInputStream(new ByteArrayInputStream(decode));
                        try {
                            obj = objectInputStream.readObject();
                            if (objectInputStream != null) {
                                objectInputStream.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (objectInputStream != null) {
                                objectInputStream.close();
                            }
                            throw th;
                        }
                    } else if (0 != 0) {
                        objectInputStream2.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    objectInputStream = null;
                }
            } catch (OptionalDataException e) {
                log.e("unserializeObject error (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            } catch (StreamCorruptedException e2) {
                log.e("unserializeObject error (" + e2.getClass().getSimpleName() + "): " + e2.getMessage());
            } catch (IOException e3) {
                log.e("unserializeObject error (" + e3.getClass().getSimpleName() + "): " + e3.getMessage());
            } catch (ClassNotFoundException e4) {
                log.e("unserializeObject error (" + e4.getClass().getSimpleName() + "): " + e4.getMessage());
            } catch (IllegalArgumentException e5) {
                log.e("unserializeObject error (" + e5.getClass().getSimpleName() + "): " + e5.getMessage());
            } catch (NullPointerException e6) {
            } catch (Exception e7) {
                log.e("unserializeObject error (" + e7.getClass().getSimpleName() + "): " + e7.getMessage());
            }
        }
        return obj;
    }
}
