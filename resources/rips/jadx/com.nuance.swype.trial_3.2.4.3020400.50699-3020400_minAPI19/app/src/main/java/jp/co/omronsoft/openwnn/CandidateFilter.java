package jp.co.omronsoft.openwnn;

import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class CandidateFilter {
    public static final int FILTER_EMOJI = 1;
    public static final int FILTER_NONE = 0;
    private static final Pattern PATTERN_EMOJI = Pattern.compile("[\udbb8\udbb9\udbba\udbbb]");
    private int mFilter = 0;

    public void setFilter(int filter) {
        this.mFilter = filter;
    }

    public boolean isAllowed(WnnWord word) {
        return this.mFilter == 0 || (this.mFilter & 1) == 0 || !PATTERN_EMOJI.matcher(word.candidate).matches();
    }
}
