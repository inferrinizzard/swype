package io.fabric.sdk.android;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.concurrency.PriorityThreadPoolExecutor;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class Fabric {
    static final Logger DEFAULT_LOGGER = new DefaultLogger((byte) 0);
    static volatile Fabric singleton;
    public WeakReference<Activity> activity;
    private ActivityLifecycleManager activityLifecycleManager;
    private final Context context;
    final boolean debuggable;
    public final ExecutorService executorService;
    private final IdManager idManager;
    private final InitializationCallback<Fabric> initializationCallback;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private final InitializationCallback<?> kitInitializationCallback;
    private final Map<Class<? extends Kit>, Kit> kits;
    final Logger logger;
    private final Handler mainHandler;
    private Onboarding onboarding;

    /* loaded from: classes.dex */
    public static class Builder {
        String appIdentifier;
        String appInstallIdentifier;
        final Context context;
        boolean debuggable;
        Handler handler;
        InitializationCallback<Fabric> initializationCallback;
        Kit[] kits;
        Logger logger;
        PriorityThreadPoolExecutor threadPoolExecutor;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }
    }

    private Fabric(Context context, Map<Class<? extends Kit>, Kit> kits, PriorityThreadPoolExecutor threadPoolExecutor, Handler mainHandler, Logger logger, boolean debuggable, InitializationCallback callback, IdManager idManager) {
        this.context = context;
        this.kits = kits;
        this.executorService = threadPoolExecutor;
        this.mainHandler = mainHandler;
        this.logger = logger;
        this.debuggable = debuggable;
        this.initializationCallback = callback;
        final int size = kits.size();
        this.kitInitializationCallback = new InitializationCallback() { // from class: io.fabric.sdk.android.Fabric.2
            final CountDownLatch kitInitializedLatch;

            {
                this.kitInitializedLatch = new CountDownLatch(size);
            }

            @Override // io.fabric.sdk.android.InitializationCallback
            public final void success$5d527811() {
                this.kitInitializedLatch.countDown();
                if (this.kitInitializedLatch.getCount() == 0) {
                    Fabric.this.initialized.set(true);
                    Fabric.this.initializationCallback.success$5d527811();
                }
            }

            @Override // io.fabric.sdk.android.InitializationCallback
            public final void failure(Exception exception) {
                Fabric.this.initializationCallback.failure(exception);
            }
        };
        this.idManager = idManager;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: ProcessVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Method arg registers not loaded: io.fabric.sdk.android.ActivityLifecycleManager.ActivityLifecycleCallbacksWrapper.1.<init>(io.fabric.sdk.android.ActivityLifecycleManager$ActivityLifecycleCallbacksWrapper, io.fabric.sdk.android.ActivityLifecycleManager$Callbacks):void, class status: GENERATED_AND_UNLOADED
        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:289)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.isArgUnused(ProcessVariables.java:146)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.lambda$isVarUnused$0(ProcessVariables.java:131)
        	at jadx.core.utils.ListUtils.allMatch(ListUtils.java:172)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.isVarUnused(ProcessVariables.java:131)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.processBlock(ProcessVariables.java:82)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:64)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.removeUnusedResults(ProcessVariables.java:73)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.visit(ProcessVariables.java:48)
        */
    public static io.fabric.sdk.android.Fabric with(android.content.Context r10, io.fabric.sdk.android.Kit... r11) {
        /*
            Method dump skipped, instructions count: 232
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.Fabric.with(android.content.Context, io.fabric.sdk.android.Kit[]):io.fabric.sdk.android.Fabric");
    }

    public final Fabric setCurrentActivity(Activity activity) {
        this.activity = new WeakReference<>(activity);
        return this;
    }

    private static void addAnnotatedDependencies(Map<Class<? extends Kit>, Kit> kits, Kit dependentKit) {
        DependsOn dependsOn = (DependsOn) dependentKit.getClass().getAnnotation(DependsOn.class);
        if (dependsOn != null) {
            Class<?>[] arr$ = dependsOn.value();
            for (Class<?> dependency : arr$) {
                if (dependency.isInterface()) {
                    for (Kit kit : kits.values()) {
                        if (dependency.isAssignableFrom(kit.getClass())) {
                            dependentKit.initializationTask.addDependency(kit.initializationTask);
                        }
                    }
                } else {
                    if (kits.get(dependency) == null) {
                        throw new UnmetDependencyException("Referenced Kit was null, does the kit exist?");
                    }
                    dependentKit.initializationTask.addDependency(kits.get(dependency).initializationTask);
                }
            }
        }
    }

    public static Logger getLogger() {
        return singleton == null ? DEFAULT_LOGGER : singleton.logger;
    }

    public static boolean isDebuggable() {
        if (singleton == null) {
            return false;
        }
        return singleton.debuggable;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void addToKitMap(Map<Class<? extends Kit>, Kit> map, Collection<? extends Kit> kits) {
        for (Object obj : kits) {
            map.put(obj.getClass(), obj);
            if (obj instanceof KitGroup) {
                addToKitMap(map, ((KitGroup) obj).getKits());
            }
        }
    }

    private void initializeKits(Context context) {
        StringBuilder initInfo;
        Collection<Kit> providedKits = this.kits.values();
        this.onboarding = new Onboarding(providedKits);
        List<Kit> kits = new ArrayList<>(providedKits);
        Collections.sort(kits);
        this.onboarding.injectParameters(context, this, InitializationCallback.EMPTY, this.idManager);
        Iterator i$ = kits.iterator();
        while (i$.hasNext()) {
            i$.next().injectParameters(context, this, this.kitInitializationCallback, this.idManager);
        }
        this.onboarding.initialize();
        if (getLogger().isLoggable$505cff18(3)) {
            initInfo = new StringBuilder("Initializing io.fabric.sdk.android:fabric [Version: 1.2.0.37], with the following kits:\n");
        } else {
            initInfo = null;
        }
        for (Kit kit : kits) {
            kit.initializationTask.addDependency(this.onboarding.initializationTask);
            addAnnotatedDependencies(this.kits, kit);
            kit.initialize();
            if (initInfo != null) {
                initInfo.append(kit.getIdentifier()).append(" [Version: ").append(kit.getVersion()).append("]\n");
            }
        }
        if (initInfo != null) {
            getLogger();
        }
    }

    public static <T extends Kit> T getKit(Class<T> cls) {
        if (singleton != null) {
            return (T) singleton.kits.get(cls);
        }
        throw new IllegalStateException("Must Initialize Fabric before using singleton()");
    }
}
