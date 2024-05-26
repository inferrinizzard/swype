package android.support.v4.view;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompatBase;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
final class LayoutInflaterCompatHC {
    private static boolean sCheckedField;
    private static Field sLayoutInflaterFactory2Field;

    /* loaded from: classes.dex */
    static class FactoryWrapperHC extends LayoutInflaterCompatBase.FactoryWrapper implements LayoutInflater.Factory2 {
        /* JADX INFO: Access modifiers changed from: package-private */
        public FactoryWrapperHC(LayoutInflaterFactory delegateFactory) {
            super(delegateFactory);
        }

        @Override // android.view.LayoutInflater.Factory2
        public final View onCreateView(View parent, String name, Context context, AttributeSet attributeSet) {
            return this.mDelegateFactory.onCreateView(parent, name, context, attributeSet);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        if (!sCheckedField) {
            try {
                Field declaredField = LayoutInflater.class.getDeclaredField("mFactory2");
                sLayoutInflaterFactory2Field = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.e("LayoutInflaterCompatHC", "forceSetFactory2 Could not find field 'mFactory2' on class " + LayoutInflater.class.getName() + "; inflation may have unexpected results.", e);
            }
            sCheckedField = true;
        }
        if (sLayoutInflaterFactory2Field != null) {
            try {
                sLayoutInflaterFactory2Field.set(inflater, factory);
            } catch (IllegalAccessException e2) {
                Log.e("LayoutInflaterCompatHC", "forceSetFactory2 could not set the Factory2 on LayoutInflater " + inflater + "; inflation may have unexpected results.", e2);
            }
        }
    }
}
