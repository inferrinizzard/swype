package jp.co.omronsoft.openwnn;

/* loaded from: classes.dex */
public class StrSegmentClause extends StrSegment {
    public WnnClause clause;

    public StrSegmentClause(WnnClause clause, int from, int to) {
        super(clause.candidate, from, to);
        this.clause = clause;
    }
}
