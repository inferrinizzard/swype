package com.bumptech.glide.request;

/* loaded from: classes.dex */
public final class ThumbnailRequestCoordinator implements Request, RequestCoordinator {
    private RequestCoordinator coordinator;
    private Request full;
    private Request thumb;

    public ThumbnailRequestCoordinator() {
        this(null);
    }

    public ThumbnailRequestCoordinator(RequestCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    public final void setRequests(Request full, Request thumb) {
        this.full = full;
        this.thumb = thumb;
    }

    @Override // com.bumptech.glide.request.RequestCoordinator
    public final void onRequestSuccess(Request request) {
        if (!request.equals(this.thumb)) {
            if (this.coordinator != null) {
                this.coordinator.onRequestSuccess(this);
            }
            if (!this.thumb.isComplete()) {
                this.thumb.clear();
            }
        }
    }

    @Override // com.bumptech.glide.request.Request
    public final void begin() {
        if (!this.thumb.isRunning()) {
            this.thumb.begin();
        }
        if (!this.full.isRunning()) {
            this.full.begin();
        }
    }

    @Override // com.bumptech.glide.request.Request
    public final void pause() {
        this.full.pause();
        this.thumb.pause();
    }

    @Override // com.bumptech.glide.request.Request
    public final void clear() {
        this.thumb.clear();
        this.full.clear();
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isRunning() {
        return this.full.isRunning();
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isComplete() {
        return this.full.isComplete() || this.thumb.isComplete();
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isResourceSet() {
        return this.full.isResourceSet() || this.thumb.isResourceSet();
    }

    @Override // com.bumptech.glide.request.Request
    public final boolean isCancelled() {
        return this.full.isCancelled();
    }

    @Override // com.bumptech.glide.request.Request
    public final void recycle() {
        this.full.recycle();
        this.thumb.recycle();
    }

    @Override // com.bumptech.glide.request.RequestCoordinator
    public final boolean canSetImage(Request request) {
        return (this.coordinator == null || this.coordinator.canSetImage(this)) && (request.equals(this.full) || !this.full.isResourceSet());
    }

    @Override // com.bumptech.glide.request.RequestCoordinator
    public final boolean canNotifyStatusChanged(Request request) {
        return (this.coordinator == null || this.coordinator.canNotifyStatusChanged(this)) && request.equals(this.full) && !isAnyResourceSet();
    }

    @Override // com.bumptech.glide.request.RequestCoordinator
    public final boolean isAnyResourceSet() {
        return (this.coordinator != null && this.coordinator.isAnyResourceSet()) || isResourceSet();
    }
}
