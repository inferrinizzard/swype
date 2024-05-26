package com.bumptech.glide.manager;

import com.bumptech.glide.request.Request;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class RequestTracker {
    public boolean isPaused;
    public final Set<Request> requests = Collections.newSetFromMap(new WeakHashMap());
    public final List<Request> pendingRequests = new ArrayList();
}
