package com.a.a;

import java.io.InputStream;

/* loaded from: classes.dex */
public interface p<MessageType> {
    MessageType parseDelimitedFrom(InputStream inputStream) throws j;

    MessageType parseDelimitedFrom(InputStream inputStream, f fVar) throws j;

    MessageType parseFrom(c cVar) throws j;

    MessageType parseFrom(c cVar, f fVar) throws j;

    MessageType parseFrom(d dVar) throws j;

    MessageType parseFrom(d dVar, f fVar) throws j;

    MessageType parseFrom(InputStream inputStream) throws j;

    MessageType parseFrom(InputStream inputStream, f fVar) throws j;

    MessageType parseFrom(byte[] bArr) throws j;

    MessageType parseFrom(byte[] bArr, f fVar) throws j;

    MessageType parsePartialFrom(d dVar, f fVar) throws j;
}
