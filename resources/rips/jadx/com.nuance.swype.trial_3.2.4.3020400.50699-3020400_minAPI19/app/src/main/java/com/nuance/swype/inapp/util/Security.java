package com.nuance.swype.inapp.util;

import android.text.TextUtils;
import android.util.Log;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: classes.dex */
public final class Security {
    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
        if (signedData == null) {
            Log.e("IABUtil/Security", "data is null");
            return false;
        }
        if (!TextUtils.isEmpty(signature) && base64PublicKey != null && !base64PublicKey.equals(XMLResultsHandler.SEP_SPACE)) {
            try {
                PublicKey key = generatePublicKey(base64PublicKey);
                if (!verify(key, signedData, signature)) {
                    Log.w("IABUtil/Security", "signature does not match data.");
                    return false;
                }
            } catch (IllegalArgumentException e) {
                Log.w("IABUtil/Security", "generate public key failed, error=" + e.getMessage());
                return false;
            } catch (RuntimeException e2) {
                Log.w("IABUtil/Security", "generate public key failed, error=" + e2.getMessage());
                return false;
            }
        }
        return true;
    }

    private static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (Base64DecoderException e) {
            Log.e("IABUtil/Security", "Base64 decoding failed.");
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        } catch (InvalidKeySpecException e3) {
            Log.e("IABUtil/Security", "Invalid key specification.");
            throw new IllegalArgumentException(e3);
        }
    }

    private static boolean verify(PublicKey publicKey, String signedData, String signature) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes("UTF-8"));
            if (!sig.verify(Base64.decode(signature))) {
                Log.e("IABUtil/Security", "Signature verification failed.");
                return false;
            }
            return true;
        } catch (Base64DecoderException e) {
            Log.e("IABUtil/Security", "Base64 decoding failed.");
            return false;
        } catch (UnsupportedEncodingException e2) {
            Log.e("IABUtil/Security", "UnsupportedEncodingException");
            return false;
        } catch (InvalidKeyException e3) {
            Log.e("IABUtil/Security", "Invalid key specification.");
            return false;
        } catch (NoSuchAlgorithmException e4) {
            Log.e("IABUtil/Security", "NoSuchAlgorithmException.");
            return false;
        } catch (SignatureException e5) {
            Log.e("IABUtil/Security", "Signature exception.");
            return false;
        }
    }
}
