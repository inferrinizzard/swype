package com.nuance.swype.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Xml;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public class GestureManager {
    private static final String TAG_GESTURE = "Gesture";
    private static final String TAG_PATH = "Path";
    private static final LogManager.Log log = LogManager.getLog("GestureManager");
    private Context context;
    private List<XT9CoreInput.Gesture> gestures = new ArrayList();

    public GestureManager(Context context) {
        this.context = context;
    }

    public List<XT9CoreInput.Gesture> getGestures() {
        return this.gestures;
    }

    public boolean loadGestures() {
        return loadGestures(this.context.getResources().getXml(R.xml.gestures));
    }

    protected boolean loadGestures(XmlPullParser parser) {
        this.gestures.clear();
        int currentValue = 0;
        StringBuilder currentGesture = new StringBuilder();
        while (true) {
            try {
                int event = parser.next();
                if (event == 1) {
                    return true;
                }
                if (event == 2) {
                    String tag = parser.getName();
                    AttributeSet attr = Xml.asAttributeSet(parser);
                    if (TAG_GESTURE.equals(tag)) {
                        currentValue = getIntValue(attr);
                    } else if (TAG_PATH.equals(tag)) {
                        short code = (short) getIntValue(attr);
                        if (code != 0) {
                            currentGesture.append((char) code);
                        } else {
                            String str = getStringValue(attr);
                            if (str != null) {
                                currentGesture.append(str);
                            }
                        }
                    }
                } else if (event == 3 && TAG_GESTURE.equals(parser.getName()) && currentValue != 0 && currentGesture.length() > 0) {
                    this.gestures.add(new XT9CoreInput.Gesture((short) currentValue, currentGesture.toString().toCharArray()));
                    currentGesture.setLength(0);
                    currentValue = 0;
                }
            } catch (Exception ex) {
                log.e("Error reading gestures file", ex);
                return false;
            }
        }
    }

    protected int getIntValue(AttributeSet attr) {
        TypedArray a = this.context.obtainStyledAttributes(attr, R.styleable.Gesture);
        int code = a.getInt(R.styleable.Gesture_gestureInt, 0);
        a.recycle();
        return code;
    }

    protected String getStringValue(AttributeSet attr) {
        TypedArray a = this.context.obtainStyledAttributes(attr, R.styleable.Gesture);
        String str = a.getString(R.styleable.Gesture_gestureString);
        a.recycle();
        return str;
    }
}
