package com.nuance.swype.input.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.emoji.util.Util;
import com.nuance.swype.util.CollectionUtils;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.util.LogManager;
import java.util.List;

/* loaded from: classes.dex */
public class EmojiGridParams {
    private static final LogManager.Log log = LogManager.getLog("EmojiGridParams");
    private static final LogManager.Trace trace = LogManager.getTrace();
    public final int baselineOffset;
    public final int cellCount;
    public final int height;
    public final DimensionData heightData;
    public final float scale;
    public final int width;
    public final DimensionData widthData;
    public final Paint paint = new Paint();
    private boolean expandPadding = false;

    /* loaded from: classes.dex */
    public static class DimensionData {
        public final int cellCount;
        public final int cellSize;
        public final int internalSize;
        public final int margin;
        public final int padding;

        public DimensionData(Resources res, int internalSize, int idPaddingFactor, int areaSize, boolean expandPadding) {
            this.internalSize = internalSize;
            this.padding = (int) Math.floor(res.getFraction(idPaddingFactor, internalSize, 1));
            EmojiGridParams.log.d("Params():  padding: ", Integer.valueOf(this.padding), "; internal: ", Integer.valueOf(internalSize));
            int minDim = internalSize + this.padding;
            if (minDim <= 0) {
                throw new IllegalStateException("Computed min cellSize dimension must be positive");
            }
            this.cellCount = Math.max((int) Math.floor(areaSize / minDim), 1);
            int extraSpace = areaSize - (this.cellCount * minDim);
            if (expandPadding) {
                int extraPerCell = extraSpace / this.cellCount;
                this.cellSize = minDim + extraPerCell;
                this.margin = 0;
            } else {
                this.cellSize = minDim;
                this.margin = extraSpace / (this.cellCount + 1);
            }
        }

        public int getCellIndexSlop(float offset) {
            int firstCellPos = (-this.margin) / 2;
            int advance = this.margin + this.cellSize;
            int cellIndex = (int) Math.floor((firstCellPos + offset) / advance);
            if (cellIndex >= this.cellCount) {
                return -1;
            }
            return cellIndex;
        }

        public int getCellIndex(float offset) {
            int advance = this.margin + this.cellSize;
            int cellIndex = (int) Math.floor(offset / advance);
            if (cellIndex >= this.cellCount) {
                return -1;
            }
            int cellOffset = (cellIndex * advance) + this.margin;
            if (offset < cellOffset) {
                return -1;
            }
            return cellIndex;
        }
    }

    public boolean matches(int width, int height, float scale) {
        log.d("matches(): ow: ", Integer.valueOf(this.width), "; oh: ", Integer.valueOf(this.height), "; w: ", Integer.valueOf(width), "; h: ", Integer.valueOf(height), "; os: ", Float.valueOf(this.scale), "; s: ", Float.valueOf(scale));
        return this.width == width && this.height == height && this.scale == scale;
    }

    public EmojiGridParams(Context context, int width, int height, float scale) {
        log.d("Params(): w: ", Integer.valueOf(width), "; h: ", Integer.valueOf(height), "; s: ", Float.valueOf(scale));
        if (width <= 0 || height <= 0 || scale <= 0.0f) {
            throw new IllegalArgumentException("All arguments must be positive");
        }
        this.width = width;
        this.height = height;
        this.scale = scale;
        Resources res = context.getResources();
        float textSizeUnscaled = res.getDimension(R.dimen.emoji_text_size);
        int textSize = (int) ((IMEApplication.from(context).isScreenLayoutTablet() ? textSizeUnscaled : Util.convertDpToPixel(res.getInteger(R.integer.emoji_text_value))) * scale);
        this.paint.setTextSize(textSize);
        Paint.FontMetricsInt fm = this.paint.getFontMetricsInt();
        int fontHeight = fm.bottom - fm.top;
        this.widthData = new DimensionData(res, fontHeight, R.fraction.emoji_hor_padding_factor, width, this.expandPadding);
        this.heightData = new DimensionData(res, fontHeight, R.fraction.emoji_ver_padding_factor, height, this.expandPadding);
        this.cellCount = this.heightData.cellCount * this.widthData.cellCount;
        this.baselineOffset = ((this.heightData.cellSize - fontHeight) / 2) - fm.top;
    }

    public int getPageForItem(int itemIndex) {
        if (this.cellCount > 0) {
            return itemIndex / this.cellCount;
        }
        return 0;
    }

    public int getFirstItemOnPage(int pageNumber) {
        return this.cellCount * pageNumber;
    }

    public int getCellIndex(float x, float y) {
        int yCell;
        int xCell = this.widthData.getCellIndexSlop(x);
        if (-1 == xCell || -1 == (yCell = this.heightData.getCellIndexSlop(y))) {
            return -1;
        }
        return (this.widthData.cellCount * yCell) + xCell;
    }

    public int getXOriginOffset(Rect textBounds, int gravity) {
        int leftPadding = this.widthData.padding / 2;
        int areaWidth = this.widthData.cellSize - this.widthData.padding;
        return GeomUtil.getOffsetX(areaWidth, textBounds.width(), gravity) + leftPadding + (-textBounds.left);
    }

    public List<EmojiGridCell> createCells(Context context, EmojiPageView parent, EmojiCategory cat, List<Emoji> emojis) {
        log.d("createCells()", "called : cellCount==" + this.cellCount + ", size ==" + emojis.size());
        List<EmojiGridCell> cells = CollectionUtils.newArrayList(this.cellCount);
        int xCell = 0;
        int yCell = 0;
        for (int idx = 0; idx < this.cellCount && idx < emojis.size(); idx++) {
            EmojiGridCell cell = new EmojiGridCell(context, parent, cat, emojis.get(idx), idx, xCell, yCell);
            cells.add(cell);
            xCell++;
            if (xCell >= this.widthData.cellCount) {
                xCell = 0;
                yCell++;
            }
        }
        return cells;
    }
}
