package com.nuance.swype.input.chinese.symbol;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SymbolViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> views = new ArrayList<>();

    @Override // android.support.v4.view.PagerAdapter
    public int getItemPosition(Object object) {
        int index = this.views.indexOf(object);
        if (index == -1) {
            return -2;
        }
        return index;
    }

    @Override // android.support.v4.view.PagerAdapter
    public Object instantiateItem(ViewGroup container, int position) {
        View v = this.views.get(position);
        container.addView(v);
        return v;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(this.views.get(position));
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        return this.views.size();
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int addView(View v) {
        return addView(v, this.views.size());
    }

    public int addView(View v, int position) {
        this.views.add(position, v);
        return position;
    }

    public int removeView(ViewPager pager, View v) {
        return removeView(pager, this.views.indexOf(v));
    }

    public int removeView(ViewPager pager, int position) {
        pager.setAdapter(null);
        this.views.remove(position);
        pager.setAdapter(this);
        return position;
    }

    public View getView(int position) {
        return this.views.get(position);
    }
}
