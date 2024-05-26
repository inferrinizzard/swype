package com.nuance.android.util;

import android.content.res.XmlResourceParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class XmlResourceParserWrapper implements XmlResourceParser {
    private final XmlResourceParser target;

    public XmlResourceParserWrapper(XmlResourceParser target) {
        this.target = target;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        this.target.defineEntityReplacementText(entityName, replacementText);
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public int getAttributeCount() {
        return this.target.getAttributeCount();
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeName(int index) {
        return this.target.getAttributeName(index);
    }

    @Override // android.content.res.XmlResourceParser, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeNamespace(int index) {
        return this.target.getAttributeNamespace(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributePrefix(int index) {
        return this.target.getAttributePrefix(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeType(int index) {
        return this.target.getAttributeType(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeValue(int index) {
        return this.target.getAttributeValue(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeValue(String namespace, String name) {
        return this.target.getAttributeValue(namespace, name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getColumnNumber() {
        return this.target.getColumnNumber();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getDepth() {
        return this.target.getDepth();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getEventType() throws XmlPullParserException {
        return this.target.getEventType();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean getFeature(String name) {
        return this.target.getFeature(name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getInputEncoding() {
        return this.target.getInputEncoding();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getLineNumber() {
        return this.target.getLineNumber();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getName() {
        return this.target.getName();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace() {
        return this.target.getNamespace();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace(String prefix) {
        return this.target.getNamespace(prefix);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return this.target.getNamespaceCount(depth);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return this.target.getNamespacePrefix(pos);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return this.target.getNamespaceUri(pos);
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getPositionDescription() {
        return this.target.getPositionDescription();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getPrefix() {
        return this.target.getPrefix();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public Object getProperty(String name) {
        return this.target.getProperty(name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getText() {
        return this.target.getText();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        return this.target.getTextCharacters(holderForStartAndLength);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isAttributeDefault(int index) {
        return this.target.isAttributeDefault(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isEmptyElementTag() throws XmlPullParserException {
        return this.target.isEmptyElementTag();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isWhitespace() throws XmlPullParserException {
        return this.target.isWhitespace();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int next() throws XmlPullParserException, IOException {
        return this.target.next();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextTag() throws XmlPullParserException, IOException {
        return this.target.nextTag();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String nextText() throws XmlPullParserException, IOException {
        return this.target.nextText();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextToken() throws XmlPullParserException, IOException {
        return this.target.nextToken();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        this.target.require(type, namespace, name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        this.target.setFeature(name, state);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(Reader in) throws XmlPullParserException {
        this.target.setInput(in);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        this.target.setInput(inputStream, inputEncoding);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setProperty(String name, Object value) throws XmlPullParserException {
        this.target.setProperty(name, value);
    }

    @Override // android.util.AttributeSet
    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return this.target.getAttributeBooleanValue(index, defaultValue);
    }

    @Override // android.util.AttributeSet
    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        return this.target.getAttributeBooleanValue(namespace, attribute, defaultValue);
    }

    @Override // android.util.AttributeSet
    public float getAttributeFloatValue(int index, float defaultValue) {
        return this.target.getAttributeFloatValue(index, defaultValue);
    }

    @Override // android.util.AttributeSet
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        return this.target.getAttributeFloatValue(namespace, attribute, defaultValue);
    }

    public int getAttributeIntValue(int index, int defaultValue) {
        return this.target.getAttributeIntValue(index, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        return this.target.getAttributeIntValue(namespace, attribute, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return this.target.getAttributeListValue(index, options, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return this.target.getAttributeListValue(namespace, attribute, options, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeNameResource(int index) {
        return this.target.getAttributeNameResource(index);
    }

    @Override // android.util.AttributeSet
    public int getAttributeResourceValue(int index, int defaultValue) {
        return this.target.getAttributeResourceValue(index, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        return this.target.getAttributeResourceValue(namespace, attribute, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return this.target.getAttributeUnsignedIntValue(index, defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        return this.target.getAttributeUnsignedIntValue(namespace, attribute, defaultValue);
    }

    @Override // android.util.AttributeSet
    public String getClassAttribute() {
        return this.target.getClassAttribute();
    }

    @Override // android.util.AttributeSet
    public String getIdAttribute() {
        return this.target.getIdAttribute();
    }

    @Override // android.util.AttributeSet
    public int getIdAttributeResourceValue(int defaultValue) {
        return this.target.getIdAttributeResourceValue(defaultValue);
    }

    @Override // android.util.AttributeSet
    public int getStyleAttribute() {
        return this.target.getStyleAttribute();
    }

    @Override // android.content.res.XmlResourceParser, java.lang.AutoCloseable
    public void close() {
        this.target.close();
    }
}
