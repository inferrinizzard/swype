package com.google.android.gms.ads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.internal.zzhi;

/* loaded from: classes.dex */
public class AdActivity extends Activity {
    public static final String CLASS_NAME = "com.google.android.gms.ads.AdActivity";
    public static final String SIMPLE_CLASS_NAME = "AdActivity";
    private zzhi zzahy;

    private void zzdb() {
        if (this.zzahy != null) {
            try {
                this.zzahy.zzdb();
            } catch (RemoteException e) {
                zzb.zzd("Could not forward setContentViewSet to ad overlay:", e);
            }
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        try {
            this.zzahy.onActivityResult(i, i2, intent);
        } catch (Exception e) {
            zzb.zzd("Could not forward onActivityResult to ad overlay:", e);
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        boolean z = true;
        try {
            if (this.zzahy != null) {
                z = this.zzahy.zznw();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onBackPressed to ad overlay:", e);
        }
        if (z) {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzahy = zzm.zzix().zzc(this);
        if (this.zzahy == null) {
            zzb.zzcx("Could not create ad overlay.");
            finish();
            return;
        }
        try {
            this.zzahy.onCreate(bundle);
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onCreate to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        try {
            if (this.zzahy != null) {
                this.zzahy.onDestroy();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onDestroy to ad overlay:", e);
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPause() {
        try {
            if (this.zzahy != null) {
                this.zzahy.onPause();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onPause to ad overlay:", e);
            finish();
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        try {
            if (this.zzahy != null) {
                this.zzahy.onRestart();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onRestart to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        try {
            if (this.zzahy != null) {
                this.zzahy.onResume();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onResume to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        try {
            if (this.zzahy != null) {
                this.zzahy.onSaveInstanceState(bundle);
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onSaveInstanceState to ad overlay:", e);
            finish();
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        try {
            if (this.zzahy != null) {
                this.zzahy.onStart();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onStart to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            if (this.zzahy != null) {
                this.zzahy.onStop();
            }
        } catch (RemoteException e) {
            zzb.zzd("Could not forward onStop to ad overlay:", e);
            finish();
        }
        super.onStop();
    }

    @Override // android.app.Activity
    public void setContentView(int i) {
        super.setContentView(i);
        zzdb();
    }

    @Override // android.app.Activity
    public void setContentView(View view) {
        super.setContentView(view);
        zzdb();
    }

    @Override // android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        super.setContentView(view, layoutParams);
        zzdb();
    }
}
