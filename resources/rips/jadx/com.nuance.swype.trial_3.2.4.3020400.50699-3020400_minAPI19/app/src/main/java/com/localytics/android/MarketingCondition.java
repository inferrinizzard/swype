package com.localytics.android;

import com.localytics.android.Localytics;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/* loaded from: classes.dex */
final class MarketingCondition {
    private final String mName;
    private final Opt mOpt;
    private String mPkgName;
    private final Vector<String> mValues;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Opt {
        INVALID,
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GREATER_THEN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        BETWEEN,
        IN_LIST
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarketingCondition(String name, String operator, Vector<String> values) {
        this.mName = name;
        this.mOpt = stringToOperator(operator);
        this.mValues = values;
    }

    private Opt stringToOperator(String operator) {
        if (operator.equals("eq")) {
            return Opt.EQUAL;
        }
        if (operator.equals("neq")) {
            return Opt.NOT_EQUAL;
        }
        if (operator.equals("gt")) {
            return Opt.GREATER_THAN;
        }
        if (operator.equals("gte")) {
            return Opt.GREATER_THEN_OR_EQUAL;
        }
        if (operator.equals("lt")) {
            return Opt.LESS_THAN;
        }
        if (operator.equals("lte")) {
            return Opt.LESS_THAN_OR_EQUAL;
        }
        if (operator.equals("btw")) {
            return Opt.BETWEEN;
        }
        if (operator.equals("in")) {
            return Opt.IN_LIST;
        }
        return Opt.INVALID;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setPackageName(String pkgName) {
        this.mPkgName = pkgName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isSatisfiedByAttributes(Map<String, String> attributes) {
        if (attributes == null) {
            return false;
        }
        Object attributeValue = attributes.get(this.mName);
        if (attributeValue == null) {
            attributeValue = attributes.get(this.mPkgName + ":" + this.mName);
        }
        if (attributeValue == null) {
            Localytics.Log.w(String.format("Could not find the in-app condition %s in the attributes dictionary.", this.mName));
            return false;
        }
        if (attributeValue instanceof String) {
            boolean satisfied = isSatisfiedByString((String) attributeValue);
            return satisfied;
        }
        if (attributeValue instanceof Number) {
            boolean satisfied2 = isSatisfiedByNumber((String) attributeValue);
            return satisfied2;
        }
        Localytics.Log.w(String.format("Invalid value type %s in the attributes dictionary.", attributeValue.getClass().getCanonicalName()));
        return false;
    }

    private boolean isSatisfiedByString(String attributeValue) {
        switch (this.mOpt) {
            case EQUAL:
                boolean satisfied = attributeValue.equals(this.mValues.get(0));
                return satisfied;
            case NOT_EQUAL:
                return !attributeValue.equals(this.mValues.get(0));
            case IN_LIST:
                Iterator i$ = this.mValues.iterator();
                while (i$.hasNext()) {
                    String conditionValue = i$.next();
                    if (attributeValue.equals(conditionValue)) {
                        return true;
                    }
                }
                return false;
            default:
                boolean satisfied2 = isSatisfiedByNumber(attributeValue);
                return satisfied2;
        }
    }

    private boolean isSatisfiedByNumber(String attributeValue) {
        BigDecimal attribute = new BigDecimal(attributeValue);
        int result1 = attribute.compareTo(new BigDecimal(this.mValues.get(0)));
        int result2 = this.mValues.size() > 1 ? attribute.compareTo(new BigDecimal(this.mValues.get(1))) : 0;
        switch (this.mOpt) {
            case EQUAL:
                return result1 == 0;
            case NOT_EQUAL:
                return result1 != 0;
            case IN_LIST:
                Iterator i$ = this.mValues.iterator();
                while (i$.hasNext()) {
                    String conditionValue = i$.next();
                    if (attribute.compareTo(new BigDecimal(conditionValue)) == 0) {
                        return true;
                    }
                }
                return false;
            case GREATER_THAN:
                return result1 > 0;
            case GREATER_THEN_OR_EQUAL:
                return result1 >= 0;
            case LESS_THAN:
                return result1 < 0;
            case LESS_THAN_OR_EQUAL:
                return result1 <= 0;
            case BETWEEN:
                return result1 >= 0 && result2 <= 0;
            default:
                return false;
        }
    }

    private String operatorToString(Opt opt) {
        switch (opt) {
            case EQUAL:
                return "is equal to";
            case NOT_EQUAL:
                return "not equal to";
            case IN_LIST:
                return "is a member of the list";
            case GREATER_THAN:
                return "is greater than";
            case GREATER_THEN_OR_EQUAL:
                return "is greater than or equal to";
            case LESS_THAN:
                return "is less than";
            case LESS_THAN_OR_EQUAL:
                return "is less than or equal to";
            case BETWEEN:
                return "is in between values";
            default:
                return "INVALID OPERATOR";
        }
    }
}
