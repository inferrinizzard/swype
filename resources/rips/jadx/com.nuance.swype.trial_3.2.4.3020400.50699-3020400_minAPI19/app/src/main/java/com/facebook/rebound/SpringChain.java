package com.facebook.rebound;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class SpringChain implements SpringListener {
    private static final int DEFAULT_ATTACHMENT_FRICTION = 10;
    private static final int DEFAULT_ATTACHMENT_TENSION = 70;
    private static final int DEFAULT_MAIN_FRICTION = 6;
    private static final int DEFAULT_MAIN_TENSION = 40;
    private final SpringConfig mAttachmentSpringConfig;
    private int mControlSpringIndex;
    private final CopyOnWriteArrayList<SpringListener> mListeners;
    private final SpringConfig mMainSpringConfig;
    private final SpringSystem mSpringSystem;
    private final CopyOnWriteArrayList<Spring> mSprings;
    private static final SpringConfigRegistry registry = SpringConfigRegistry.getInstance();
    private static int id = 0;

    public static SpringChain create() {
        return new SpringChain();
    }

    public static SpringChain create(int mainTension, int mainFriction, int attachmentTension, int attachmentFriction) {
        return new SpringChain(mainTension, mainFriction, attachmentTension, attachmentFriction);
    }

    private SpringChain() {
        this(40, 6, 70, 10);
    }

    private SpringChain(int mainTension, int mainFriction, int attachmentTension, int attachmentFriction) {
        this.mSpringSystem = SpringSystem.create();
        this.mListeners = new CopyOnWriteArrayList<>();
        this.mSprings = new CopyOnWriteArrayList<>();
        this.mControlSpringIndex = -1;
        this.mMainSpringConfig = SpringConfig.fromOrigamiTensionAndFriction(mainTension, mainFriction);
        this.mAttachmentSpringConfig = SpringConfig.fromOrigamiTensionAndFriction(attachmentTension, attachmentFriction);
        SpringConfigRegistry springConfigRegistry = registry;
        SpringConfig springConfig = this.mMainSpringConfig;
        StringBuilder sb = new StringBuilder("main spring ");
        int i = id;
        id = i + 1;
        springConfigRegistry.addSpringConfig(springConfig, sb.append(i).toString());
        SpringConfigRegistry springConfigRegistry2 = registry;
        SpringConfig springConfig2 = this.mAttachmentSpringConfig;
        StringBuilder sb2 = new StringBuilder("attachment spring ");
        int i2 = id;
        id = i2 + 1;
        springConfigRegistry2.addSpringConfig(springConfig2, sb2.append(i2).toString());
    }

    public SpringConfig getMainSpringConfig() {
        return this.mMainSpringConfig;
    }

    public SpringConfig getAttachmentSpringConfig() {
        return this.mAttachmentSpringConfig;
    }

    public SpringChain addSpring(SpringListener listener) {
        Spring spring = this.mSpringSystem.createSpring().addListener(this).setSpringConfig(this.mAttachmentSpringConfig);
        this.mSprings.add(spring);
        this.mListeners.add(listener);
        return this;
    }

    public SpringChain setControlSpringIndex(int i) {
        this.mControlSpringIndex = i;
        if (this.mSprings.get(this.mControlSpringIndex) == null) {
            return null;
        }
        Iterator i$ = this.mSpringSystem.getAllSprings().iterator();
        while (i$.hasNext()) {
            i$.next().setSpringConfig(this.mAttachmentSpringConfig);
        }
        getControlSpring().setSpringConfig(this.mMainSpringConfig);
        return this;
    }

    public Spring getControlSpring() {
        return this.mSprings.get(this.mControlSpringIndex);
    }

    public List<Spring> getAllSprings() {
        return this.mSprings;
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringUpdate(Spring spring) {
        int idx = this.mSprings.indexOf(spring);
        SpringListener listener = this.mListeners.get(idx);
        int above = -1;
        int below = -1;
        if (idx == this.mControlSpringIndex) {
            below = idx - 1;
            above = idx + 1;
        } else if (idx < this.mControlSpringIndex) {
            below = idx - 1;
        } else if (idx > this.mControlSpringIndex) {
            above = idx + 1;
        }
        if (above >= 0 && above < this.mSprings.size()) {
            this.mSprings.get(above).setEndValue(spring.getCurrentValue());
        }
        if (below >= 0 && below < this.mSprings.size()) {
            this.mSprings.get(below).setEndValue(spring.getCurrentValue());
        }
        listener.onSpringUpdate(spring);
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringAtRest(Spring spring) {
        int idx = this.mSprings.indexOf(spring);
        this.mListeners.get(idx).onSpringAtRest(spring);
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringActivate(Spring spring) {
        int idx = this.mSprings.indexOf(spring);
        this.mListeners.get(idx).onSpringActivate(spring);
    }

    @Override // com.facebook.rebound.SpringListener
    public void onSpringEndStateChange(Spring spring) {
        int idx = this.mSprings.indexOf(spring);
        this.mListeners.get(idx).onSpringEndStateChange(spring);
    }
}
