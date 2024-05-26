package com.nuance.nmdp.speechkit.util.parsers;

/* loaded from: classes.dex */
public interface IParser<SourceType, ResultType> {
    boolean expectMore();

    ResultType getParsed();

    boolean parse(SourceType sourcetype);
}
