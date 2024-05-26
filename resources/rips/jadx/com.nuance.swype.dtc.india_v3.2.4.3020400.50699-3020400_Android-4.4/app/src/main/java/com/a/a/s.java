package com.a.a;

import com.a.a.g;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: Add missing generic type declarations: [FieldDescriptorType] */
/* loaded from: classes.dex */
public final class s<FieldDescriptorType> extends r<FieldDescriptorType, Object> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public s(int i) {
        super(i, (byte) 0);
    }

    @Override // com.a.a.r, java.util.AbstractMap, java.util.Map
    public final /* synthetic */ Object put(Object obj, Object obj2) {
        return super.a((s<FieldDescriptorType>) obj, (g.a) obj2);
    }

    @Override // com.a.a.r
    public final void a() {
        if (!this.d) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= c()) {
                    break;
                }
                Map.Entry<FieldDescriptorType, Object> b = b(i2);
                if (((g.a) b.getKey()).b()) {
                    b.setValue(Collections.unmodifiableList((List) b.getValue()));
                }
                i = i2 + 1;
            }
            for (Map.Entry<FieldDescriptorType, Object> entry : d()) {
                if (((g.a) entry.getKey()).b()) {
                    entry.setValue(Collections.unmodifiableList((List) entry.getValue()));
                }
            }
        }
        super.a();
    }
}
