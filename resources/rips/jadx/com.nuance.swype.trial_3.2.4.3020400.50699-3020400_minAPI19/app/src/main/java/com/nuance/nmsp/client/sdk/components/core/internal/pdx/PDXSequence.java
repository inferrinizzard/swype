package com.nuance.nmsp.client.sdk.components.core.internal.pdx;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.swype.input.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/* loaded from: classes.dex */
public class PDXSequence extends PDXClass implements Sequence {
    private static final LogFactory.Log a = LogFactory.getLog(PDXSequence.class);
    private Vector b;

    public PDXSequence() {
        super((short) 16);
        this.b = new Vector();
    }

    public PDXSequence(byte[] bArr) {
        super((short) 16);
        this.b = new Vector();
        int i = 0;
        while (i < bArr.length) {
            int i2 = i + 1;
            int i3 = bArr[i] & 255;
            int length = getLength(bArr, i2);
            int lengthSize = i2 + getLengthSize(length);
            byte[] bArr2 = new byte[length];
            System.arraycopy(bArr, lengthSize, bArr2, 0, bArr2.length);
            i = length + lengthSize;
            switch (i3) {
                case 4:
                    this.b.addElement(new PDXByteString(bArr2));
                    break;
                case 5:
                    this.b.addElement(new PDXNull());
                    break;
                case 16:
                    this.b.addElement(new PDXSequence(bArr2));
                    break;
                case 22:
                    this.b.addElement(new PDXAsciiString(bArr2));
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop /* 192 */:
                    this.b.addElement(new PDXInteger(bArr2));
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5row /* 193 */:
                    this.b.addElement(new PDXUTF8String(bArr2));
                    break;
                case 224:
                    this.b.addElement(new PDXDictionary(bArr2));
                    break;
                default:
                    a.error("PDXSequence() Unknown PDXClass type: " + i3 + ". ");
                    break;
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public void addAsciiString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("value is null.");
        }
        this.b.addElement(new PDXAsciiString(str));
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public void addByteString(byte[] bArr) {
        if (bArr == null) {
            throw new IllegalArgumentException("value is null.");
        }
        this.b.addElement(new PDXByteString(bArr));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public void addDictionary(Dictionary dictionary) {
        if (dictionary == 0) {
            throw new IllegalArgumentException("value is null.");
        }
        if (((PDXClass) dictionary).getType() != 224) {
            a.error("PDXSequence.addDictionary() value is not a valid dictionary.");
            throw new IllegalArgumentException("value is not a valid dictionary. ");
        }
        this.b.addElement(dictionary);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public void addInteger(int i) {
        this.b.addElement(new PDXInteger(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public void addSequence(Sequence sequence) {
        if (sequence == 0) {
            throw new IllegalArgumentException("value is null.");
        }
        if (((PDXClass) sequence).getType() != 16) {
            a.error("PDXSequence.addSequence() value is not a valid sequence.");
            throw new IllegalArgumentException("value is not a valid sequence. ");
        }
        this.b.addElement(sequence);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public void addUTF8String(String str) {
        if (str == null) {
            throw new IllegalArgumentException("value is null.");
        }
        this.b.addElement(new PDXUTF8String(str));
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public String getAsciiString(int i) {
        if (i >= this.b.size()) {
            a.error("PDXSequence.getAsciiString() index " + i + " is out of range. ");
            throw new IndexOutOfBoundsException();
        }
        PDXClass pDXClass = (PDXClass) this.b.elementAt(i);
        if (pDXClass.getType() == 22) {
            return ((PDXAsciiString) pDXClass).getValue();
        }
        a.error("PDXSequence.getAsciiString() index " + i + " is not a PDXAsciiString. ");
        throw new RuntimeException(Sequence.INDEX_IS_OF_WRONG_TYPE);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public byte[] getByteString(int i) {
        if (i >= this.b.size()) {
            a.error("PDXSequence.getByteString() index " + i + " is out of range. ");
            throw new IndexOutOfBoundsException();
        }
        PDXClass pDXClass = (PDXClass) this.b.elementAt(i);
        if (pDXClass.getType() == 4) {
            return ((PDXByteString) pDXClass).getValue();
        }
        a.error("PDXSequence.getByteString() index " + i + " is not a PDXByteString. ");
        throw new RuntimeException(Sequence.INDEX_IS_OF_WRONG_TYPE);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public Dictionary getDictionary(int i) {
        if (i >= this.b.size()) {
            a.error("PDXSequence.getDictionary() index " + i + " is out of range. ");
            throw new IndexOutOfBoundsException();
        }
        PDXClass pDXClass = (PDXClass) this.b.elementAt(i);
        if (pDXClass.getType() == 224) {
            return (PDXDictionary) pDXClass;
        }
        a.error("PDXSequence.getDictionary() index " + i + " is not a PDXDictionary. ");
        throw new RuntimeException(Sequence.INDEX_IS_OF_WRONG_TYPE);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public int getInteger(int i) {
        if (i >= this.b.size()) {
            a.error("PDXSequence.getInteger() index " + i + " is out of range. ");
            throw new IndexOutOfBoundsException();
        }
        PDXClass pDXClass = (PDXClass) this.b.elementAt(i);
        if (pDXClass.getType() == 192) {
            return ((PDXInteger) pDXClass).getValue();
        }
        a.error("PDXSequence.getInteger() index " + i + " is not a PDXInteger. ");
        throw new RuntimeException(Sequence.INDEX_IS_OF_WRONG_TYPE);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public Sequence getSequence(int i) {
        if (i >= this.b.size()) {
            a.error("PDXSequence.getSequence() index " + i + " is out of range. ");
            throw new IndexOutOfBoundsException();
        }
        PDXClass pDXClass = (PDXClass) this.b.elementAt(i);
        if (pDXClass.getType() == 16) {
            return (PDXSequence) pDXClass;
        }
        a.error("PDXSequence.getSequence() index " + i + " is not a PDXSequence. ");
        throw new RuntimeException(Sequence.INDEX_IS_OF_WRONG_TYPE);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public short getType(int i) {
        if (i < this.b.size()) {
            return ((PDXClass) this.b.elementAt(i)).getType();
        }
        a.error("PDXSequence.getType() index " + i + " is out of range. ");
        throw new IndexOutOfBoundsException();
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public String getUTF8String(int i) {
        if (i >= this.b.size()) {
            a.error("PDXSequence.getUTF8String() index " + i + " is out of range. ");
            throw new IndexOutOfBoundsException();
        }
        PDXClass pDXClass = (PDXClass) this.b.elementAt(i);
        if (pDXClass.getType() == 193) {
            return ((PDXUTF8String) pDXClass).getValue();
        }
        a.error("PDXSequence.getUTF8String() index " + i + " is not a PDXUTF8String. ");
        throw new RuntimeException(Sequence.INDEX_IS_OF_WRONG_TYPE);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.pdx.Sequence
    public int size() {
        return this.b.size();
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Enumeration elements = this.b.elements();
        while (elements.hasMoreElements()) {
            PDXClass pDXClass = (PDXClass) elements.nextElement();
            try {
                switch (pDXClass.getType()) {
                    case 4:
                        byteArrayOutputStream.write(((PDXByteString) pDXClass).toByteArray());
                        continue;
                    case 5:
                        byteArrayOutputStream.write(((PDXNull) pDXClass).toByteArray());
                        continue;
                    case 16:
                        byteArrayOutputStream.write(((PDXSequence) pDXClass).toByteArray());
                        continue;
                    case 22:
                        byteArrayOutputStream.write(((PDXAsciiString) pDXClass).toByteArray());
                        continue;
                    case R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop /* 192 */:
                        byteArrayOutputStream.write(((PDXInteger) pDXClass).toByteArray());
                        continue;
                    case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5row /* 193 */:
                        byteArrayOutputStream.write(((PDXUTF8String) pDXClass).toByteArray());
                        continue;
                    case 224:
                        byteArrayOutputStream.write(((PDXDictionary) pDXClass).toByteArray());
                        continue;
                    default:
                        continue;
                }
            } catch (IOException e) {
                a.error("PDXSequence.toByteArray() " + e.toString() + ". ");
            }
            a.error("PDXSequence.toByteArray() " + e.toString() + ". ");
        }
        return super.toByteArray(byteArrayOutputStream.toByteArray());
    }

    public String toString() {
        return toString(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:12:0x0069. Please report as an issue. */
    public String toString(int i) {
        String str;
        String str2 = "";
        int i2 = 0;
        while (true) {
            str = str2;
            if (i2 >= i - 1) {
                break;
            }
            str2 = str + "    ";
            i2++;
        }
        String str3 = i > 0 ? str + "    " : "";
        Enumeration elements = this.b.elements();
        String str4 = "[ \n";
        while (true) {
            String str5 = str4;
            if (!elements.hasMoreElements()) {
                return str5 + str + "] ";
            }
            PDXClass pDXClass = (PDXClass) elements.nextElement();
            switch (pDXClass.getType()) {
                case 4:
                    str5 = str5 + str3 + "<BYTES> \"" + ((PDXByteString) pDXClass).getValue() + "\" ";
                    break;
                case 5:
                    str5 = str5 + str3 + "<NULL> ";
                    break;
                case 16:
                    str5 = str5 + str3 + ((PDXSequence) pDXClass).toString(i + 1);
                    break;
                case 22:
                    str5 = str5 + str3 + "<ASCII> \"" + ((PDXAsciiString) pDXClass).getValue() + "\" ";
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop /* 192 */:
                    str5 = str5 + str3 + "<INT> " + ((PDXInteger) pDXClass).getValue();
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5row /* 193 */:
                    str5 = str5 + str3 + "<UTF8> \"" + ((PDXUTF8String) pDXClass).getValue() + "\" ";
                    break;
                case 224:
                    str5 = str5 + str3 + ((PDXDictionary) pDXClass).toString(i + 1);
                    break;
            }
            if (elements.hasMoreElements()) {
                str5 = str5 + ",";
            }
            str4 = str5 + "\n";
        }
    }
}
