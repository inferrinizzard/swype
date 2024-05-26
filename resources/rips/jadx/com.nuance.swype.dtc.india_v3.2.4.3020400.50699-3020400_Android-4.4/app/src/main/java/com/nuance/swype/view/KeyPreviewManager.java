package com.nuance.swype.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.R;
import com.nuance.swype.util.Callback;
import com.nuance.swype.util.CollectionUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.ViewUtil;
import com.nuance.swype.widget.PreviewView;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class KeyPreviewManager {
    protected static final LogManager.Log log = LogManager.getLog("KeyPreviewManager");
    private Context context;
    public KeyboardViewEx keyboardView;
    public final OverlayView overlayView;
    private StyleParams styleParams;
    public final SparseArray<PreviewInfo> previewInfo = new SparseArray<>();
    private final CollectionUtils.FiniteSet<Integer> added = new CollectionUtils.FiniteSet<>();
    public boolean enabled = true;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PreviewInfo {
        public int state = 0;
        public Callback<? extends Runnable> timer;
        public PreviewView view;

        public PreviewInfo(Context context, int layoutId, int hideDelay) {
            if (layoutId == 0) {
                this.view = new PreviewView(context);
            } else {
                LayoutInflater inflater = IMEApplication.from(context).getThemedLayoutInflater(LayoutInflater.from(context));
                IMEApplication.from(context).getThemeLoader().setLayoutInflaterFactory(inflater);
                this.view = (PreviewView) inflater.inflate(layoutId, (ViewGroup) null);
                IMEApplication.from(context).getThemeLoader().applyTheme(this.view);
            }
            this.timer = Callback.create$afe0100(new Runnable() { // from class: com.nuance.swype.view.KeyPreviewManager.PreviewInfo.1
                @Override // java.lang.Runnable
                public final void run() {
                    PreviewInfo.this.hideNow();
                }
            }, hideDelay);
        }

        public final void hideNow() {
            this.state = 0;
            this.view.setVisibility(4);
            this.view.requestLayout();
            this.timer.stop();
        }
    }

    /* loaded from: classes.dex */
    public static class StyleParams {
        protected final int postReleaseHideDelay;
        protected final int previewViewLayoutId;

        public StyleParams(Context context, TypedArray a) {
            this.previewViewLayoutId = a.getResourceId(R.styleable.KeyboardViewEx_keyPreviewLayout, 0);
            this.postReleaseHideDelay = context.getResources().getInteger(R.integer.preview_popup_hide_delay);
        }
    }

    public KeyPreviewManager(Context context, KeyboardViewEx keyboardView, OverlayView overlay, StyleParams params) {
        this.context = context;
        this.keyboardView = keyboardView;
        this.overlayView = overlay;
        this.styleParams = params;
    }

    public final void dismissNow(int id) {
        PreviewInfo info = getPreviewInfo(id);
        if (info != null) {
            info.hideNow();
        }
    }

    public final PreviewInfo getPreviewInfo(int idPointer) {
        return this.previewInfo.get(idPointer);
    }

    public final PreviewInfo getPreviewInfoCreate(int pointerId) {
        Integer removed;
        PreviewInfo info;
        PreviewInfo info2 = getPreviewInfo(pointerId);
        if (info2 == null) {
            CollectionUtils.FiniteSet<Integer> finiteSet = this.added;
            Integer valueOf = Integer.valueOf(pointerId);
            Integer num = null;
            if (finiteSet.list.contains(valueOf)) {
                removed = null;
            } else {
                if (finiteSet.list.size() == finiteSet.max) {
                    Iterator<Integer> it = finiteSet.list.iterator();
                    num = it.next();
                    it.remove();
                }
                finiteSet.list.add(valueOf);
                removed = num;
            }
            Integer removed2 = removed;
            if (removed2 != null) {
                PreviewInfo old = getPreviewInfo(removed2.intValue());
                old.hideNow();
                this.previewInfo.remove(removed2.intValue());
                info = old;
            } else {
                info = new PreviewInfo(this.context, this.styleParams.previewViewLayoutId, this.styleParams.postReleaseHideDelay);
                addKeyPreview(info.view);
            }
            this.previewInfo.put(pointerId, info);
            return info;
        }
        PreviewView previewView = info2.view;
        if (previewView.getParent() == null) {
            addKeyPreview(previewView);
        }
        return info2;
    }

    private void addKeyPreview(PreviewView previewView) {
        ViewUtil.forceLayout(previewView);
        this.overlayView.addView(previewView);
    }

    public final void hide(int id) {
        PreviewInfo info = getPreviewInfo(id);
        if (info == null) {
            return;
        }
        info.state = 0;
        info.timer.start();
    }
}
