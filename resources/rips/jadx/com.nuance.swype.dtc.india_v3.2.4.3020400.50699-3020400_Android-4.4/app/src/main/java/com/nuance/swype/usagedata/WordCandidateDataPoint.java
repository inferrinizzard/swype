package com.nuance.swype.usagedata;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import com.nuance.connect.common.Strings;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.usagedata.SessionData;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.util.LogManager;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class WordCandidateDataPoint {
    private static final String TAG = WordCandidateDataPoint.class.getSimpleName();
    private static final LogManager.Log log = LogManager.getLog("WordCandidateDataPoint");
    private final XT9CoreAlphaInput alphaInput;
    private final Context context;
    private final String country;
    private final DisplayMetrics displayMetrics;
    private long lastSent = System.currentTimeMillis();

    public WordCandidateDataPoint(XT9CoreAlphaInput alphaInput, Context ctx, String country, DisplayMetrics displayMetrics) {
        this.alphaInput = alphaInput;
        this.context = ctx;
        this.country = country;
        this.displayMetrics = displayMetrics;
    }

    public final void write(EditorInfo editorInfo, DataPointWriter writer) {
        String str;
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastSent > IME.NEXT_SCAN_IN_MILLIS) {
            SessionData sessionData = this.alphaInput.getSessionData();
            if (sessionData.getSelectedCandidateCount() > 0) {
                JSONObject jsonObject = sessionData.getData();
                try {
                    jsonObject.put("app", editorInfo.packageName);
                    jsonObject.put("ScreenResolution", this.displayMetrics.widthPixels + "x" + this.displayMetrics.heightPixels);
                    jsonObject.put("Country", this.country);
                    int languageID = sessionData.getLanguageID();
                    if (InputMethods.from(this.context).getLanguageById(languageID) != null) {
                        str = InputMethods.from(this.context).getLanguageById(languageID).getDisplayName();
                    } else {
                        log.e("getLanguageName - invalid language ID: " + languageID);
                        str = "";
                    }
                    jsonObject.put(Strings.MESSAGE_BUNDLE_LANGUAGE, str);
                    jsonObject.put("traceDistance", getTraceDistance(sessionData));
                    writer.write(sessionData);
                } catch (JSONException ex) {
                    Log.e(TAG, ex.toString());
                }
            }
            this.lastSent = currentTime;
        }
    }

    private long getTraceDistance(SessionData sessionData) {
        long distance = 0;
        List<Point> tracePoints = sessionData.getTracePoints();
        if (tracePoints.size() > 0) {
            float startX = tracePoints.get(0).x;
            float startY = tracePoints.get(0).y;
            for (int h = 1; h < tracePoints.size(); h++) {
                Point pt = tracePoints.get(h);
                float hx = pt.x;
                float hy = pt.y;
                float dx = (hx - startX) / this.displayMetrics.xdpi;
                float dy = (hy - startY) / this.displayMetrics.ydpi;
                distance = (long) (distance + Math.sqrt((dx * dx) + (dy * dy)));
                startX = hx;
                startY = hy;
            }
        }
        return distance;
    }
}
