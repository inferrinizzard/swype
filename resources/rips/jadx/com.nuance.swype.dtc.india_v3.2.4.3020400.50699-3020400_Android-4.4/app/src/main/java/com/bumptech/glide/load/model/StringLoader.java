package com.bumptech.glide.load.model;

import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.load.data.DataFetcher;
import java.io.File;

/* loaded from: classes.dex */
public class StringLoader<T> implements ModelLoader<String, T> {
    private final ModelLoader<Uri, T> uriLoader;

    @Override // com.bumptech.glide.load.model.ModelLoader
    public final /* bridge */ /* synthetic */ DataFetcher getResourceFetcher(String str, int x1, int x2) {
        Uri parse;
        String str2 = str;
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        if (str2.startsWith("/")) {
            parse = toFileUri(str2);
        } else {
            parse = Uri.parse(str2);
            if (parse.getScheme() == null) {
                parse = toFileUri(str2);
            }
        }
        return this.uriLoader.getResourceFetcher(parse, x1, x2);
    }

    public StringLoader(ModelLoader<Uri, T> uriLoader) {
        this.uriLoader = uriLoader;
    }

    private static Uri toFileUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
