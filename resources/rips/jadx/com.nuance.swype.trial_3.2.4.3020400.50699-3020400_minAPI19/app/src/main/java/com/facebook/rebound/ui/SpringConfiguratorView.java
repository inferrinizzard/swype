package com.facebook.rebound.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import com.facebook.rebound.OrigamiValueConverter;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringConfigRegistry;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.nuance.swype.input.R;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class SpringConfiguratorView extends FrameLayout {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private static final float MAX_FRICTION = 50.0f;
    private static final int MAX_SEEKBAR_VAL = 100000;
    private static final float MAX_TENSION = 200.0f;
    private static final float MIN_FRICTION = 0.0f;
    private static final float MIN_TENSION = 0.0f;
    private TextView mFrictionLabel;
    private SeekBar mFrictionSeekBar;
    private final float mRevealPx;
    private final Spring mRevealerSpring;
    private SpringConfig mSelectedSpringConfig;
    private final List<SpringConfig> mSpringConfigs;
    private Spinner mSpringSelectorSpinner;
    private final float mStashPx;
    private TextView mTensionLabel;
    private SeekBar mTensionSeekBar;
    private final int mTextColor;
    private final SpinnerAdapter spinnerAdapter;
    private final SpringConfigRegistry springConfigRegistry;

    public SpringConfiguratorView(Context context) {
        this(context, null);
    }

    public SpringConfiguratorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @TargetApi(11)
    public SpringConfiguratorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSpringConfigs = new ArrayList();
        this.mTextColor = Color.argb(255, 225, 225, 225);
        SpringSystem springSystem = SpringSystem.create();
        this.springConfigRegistry = SpringConfigRegistry.getInstance();
        this.spinnerAdapter = new SpinnerAdapter(context);
        Resources resources = getResources();
        this.mRevealPx = Util.dpToPx(40.0f, resources);
        this.mStashPx = Util.dpToPx(280.0f, resources);
        this.mRevealerSpring = springSystem.createSpring();
        SpringListener revealerSpringListener = new RevealerSpringListener();
        this.mRevealerSpring.setCurrentValue(1.0d).setEndValue(1.0d).addListener(revealerSpringListener);
        addView(generateHierarchy(context));
        SeekbarListener seekbarListener = new SeekbarListener();
        this.mTensionSeekBar.setMax(100000);
        this.mTensionSeekBar.setOnSeekBarChangeListener(seekbarListener);
        this.mFrictionSeekBar.setMax(100000);
        this.mFrictionSeekBar.setOnSeekBarChangeListener(seekbarListener);
        this.mSpringSelectorSpinner.setAdapter((android.widget.SpinnerAdapter) this.spinnerAdapter);
        this.mSpringSelectorSpinner.setOnItemSelectedListener(new SpringSelectedListener());
        refreshSpringConfigurations();
        setTranslationY(this.mStashPx);
    }

    private View generateHierarchy(Context context) {
        Resources resources = getResources();
        int fivePx = Util.dpToPx(5.0f, resources);
        int tenPx = Util.dpToPx(10.0f, resources);
        int twentyPx = Util.dpToPx(20.0f, resources);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(0, -2, 1.0f);
        tableLayoutParams.setMargins(0, 0, fivePx, 0);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(Util.createLayoutParams(-1, Util.dpToPx(300.0f, resources)));
        FrameLayout frameLayout2 = new FrameLayout(context);
        FrameLayout.LayoutParams params = Util.createMatchParams();
        params.setMargins(0, twentyPx, 0, 0);
        frameLayout2.setLayoutParams(params);
        frameLayout2.setBackgroundColor(Color.argb(100, 0, 0, 0));
        frameLayout.addView(frameLayout2);
        this.mSpringSelectorSpinner = new Spinner(context, 0);
        FrameLayout.LayoutParams params2 = Util.createMatchWrapParams();
        params2.gravity = 48;
        params2.setMargins(tenPx, tenPx, tenPx, 0);
        this.mSpringSelectorSpinner.setLayoutParams(params2);
        frameLayout2.addView(this.mSpringSelectorSpinner);
        LinearLayout linearLayout = new LinearLayout(context);
        FrameLayout.LayoutParams params3 = Util.createMatchWrapParams();
        params3.setMargins(0, 0, 0, Util.dpToPx(80.0f, resources));
        params3.gravity = 80;
        linearLayout.setLayoutParams(params3);
        linearLayout.setOrientation(1);
        frameLayout2.addView(linearLayout);
        LinearLayout seekWrapper = new LinearLayout(context);
        FrameLayout.LayoutParams params4 = Util.createMatchWrapParams();
        params4.setMargins(tenPx, tenPx, tenPx, twentyPx);
        seekWrapper.setPadding(tenPx, tenPx, tenPx, tenPx);
        seekWrapper.setLayoutParams(params4);
        seekWrapper.setOrientation(0);
        linearLayout.addView(seekWrapper);
        this.mTensionSeekBar = new SeekBar(context);
        this.mTensionSeekBar.setLayoutParams(tableLayoutParams);
        seekWrapper.addView(this.mTensionSeekBar);
        this.mTensionLabel = new TextView(getContext());
        this.mTensionLabel.setTextColor(this.mTextColor);
        FrameLayout.LayoutParams params5 = Util.createLayoutParams(Util.dpToPx(MAX_FRICTION, resources), -1);
        this.mTensionLabel.setGravity(19);
        this.mTensionLabel.setLayoutParams(params5);
        this.mTensionLabel.setMaxLines(1);
        seekWrapper.addView(this.mTensionLabel);
        LinearLayout seekWrapper2 = new LinearLayout(context);
        FrameLayout.LayoutParams params6 = Util.createMatchWrapParams();
        params6.setMargins(tenPx, tenPx, tenPx, twentyPx);
        seekWrapper2.setPadding(tenPx, tenPx, tenPx, tenPx);
        seekWrapper2.setLayoutParams(params6);
        seekWrapper2.setOrientation(0);
        linearLayout.addView(seekWrapper2);
        this.mFrictionSeekBar = new SeekBar(context);
        this.mFrictionSeekBar.setLayoutParams(tableLayoutParams);
        seekWrapper2.addView(this.mFrictionSeekBar);
        this.mFrictionLabel = new TextView(getContext());
        this.mFrictionLabel.setTextColor(this.mTextColor);
        FrameLayout.LayoutParams params7 = Util.createLayoutParams(Util.dpToPx(MAX_FRICTION, resources), -1);
        this.mFrictionLabel.setGravity(19);
        this.mFrictionLabel.setLayoutParams(params7);
        this.mFrictionLabel.setMaxLines(1);
        seekWrapper2.addView(this.mFrictionLabel);
        View nub = new View(context);
        FrameLayout.LayoutParams params8 = Util.createLayoutParams(Util.dpToPx(60.0f, resources), Util.dpToPx(40.0f, resources));
        params8.gravity = 49;
        nub.setLayoutParams(params8);
        nub.setOnTouchListener(new OnNubTouchListener());
        nub.setBackgroundColor(Color.argb(255, 0, R.styleable.ThemeTemplate_btnKeyboardActionKeyNormal5rowBottom, R.styleable.ThemeTemplate_btnKeyboardNumberNormal5row));
        frameLayout.addView(nub);
        return frameLayout;
    }

    public void destroy() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
        this.mRevealerSpring.destroy();
    }

    public void refreshSpringConfigurations() {
        Map<SpringConfig, String> springConfigMap = this.springConfigRegistry.getAllSpringConfig();
        this.spinnerAdapter.clear();
        this.mSpringConfigs.clear();
        for (Map.Entry<SpringConfig, String> entry : springConfigMap.entrySet()) {
            if (entry.getKey() != SpringConfig.defaultConfig) {
                this.mSpringConfigs.add(entry.getKey());
                this.spinnerAdapter.add(entry.getValue());
            }
        }
        this.mSpringConfigs.add(SpringConfig.defaultConfig);
        this.spinnerAdapter.add(springConfigMap.get(SpringConfig.defaultConfig));
        this.spinnerAdapter.notifyDataSetChanged();
        if (this.mSpringConfigs.size() > 0) {
            this.mSpringSelectorSpinner.setSelection(0);
        }
    }

    /* loaded from: classes.dex */
    private class SpringSelectedListener implements AdapterView.OnItemSelectedListener {
        private SpringSelectedListener() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            SpringConfiguratorView.this.mSelectedSpringConfig = (SpringConfig) SpringConfiguratorView.this.mSpringConfigs.get(i);
            SpringConfiguratorView.this.updateSeekBarsForSpringConfig(SpringConfiguratorView.this.mSelectedSpringConfig);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* loaded from: classes.dex */
    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {
        private SeekbarListener() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int val, boolean b) {
            if (seekBar == SpringConfiguratorView.this.mTensionSeekBar) {
                float scaledTension = ((val * SpringConfiguratorView.MAX_TENSION) / 100000.0f) + 0.0f;
                SpringConfiguratorView.this.mSelectedSpringConfig.tension = OrigamiValueConverter.tensionFromOrigamiValue(scaledTension);
                String roundedTensionLabel = SpringConfiguratorView.DECIMAL_FORMAT.format(scaledTension);
                SpringConfiguratorView.this.mTensionLabel.setText("T:" + roundedTensionLabel);
            }
            if (seekBar == SpringConfiguratorView.this.mFrictionSeekBar) {
                float scaledFriction = ((val * SpringConfiguratorView.MAX_FRICTION) / 100000.0f) + 0.0f;
                SpringConfiguratorView.this.mSelectedSpringConfig.friction = OrigamiValueConverter.frictionFromOrigamiValue(scaledFriction);
                String roundedFrictionLabel = SpringConfiguratorView.DECIMAL_FORMAT.format(scaledFriction);
                SpringConfiguratorView.this.mFrictionLabel.setText("F:" + roundedFrictionLabel);
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSeekBarsForSpringConfig(SpringConfig springConfig) {
        int scaledTension = Math.round(((((float) OrigamiValueConverter.origamiValueFromTension(springConfig.tension)) - 0.0f) * 100000.0f) / MAX_TENSION);
        int scaledFriction = Math.round(((((float) OrigamiValueConverter.origamiValueFromFriction(springConfig.friction)) - 0.0f) * 100000.0f) / MAX_FRICTION);
        this.mTensionSeekBar.setProgress(scaledTension);
        this.mFrictionSeekBar.setProgress(scaledFriction);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OnNubTouchListener implements View.OnTouchListener {
        private OnNubTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                SpringConfiguratorView.this.togglePosition();
                return true;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void togglePosition() {
        double currentValue = this.mRevealerSpring.getEndValue();
        this.mRevealerSpring.setEndValue(currentValue == 1.0d ? 0.0d : 1.0d);
    }

    /* loaded from: classes.dex */
    private class RevealerSpringListener implements SpringListener {
        private RevealerSpringListener() {
        }

        @Override // com.facebook.rebound.SpringListener
        public void onSpringUpdate(Spring spring) {
            float val = (float) spring.getCurrentValue();
            float minTranslate = SpringConfiguratorView.this.mRevealPx;
            float range = SpringConfiguratorView.this.mStashPx - minTranslate;
            float yTranslate = (val * range) + minTranslate;
            SpringConfiguratorView.this.setTranslationY(yTranslate);
        }

        @Override // com.facebook.rebound.SpringListener
        public void onSpringAtRest(Spring spring) {
        }

        @Override // com.facebook.rebound.SpringListener
        public void onSpringActivate(Spring spring) {
        }

        @Override // com.facebook.rebound.SpringListener
        public void onSpringEndStateChange(Spring spring) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SpinnerAdapter extends BaseAdapter {
        private final Context mContext;
        private final List<String> mStrings = new ArrayList();

        public SpinnerAdapter(Context context) {
            this.mContext = context;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mStrings.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.mStrings.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void add(String string) {
            this.mStrings.add(string);
            notifyDataSetChanged();
        }

        public void clear() {
            this.mStrings.clear();
            notifyDataSetChanged();
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(this.mContext);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(-1, -1);
                textView.setLayoutParams(params);
                int twelvePx = Util.dpToPx(12.0f, SpringConfiguratorView.this.getResources());
                textView.setPadding(twelvePx, twelvePx, twelvePx, twelvePx);
                textView.setTextColor(SpringConfiguratorView.this.mTextColor);
            } else {
                textView = (TextView) convertView;
            }
            textView.setText(this.mStrings.get(position));
            return textView;
        }
    }
}
