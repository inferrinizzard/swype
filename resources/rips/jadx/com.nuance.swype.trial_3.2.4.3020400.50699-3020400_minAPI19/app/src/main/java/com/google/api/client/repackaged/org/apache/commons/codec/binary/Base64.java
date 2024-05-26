package com.google.api.client.repackaged.org.apache.commons.codec.binary;

import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;

/* loaded from: classes.dex */
public final class Base64 extends BaseNCodec {
    private int bitWorkArea;
    private final int decodeSize;
    private final byte[] decodeTable;
    private final int encodeSize;
    private final byte[] encodeTable;
    private final byte[] lineSeparator;
    static final byte[] CHUNK_SEPARATOR = {13, 10};
    private static final byte[] STANDARD_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE, ProtocolDefines.XMODE_SERVER_SESSION_UUID_SIZE, 17, ProtocolDefines.XMODE_VERSION_VAP, 19, 20, 21, 22, ProtocolDefines.XMODE_VERSION_COP, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, ProtocolDefines.XMODE_VERSION_BCP, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};

    public Base64() {
        this((byte) 0);
    }

    private Base64(byte b) {
        this(CHUNK_SEPARATOR);
    }

    private Base64(byte[] lineSeparator) {
        this(0, lineSeparator, false);
    }

    private Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        super(0, lineSeparator == null ? 0 : lineSeparator.length);
        this.decodeTable = DECODE_TABLE;
        if (lineSeparator != null) {
            if (!containsAlphabetOrPad(lineSeparator)) {
                this.encodeSize = 4;
                this.lineSeparator = null;
            } else {
                String sep = StringUtils.newString(lineSeparator, "UTF-8");
                throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
            }
        } else {
            this.encodeSize = 4;
            this.lineSeparator = null;
        }
        this.decodeSize = this.encodeSize - 1;
        this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
    }

    @Override // com.google.api.client.repackaged.org.apache.commons.codec.binary.BaseNCodec
    final void encode(byte[] bArr, int inPos, int inAvail) {
        if (!this.eof) {
            if (inAvail < 0) {
                this.eof = true;
                if (this.modulus != 0 || this.lineLength != 0) {
                    ensureBufferSize(this.encodeSize);
                    int savedPos = this.pos;
                    switch (this.modulus) {
                        case 1:
                            byte[] bArr2 = this.buffer;
                            int i = this.pos;
                            this.pos = i + 1;
                            bArr2[i] = this.encodeTable[(this.bitWorkArea >> 2) & 63];
                            byte[] bArr3 = this.buffer;
                            int i2 = this.pos;
                            this.pos = i2 + 1;
                            bArr3[i2] = this.encodeTable[(this.bitWorkArea << 4) & 63];
                            if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                                byte[] bArr4 = this.buffer;
                                int i3 = this.pos;
                                this.pos = i3 + 1;
                                bArr4[i3] = 61;
                                byte[] bArr5 = this.buffer;
                                int i4 = this.pos;
                                this.pos = i4 + 1;
                                bArr5[i4] = 61;
                                break;
                            }
                            break;
                        case 2:
                            byte[] bArr6 = this.buffer;
                            int i5 = this.pos;
                            this.pos = i5 + 1;
                            bArr6[i5] = this.encodeTable[(this.bitWorkArea >> 10) & 63];
                            byte[] bArr7 = this.buffer;
                            int i6 = this.pos;
                            this.pos = i6 + 1;
                            bArr7[i6] = this.encodeTable[(this.bitWorkArea >> 4) & 63];
                            byte[] bArr8 = this.buffer;
                            int i7 = this.pos;
                            this.pos = i7 + 1;
                            bArr8[i7] = this.encodeTable[(this.bitWorkArea << 2) & 63];
                            if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                                byte[] bArr9 = this.buffer;
                                int i8 = this.pos;
                                this.pos = i8 + 1;
                                bArr9[i8] = 61;
                                break;
                            }
                            break;
                    }
                    this.currentLinePos += this.pos - savedPos;
                    if (this.lineLength > 0 && this.currentLinePos > 0) {
                        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                        this.pos += this.lineSeparator.length;
                        return;
                    }
                    return;
                }
                return;
            }
            int i9 = 0;
            int inPos2 = inPos;
            while (i9 < inAvail) {
                ensureBufferSize(this.encodeSize);
                this.modulus = (this.modulus + 1) % 3;
                int inPos3 = inPos2 + 1;
                int i10 = bArr[inPos2];
                if (i10 < 0) {
                    i10 += 256;
                }
                this.bitWorkArea = (this.bitWorkArea << 8) + i10;
                if (this.modulus == 0) {
                    byte[] bArr10 = this.buffer;
                    int i11 = this.pos;
                    this.pos = i11 + 1;
                    bArr10[i11] = this.encodeTable[(this.bitWorkArea >> 18) & 63];
                    byte[] bArr11 = this.buffer;
                    int i12 = this.pos;
                    this.pos = i12 + 1;
                    bArr11[i12] = this.encodeTable[(this.bitWorkArea >> 12) & 63];
                    byte[] bArr12 = this.buffer;
                    int i13 = this.pos;
                    this.pos = i13 + 1;
                    bArr12[i13] = this.encodeTable[(this.bitWorkArea >> 6) & 63];
                    byte[] bArr13 = this.buffer;
                    int i14 = this.pos;
                    this.pos = i14 + 1;
                    bArr13[i14] = this.encodeTable[this.bitWorkArea & 63];
                    this.currentLinePos += 4;
                    if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
                        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                        this.pos += this.lineSeparator.length;
                        this.currentLinePos = 0;
                    }
                }
                i9++;
                inPos2 = inPos3;
            }
        }
    }

    @Override // com.google.api.client.repackaged.org.apache.commons.codec.binary.BaseNCodec
    final void decode(byte[] in, int inPos, int inAvail) {
        int result;
        if (!this.eof) {
            if (inAvail < 0) {
                this.eof = true;
            }
            int i = 0;
            int inPos2 = inPos;
            while (true) {
                if (i >= inAvail) {
                    break;
                }
                ensureBufferSize(this.decodeSize);
                int inPos3 = inPos2 + 1;
                byte b = in[inPos2];
                if (b == 61) {
                    this.eof = true;
                    break;
                }
                if (b >= 0 && b < DECODE_TABLE.length && (result = DECODE_TABLE[b]) >= 0) {
                    this.modulus = (this.modulus + 1) % 4;
                    this.bitWorkArea = (this.bitWorkArea << 6) + result;
                    if (this.modulus == 0) {
                        byte[] bArr = this.buffer;
                        int i2 = this.pos;
                        this.pos = i2 + 1;
                        bArr[i2] = (byte) ((this.bitWorkArea >> 16) & 255);
                        byte[] bArr2 = this.buffer;
                        int i3 = this.pos;
                        this.pos = i3 + 1;
                        bArr2[i3] = (byte) ((this.bitWorkArea >> 8) & 255);
                        byte[] bArr3 = this.buffer;
                        int i4 = this.pos;
                        this.pos = i4 + 1;
                        bArr3[i4] = (byte) (this.bitWorkArea & 255);
                    }
                }
                i++;
                inPos2 = inPos3;
            }
            if (this.eof && this.modulus != 0) {
                ensureBufferSize(this.decodeSize);
                switch (this.modulus) {
                    case 2:
                        this.bitWorkArea >>= 4;
                        byte[] bArr4 = this.buffer;
                        int i5 = this.pos;
                        this.pos = i5 + 1;
                        bArr4[i5] = (byte) (this.bitWorkArea & 255);
                        return;
                    case 3:
                        this.bitWorkArea >>= 2;
                        byte[] bArr5 = this.buffer;
                        int i6 = this.pos;
                        this.pos = i6 + 1;
                        bArr5[i6] = (byte) ((this.bitWorkArea >> 8) & 255);
                        byte[] bArr6 = this.buffer;
                        int i7 = this.pos;
                        this.pos = i7 + 1;
                        bArr6[i7] = (byte) (this.bitWorkArea & 255);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return StringUtils.newString(encodeBase64$7ec3bde1(binaryData, true), "UTF-8");
    }

    public static byte[] decodeBase64(String base64String) {
        return new Base64().decode(StringUtils.getBytesUnchecked(base64String, "UTF-8"));
    }

    @Override // com.google.api.client.repackaged.org.apache.commons.codec.binary.BaseNCodec
    protected final boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }

    public static String encodeBase64String(byte[] binaryData) {
        return StringUtils.newString(encodeBase64$7ec3bde1(binaryData, false), "UTF-8");
    }

    private static byte[] encodeBase64$7ec3bde1(byte[] binaryData, boolean urlSafe) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }
        Base64 base64 = new Base64(0, CHUNK_SEPARATOR, urlSafe);
        long encodedLength = base64.getEncodedLength(binaryData);
        if (encodedLength > 2147483647L) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + encodedLength + ") than the specified maximum size of 2147483647");
        }
        return base64.encode(binaryData);
    }
}
