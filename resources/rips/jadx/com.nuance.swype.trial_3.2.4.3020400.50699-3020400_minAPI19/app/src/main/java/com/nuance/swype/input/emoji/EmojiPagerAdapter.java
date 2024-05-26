package com.nuance.swype.input.emoji;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;
import com.nuance.swype.input.emoji.EmojiCategoryGroup;
import com.nuance.swype.input.emoji.EmojiPageView;
import com.nuance.swype.input.emoji.finger.FingerStateListener;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.view.OverlayView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class EmojiPagerAdapter extends PagerAdapter {
    private static final LogManager.Log log = LogManager.getLog("EmojiPagerAdapter");
    private static final LogManager.Trace trace = LogManager.getTrace();
    private EmojiCategoryGroup catGroup;
    private EmojiPageView currentPage;
    FingerStateListener fingerListener;
    private LayoutInflater inflater;
    private EmojiCategoryGroup.Iterator iter;
    private EmojiPageView.Listener listener;
    private OverlayView overlayView;
    private EmojiViewPager pager;
    private HashSet<EmojiPageView> views = new HashSet<>();

    public EmojiPagerAdapter(Context context, OverlayView overlayView, LayoutInflater inflater, EmojiCategoryGroup catGroup, EmojiPageView.Listener listener, FingerStateListener fingerListener, EmojiViewPager pager) {
        this.overlayView = overlayView;
        this.inflater = inflater;
        this.catGroup = catGroup;
        this.iter = catGroup.iterator();
        this.listener = listener;
        this.fingerListener = fingerListener;
        this.pager = pager;
    }

    public EmojiCategoryGroup getGroup() {
        return this.catGroup;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        return this.catGroup.getGlobalPageCount();
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getItemPosition(Object object) {
        if (!(object instanceof EmojiPageView)) {
            return -2;
        }
        PageInfo info = (PageInfo) ((EmojiPageView) object).getTag();
        int globalPage = getGroup().getGlobalPage(info.cat) + info.page;
        log.d("getItemPostiion(): called  :: cat: ", info.cat, "; loc: ", Integer.valueOf(info.page), "; page: ", Integer.valueOf(globalPage));
        return globalPage;
    }

    public void onCategoryChanged(EmojiCategory cat, int oldCount, int newCount) {
        if (oldCount != newCount) {
            log.d("onCategoryChanged(): recent page count: ", Integer.valueOf(oldCount), "->", Integer.valueOf(newCount));
            this.catGroup.updateGlobalPageCount();
            if (this.iter.getCategory().equals(cat)) {
                log.d("onCategoryChanged(): warning: modifying current category (iter invalid)");
                this.iter.moveToGlobalPage(0);
            }
            notifyDataSetChanged();
            return;
        }
        refreshDynamicPages();
    }

    public void refreshDynamicPages() {
        Iterator<EmojiPageView> it = this.views.iterator();
        while (it.hasNext()) {
            EmojiPageView view = it.next();
            PageInfo info = (PageInfo) view.getTag();
            if (info.cat.isDynamic()) {
                view.setItems(info.cat.getEmojiList(), info.cat, info.page);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PageInfo {
        public EmojiCategory cat;
        public int page;

        public PageInfo(EmojiCategory cat, int page) {
            this.cat = cat;
            this.page = page;
        }
    }

    private EmojiPageView createPageView(int pageNumber) {
        log.d("createPageView()", " called :: pageNumber==" + pageNumber);
        EmojiPageView pageView = (EmojiPageView) this.inflater.inflate(R.layout.emoji_page, (ViewGroup) null);
        this.iter.moveToGlobalPage(pageNumber);
        EmojiCategory cat = this.iter.getCategory();
        List<Emoji> items = cat.getEmojiList();
        log.d("createPageView()", " called : items.size==" + items.size());
        pageView.setTag(new PageInfo(cat, this.iter.getLocalPage()));
        pageView.init(this.overlayView, cat, this.catGroup.getGrid(), this.fingerListener, items, this.iter.getLocalPage(), this.pager);
        pageView.setListener(this.listener);
        return pageView;
    }

    public EmojiPageView getActivePageView() {
        return this.currentPage;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        EmojiPageView primaryView = (EmojiPageView) object;
        if (this.currentPage != primaryView) {
            if (this.currentPage != null) {
                this.currentPage.setTouchable(false);
            }
            this.currentPage = (EmojiPageView) object;
            this.currentPage.setTouchable(true);
        }
    }

    @Override // android.support.v4.view.PagerAdapter
    public Object instantiateItem(ViewGroup container, int position) {
        log.d("instantiateItem() :: callled ", Integer.valueOf(position));
        EmojiPageView page = createPageView(position);
        this.views.add(page);
        container.addView(page);
        return page;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        EmojiPageView page = (EmojiPageView) object;
        if (object.equals(this.currentPage)) {
            this.currentPage = null;
        }
        this.views.add(page);
        container.removeView(page);
    }
}
