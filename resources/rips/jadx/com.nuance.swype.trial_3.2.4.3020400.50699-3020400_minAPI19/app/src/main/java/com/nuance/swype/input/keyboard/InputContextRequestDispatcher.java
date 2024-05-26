package com.nuance.swype.input.keyboard;

import com.nuance.input.swypecorelib.InputContextRequest;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class InputContextRequestDispatcher implements InputContextRequest {
    private DefaultInputContextRequestHandler defaultRequestHandler = new DefaultInputContextRequestHandler();
    private InputContextRequestHandler requestHandler;
    private static LogManager.Log log = LogManager.getLog(InputContextRequestDispatcher.class.getSimpleName());
    private static final InputContextRequestDispatcher INPUTCONTEXTREQUESTDISPATCHER = new InputContextRequestDispatcher(null);

    /* loaded from: classes.dex */
    public interface InputContextRequestHandler {
        boolean autoAccept(boolean z);

        char[] getAutoCapitalizationTextBuffer(int i);

        char[] getContextBuffer(int i);
    }

    public static InputContextRequestDispatcher getDispatcherInstance() {
        return INPUTCONTEXTREQUESTDISPATCHER;
    }

    private InputContextRequestDispatcher(InputContextRequestHandler requestHandler) {
        setHandler(requestHandler);
    }

    public final InputContextRequestDispatcher setHandler(InputContextRequestHandler requestHandler) {
        if (requestHandler != null) {
            this.requestHandler = requestHandler;
        } else {
            this.requestHandler = this.defaultRequestHandler;
        }
        return this;
    }

    @Override // com.nuance.input.swypecorelib.InputContextRequest
    public final char[] getContextBuffer(int maxBufferLen) {
        char[] contextBuffer = this.requestHandler.getContextBuffer(maxBufferLen);
        LogManager.Log log2 = log;
        Object[] objArr = new Object[4];
        objArr[0] = "getContextBuffer(";
        objArr[1] = Integer.valueOf(maxBufferLen);
        objArr[2] = "): contextBuffer = ";
        objArr[3] = contextBuffer == null ? "EMPTY" : new String(contextBuffer);
        log2.d(objArr);
        return contextBuffer;
    }

    @Override // com.nuance.input.swypecorelib.InputContextRequest
    public final char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
        char[] contextBuffer = this.requestHandler.getAutoCapitalizationTextBuffer(maxBufferLen);
        LogManager.Log log2 = log;
        Object[] objArr = new Object[4];
        objArr[0] = "getAutoCapitalizationTextBuffer(";
        objArr[1] = Integer.valueOf(maxBufferLen);
        objArr[2] = "): contextBuffer = ";
        objArr[3] = contextBuffer == null ? "EMPTY" : new String(contextBuffer);
        log2.d(objArr);
        return contextBuffer;
    }

    @Override // com.nuance.input.swypecorelib.InputContextRequest
    public final boolean autoAccept(boolean addSeparator) {
        log.d("autoAccept(", Boolean.valueOf(addSeparator), ")");
        return this.requestHandler.autoAccept(addSeparator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DefaultInputContextRequestHandler implements InputContextRequestHandler {
        private DefaultInputContextRequestHandler() {
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getContextBuffer(int maxBufferLen) {
            InputContextRequestDispatcher.log.d("DefaultInputContextRequestHandler.getContextBuffer()");
            return null;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
            InputContextRequestDispatcher.log.d("DefaultInputContextRequestHandler.getAutoCapitalizationTextBuffer()");
            return null;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public boolean autoAccept(boolean addSeparator) {
            InputContextRequestDispatcher.log.d("DefaultInputContextRequestHandler.autoAccept(", Boolean.valueOf(addSeparator), ")");
            return false;
        }
    }
}
