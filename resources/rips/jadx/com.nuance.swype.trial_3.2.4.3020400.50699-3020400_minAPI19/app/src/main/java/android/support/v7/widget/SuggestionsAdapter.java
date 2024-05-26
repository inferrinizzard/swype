package android.support.v7.widget;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.appcompat.R;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuance.connect.comm.MessageAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
final class SuggestionsAdapter extends ResourceCursorAdapter implements View.OnClickListener {
    private boolean mClosed;
    private final int mCommitIconResId;
    private int mFlagsCol;
    private int mIconName1Col;
    private int mIconName2Col;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private final Context mProviderContext;
    int mQueryRefinement;
    private final SearchManager mSearchManager;
    private final SearchView mSearchView;
    private final SearchableInfo mSearchable;
    private int mText1Col;
    private int mText2Col;
    private int mText2UrlCol;
    private ColorStateList mUrlColor;

    public SuggestionsAdapter(Context context, SearchView searchView, SearchableInfo searchable, WeakHashMap<String, Drawable.ConstantState> outsideDrawablesCache) {
        super(context, searchView.getSuggestionRowLayout());
        this.mClosed = false;
        this.mQueryRefinement = 1;
        this.mText1Col = -1;
        this.mText2Col = -1;
        this.mText2UrlCol = -1;
        this.mIconName1Col = -1;
        this.mIconName2Col = -1;
        this.mFlagsCol = -1;
        this.mSearchManager = (SearchManager) this.mContext.getSystemService("search");
        this.mSearchView = searchView;
        this.mSearchable = searchable;
        this.mCommitIconResId = searchView.getSuggestionCommitIconResId();
        this.mProviderContext = context;
        this.mOutsideDrawablesCache = outsideDrawablesCache;
    }

    @Override // android.support.v4.widget.CursorAdapter, android.widget.BaseAdapter, android.widget.Adapter
    public final boolean hasStableIds() {
        return false;
    }

    @Override // android.support.v4.widget.CursorAdapter, android.support.v4.widget.CursorFilter.CursorFilterClient
    public final Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String[] strArr;
        Cursor cursor;
        String query = constraint == null ? "" : constraint.toString();
        if (this.mSearchView.getVisibility() != 0 || this.mSearchView.getWindowVisibility() != 0) {
            return null;
        }
        try {
            SearchableInfo searchableInfo = this.mSearchable;
            if (searchableInfo == null) {
                cursor = null;
            } else {
                String suggestAuthority = searchableInfo.getSuggestAuthority();
                if (suggestAuthority == null) {
                    cursor = null;
                } else {
                    Uri.Builder fragment = new Uri.Builder().scheme("content").authority(suggestAuthority).query("").fragment("");
                    String suggestPath = searchableInfo.getSuggestPath();
                    if (suggestPath != null) {
                        fragment.appendEncodedPath(suggestPath);
                    }
                    fragment.appendPath("search_suggest_query");
                    String suggestSelection = searchableInfo.getSuggestSelection();
                    if (suggestSelection != null) {
                        strArr = new String[]{query};
                    } else {
                        fragment.appendPath(query);
                        strArr = null;
                    }
                    fragment.appendQueryParameter("limit", MessageAPI.VERSION_ID);
                    cursor = this.mContext.getContentResolver().query(fragment.build(), null, suggestSelection, strArr, null);
                }
            }
            if (cursor != null) {
                cursor.getCount();
                return cursor;
            }
        } catch (RuntimeException e) {
            Log.w("SuggestionsAdapter", "Search suggestions query threw an exception.", e);
        }
        return null;
    }

    @Override // android.widget.BaseAdapter
    public final void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        updateSpinnerState(this.mCursor);
    }

    @Override // android.widget.BaseAdapter
    public final void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        updateSpinnerState(this.mCursor);
    }

    private static void updateSpinnerState(Cursor cursor) {
        Bundle extras = cursor != null ? cursor.getExtras() : null;
        if (extras == null || extras.getBoolean("in_progress")) {
        }
    }

    @Override // android.support.v4.widget.CursorAdapter, android.support.v4.widget.CursorFilter.CursorFilterClient
    public final void changeCursor(Cursor c) {
        if (this.mClosed) {
            Log.w("SuggestionsAdapter", "Tried to change cursor after adapter was closed.");
            if (c != null) {
                c.close();
                return;
            }
            return;
        }
        try {
            super.changeCursor(c);
            if (c != null) {
                this.mText1Col = c.getColumnIndex("suggest_text_1");
                this.mText2Col = c.getColumnIndex("suggest_text_2");
                this.mText2UrlCol = c.getColumnIndex("suggest_text_2_url");
                this.mIconName1Col = c.getColumnIndex("suggest_icon_1");
                this.mIconName2Col = c.getColumnIndex("suggest_icon_2");
                this.mFlagsCol = c.getColumnIndex("suggest_flags");
            }
        } catch (Exception e) {
            Log.e("SuggestionsAdapter", "error changing cursor and caching columns", e);
        }
    }

    @Override // android.support.v4.widget.ResourceCursorAdapter, android.support.v4.widget.CursorAdapter
    public final View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        v.setTag(new ChildViewCache(v));
        ((ImageView) v.findViewById(R.id.edit_query)).setImageResource(this.mCommitIconResId);
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ChildViewCache {
        public final ImageView mIcon1;
        public final ImageView mIcon2;
        public final ImageView mIconRefine;
        public final TextView mText1;
        public final TextView mText2;

        public ChildViewCache(View v) {
            this.mText1 = (TextView) v.findViewById(android.R.id.text1);
            this.mText2 = (TextView) v.findViewById(android.R.id.text2);
            this.mIcon1 = (ImageView) v.findViewById(android.R.id.icon1);
            this.mIcon2 = (ImageView) v.findViewById(android.R.id.icon2);
            this.mIconRefine = (ImageView) v.findViewById(R.id.edit_query);
        }
    }

    @Override // android.support.v4.widget.CursorAdapter
    public final void bindView$4693bf34(View view, Cursor cursor) {
        Drawable drawableFromResourceValue;
        Drawable drawableFromResourceValue2;
        CharSequence text2;
        ChildViewCache views = (ChildViewCache) view.getTag();
        int flags = 0;
        if (this.mFlagsCol != -1) {
            flags = cursor.getInt(this.mFlagsCol);
        }
        if (views.mText1 != null) {
            String text1 = getStringOrNull(cursor, this.mText1Col);
            setViewText(views.mText1, text1);
        }
        if (views.mText2 != null) {
            CharSequence text22 = getStringOrNull(cursor, this.mText2UrlCol);
            if (text22 == null) {
                text2 = getStringOrNull(cursor, this.mText2Col);
            } else {
                if (this.mUrlColor == null) {
                    TypedValue typedValue = new TypedValue();
                    this.mContext.getTheme().resolveAttribute(R.attr.textColorSearchUrl, typedValue, true);
                    this.mUrlColor = this.mContext.getResources().getColorStateList(typedValue.resourceId);
                }
                SpannableString spannableString = new SpannableString(text22);
                spannableString.setSpan(new TextAppearanceSpan(null, 0, 0, this.mUrlColor, null), 0, text22.length(), 33);
                text2 = spannableString;
            }
            if (TextUtils.isEmpty(text2)) {
                if (views.mText1 != null) {
                    views.mText1.setSingleLine(false);
                    views.mText1.setMaxLines(2);
                }
            } else if (views.mText1 != null) {
                views.mText1.setSingleLine(true);
                views.mText1.setMaxLines(1);
            }
            setViewText(views.mText2, text2);
        }
        if (views.mIcon1 != null) {
            ImageView imageView = views.mIcon1;
            if (this.mIconName1Col == -1) {
                drawableFromResourceValue2 = null;
            } else {
                drawableFromResourceValue2 = getDrawableFromResourceValue(cursor.getString(this.mIconName1Col));
                if (drawableFromResourceValue2 == null) {
                    ComponentName searchActivity = this.mSearchable.getSearchActivity();
                    String flattenToShortString = searchActivity.flattenToShortString();
                    if (this.mOutsideDrawablesCache.containsKey(flattenToShortString)) {
                        Drawable.ConstantState constantState = this.mOutsideDrawablesCache.get(flattenToShortString);
                        drawableFromResourceValue2 = constantState == null ? null : constantState.newDrawable(this.mProviderContext.getResources());
                    } else {
                        Drawable activityIcon = getActivityIcon(searchActivity);
                        this.mOutsideDrawablesCache.put(flattenToShortString, activityIcon == null ? null : activityIcon.getConstantState());
                        drawableFromResourceValue2 = activityIcon;
                    }
                    if (drawableFromResourceValue2 == null) {
                        drawableFromResourceValue2 = this.mContext.getPackageManager().getDefaultActivityIcon();
                    }
                }
            }
            setViewDrawable(imageView, drawableFromResourceValue2, 4);
        }
        if (views.mIcon2 != null) {
            ImageView imageView2 = views.mIcon2;
            if (this.mIconName2Col == -1) {
                drawableFromResourceValue = null;
            } else {
                drawableFromResourceValue = getDrawableFromResourceValue(cursor.getString(this.mIconName2Col));
            }
            setViewDrawable(imageView2, drawableFromResourceValue, 8);
        }
        if (this.mQueryRefinement == 2 || (this.mQueryRefinement == 1 && (flags & 1) != 0)) {
            views.mIconRefine.setVisibility(0);
            views.mIconRefine.setTag(views.mText1.getText());
            views.mIconRefine.setOnClickListener(this);
            return;
        }
        views.mIconRefine.setVisibility(8);
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View v) {
        Object tag = v.getTag();
        if (!(tag instanceof CharSequence)) {
            return;
        }
        this.mSearchView.setQuery((CharSequence) tag);
    }

    private static void setViewText(TextView v, CharSequence text) {
        v.setText(text);
        if (TextUtils.isEmpty(text)) {
            v.setVisibility(8);
        } else {
            v.setVisibility(0);
        }
    }

    private static void setViewDrawable(ImageView v, Drawable drawable, int nullVisibility) {
        v.setImageDrawable(drawable);
        if (drawable == null) {
            v.setVisibility(nullVisibility);
            return;
        }
        v.setVisibility(0);
        drawable.setVisible(false, false);
        drawable.setVisible(true, false);
    }

    @Override // android.support.v4.widget.CursorAdapter, android.support.v4.widget.CursorFilter.CursorFilterClient
    public final CharSequence convertToString(Cursor cursor) {
        String text1;
        String data;
        if (cursor == null) {
            return null;
        }
        String query = getColumnString(cursor, "suggest_intent_query");
        if (query == null) {
            if (this.mSearchable.shouldRewriteQueryFromData() && (data = getColumnString(cursor, "suggest_intent_data")) != null) {
                return data;
            }
            if (!this.mSearchable.shouldRewriteQueryFromText() || (text1 = getColumnString(cursor, "suggest_text_1")) == null) {
                return null;
            }
            return text1;
        }
        return query;
    }

    @Override // android.support.v4.widget.CursorAdapter, android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        try {
            return super.getView(position, convertView, parent);
        } catch (RuntimeException e) {
            Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", e);
            View v = newView(this.mContext, this.mCursor, parent);
            if (v != null) {
                ((ChildViewCache) v.getTag()).mText1.setText(e.toString());
                return v;
            }
            return v;
        }
    }

    private Drawable getDrawableFromResourceValue(String drawableId) {
        if (drawableId == null || drawableId.length() == 0 || "0".equals(drawableId)) {
            return null;
        }
        try {
            int resourceId = Integer.parseInt(drawableId);
            String drawableUri = "android.resource://" + this.mProviderContext.getPackageName() + "/" + resourceId;
            Drawable drawable = checkIconCache(drawableUri);
            if (drawable == null) {
                Drawable drawable2 = ContextCompat.getDrawable(this.mProviderContext, resourceId);
                storeInIconCache(drawableUri, drawable2);
                return drawable2;
            }
            return drawable;
        } catch (Resources.NotFoundException e) {
            Log.w("SuggestionsAdapter", "Icon resource not found: " + drawableId);
            return null;
        } catch (NumberFormatException e2) {
            Drawable drawable3 = checkIconCache(drawableId);
            if (drawable3 == null) {
                Uri uri = Uri.parse(drawableId);
                Drawable drawable4 = getDrawable(uri);
                storeInIconCache(drawableId, drawable4);
                return drawable4;
            }
            return drawable3;
        }
    }

    private Drawable getDrawable(Uri uri) {
        try {
            String scheme = uri.getScheme();
            if ("android.resource".equals(scheme)) {
                try {
                    return getDrawableFromResourceUri(uri);
                } catch (Resources.NotFoundException e) {
                    throw new FileNotFoundException("Resource does not exist: " + uri);
                }
            }
            InputStream stream = this.mProviderContext.getContentResolver().openInputStream(uri);
            if (stream == null) {
                throw new FileNotFoundException("Failed to open " + uri);
            }
            try {
                Drawable createFromStream = Drawable.createFromStream(stream, null);
                try {
                    stream.close();
                    return createFromStream;
                } catch (IOException ex) {
                    Log.e("SuggestionsAdapter", "Error closing icon stream for " + uri, ex);
                    return createFromStream;
                }
            } finally {
            }
        } catch (FileNotFoundException fnfe) {
            Log.w("SuggestionsAdapter", "Icon not found: " + uri + ", " + fnfe.getMessage());
            return null;
        }
        Log.w("SuggestionsAdapter", "Icon not found: " + uri + ", " + fnfe.getMessage());
        return null;
    }

    private Drawable checkIconCache(String resourceUri) {
        Drawable.ConstantState cached = this.mOutsideDrawablesCache.get(resourceUri);
        if (cached == null) {
            return null;
        }
        return cached.newDrawable();
    }

    private void storeInIconCache(String resourceUri, Drawable drawable) {
        if (drawable != null) {
            this.mOutsideDrawablesCache.put(resourceUri, drawable.getConstantState());
        }
    }

    private Drawable getActivityIcon(ComponentName component) {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            ActivityInfo activityInfo = pm.getActivityInfo(component, 128);
            int iconId = activityInfo.getIconResource();
            if (iconId == 0) {
                return null;
            }
            String pkg = component.getPackageName();
            Drawable drawable = pm.getDrawable(pkg, iconId, activityInfo.applicationInfo);
            if (drawable == null) {
                Log.w("SuggestionsAdapter", "Invalid icon resource " + iconId + " for " + component.flattenToShortString());
                return null;
            }
            return drawable;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.w("SuggestionsAdapter", ex.toString());
            return null;
        }
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        int col = cursor.getColumnIndex(columnName);
        return getStringOrNull(cursor, col);
    }

    private static String getStringOrNull(Cursor cursor, int col) {
        if (col == -1) {
            return null;
        }
        try {
            return cursor.getString(col);
        } catch (Exception e) {
            Log.e("SuggestionsAdapter", "unexpected error retrieving valid column from cursor, did the remote process die?", e);
            return null;
        }
    }

    private Drawable getDrawableFromResourceUri(Uri uri) throws FileNotFoundException {
        int id;
        String authority = uri.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            throw new FileNotFoundException("No authority: " + uri);
        }
        try {
            Resources r = this.mContext.getPackageManager().getResourcesForApplication(authority);
            List<String> path = uri.getPathSegments();
            if (path == null) {
                throw new FileNotFoundException("No path: " + uri);
            }
            int len = path.size();
            if (len == 1) {
                try {
                    id = Integer.parseInt(path.get(0));
                } catch (NumberFormatException e) {
                    throw new FileNotFoundException("Single path segment is not a resource ID: " + uri);
                }
            } else if (len == 2) {
                id = r.getIdentifier(path.get(1), path.get(0), authority);
            } else {
                throw new FileNotFoundException("More than two path segments: " + uri);
            }
            if (id == 0) {
                throw new FileNotFoundException("No resource found for: " + uri);
            }
            return r.getDrawable(id);
        } catch (PackageManager.NameNotFoundException e2) {
            throw new FileNotFoundException("No package found for authority: " + uri);
        }
    }
}
