package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class TextBuffer {
    static final char[] NO_CHARS = new char[0];
    public final BufferRecycler _allocator;
    public char[] _currentSegment;
    public int _currentSize;
    public boolean _hasSegments = false;
    public char[] _inputBuffer;
    public int _inputLen;
    public int _inputStart;
    public char[] _resultArray;
    public String _resultString;
    public int _segmentSize;
    private ArrayList<char[]> _segments;

    public TextBuffer(BufferRecycler allocator) {
        this._allocator = allocator;
    }

    public final void resetWithEmpty() {
        this._inputStart = -1;
        this._currentSize = 0;
        this._inputLen = 0;
        this._inputBuffer = null;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        }
    }

    public final void resetWithShared(char[] buf, int start, int len) {
        this._resultString = null;
        this._resultArray = null;
        this._inputBuffer = buf;
        this._inputStart = start;
        this._inputLen = len;
        if (this._hasSegments) {
            clearSegments();
        }
    }

    public final char[] findBuffer(int needed) {
        return this._allocator != null ? this._allocator.allocCharBuffer(BufferRecycler.CharBufferType.TEXT_BUFFER, needed) : new char[Math.max(needed, 1000)];
    }

    public final void clearSegments() {
        this._hasSegments = false;
        this._segments.clear();
        this._segmentSize = 0;
        this._currentSize = 0;
    }

    public final int size() {
        if (this._inputStart >= 0) {
            return this._inputLen;
        }
        if (this._resultArray != null) {
            return this._resultArray.length;
        }
        if (this._resultString != null) {
            return this._resultString.length();
        }
        return this._segmentSize + this._currentSize;
    }

    public final int getTextOffset() {
        if (this._inputStart >= 0) {
            return this._inputStart;
        }
        return 0;
    }

    public final char[] getTextBuffer() {
        if (this._inputStart >= 0) {
            return this._inputBuffer;
        }
        if (this._resultArray != null) {
            return this._resultArray;
        }
        if (this._resultString != null) {
            char[] charArray = this._resultString.toCharArray();
            this._resultArray = charArray;
            return charArray;
        }
        if (!this._hasSegments) {
            return this._currentSegment;
        }
        return contentsAsArray();
    }

    public final String contentsAsString() {
        if (this._resultString == null) {
            if (this._resultArray != null) {
                this._resultString = new String(this._resultArray);
            } else if (this._inputStart >= 0) {
                if (this._inputLen <= 0) {
                    this._resultString = "";
                    return "";
                }
                this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
            } else {
                int segLen = this._segmentSize;
                int currLen = this._currentSize;
                if (segLen == 0) {
                    this._resultString = currLen == 0 ? "" : new String(this._currentSegment, 0, currLen);
                } else {
                    StringBuilder sb = new StringBuilder(segLen + currLen);
                    if (this._segments != null) {
                        int len = this._segments.size();
                        for (int i = 0; i < len; i++) {
                            char[] curr = this._segments.get(i);
                            sb.append(curr, 0, curr.length);
                        }
                    }
                    sb.append(this._currentSegment, 0, this._currentSize);
                    this._resultString = sb.toString();
                }
            }
        }
        return this._resultString;
    }

    public final char[] contentsAsArray() {
        int i;
        char[] cArr;
        char[] result = this._resultArray;
        if (result == null) {
            if (this._resultString != null) {
                result = this._resultString.toCharArray();
            } else if (this._inputStart >= 0) {
                if (this._inputLen <= 0) {
                    result = NO_CHARS;
                } else {
                    cArr = new char[this._inputLen];
                    System.arraycopy(this._inputBuffer, this._inputStart, cArr, 0, this._inputLen);
                    result = cArr;
                }
            } else {
                int size = size();
                if (size <= 0) {
                    result = NO_CHARS;
                } else {
                    char[] cArr2 = new char[size];
                    if (this._segments != null) {
                        int size2 = this._segments.size();
                        int i2 = 0;
                        for (int i3 = 0; i3 < size2; i3++) {
                            char[] cArr3 = this._segments.get(i3);
                            int length = cArr3.length;
                            System.arraycopy(cArr3, 0, cArr2, i2, length);
                            i2 += length;
                        }
                        i = i2;
                    } else {
                        i = 0;
                    }
                    System.arraycopy(this._currentSegment, 0, cArr2, i, this._currentSize);
                    cArr = cArr2;
                    result = cArr;
                }
            }
            this._resultArray = result;
        }
        return result;
    }

    public final char[] getCurrentSegment() {
        if (this._inputStart >= 0) {
            unshare(1);
        } else {
            char[] curr = this._currentSegment;
            if (curr == null) {
                this._currentSegment = findBuffer(0);
            } else if (this._currentSize >= curr.length) {
                expand(1);
            }
        }
        return this._currentSegment;
    }

    public final char[] emptyAndGetCurrentSegment() {
        this._inputStart = -1;
        this._currentSize = 0;
        this._inputLen = 0;
        this._inputBuffer = null;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        }
        char[] curr = this._currentSegment;
        if (curr == null) {
            char[] curr2 = findBuffer(0);
            this._currentSegment = curr2;
            return curr2;
        }
        return curr;
    }

    public final char[] finishCurrentSegment() {
        if (this._segments == null) {
            this._segments = new ArrayList<>();
        }
        this._hasSegments = true;
        this._segments.add(this._currentSegment);
        int oldLen = this._currentSegment.length;
        this._segmentSize += oldLen;
        char[] curr = new char[Math.min((oldLen >> 1) + oldLen, HardKeyboardManager.META_META_RIGHT_ON)];
        this._currentSize = 0;
        this._currentSegment = curr;
        return curr;
    }

    public final char[] expandCurrentSegment() {
        char[] curr = this._currentSegment;
        int len = curr.length;
        int newLen = len == 262144 ? 262145 : Math.min(HardKeyboardManager.META_META_RIGHT_ON, (len >> 1) + len);
        this._currentSegment = new char[newLen];
        System.arraycopy(curr, 0, this._currentSegment, 0, len);
        return this._currentSegment;
    }

    public final String toString() {
        return contentsAsString();
    }

    public final void unshare(int needExtra) {
        int sharedLen = this._inputLen;
        this._inputLen = 0;
        char[] inputBuf = this._inputBuffer;
        this._inputBuffer = null;
        int start = this._inputStart;
        this._inputStart = -1;
        int needed = sharedLen + needExtra;
        if (this._currentSegment == null || needed > this._currentSegment.length) {
            this._currentSegment = findBuffer(needed);
        }
        if (sharedLen > 0) {
            System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
        }
        this._segmentSize = 0;
        this._currentSize = sharedLen;
    }

    public final void expand(int minNewSegmentSize) {
        if (this._segments == null) {
            this._segments = new ArrayList<>();
        }
        char[] curr = this._currentSegment;
        this._hasSegments = true;
        this._segments.add(curr);
        this._segmentSize += curr.length;
        int oldLen = curr.length;
        int sizeAddition = oldLen >> 1;
        if (sizeAddition < minNewSegmentSize) {
            sizeAddition = minNewSegmentSize;
        }
        char[] curr2 = new char[Math.min(HardKeyboardManager.META_META_RIGHT_ON, oldLen + sizeAddition)];
        this._currentSize = 0;
        this._currentSegment = curr2;
    }
}
