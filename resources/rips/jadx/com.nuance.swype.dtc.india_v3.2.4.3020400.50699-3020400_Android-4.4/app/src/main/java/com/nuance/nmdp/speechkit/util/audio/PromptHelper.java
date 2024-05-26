package com.nuance.nmdp.speechkit.util.audio;

import com.nuance.nmdp.speechkit.util.Logger;

/* loaded from: classes.dex */
public final class PromptHelper {
    private final Object _appContext;
    private final Object _context;
    private final IPlayerHelperListener _listener;
    private final IPrompt _prompt;
    private boolean _started = false;
    private boolean _stopped = false;
    private final Object _stopSync = new Object();

    public PromptHelper(IPrompt p, Object appContext, Object context, final IPlayerHelperListener listener) {
        this._prompt = p;
        this._context = context;
        this._appContext = appContext;
        this._listener = new IPlayerHelperListener() { // from class: com.nuance.nmdp.speechkit.util.audio.PromptHelper.1
            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void error(Object context2) {
                synchronized (PromptHelper.this._stopSync) {
                    PromptHelper.this._stopped = true;
                }
                listener.error(context2);
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void started(Object context2) {
                listener.started(context2);
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void stopped(Object context2) {
                synchronized (PromptHelper.this._stopSync) {
                    PromptHelper.this._stopped = true;
                }
                listener.stopped(context2);
            }
        };
    }

    public final void start() {
        if (!this._started) {
            this._started = true;
            this._prompt.start(this._appContext, this._listener, this._context);
        } else {
            Logger.error(this, "Prompt already started");
        }
    }

    public final void stop() {
        if (this._started) {
            synchronized (this._stopSync) {
                if (!this._stopped) {
                    Logger.info(this, "Stopping prompt");
                    this._stopped = true;
                    this._prompt.stop();
                }
            }
        }
    }
}
