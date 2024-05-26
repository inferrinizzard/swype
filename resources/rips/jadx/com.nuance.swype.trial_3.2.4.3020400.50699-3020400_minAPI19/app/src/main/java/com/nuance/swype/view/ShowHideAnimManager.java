package com.nuance.swype.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.view.View;
import com.nuance.swype.util.LogManager;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class ShowHideAnimManager {
    protected static final LogManager.Log log = LogManager.getLog("ShowHideAnimManager");
    private Animator animHide;
    private Animator animShow;
    public Listener listener;
    private final LinkedHashMap<View, Animator> hidePending = new LinkedHashMap<>();
    private final LinkedHashMap<View, Animator> showPending = new LinkedHashMap<>();

    /* loaded from: classes.dex */
    public interface Listener {
        void onHideComplete(View view);
    }

    public ShowHideAnimManager(Animator animShow, Animator animHide) {
        this.animShow = animShow;
        this.animHide = animHide;
    }

    public static Listener createDefaultListener$378274fe() {
        return new Listener() { // from class: com.nuance.swype.view.ShowHideAnimManager.1
            final /* synthetic */ int val$hideVisibility = 8;

            @Override // com.nuance.swype.view.ShowHideAnimManager.Listener
            public final void onHideComplete(View view) {
                view.setVisibility(this.val$hideVisibility);
            }
        };
    }

    protected final void onHideComplete(View view, boolean alwaysNotify) {
        if ((this.hidePending.remove(view) != null) || alwaysNotify) {
            log.d("onHideComplete(): view: " + view);
            this.listener.onHideComplete(view);
        } else {
            log.d("onHideComplete(): view hide not pending: " + view);
        }
    }

    protected final void onShowComplete(View view, boolean alwaysNotify) {
        if ((this.showPending.remove(view) != null) || alwaysNotify) {
            log.d("onShowComplete(): view: " + view);
        } else {
            log.d("onShowComplete(): view show not pending: " + view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(11)
    /* loaded from: classes.dex */
    public class AnimListener extends AnimatorListenerAdapter {
        private boolean isShow;
        private View view;

        AnimListener(View view, boolean isShow) {
            this.view = view;
            this.isShow = isShow;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animation) {
            if (this.isShow) {
                ShowHideAnimManager.this.onShowComplete(this.view, false);
            } else {
                ShowHideAnimManager.this.onHideComplete(this.view, false);
            }
            animation.removeListener(this);
        }
    }

    public final void show(View view, boolean skipAnim) {
        cancelHideAnim(view, false);
        if (skipAnim) {
            cancelShowAnim(view, true);
            onShowComplete(view, true);
        } else {
            if (!this.showPending.containsKey(view)) {
                if (!initAnimation(view, this.animShow, true)) {
                    onShowComplete(view, true);
                    return;
                }
                return;
            }
            log.d("show(): show pending (ignore)");
        }
    }

    public final void hide(View view, boolean skipAnim) {
        if (cancelShowAnim(view, false)) {
            log.d("hide(): hide while showing (hide without anim): " + view);
            onHideComplete(view, true);
        } else if (skipAnim) {
            cancelHideAnim(view, true);
            onHideComplete(view, true);
        } else {
            if (!this.hidePending.containsKey(view)) {
                if (!initAnimation(view, this.animHide, false)) {
                    onHideComplete(view, true);
                    return;
                }
                return;
            }
            log.d("hide(): hide pending (ignoring): " + view);
        }
    }

    private boolean initAnimation(View view, Animator proto, boolean isShow) {
        if (proto == null) {
            return false;
        }
        log.d("initAnimation(): starting; is show: " + isShow);
        if (isShow) {
            startAnim(this.showPending, view, proto, new AnimListener(view, isShow));
            return true;
        }
        startAnim(this.hidePending, view, proto, new AnimListener(view, isShow));
        return true;
    }

    private static void startAnim(Map<View, Animator> map, View view, Animator proto, AnimListener listener) {
        Animator anim = proto.clone();
        map.put(view, anim);
        anim.addListener(listener);
        anim.setTarget(view);
        anim.start();
    }

    private static boolean cancelAnim(Map<View, Animator> map, View view, boolean resetToEnd) {
        Animator anim = map.get(view);
        if (anim == null) {
            return false;
        }
        map.remove(view);
        if (resetToEnd) {
            log.d("cancelAnim(): reset to end");
            anim.end();
        } else if (anim instanceof ValueAnimator) {
            log.d("cancelAnim(): reset to start");
            ((ValueAnimator) anim).reverse();
            anim.end();
        } else {
            log.d("cancelAnim(): stop at current step");
            anim.cancel();
        }
        return true;
    }

    private boolean cancelHideAnim(View view, boolean resetToEnd) {
        return cancelAnim(this.hidePending, view, resetToEnd);
    }

    private boolean cancelShowAnim(View view, boolean resetToEnd) {
        return cancelAnim(this.showPending, view, resetToEnd);
    }
}
