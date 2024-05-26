package com.google.android.gms.ads.internal.overlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.nuance.swype.input.KeyboardEx;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TargetApi(14)
@zzin
/* loaded from: classes.dex */
public class zzc extends zzi implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, TextureView.SurfaceTextureListener {
    private static final Map<Integer, String> zzbrr;
    private final zzx zzbrs;
    private final boolean zzbrt;
    private int zzbru;
    private int zzbrv;
    private MediaPlayer zzbrw;
    private Uri zzbrx;
    private int zzbry;
    private int zzbrz;
    private int zzbsa;
    private int zzbsb;
    private int zzbsc;
    private float zzbsd;
    private boolean zzbse;
    private boolean zzbsf;
    private zzw zzbsg;
    private boolean zzbsh;
    private int zzbsi;
    private zzh zzbsj;

    static {
        HashMap hashMap = new HashMap();
        zzbrr = hashMap;
        hashMap.put(-1004, "MEDIA_ERROR_IO");
        zzbrr.put(-1007, "MEDIA_ERROR_MALFORMED");
        zzbrr.put(-1010, "MEDIA_ERROR_UNSUPPORTED");
        zzbrr.put(Integer.valueOf(KeyboardEx.GESTURE_KEYCODE_CASE_EDIT), "MEDIA_ERROR_TIMED_OUT");
        zzbrr.put(100, "MEDIA_ERROR_SERVER_DIED");
        zzbrr.put(1, "MEDIA_ERROR_UNKNOWN");
        zzbrr.put(1, "MEDIA_INFO_UNKNOWN");
        zzbrr.put(700, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
        zzbrr.put(3, "MEDIA_INFO_VIDEO_RENDERING_START");
        zzbrr.put(701, "MEDIA_INFO_BUFFERING_START");
        zzbrr.put(702, "MEDIA_INFO_BUFFERING_END");
        zzbrr.put(800, "MEDIA_INFO_BAD_INTERLEAVING");
        zzbrr.put(801, "MEDIA_INFO_NOT_SEEKABLE");
        zzbrr.put(802, "MEDIA_INFO_METADATA_UPDATE");
        zzbrr.put(901, "MEDIA_INFO_UNSUPPORTED_SUBTITLE");
        zzbrr.put(902, "MEDIA_INFO_SUBTITLE_TIMED_OUT");
    }

    public zzc(Context context, boolean z, boolean z2, zzx zzxVar) {
        super(context);
        this.zzbru = 0;
        this.zzbrv = 0;
        this.zzbsd = 1.0f;
        setSurfaceTextureListener(this);
        this.zzbrs = zzxVar;
        this.zzbsh = z;
        this.zzbrt = z2;
        this.zzbrs.zza(this);
    }

    private void zzad(int i) {
        if (i == 3) {
            this.zzbrs.zzpi();
        } else if (this.zzbru == 3) {
            this.zzbrs.zzpj();
        }
        this.zzbru = i;
    }

    private void zzb(float f) {
        if (this.zzbrw == null) {
            zzkd.zzcx("AdMediaPlayerView setMediaPlayerVolume() called before onPrepared().");
        } else {
            try {
                this.zzbrw.setVolume(f, f);
            } catch (IllegalStateException e) {
            }
        }
    }

    private void zznk() {
        if (this.zzbrt && zznn() && this.zzbrw.getCurrentPosition() > 0 && this.zzbrv != 3) {
            zzkd.v("AdMediaPlayerView nudging MediaPlayer");
            zzb(0.0f);
            this.zzbrw.start();
            int currentPosition = this.zzbrw.getCurrentPosition();
            long currentTimeMillis = com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis();
            while (zznn() && this.zzbrw.getCurrentPosition() == currentPosition && com.google.android.gms.ads.internal.zzu.zzfu().currentTimeMillis() - currentTimeMillis <= 250) {
            }
            this.zzbrw.pause();
            zzns();
        }
    }

    private void zznm() {
        zzkd.v("AdMediaPlayerView abandon audio focus");
        AudioManager zznt = zznt();
        if (zznt == null || !this.zzbsf) {
            return;
        }
        if (zznt.abandonAudioFocus(this) == 1) {
            this.zzbsf = false;
        } else {
            zzkd.zzcx("AdMediaPlayerView abandon audio focus failed");
        }
    }

    private boolean zznn() {
        return (this.zzbrw == null || this.zzbru == -1 || this.zzbru == 0 || this.zzbru == 1) ? false : true;
    }

    private void zznq() {
        zzkd.v("AdMediaPlayerView audio focus gained");
        this.zzbsf = true;
        zzns();
    }

    private void zzns() {
        if (this.zzbse || !this.zzbsf) {
            zzb(0.0f);
        } else {
            zzb(this.zzbsd);
        }
    }

    private AudioManager zznt() {
        return (AudioManager) getContext().getSystemService("audio");
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public int getCurrentPosition() {
        if (zznn()) {
            return this.zzbrw.getCurrentPosition();
        }
        return 0;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public int getDuration() {
        if (zznn()) {
            return this.zzbrw.getDuration();
        }
        return -1;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public int getVideoHeight() {
        if (this.zzbrw != null) {
            return this.zzbrw.getVideoHeight();
        }
        return 0;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public int getVideoWidth() {
        if (this.zzbrw != null) {
            return this.zzbrw.getVideoWidth();
        }
        return 0;
    }

    @Override // android.media.MediaPlayer.OnBufferingUpdateListener
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        this.zzbsa = i;
    }

    @Override // android.media.MediaPlayer.OnInfoListener
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        String str = zzbrr.get(Integer.valueOf(i));
        String str2 = zzbrr.get(Integer.valueOf(i2));
        zzkd.v(new StringBuilder(String.valueOf(str).length() + 37 + String.valueOf(str2).length()).append("AdMediaPlayerView MediaPlayer info: ").append(str).append(":").append(str2).toString());
        return true;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int defaultSize = getDefaultSize(this.zzbry, i);
        int defaultSize2 = getDefaultSize(this.zzbrz, i2);
        if (this.zzbry > 0 && this.zzbrz > 0 && this.zzbsg == null) {
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            int mode2 = View.MeasureSpec.getMode(i2);
            defaultSize2 = View.MeasureSpec.getSize(i2);
            if (mode == 1073741824 && mode2 == 1073741824) {
                if (this.zzbry * defaultSize2 < this.zzbrz * size) {
                    defaultSize = (this.zzbry * defaultSize2) / this.zzbrz;
                } else if (this.zzbry * defaultSize2 > this.zzbrz * size) {
                    defaultSize2 = (this.zzbrz * size) / this.zzbry;
                    defaultSize = size;
                } else {
                    defaultSize = size;
                }
            } else if (mode == 1073741824) {
                int i3 = (this.zzbrz * size) / this.zzbry;
                if (mode2 != Integer.MIN_VALUE || i3 <= defaultSize2) {
                    defaultSize2 = i3;
                    defaultSize = size;
                } else {
                    defaultSize = size;
                }
            } else if (mode2 == 1073741824) {
                defaultSize = (this.zzbry * defaultSize2) / this.zzbrz;
                if (mode == Integer.MIN_VALUE && defaultSize > size) {
                    defaultSize = size;
                }
            } else {
                int i4 = this.zzbry;
                int i5 = this.zzbrz;
                if (mode2 != Integer.MIN_VALUE || i5 <= defaultSize2) {
                    defaultSize2 = i5;
                    defaultSize = i4;
                } else {
                    defaultSize = (this.zzbry * defaultSize2) / this.zzbrz;
                }
                if (mode == Integer.MIN_VALUE && defaultSize > size) {
                    defaultSize2 = (this.zzbrz * size) / this.zzbry;
                    defaultSize = size;
                }
            }
        }
        setMeasuredDimension(defaultSize, defaultSize2);
        if (this.zzbsg != null) {
            this.zzbsg.zzg(defaultSize, defaultSize2);
        }
        if (Build.VERSION.SDK_INT == 16) {
            if ((this.zzbsb > 0 && this.zzbsb != defaultSize) || (this.zzbsc > 0 && this.zzbsc != defaultSize2)) {
                zznk();
            }
            this.zzbsb = defaultSize;
            this.zzbsc = defaultSize2;
        }
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        zzkd.v("AdMediaPlayerView surface created");
        zznj();
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.4
            @Override // java.lang.Runnable
            public final void run() {
                if (zzc.this.zzbsj != null) {
                    zzc.this.zzbsj.zzoi();
                }
            }
        });
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        zzkd.v("AdMediaPlayerView surface destroyed");
        if (this.zzbrw != null && this.zzbsi == 0) {
            this.zzbsi = this.zzbrw.getCurrentPosition();
        }
        if (this.zzbsg != null) {
            this.zzbsg.zzow();
        }
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.5
            @Override // java.lang.Runnable
            public final void run() {
                if (zzc.this.zzbsj != null) {
                    zzc.this.zzbsj.onPaused();
                    zzc.this.zzbsj.zzom();
                }
            }
        });
        zzy(true);
        return true;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        zzkd.v("AdMediaPlayerView surface changed");
        boolean z = this.zzbrv == 3;
        boolean z2 = this.zzbry == i && this.zzbrz == i2;
        if (this.zzbrw != null && z && z2) {
            if (this.zzbsi != 0) {
                seekTo(this.zzbsi);
            }
            play();
        }
        if (this.zzbsg != null) {
            this.zzbsg.zzg(i, i2);
        }
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.zzbrs.zzb(this);
    }

    @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
        zzkd.v(new StringBuilder(57).append("AdMediaPlayerView size changed: ").append(i).append(" x ").append(i2).toString());
        this.zzbry = mediaPlayer.getVideoWidth();
        this.zzbrz = mediaPlayer.getVideoHeight();
        if (this.zzbry == 0 || this.zzbrz == 0) {
            return;
        }
        requestLayout();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void seekTo(int i) {
        zzkd.v(new StringBuilder(34).append("AdMediaPlayerView seek ").append(i).toString());
        if (!zznn()) {
            this.zzbsi = i;
        } else {
            this.zzbrw.seekTo(i);
            this.zzbsi = 0;
        }
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void setMimeType(String str) {
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void setVideoPath(String str) {
        setVideoURI(Uri.parse(str));
    }

    public void setVideoURI(Uri uri) {
        this.zzbrx = uri;
        this.zzbsi = 0;
        zznj();
        requestLayout();
        invalidate();
    }

    @Override // android.view.View
    public String toString() {
        String valueOf = String.valueOf(getClass().getName());
        String valueOf2 = String.valueOf(Integer.toHexString(hashCode()));
        return new StringBuilder(String.valueOf(valueOf).length() + 1 + String.valueOf(valueOf2).length()).append(valueOf).append("@").append(valueOf2).toString();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void zza(float f) {
        this.zzbsd = f;
        zzns();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void zza(zzh zzhVar) {
        this.zzbsj = zzhVar;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public String zzni() {
        String valueOf = String.valueOf(this.zzbsh ? " spherical" : "");
        return valueOf.length() != 0 ? "MediaPlayer".concat(valueOf) : new String("MediaPlayer");
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void zzno() {
        this.zzbse = true;
        zzns();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void zznp() {
        this.zzbse = false;
        zzns();
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void stop() {
        zzkd.v("AdMediaPlayerView stop");
        if (this.zzbrw != null) {
            this.zzbrw.stop();
            this.zzbrw.release();
            this.zzbrw = null;
            zzad(0);
            this.zzbrv = 0;
            zznm();
        }
        this.zzbrs.onStop();
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mediaPlayer) {
        zzkd.v("AdMediaPlayerView prepared");
        zzad(2);
        this.zzbrs.zzoj();
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.1
            @Override // java.lang.Runnable
            public final void run() {
                if (zzc.this.zzbsj != null) {
                    zzc.this.zzbsj.zzoj();
                }
            }
        });
        this.zzbry = mediaPlayer.getVideoWidth();
        this.zzbrz = mediaPlayer.getVideoHeight();
        if (this.zzbsi != 0) {
            seekTo(this.zzbsi);
        }
        zznk();
        int i = this.zzbry;
        zzkd.zzcw(new StringBuilder(62).append("AdMediaPlayerView stream dimensions: ").append(i).append(" x ").append(this.zzbrz).toString());
        if (this.zzbrv == 3) {
            play();
        }
        AudioManager zznt = zznt();
        if (zznt != null && !this.zzbsf) {
            if (zznt.requestAudioFocus(this, 3, 2) == 1) {
                zznq();
            } else {
                zzkd.zzcx("AdMediaPlayerView audio focus request failed");
            }
        }
        zzns();
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        zzkd.v("AdMediaPlayerView completion");
        zzad(5);
        this.zzbrv = 5;
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.2
            @Override // java.lang.Runnable
            public final void run() {
                if (zzc.this.zzbsj != null) {
                    zzc.this.zzbsj.zzol();
                }
            }
        });
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        final String str = zzbrr.get(Integer.valueOf(i));
        final String str2 = zzbrr.get(Integer.valueOf(i2));
        zzkd.zzcx(new StringBuilder(String.valueOf(str).length() + 38 + String.valueOf(str2).length()).append("AdMediaPlayerView MediaPlayer error: ").append(str).append(":").append(str2).toString());
        zzad(-1);
        this.zzbrv = -1;
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.3
            @Override // java.lang.Runnable
            public final void run() {
                if (zzc.this.zzbsj != null) {
                    zzc.this.zzbsj.zzl(str, str2);
                }
            }
        });
        return true;
    }

    private void zznj() {
        SurfaceTexture surfaceTexture;
        zzkd.v("AdMediaPlayerView init MediaPlayer");
        SurfaceTexture surfaceTexture2 = getSurfaceTexture();
        if (this.zzbrx == null || surfaceTexture2 == null) {
            return;
        }
        zzy(false);
        try {
            this.zzbrw = com.google.android.gms.ads.internal.zzu.zzgd().zzov();
            this.zzbrw.setOnBufferingUpdateListener(this);
            this.zzbrw.setOnCompletionListener(this);
            this.zzbrw.setOnErrorListener(this);
            this.zzbrw.setOnInfoListener(this);
            this.zzbrw.setOnPreparedListener(this);
            this.zzbrw.setOnVideoSizeChangedListener(this);
            this.zzbsa = 0;
            if (this.zzbsh) {
                this.zzbsg = new zzw(getContext());
                zzw zzwVar = this.zzbsg;
                int width = getWidth();
                int height = getHeight();
                zzwVar.zzaie = width;
                zzwVar.zzaif = height;
                zzwVar.zzbvd = surfaceTexture2;
                this.zzbsg.start();
                surfaceTexture = this.zzbsg.zzox();
                if (surfaceTexture == null) {
                    this.zzbsg.zzow();
                    this.zzbsg = null;
                }
                this.zzbrw.setDataSource(getContext(), this.zzbrx);
                this.zzbrw.setSurface(com.google.android.gms.ads.internal.zzu.zzge().zza(surfaceTexture));
                this.zzbrw.setAudioStreamType(3);
                this.zzbrw.setScreenOnWhilePlaying(true);
                this.zzbrw.prepareAsync();
                zzad(1);
            }
            surfaceTexture = surfaceTexture2;
            this.zzbrw.setDataSource(getContext(), this.zzbrx);
            this.zzbrw.setSurface(com.google.android.gms.ads.internal.zzu.zzge().zza(surfaceTexture));
            this.zzbrw.setAudioStreamType(3);
            this.zzbrw.setScreenOnWhilePlaying(true);
            this.zzbrw.prepareAsync();
            zzad(1);
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            String valueOf = String.valueOf(this.zzbrx);
            zzkd.zzd(new StringBuilder(String.valueOf(valueOf).length() + 36).append("Failed to initialize MediaPlayer at ").append(valueOf).toString(), e);
            onError(this.zzbrw, 1, 0);
        }
    }

    private void zzy(boolean z) {
        zzkd.v("AdMediaPlayerView release");
        if (this.zzbsg != null) {
            this.zzbsg.zzow();
            this.zzbsg = null;
        }
        if (this.zzbrw != null) {
            this.zzbrw.reset();
            this.zzbrw.release();
            this.zzbrw = null;
            zzad(0);
            if (z) {
                this.zzbrv = 0;
                this.zzbrv = 0;
            }
            zznm();
        }
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void play() {
        zzkd.v("AdMediaPlayerView play");
        if (zznn()) {
            this.zzbrw.start();
            zzad(3);
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.6
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzc.this.zzbsj != null) {
                        zzc.this.zzbsj.zzok();
                    }
                }
            });
        }
        this.zzbrv = 3;
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void pause() {
        zzkd.v("AdMediaPlayerView pause");
        if (zznn() && this.zzbrw.isPlaying()) {
            this.zzbrw.pause();
            zzad(4);
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzc.7
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzc.this.zzbsj != null) {
                        zzc.this.zzbsj.onPaused();
                    }
                }
            });
        }
        this.zzbrv = 4;
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(int i) {
        if (i > 0) {
            zznq();
        } else if (i < 0) {
            zzkd.v("AdMediaPlayerView audio focus lost");
            this.zzbsf = false;
            zzns();
        }
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzi
    public void zza(float f, float f2) {
        float f3;
        float f4;
        if (this.zzbsg != null) {
            zzw zzwVar = this.zzbsg;
            if (zzwVar.zzaie > zzwVar.zzaif) {
                f3 = (1.7453293f * f) / zzwVar.zzaie;
                f4 = (1.7453293f * f2) / zzwVar.zzaie;
            } else {
                f3 = (1.7453293f * f) / zzwVar.zzaif;
                f4 = (1.7453293f * f2) / zzwVar.zzaif;
            }
            zzwVar.zzbva -= f3;
            zzwVar.zzbvb -= f4;
            if (zzwVar.zzbvb < -1.5707964f) {
                zzwVar.zzbvb = -1.5707964f;
            }
            if (zzwVar.zzbvb > 1.5707964f) {
                zzwVar.zzbvb = 1.5707964f;
            }
        }
    }
}
