package com.nuance.swype.input.emoji;

import android.content.Context;
import com.nuance.swype.util.LogManager;
import java.util.List;
import java.util.NoSuchElementException;

/* loaded from: classes.dex */
public class EmojiCategoryGroup {
    private static final LogManager.Log log = LogManager.getLog("EmojiCategoryGroup");
    private EmojiCategoryList categoryList;
    private Context context;
    private int globalPageCount = calcGlobalPageCount();
    private EmojiGridParams grid;

    /* loaded from: classes.dex */
    public class Iterator {
        private List<EmojiCategory> cats;
        private int currentCategory;
        private int currentPage;
        private int pageCount;

        public Iterator(int category, int page) {
            this.currentCategory = category;
            this.currentPage = page;
            this.cats = EmojiCategoryGroup.this.categoryList.getAllCategories();
            updatePageCount();
            if (page >= this.pageCount) {
                throw new IndexOutOfBoundsException("Page beyond category page range");
            }
        }

        public Iterator(EmojiCategoryGroup this$0) {
            this(0, 0);
        }

        public void moveToGlobalPage(int page) {
            int curCat = 0;
            int curPage = 0;
            int catFirstPageOffset = 0;
            for (EmojiCategory cat : this.cats) {
                int pagesInCat = EmojiCategoryGroup.this.getPageCount(cat);
                curPage += pagesInCat;
                if (curPage > page) {
                    break;
                }
                curCat++;
                catFirstPageOffset += pagesInCat;
            }
            this.currentCategory = curCat;
            updatePageCount();
            this.currentPage = page - catFirstPageOffset;
        }

        public final void updatePageCount() {
            this.pageCount = EmojiCategoryGroup.this.getPageCount(getCategory());
        }

        public EmojiCategory getCategory() {
            EmojiCategoryGroup.log.d("getCategory(): called >>> currentCategory==" + this.currentCategory);
            return this.cats.get(this.currentCategory);
        }

        public int getLocalPage() {
            return this.currentPage;
        }

        public int getLocalPageCount() {
            return this.pageCount;
        }

        public boolean hasCategoryNext() {
            return this.currentCategory < this.cats.size() + (-1);
        }

        public boolean hasPageNext() {
            return hasCategoryNext() || this.currentPage < this.pageCount + (-1);
        }

        public boolean hasCategoryPrev() {
            return this.currentCategory > 0;
        }

        public boolean hasPagePrev() {
            return hasCategoryPrev() || this.currentPage > 0;
        }

        public boolean nextPage() {
            if (!hasPageNext()) {
                throw new NoSuchElementException();
            }
            int i = this.currentPage + 1;
            this.currentPage = i;
            if (i != this.pageCount) {
                return false;
            }
            this.currentCategory++;
            this.currentPage = 0;
            updatePageCount();
            return true;
        }

        public boolean prevPage() {
            if (!hasPagePrev()) {
                throw new NoSuchElementException();
            }
            if (this.currentPage == 0) {
                this.currentCategory--;
                updatePageCount();
                this.currentPage = this.pageCount - 1;
                return true;
            }
            this.currentPage--;
            return false;
        }

        public void nextCategory() {
            if (!hasCategoryNext()) {
                throw new NoSuchElementException();
            }
            this.currentCategory++;
            this.currentPage = 0;
            updatePageCount();
        }

        public void prevCategory() {
            if (!hasCategoryPrev()) {
                throw new NoSuchElementException();
            }
            this.currentCategory--;
            updatePageCount();
            this.currentPage = 0;
        }
    }

    public EmojiCategoryGroup(Context context, EmojiGridParams grid, EmojiCategoryList categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.grid = grid;
        if (grid.cellCount <= 0) {
            throw new IllegalArgumentException("Invalid grid");
        }
    }

    public Iterator iterator(int category, int page) {
        return new Iterator(category, page);
    }

    public Iterator iterator() {
        return new Iterator(this);
    }

    public int getPageCount(EmojiCategory cat) {
        List<Emoji> emojis = cat.getEmojiList();
        int count = (emojis.size() + (this.grid.cellCount - 1)) / this.grid.cellCount;
        log.d("getPageCount(): called >>> count==" + count + ", grid.cellCount==" + this.grid.cellCount + " emojis.size()==" + emojis.size());
        return Math.max(1, count);
    }

    public void updateGlobalPageCount() {
        this.globalPageCount = calcGlobalPageCount();
    }

    public int getGlobalPageCount() {
        log.d("getGlobalPageCount(): called >>>> globalPageCount==" + this.globalPageCount);
        return this.globalPageCount;
    }

    public EmojiGridParams getGrid() {
        return this.grid;
    }

    private int calcGlobalPageCount() {
        log.d("calcGlobalPageCount(): called >>>> globalPageCount==" + this.globalPageCount);
        int total = 0;
        for (EmojiCategory cat : this.categoryList.getAllCategories()) {
            int categoryPageCount = getPageCount(cat);
            total += categoryPageCount;
        }
        log.d("calcGlobalPageCount(): called >>>> total==" + total);
        return total;
    }

    public int getGlobalPage(EmojiCategory category) {
        int catIndex = this.categoryList.getCategoryIndex(category);
        int page = 0;
        int curIndex = 0;
        for (EmojiCategory cat : this.categoryList.getAllCategories()) {
            if (curIndex >= catIndex) {
                break;
            }
            int pagesInCat = getPageCount(cat);
            page += pagesInCat;
            curIndex++;
        }
        return page;
    }

    private int getCatIndexForGlobalPage(int page) {
        int curCat = 0;
        int curPage = 0;
        for (EmojiCategory cat : this.categoryList.getAllCategories()) {
            int pagesInCat = getPageCount(cat);
            curPage += pagesInCat;
            if (curPage > page) {
                break;
            }
            curCat++;
        }
        return curCat;
    }

    public EmojiCategory getCatForGlobalPage(int page) {
        int catIndex = getCatIndexForGlobalPage(page);
        return this.categoryList.getAllCategories().get(catIndex);
    }

    public List<EmojiCategory> getAllCategories() {
        return this.categoryList.getAllCategories();
    }
}
