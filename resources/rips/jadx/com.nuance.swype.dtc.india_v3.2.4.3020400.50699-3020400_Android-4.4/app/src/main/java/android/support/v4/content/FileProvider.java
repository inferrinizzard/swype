package android.support.v4.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class FileProvider extends ContentProvider {
    private static final String[] COLUMNS = {"_display_name", "_size"};
    private static final File DEVICE_ROOT = new File("/");
    private static HashMap<String, PathStrategy> sCache = new HashMap<>();
    private PathStrategy mStrategy;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface PathStrategy {
        File getFileForUri(Uri uri);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info.exported) {
            throw new SecurityException("Provider must not be exported");
        }
        if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grant uri permissions");
        }
        this.mStrategy = getPathStrategy(context, info.authority);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int i;
        File file = this.mStrategy.getFileForUri(uri);
        if (projection == null) {
            projection = COLUMNS;
        }
        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int length = projection.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            String col = projection[i2];
            if ("_display_name".equals(col)) {
                cols[i3] = "_display_name";
                i = i3 + 1;
                values[i3] = file.getName();
            } else if ("_size".equals(col)) {
                cols[i3] = "_size";
                i = i3 + 1;
                values[i3] = Long.valueOf(file.length());
            } else {
                i = i3;
            }
            i2++;
            i3 = i;
        }
        String[] cols2 = new String[i3];
        System.arraycopy(cols, 0, cols2, 0, i3);
        Object[] values2 = new Object[i3];
        System.arraycopy(values, 0, values2, 0, i3);
        MatrixCursor cursor = new MatrixCursor(cols2, 1);
        cursor.addRow(values2);
        return cursor;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        File file = this.mStrategy.getFileForUri(uri);
        int lastDot = file.getName().lastIndexOf(46);
        if (lastDot >= 0) {
            String extension = file.getName().substring(lastDot + 1);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return this.mStrategy.getFileForUri(uri).delete() ? 1 : 0;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        int fileMode;
        File file = this.mStrategy.getFileForUri(uri);
        if ("r".equals(mode)) {
            fileMode = 268435456;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            fileMode = 738197504;
        } else if ("wa".equals(mode)) {
            fileMode = 704643072;
        } else if ("rw".equals(mode)) {
            fileMode = 939524096;
        } else if ("rwt".equals(mode)) {
            fileMode = 1006632960;
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        return ParcelFileDescriptor.open(file, fileMode);
    }

    private static PathStrategy getPathStrategy(Context context, String str) {
        File buildPath;
        PathStrategy pathStrategy;
        synchronized (sCache) {
            PathStrategy pathStrategy2 = sCache.get(str);
            pathStrategy = pathStrategy2;
            if (pathStrategy2 == null) {
                try {
                    try {
                        SimplePathStrategy simplePathStrategy = new SimplePathStrategy(str);
                        XmlResourceParser loadXmlMetaData = context.getPackageManager().resolveContentProvider(str, 128).loadXmlMetaData(context.getPackageManager(), "android.support.FILE_PROVIDER_PATHS");
                        if (loadXmlMetaData != null) {
                            while (true) {
                                int next = loadXmlMetaData.next();
                                if (next == 1) {
                                    sCache.put(str, simplePathStrategy);
                                    pathStrategy = simplePathStrategy;
                                    break;
                                }
                                if (next == 2) {
                                    String name = loadXmlMetaData.getName();
                                    String attributeValue = loadXmlMetaData.getAttributeValue(null, "name");
                                    String attributeValue2 = loadXmlMetaData.getAttributeValue(null, "path");
                                    if ("root-path".equals(name)) {
                                        buildPath = buildPath(DEVICE_ROOT, attributeValue2);
                                    } else if ("files-path".equals(name)) {
                                        buildPath = buildPath(context.getFilesDir(), attributeValue2);
                                    } else if ("cache-path".equals(name)) {
                                        buildPath = buildPath(context.getCacheDir(), attributeValue2);
                                    } else {
                                        buildPath = "external-path".equals(name) ? buildPath(Environment.getExternalStorageDirectory(), attributeValue2) : null;
                                    }
                                    if (buildPath == null) {
                                        continue;
                                    } else {
                                        if (TextUtils.isEmpty(attributeValue)) {
                                            throw new IllegalArgumentException("Name must not be empty");
                                        }
                                        try {
                                            simplePathStrategy.mRoots.put(attributeValue, buildPath.getCanonicalFile());
                                        } catch (IOException e) {
                                            throw new IllegalArgumentException("Failed to resolve canonical path for " + buildPath, e);
                                        }
                                    }
                                }
                            }
                        } else {
                            throw new IllegalArgumentException("Missing android.support.FILE_PROVIDER_PATHS meta-data");
                        }
                    } catch (IOException e2) {
                        throw new IllegalArgumentException("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", e2);
                    }
                } catch (XmlPullParserException e3) {
                    throw new IllegalArgumentException("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", e3);
                }
            }
        }
        return pathStrategy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SimplePathStrategy implements PathStrategy {
        private final String mAuthority;
        final HashMap<String, File> mRoots = new HashMap<>();

        public SimplePathStrategy(String authority) {
            this.mAuthority = authority;
        }

        @Override // android.support.v4.content.FileProvider.PathStrategy
        public final File getFileForUri(Uri uri) {
            String path = uri.getEncodedPath();
            int splitIndex = path.indexOf(47, 1);
            String tag = Uri.decode(path.substring(1, splitIndex));
            String path2 = Uri.decode(path.substring(splitIndex + 1));
            File root = this.mRoots.get(tag);
            if (root == null) {
                throw new IllegalArgumentException("Unable to find configured root for " + uri);
            }
            File file = new File(root, path2);
            try {
                File file2 = file.getCanonicalFile();
                if (!file2.getPath().startsWith(root.getPath())) {
                    throw new SecurityException("Resolved path jumped beyond configured root");
                }
                return file2;
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }
        }
    }

    private static File buildPath(File base, String... segments) {
        int i = 0;
        File cur = base;
        while (i <= 0) {
            String segment = segments[0];
            i++;
            cur = segment != null ? new File(cur, segment) : cur;
        }
        return cur;
    }
}
