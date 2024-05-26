package com.google.android.gms.common;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzs;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/* loaded from: classes.dex */
final class zzd {
    private static Context rh;

    /* loaded from: classes.dex */
    static class zzb extends zza {
        private final byte[] rl;

        /* JADX INFO: Access modifiers changed from: package-private */
        public zzb(byte[] bArr) {
            super(Arrays.copyOfRange(bArr, 0, 25));
            this.rl = bArr;
        }

        @Override // com.google.android.gms.common.zzd.zza
        final byte[] getBytes() {
            return this.rl;
        }
    }

    /* loaded from: classes.dex */
    static abstract class zzc extends zza {
        private static final WeakReference<byte[]> rn = new WeakReference<>(null);
        private WeakReference<byte[]> rm;

        zzc(byte[] bArr) {
            super(bArr);
            this.rm = rn;
        }

        @Override // com.google.android.gms.common.zzd.zza
        final byte[] getBytes() {
            byte[] bArr;
            synchronized (this) {
                bArr = this.rm.get();
                if (bArr == null) {
                    bArr = zzanm();
                    this.rm = new WeakReference<>(bArr);
                }
            }
            return bArr;
        }

        protected abstract byte[] zzanm();
    }

    /* renamed from: com.google.android.gms.common.zzd$zzd, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    static final class C0053zzd {
        static final zza[] ro = {new zzc(zza.zzgv("0\u0082\u0004C0\u0082\u0003+ \u0003\u0002\u0001\u0002\u0002\t\u0000Âà\u0087FdJ0\u008d0")) { // from class: com.google.android.gms.common.zzd.zzd.1
            @Override // com.google.android.gms.common.zzd.zzc
            protected final byte[] zzanm() {
                return zza.zzgv("0\u0082\u0004C0\u0082\u0003+ \u0003\u0002\u0001\u0002\u0002\t\u0000Âà\u0087FdJ0\u008d0\r\u0006\t*\u0086H\u0086÷\r\u0001\u0001\u0004\u0005\u00000t1\u000b0\t\u0006\u0003U\u0004\u0006\u0013\u0002US1\u00130\u0011\u0006\u0003U\u0004\b\u0013\nCalifornia1\u00160\u0014\u0006\u0003U\u0004\u0007\u0013\rMountain View1\u00140\u0012\u0006\u0003U\u0004\n\u0013\u000bGoogle Inc.1\u00100\u000e\u0006\u0003U\u0004\u000b\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u0003\u0013\u0007Android0\u001e\u0017\r080821231334Z\u0017\r360107231334Z0t1\u000b0\t\u0006\u0003U\u0004\u0006\u0013\u0002US1\u00130\u0011\u0006\u0003U\u0004\b\u0013\nCalifornia1\u00160\u0014\u0006\u0003U\u0004\u0007\u0013\rMountain View1\u00140\u0012\u0006\u0003U\u0004\n\u0013\u000bGoogle Inc.1\u00100\u000e\u0006\u0003U\u0004\u000b\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u0003\u0013\u0007Android0\u0082\u0001 0\r\u0006\t*\u0086H\u0086÷\r\u0001\u0001\u0001\u0005\u0000\u0003\u0082\u0001\r\u00000\u0082\u0001\b\u0002\u0082\u0001\u0001\u0000«V.\u0000Ø;¢\b®\n\u0096o\u0012N)Ú\u0011ò«VÐ\u008fXâÌ©\u0013\u0003é·TÓrö@§\u001b\u001dË\u0013\tgbNFV§wj\u0092\u0019=²å¿·$©\u001ew\u0018\u008b\u000ejG¤;3Ù`\u009bw\u00181EÌß{.XftÉáV[\u001fLjYU¿òQ¦=«ùÅ\\'\"\"Rèuäø\u0015Jd_\u0089qhÀ±¿Æ\u0012ê¿xWi»4ªy\u0084Ü~.¢vL®\u0083\u0007ØÁqT×î_d¥\u001aD¦\u0002ÂI\u0005AWÜ\u0002Í_\\\u000eUûï\u0085\u0019ûã'ð±Q\u0016\u0092Å o\u0019Ñ\u0083\u0085õÄÛÂÖ¹?hÌ)yÇ\u000e\u0018«\u0093\u0086k;ÕÛ\u0089\u0099U*\u000e;L\u0099ßXû\u0091\u008bíÁ\u0082º5à\u0003Á´±\rÒD¨î$ÿý38r«R!\u0098^Ú°ü\r\u000b\u0014[j¡\u0092\u0085\u008ey\u0002\u0001\u0003£\u0081Ù0\u0081Ö0\u001d\u0006\u0003U\u001d\u000e\u0004\u0016\u0004\u0014Ç}\u008cÂ!\u0017V%\u009a\u007fÓ\u0082ßkã\u0098ä×\u0086¥0\u0081¦\u0006\u0003U\u001d#\u0004\u0081\u009e0\u0081\u009b\u0080\u0014Ç}\u008cÂ!\u0017V%\u009a\u007fÓ\u0082ßkã\u0098ä×\u0086¥¡x¤v0t1\u000b0\t\u0006\u0003U\u0004\u0006\u0013\u0002US1\u00130\u0011\u0006\u0003U\u0004\b\u0013\nCalifornia1\u00160\u0014\u0006\u0003U\u0004\u0007\u0013\rMountain View1\u00140\u0012\u0006\u0003U\u0004\n\u0013\u000bGoogle Inc.1\u00100\u000e\u0006\u0003U\u0004\u000b\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u0003\u0013\u0007Android\u0082\t\u0000Âà\u0087FdJ0\u008d0\f\u0006\u0003U\u001d\u0013\u0004\u00050\u0003\u0001\u0001ÿ0\r\u0006\t*\u0086H\u0086÷\r\u0001\u0001\u0004\u0005\u0000\u0003\u0082\u0001\u0001\u0000mÒRÎï\u00850,6\nªÎ\u0093\u009bÏòÌ©\u0004»]z\u0016aø®F²\u0099B\u0004ÐÿJhÇí\u001aS\u001eÄYZb<æ\u0007c±g)zzãW\u0012Ä\u0007ò\bðË\u0010\u0094)\u0012M{\u0010b\u0019À\u0084Ê>³ù\u00ad_¸qï\u0092&\u009a\u008bâ\u008bñmDÈÙ \u008el²ð\u0005»?âË\u0096D~\u0086\u008es\u0010v\u00adE³?`\tê\u0019Áaæ&Aª\u0099'\u001dýR(ÅÅ\u0087\u0087]Û\u007fE'XÖaöÌ\fÌ·5.BLÄ6\\R52÷2Q7Y<JãAôÛAíÚ\r\u000b\u0010q§Ä@ðþ\u009e \u001c¶'ÊgCiÐ\u0084½/Ù\u0011ÿ\u0006Í¿,ú\u0010Ü\u000f\u0089:ãWb\u0091\u0090HÇïÆLqD\u0017\u0083B÷\u0005\u0081ÉÞW:õ[9\r×ý¹A\u00861\u0089]_u\u009f0\u0011&\u0087ÿb\u0014\u0010Ài0\u008a");
            }
        }, new zzc(zza.zzgv("0\u0082\u0004¨0\u0082\u0003\u0090 \u0003\u0002\u0001\u0002\u0002\t\u0000Õ\u0085¸l}ÓNõ0")) { // from class: com.google.android.gms.common.zzd.zzd.2
            @Override // com.google.android.gms.common.zzd.zzc
            protected final byte[] zzanm() {
                return zza.zzgv("0\u0082\u0004¨0\u0082\u0003\u0090 \u0003\u0002\u0001\u0002\u0002\t\u0000Õ\u0085¸l}ÓNõ0\r\u0006\t*\u0086H\u0086÷\r\u0001\u0001\u0004\u0005\u00000\u0081\u00941\u000b0\t\u0006\u0003U\u0004\u0006\u0013\u0002US1\u00130\u0011\u0006\u0003U\u0004\b\u0013\nCalifornia1\u00160\u0014\u0006\u0003U\u0004\u0007\u0013\rMountain View1\u00100\u000e\u0006\u0003U\u0004\n\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u000b\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u0003\u0013\u0007Android1\"0 \u0006\t*\u0086H\u0086÷\r\u0001\t\u0001\u0016\u0013android@android.com0\u001e\u0017\r080415233656Z\u0017\r350901233656Z0\u0081\u00941\u000b0\t\u0006\u0003U\u0004\u0006\u0013\u0002US1\u00130\u0011\u0006\u0003U\u0004\b\u0013\nCalifornia1\u00160\u0014\u0006\u0003U\u0004\u0007\u0013\rMountain View1\u00100\u000e\u0006\u0003U\u0004\n\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u000b\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u0003\u0013\u0007Android1\"0 \u0006\t*\u0086H\u0086÷\r\u0001\t\u0001\u0016\u0013android@android.com0\u0082\u0001 0\r\u0006\t*\u0086H\u0086÷\r\u0001\u0001\u0001\u0005\u0000\u0003\u0082\u0001\r\u00000\u0082\u0001\b\u0002\u0082\u0001\u0001\u0000ÖÎ.\b\n¿â1MÑ\u008d³ÏÓ\u0018\\´=3ú\ftá½¶ÑÛ\u0089\u0013ö,\\9ßVøF\u0081=e¾ÀóÊBk\u0007Å¨íZ9\u0090ÁgçkÉ\u0099¹'\u0089K\u008f\u000b\"\u0000\u0019\u0094©)\u0015årÅm*0\u001b£oÅü\u0011:ÖË\u009et5¡m#«}úîáeäß\u001f\n\u008d½§\n\u0086\u009dQlN\u009d\u0005\u0011\u0096Ê|\fU\u007f\u0017[ÃuùHÅj®\u0086\b\u009b¤O\u008a¦¤Ý\u009a}¿,\n5\"\u0082\u00ad\u0006¸Ì\u0018^±Uyîøm\b\u000b\u001da\u0089Àù¯\u0098±ÂëÑ\u0007êE«Ûh£Ç\u0083\u008a^T\u0088ÇlSÔ\u000b\u0012\u001dç»Ó\u000eb\f\u0018\u008aáªaÛ¼\u0087Ý<d_/UóÔÃuì@p©?qQØ6pÁj\u0097\u001a¾^òÑ\u0018\u0090á¸®ó)\u008cðf¿\u009eláD¬\u009aèm\u001c\u001b\u000f\u0002\u0001\u0003£\u0081ü0\u0081ù0\u001d\u0006\u0003U\u001d\u000e\u0004\u0016\u0004\u0014\u008d\u001cÅ¾\u0095LC<a\u0086:\u0015°L¼\u0003òOà²0\u0081É\u0006\u0003U\u001d#\u0004\u0081Á0\u0081¾\u0080\u0014\u008d\u001cÅ¾\u0095LC<a\u0086:\u0015°L¼\u0003òOà²¡\u0081\u009a¤\u0081\u00970\u0081\u00941\u000b0\t\u0006\u0003U\u0004\u0006\u0013\u0002US1\u00130\u0011\u0006\u0003U\u0004\b\u0013\nCalifornia1\u00160\u0014\u0006\u0003U\u0004\u0007\u0013\rMountain View1\u00100\u000e\u0006\u0003U\u0004\n\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u000b\u0013\u0007Android1\u00100\u000e\u0006\u0003U\u0004\u0003\u0013\u0007Android1\"0 \u0006\t*\u0086H\u0086÷\r\u0001\t\u0001\u0016\u0013android@android.com\u0082\t\u0000Õ\u0085¸l}ÓNõ0\f\u0006\u0003U\u001d\u0013\u0004\u00050\u0003\u0001\u0001ÿ0\r\u0006\t*\u0086H\u0086÷\r\u0001\u0001\u0004\u0005\u0000\u0003\u0082\u0001\u0001\u0000\u0019Ó\fñ\u0005ûx\u0092?L\r}Ò##=@\u0096zÏÎ\u0000\b\u001d[×ÆéÖí k\u000e\u0011 \u0095\u0006Al¢D\u0093\u0099\u0013ÒkJ àõ$ÊÒ»\\nL¡\u0001j\u0015\u0091n¡ì]ÉZ^:\u0001\u00006ô\u0092HÕ\u0010\u009b¿.\u001ea\u0081\u0086g:;åm¯\u000bw±Â)ãÂUãèL\u0090]#\u0087ïº\tËñ; +NZ\"É2cHJ#Òü)ú\u009f\u00199u\u00973¯Øª\u0016\u000fB\u0096ÂÐ\u0016>\u0081\u0082\u0085\u009cfCéÁ\u0096/ Á\u008333[À\u0090ÿ\u009ak\"ÞÑ\u00adDB)¥9©Nï\u00ad«ÐeÎÒK>QåÝ{fx{ï\u0012þ\u0097û¤\u0084Ä#ûOøÌIL\u0002ðõ\u0005\u0016\u0012ÿe)9>\u008eFêÅ»!òwÁQª_*¦'Ñè\u009d§\n¶\u00035iÞ;\u0098\u0097¿ÿ|©Ú>\u0012Cö\u000b");
            }
        }};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized void zzbq(Context context) {
        synchronized (zzd.class) {
            if (rh != null) {
                Log.w("GoogleCertificates", "GoogleCertificates has been initialized already");
            } else if (context != null) {
                rh = context.getApplicationContext();
            }
        }
    }

    /* loaded from: classes.dex */
    static abstract class zza extends zzs.zza {
        private int rk;

        protected static byte[] zzgv(String str) {
            try {
                return str.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }

        public boolean equals(Object obj) {
            com.google.android.gms.dynamic.zzd zzank;
            if (obj == null || !(obj instanceof zzs)) {
                return false;
            }
            try {
                zzs zzsVar = (zzs) obj;
                if (zzsVar.zzanl() == hashCode() && (zzank = zzsVar.zzank()) != null) {
                    return Arrays.equals(getBytes(), (byte[]) com.google.android.gms.dynamic.zze.zzad(zzank));
                }
                return false;
            } catch (RemoteException e) {
                Log.e("GoogleCertificates", "iCertData failed to retrive data from remote");
                return false;
            }
        }

        abstract byte[] getBytes();

        public int hashCode() {
            return this.rk;
        }

        @Override // com.google.android.gms.common.internal.zzs
        public final com.google.android.gms.dynamic.zzd zzank() {
            return com.google.android.gms.dynamic.zze.zzac(getBytes());
        }

        @Override // com.google.android.gms.common.internal.zzs
        public final int zzanl() {
            return hashCode();
        }

        protected zza(byte[] bArr) {
            String str;
            if (bArr.length != 25) {
                int length = bArr.length;
                int length2 = bArr.length;
                if (bArr == null || bArr.length == 0 || length2 <= 0 || length2 + 0 > bArr.length) {
                    str = null;
                } else {
                    StringBuilder sb = new StringBuilder((((length2 + 16) - 1) / 16) * 57);
                    int i = 0;
                    int i2 = length2;
                    int i3 = 0;
                    while (i2 > 0) {
                        if (i3 == 0) {
                            if (length2 < 65536) {
                                sb.append(String.format("%04X:", Integer.valueOf(i)));
                            } else {
                                sb.append(String.format("%08X:", Integer.valueOf(i)));
                            }
                        } else if (i3 == 8) {
                            sb.append(" -");
                        }
                        sb.append(String.format(" %02X", Integer.valueOf(bArr[i] & 255)));
                        int i4 = i2 - 1;
                        int i5 = i3 + 1;
                        if (i5 == 16 || i4 == 0) {
                            sb.append('\n');
                            i5 = 0;
                        }
                        i++;
                        i3 = i5;
                        i2 = i4;
                    }
                    str = sb.toString();
                }
                String valueOf = String.valueOf(str);
                Log.wtf("GoogleCertificates", new StringBuilder(String.valueOf(valueOf).length() + 51).append("Cert hash data has incorrect length (").append(length).append("):\n").append(valueOf).toString(), new Exception());
                bArr = Arrays.copyOfRange(bArr, 0, 25);
                zzab.zzb(bArr.length == 25, new StringBuilder(55).append("cert hash data has incorrect length. length=").append(bArr.length).toString());
            }
            this.rk = Arrays.hashCode(bArr);
        }
    }
}
