package com.nuance.swype.input;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteAlpha;
import com.nuance.input.swypecorelib.T9WriteChinese;
import com.nuance.input.swypecorelib.T9WriteJapanese;
import com.nuance.input.swypecorelib.T9WriteKorean;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9CoreJapaneseInput;
import com.nuance.input.swypecorelib.XT9CoreKoreanInput;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.chinese.ChineseOneStepPYContainer;
import com.nuance.swype.input.japanese.JapaneseWordPageContainer;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class KeyboardInputInflater {
    public static final String ALPHA_HANDWRITING = "Alpha.HandWriting";
    public static final String ALPHA_KEYBOARDINPUT = "Alpha.KeyboardInput";
    public static final String CHINESEFS_HANDWRITING = "Chinesefs.handwriting";
    public static final String CHINESE_HANDWRITING = "Chinese.HandWriting";
    public static final String CHINESE_INPUT = "Chinese.input";
    public static final String JAPANESE_HANDWRITING = "Japanese.HandWriting";
    public static final String JAPANESE_INPUT = "Japanese.Input";
    public static final String KOREAN_HANDWRITING = "Korean.HandWriting";
    public static final String KOREAN_INPUT = "Korean.Input";
    public static final String NO_INPUTVIEW = "NO_VIEW";
    protected static final LogManager.Log log = LogManager.getLog("KeyboardInputInflater");
    protected static final LogManager.Trace trace = LogManager.getTrace();
    private IME ime;
    private final Map<String, InputViewHolder> inputViewMap = new HashMap();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InputViewHolder {
        private final View container;
        private final InputView inputView;
        private final T9Write t9write;
        private final XT9CoreInput xt9coreinput;

        InputViewHolder(View container, InputView inputView, XT9CoreInput xt9coreinput, T9Write t9write) {
            this.container = container;
            this.inputView = inputView;
            this.xt9coreinput = xt9coreinput;
            this.t9write = t9write;
        }
    }

    public KeyboardInputInflater(IME ime) {
        this.ime = ime;
    }

    public boolean isEmpty() {
        return this.inputViewMap.isEmpty();
    }

    public void reset() {
        Connect.from(this.ime).clearPostStartEvents();
        this.inputViewMap.clear();
    }

    public List<InputView> getAllInputViews() {
        List<InputView> lists = new ArrayList<>();
        for (Map.Entry<String, InputViewHolder> entry : this.inputViewMap.entrySet()) {
            lists.add(entry.getValue().inputView);
        }
        return lists;
    }

    public void callAllInputViewToDestroy(boolean destroyCore) {
        log.d("#callAllInputViewToDestroy() destroyCore = ", Boolean.valueOf(destroyCore));
        for (Map.Entry<String, InputViewHolder> entry : this.inputViewMap.entrySet()) {
            entry.getValue().inputView.destroy();
            if (destroyCore) {
                entry.getValue().xt9coreinput.destroySession();
                if (entry.getValue().t9write != null) {
                    entry.getValue().t9write.destroySession();
                }
            }
        }
    }

    public void callAllInputViewToFinish() {
        Iterator<Map.Entry<String, InputViewHolder>> it = this.inputViewMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().inputView.finishInput();
        }
    }

    public InputView getInputView(String inputViewName) {
        if (!this.inputViewMap.containsKey(inputViewName)) {
            return null;
        }
        InputView inputView = this.inputViewMap.get(inputViewName).inputView;
        if (!inputView.isDrawBufferManagerSet()) {
            DrawBufferManager bufferManager = IMEApplication.from(this.ime).getDrawBufferManager();
            inputView.setDrawBufferManager(bufferManager);
            return inputView;
        }
        return inputView;
    }

    public View inflateKeyboardInput(String inputViewName) {
        InputViewHolder holder;
        if (IMEApplication.from(this.ime).checkThemeChanged()) {
            callAllInputViewToFinish();
            this.ime.resetContainerView();
            reset();
        }
        InputViewHolder holder2 = this.inputViewMap.get(inputViewName);
        if (holder2 != null) {
            return holder2.container;
        }
        if (ALPHA_KEYBOARDINPUT.equals(inputViewName)) {
            holder = inflateAlphaKeyboardInput();
        } else if (ALPHA_HANDWRITING.equals(inputViewName)) {
            holder = inflateAlphaHandwritingInput();
        } else if (CHINESE_INPUT.equals(inputViewName)) {
            holder = inflateChineseKeyboardInput();
        } else if (CHINESE_HANDWRITING.equals(inputViewName)) {
            holder = inflateChineseHandWriting();
        } else if (CHINESEFS_HANDWRITING.equals(inputViewName)) {
            holder = inflateChineseFSHandWritingInput();
        } else if (KOREAN_INPUT.equals(inputViewName)) {
            holder = inflateKoreanKeyboardInput();
        } else if (KOREAN_HANDWRITING.equals(inputViewName)) {
            holder = inflateKoreanHandwritingInput();
        } else if (JAPANESE_INPUT.equals(inputViewName)) {
            holder = inflateJapaneseKeyboardInput();
        } else if (JAPANESE_HANDWRITING.equals(inputViewName)) {
            holder = inflateJapaneseHandwritingInput();
        } else {
            throw new IllegalArgumentException(inputViewName + " is not a valid input view name");
        }
        this.inputViewMap.put(inputViewName, holder);
        return holder.container;
    }

    private InputViewHolder inflateAlphaKeyboardInput() {
        XT9CoreAlphaInput alphaInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreAlphaInputSession();
        InputViewHolder holder = inflateSimpleKeyboard(R.layout.alpha_input, alphaInput, null);
        holder.inputView.create(this.ime, alphaInput, IMEApplication.from(this.ime).getSpeechWrapper());
        return holder;
    }

    private InputViewHolder inflateAlphaHandwritingInput() {
        View container = inflate(R.layout.alpha_handwriting_container);
        AbstractHandWritingContainer hwrContainer = (AbstractHandWritingContainer) container;
        hwrContainer.initViews();
        XT9CoreAlphaInput alphaInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreAlphaInputSession();
        T9WriteAlpha t9write = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getT9WriteAlphaSession();
        t9write.enableInstantGesture(UserPreferences.from(this.ime).isInstantGestureEnabled());
        hwrContainer.getInputView().create(this.ime, alphaInput, t9write, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, hwrContainer.getInputView(), alphaInput, t9write);
    }

    private InputViewHolder inflateChineseKeyboardInput() {
        View container = inflate(R.layout.chinese_onestep_pinyin_container);
        ChineseOneStepPYContainer onesteppinyinContainer = (ChineseOneStepPYContainer) container;
        onesteppinyinContainer.initViews();
        XT9CoreChineseInput chineseInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
        T9WriteChinese t9write = (T9WriteChinese) IMEApplication.from(this.ime).getSwypeCoreLibMgr().getT9WriteChineseSession();
        onesteppinyinContainer.getInputView().create(this.ime, chineseInput, t9write, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, onesteppinyinContainer.getInputView(), chineseInput, t9write);
    }

    private InputViewHolder inflateChineseFSHandWritingInput() {
        View container = inflate(R.layout.chinese_fs_handwriting_container);
        AbstractHandWritingContainer cnFScontainer = (AbstractHandWritingContainer) container;
        cnFScontainer.initViews();
        XT9CoreChineseInput chineseInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
        T9WriteChinese t9write = (T9WriteChinese) IMEApplication.from(this.ime).getSwypeCoreLibMgr().getT9WriteChineseSession();
        cnFScontainer.getInputView().create(this.ime, chineseInput, t9write, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, cnFScontainer.getInputView(), chineseInput, t9write);
    }

    private InputViewHolder inflateChineseHandWriting() {
        View container = inflate(R.layout.chinese_handwriting_container);
        AbstractHandWritingContainer chHWcontainer = (AbstractHandWritingContainer) container;
        chHWcontainer.initViews();
        XT9CoreChineseInput chineseInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
        T9WriteChinese t9write = (T9WriteChinese) IMEApplication.from(this.ime).getSwypeCoreLibMgr().getT9WriteChineseSession();
        chHWcontainer.getInputView().create(this.ime, chineseInput, t9write, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, chHWcontainer.getInputView(), chineseInput, t9write);
    }

    private InputViewHolder inflateKoreanKeyboardInput() {
        XT9CoreKoreanInput koreanInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreKoreanInputSession();
        InputViewHolder holder = inflateSimpleKeyboard(R.layout.korean_input, koreanInput, null);
        holder.inputView.create(this.ime, koreanInput, IMEApplication.from(this.ime).getSpeechWrapper());
        return holder;
    }

    private InputViewHolder inflateKoreanHandwritingInput() {
        View container = inflate(R.layout.korean_handwriting_container);
        AbstractHandWritingContainer krContainer = (AbstractHandWritingContainer) container;
        krContainer.initViews();
        XT9CoreKoreanInput koreanInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreKoreanInputSession();
        T9WriteKorean t9write = (T9WriteKorean) IMEApplication.from(this.ime).getSwypeCoreLibMgr().getT9WriteKoreanSession();
        krContainer.getInputView().create(this.ime, koreanInput, t9write, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, krContainer.getInputView(), koreanInput, t9write);
    }

    private InputViewHolder inflateJapaneseKeyboardInput() {
        View container = inflate(R.layout.japanese_wordlist_page);
        JapaneseWordPageContainer wordpageContainer = (JapaneseWordPageContainer) container;
        wordpageContainer.initViews();
        XT9CoreJapaneseInput japaneseInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreJapaneseInputSession();
        wordpageContainer.getInputView().create(this.ime, japaneseInput, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, wordpageContainer.getInputView(), japaneseInput, null);
    }

    private InputViewHolder inflateJapaneseHandwritingInput() {
        View container = inflate(R.layout.japanese_handwriting_container);
        AbstractHandWritingContainer JpContainer = (AbstractHandWritingContainer) container;
        JpContainer.initViews();
        XT9CoreJapaneseInput japaneseInput = IMEApplication.from(this.ime).getSwypeCoreLibMgr().getXT9CoreJapaneseInputSession();
        T9WriteJapanese t9write = (T9WriteJapanese) IMEApplication.from(this.ime).getSwypeCoreLibMgr().getT9WriteJapaneseSession();
        JpContainer.getInputView().create(this.ime, japaneseInput, t9write, IMEApplication.from(this.ime).getSpeechWrapper());
        return new InputViewHolder(container, JpContainer.getInputView(), japaneseInput, t9write);
    }

    private InputViewHolder inflateSimpleKeyboard(int layoutId, XT9CoreInput coreinput, T9Write t9write) {
        View view = inflate(layoutId);
        InputView inputView = (InputView) view;
        return new InputViewHolder(view, inputView, coreinput, t9write);
    }

    private View inflate(int layoutId) {
        LayoutInflater inflater = IMEApplication.from(this.ime).getThemedLayoutInflater(this.ime.getLayoutInflater());
        IMEApplication.from(this.ime).getThemeLoader().setLayoutInflaterFactory(inflater);
        View view = inflater.inflate(layoutId, (ViewGroup) null);
        IMEApplication.from(this.ime).getThemeLoader().applyTheme(view);
        return view;
    }
}
