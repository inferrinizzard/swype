package com.nuance.swype.input.store;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.nuance.swype.input.settings.ThemesFragment;
import com.nuance.swype.util.LogManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ThemePageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private SparseArray<Fragment> registeredFragments;
    protected static final LogManager.Log log = LogManager.getLog("ThemePageAdapter");
    private static final Map<String, List<Fragment>> fragmentsMap = new LinkedHashMap();

    public ThemePageAdapter(FragmentManager fm) {
        super(fm);
        this.registeredFragments = new SparseArray<>();
    }

    public void addFragments(String key, List<Fragment> fragmentsList) {
        fragmentsMap.put(key, fragmentsList);
        log.d("addFragments...fragments: ", fragmentsList, "...fragmentsMap: ", fragmentsMap);
    }

    public synchronized void setCurrentFragments(String key) {
        this.fragments = fragmentsMap.get(key);
        notifyDataSetChanged();
        log.d("setCurrentFragments...key: ", key, "....fragments: ", this.fragments, "...fragmentsMap: ", fragmentsMap);
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getItemPosition(Object item) {
        return super.getItemPosition(item);
    }

    @Override // android.support.v4.app.FragmentStatePagerAdapter
    public Fragment getItem(int position) {
        if (this.fragments != null) {
            return this.fragments.get(position);
        }
        return null;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        if (this.fragments != null) {
            return this.fragments.size();
        }
        return 0;
    }

    @Override // android.support.v4.app.FragmentStatePagerAdapter, android.support.v4.view.PagerAdapter
    public synchronized Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment;
        fragment = (Fragment) super.instantiateItem(container, position);
        this.registeredFragments.put(position, fragment);
        if (this.fragments != null && position < this.fragments.size() && this.fragments.get(position) != fragment) {
            Fragment fm = this.fragments.get(position);
            if ((fm instanceof ThemesFragment) && ((ThemesFragment) fm).getFragmentCategory() == null) {
                log.d("instantiateItem...replace fragment at position: ", Integer.valueOf(position));
                this.fragments.remove(position);
                this.fragments.add(position, fragment);
            }
        }
        log.d("instantiateItem...position: ", Integer.valueOf(position), "...fragment: ", fragment, "...registeredFragments: ", this.registeredFragments, "...fragments: ", this.fragments, "...fragmentsMap: ", fragmentsMap);
        return fragment;
    }

    @Override // android.support.v4.app.FragmentStatePagerAdapter, android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        this.registeredFragments.remove(position);
        super.destroyItem(container, position, object);
        log.d("destroyItem...position: ", Integer.valueOf(position), "...registeredFragments: ", this.registeredFragments);
    }

    public Fragment getRegisteredFragment(int position) {
        log.d("getRegisteredFragment...position: ", Integer.valueOf(position), "...registeredFragments: ", this.registeredFragments);
        if (position < this.registeredFragments.size()) {
            return this.registeredFragments.get(position);
        }
        return null;
    }

    public Fragment getRegisteredFragmentByCategory(int position) {
        String category;
        log.d("getRegisteredFragmentByCategory...position: ", Integer.valueOf(position), "...fragments: ", this.fragments, "...registeredFragments: ", this.registeredFragments);
        if (position < this.fragments.size()) {
            Fragment fm = this.fragments.get(position);
            if ((fm instanceof ThemesFragment) && (category = ((ThemesFragment) fm).getFragmentCategory()) != null) {
                for (int i = 0; i < this.registeredFragments.size(); i++) {
                    Fragment item = this.registeredFragments.valueAt(i);
                    if (item != null && ((ThemesFragment) item).getFragmentCategory() != null && ((ThemesFragment) item).getFragmentCategory().equals(category)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }
}
