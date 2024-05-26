package com.nuance.connect.internal;

import com.nuance.connect.internal.Property;
import com.nuance.connect.store.PersistentDataStore;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class GenericProperty {
    protected Property<?> reference;

    /* loaded from: classes.dex */
    public static class BooleanProperty extends GenericProperty implements Property<Boolean> {
        private String key;
        private final List<WeakReference<Property.ValueListener<Boolean>>> listeners;
        private int overrideFlags;
        private Property.Source source;
        private PersistentDataStore store;
        private Boolean value;
        private int verification;
        private Property.Verifier<Boolean> verifier;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class SimpleBooleanProperty implements Serializable {
            private static final long serialVersionUID = 2722068787372564484L;
            String source;
            Boolean value;

            SimpleBooleanProperty(Boolean bool, Property.Source source) {
                this.value = bool;
                this.source = source.name();
            }

            Property.Source getSource() {
                return Property.Source.from(this.source);
            }

            Boolean getValue() {
                return this.value;
            }
        }

        public BooleanProperty(String str, Boolean bool) {
            this.verification = 1;
            this.verifier = new DefaultBooleanVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.key = str;
            this.value = bool;
            this.source = Property.Source.DEFAULT;
        }

        public BooleanProperty(String str, Boolean bool, PersistentDataStore persistentDataStore) {
            this.verification = 1;
            this.verifier = new DefaultBooleanVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            loadValue(bool, Property.Source.DEFAULT);
        }

        public BooleanProperty(String str, Boolean bool, PersistentDataStore persistentDataStore, int i, int i2, Property.Verifier<Boolean> verifier) {
            this.verification = 1;
            this.verifier = new DefaultBooleanVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            this.verification = i;
            this.overrideFlags = i2;
            if (verifier != null) {
                this.verifier = verifier;
            }
            loadValue(bool, Property.Source.DEFAULT);
        }

        private boolean allowOverride(Property.Source source) {
            if (this.source.equals(Property.Source.CONNECT_DAT)) {
                return false;
            }
            if (Property.Source.CONNECT_DAT.equals(source) && (this.overrideFlags & 2) != 2) {
                return false;
            }
            if (!Property.Source.SERVER.equals(source) || (this.overrideFlags & 1) == 1) {
                return !Property.Source.DEFAULT.equals(source) || this.source.equals(Property.Source.DEFAULT);
            }
            return false;
        }

        private static <T> void helper(Property<T> property, Object obj, Property.Source source) {
            try {
                if (obj instanceof String) {
                    if (property.getTypeArgument().equals(Boolean.class)) {
                        property.setValue(property.getTypeArgument().cast(Boolean.valueOf(Boolean.parseBoolean((String) obj))), source);
                    }
                } else if (obj instanceof Boolean) {
                    property.setValue(property.getTypeArgument().cast(obj), source);
                } else {
                    if (obj != null) {
                        throw new IllegalArgumentException("Invalid argument supplied for Property<Boolean>.set() " + obj.getClass());
                    }
                    property.setValue(null, source);
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid argument supplied for Property<Boolean>.set() ");
            }
        }

        private void loadValue(Boolean bool, Property.Source source) {
            SimpleBooleanProperty simpleBooleanProperty;
            if (this.store != null) {
                Object readObject = this.store.readObject(this.key);
                simpleBooleanProperty = readObject instanceof SimpleBooleanProperty ? (SimpleBooleanProperty) readObject : null;
            } else {
                simpleBooleanProperty = null;
            }
            if (simpleBooleanProperty != null) {
                this.value = simpleBooleanProperty.getValue();
                this.source = simpleBooleanProperty.getSource();
                return;
            }
            this.value = bool;
            this.source = source;
            if (this.store == null || this.store.exists(this.key) || bool == null) {
                return;
            }
            save();
        }

        private void notifyListeners() {
            Iterator<WeakReference<Property.ValueListener<Boolean>>> it = this.listeners.iterator();
            while (it.hasNext()) {
                Property.ValueListener<Boolean> valueListener = it.next().get();
                if (valueListener != null) {
                    valueListener.onValueChanged(this);
                }
            }
        }

        private void save() {
            SimpleBooleanProperty simpleBooleanProperty = new SimpleBooleanProperty(this.value, this.source);
            if (this.store != null) {
                this.store.saveObject(this.key, simpleBooleanProperty);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<Boolean> valueListener) {
            addListener(valueListener, false);
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<Boolean> valueListener, boolean z) {
            this.listeners.add(new WeakReference<>(valueListener));
            if (z) {
                valueListener.onValueChanged(this);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListeners(List<Property.ValueListener<Boolean>> list) {
            Iterator<Property.ValueListener<Boolean>> it = list.iterator();
            while (it.hasNext()) {
                addListener(it.next());
            }
        }

        @Override // com.nuance.connect.internal.Property
        public String getKey() {
            return this.key;
        }

        @Override // com.nuance.connect.internal.Property
        public int getOverrideFlags() {
            return this.overrideFlags;
        }

        @Override // com.nuance.connect.internal.Property
        public Property.Source getSource() {
            return this.source;
        }

        @Override // com.nuance.connect.internal.Property
        public Class<Boolean> getTypeArgument() {
            return Boolean.class;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.nuance.connect.internal.Property
        public Boolean getValue() {
            return this.value;
        }

        @Override // com.nuance.connect.internal.Property
        public int getVerification() {
            return this.verification;
        }

        @Override // com.nuance.connect.internal.Property
        public void set(Object obj, Property.Source source) {
            helper(this.reference, obj, source);
        }

        @Override // com.nuance.connect.internal.Property
        public void setOverrideFlags(int i) {
            this.overrideFlags = i;
        }

        @Override // com.nuance.connect.internal.Property
        public void setValue(Boolean bool, Property.Source source) {
            if (((this.value == null || bool == null || this.value.equals(bool)) && (bool == null || bool.equals(this.value))) || !allowOverride(source)) {
                return;
            }
            this.value = bool;
            this.source = source;
            save();
            notifyListeners();
        }

        public String toString() {
            return this.value != null ? String.valueOf(this.value) : "null";
        }

        @Override // com.nuance.connect.internal.Property
        public boolean verify(Object obj, Property.Source source) {
            if (this.verifier != null) {
                return this.verifier.verify(obj, this.source, this);
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class DefaultBooleanVerifier implements Property.Verifier<Boolean> {
        DefaultBooleanVerifier() {
        }

        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<Boolean> property) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    static class DefaultIntegerVerifier implements Property.Verifier<Integer> {
        DefaultIntegerVerifier() {
        }

        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<Integer> property) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    static class DefaultLongVerifier implements Property.Verifier<Long> {
        DefaultLongVerifier() {
        }

        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<Long> property) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    static class DefaultStringVerifier implements Property.Verifier<String> {
        DefaultStringVerifier() {
        }

        @Override // com.nuance.connect.internal.Property.Verifier
        public boolean verify(Object obj, Property.Source source, Property<String> property) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    public static class IntegerProperty extends GenericProperty implements Property<Integer> {
        private String key;
        private final List<WeakReference<Property.ValueListener<Integer>>> listeners;
        private int overrideFlags;
        private Property.Source source;
        private PersistentDataStore store;
        private Integer value;
        private int verification;
        private Property.Verifier<Integer> verifier;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class SimpleIntegerProperty implements Serializable {
            private static final long serialVersionUID = 8538213521223464490L;
            String source;
            Integer value;

            SimpleIntegerProperty(Integer num, Property.Source source) {
                this.value = num;
                this.source = source.name();
            }

            Property.Source getSource() {
                return Property.Source.from(this.source);
            }

            Integer getValue() {
                return this.value;
            }
        }

        public IntegerProperty(String str, Integer num) {
            this.verification = 1;
            this.source = Property.Source.DEFAULT;
            this.verifier = new DefaultIntegerVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.key = str;
            this.value = num;
        }

        public IntegerProperty(String str, Integer num, PersistentDataStore persistentDataStore) {
            this.verification = 1;
            this.source = Property.Source.DEFAULT;
            this.verifier = new DefaultIntegerVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            loadPersistedData(num, Property.Source.DEFAULT);
        }

        public IntegerProperty(String str, Integer num, PersistentDataStore persistentDataStore, int i, int i2, Property.Verifier<Integer> verifier) {
            this.verification = 1;
            this.source = Property.Source.DEFAULT;
            this.verifier = new DefaultIntegerVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            this.verification = i;
            this.overrideFlags = i2;
            if (verifier != null) {
                this.verifier = verifier;
            }
            loadPersistedData(num, Property.Source.DEFAULT);
        }

        private boolean allowOverride(Property.Source source) {
            if (this.source.equals(Property.Source.CONNECT_DAT)) {
                return false;
            }
            if (Property.Source.CONNECT_DAT.equals(source) && (this.overrideFlags & 2) != 2) {
                return false;
            }
            if (!Property.Source.SERVER.equals(source) || (this.overrideFlags & 1) == 1) {
                return !Property.Source.DEFAULT.equals(source) || this.source.equals(Property.Source.DEFAULT);
            }
            return false;
        }

        private static <T> void helper(Property<T> property, Object obj, Property.Source source) {
            try {
                if (obj instanceof String) {
                    String str = (String) String.class.cast(obj);
                    if (property.getTypeArgument().equals(Integer.class)) {
                        property.setValue(property.getTypeArgument().cast(Integer.valueOf(Integer.parseInt(str))), source);
                        return;
                    }
                    return;
                }
                if (obj instanceof Integer) {
                    property.setValue(property.getTypeArgument().cast(obj), source);
                } else {
                    if (obj != null) {
                        throw new IllegalArgumentException("Invalid argument supplied for Property<Integer>.set() " + obj.getClass());
                    }
                    property.setValue(null, source);
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid argument supplied for Property<Integer>.set() ");
            }
        }

        private void loadPersistedData(Integer num, Property.Source source) {
            SimpleIntegerProperty simpleIntegerProperty;
            if (this.store != null) {
                Object readObject = this.store.readObject(this.key);
                simpleIntegerProperty = readObject instanceof SimpleIntegerProperty ? (SimpleIntegerProperty) readObject : null;
            } else {
                simpleIntegerProperty = null;
            }
            if (simpleIntegerProperty != null) {
                this.value = simpleIntegerProperty.getValue();
                this.source = simpleIntegerProperty.getSource();
                return;
            }
            this.value = num;
            this.source = source;
            if (this.store == null || this.store.exists(this.key) || num == null) {
                return;
            }
            save();
        }

        private void notifyListeners() {
            Iterator<WeakReference<Property.ValueListener<Integer>>> it = this.listeners.iterator();
            while (it.hasNext()) {
                Property.ValueListener<Integer> valueListener = it.next().get();
                if (valueListener != null) {
                    valueListener.onValueChanged(this);
                }
            }
        }

        private void save() {
            SimpleIntegerProperty simpleIntegerProperty = new SimpleIntegerProperty(this.value, this.source);
            if (this.store != null) {
                this.store.saveObject(this.key, simpleIntegerProperty);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<Integer> valueListener) {
            addListener(valueListener, false);
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<Integer> valueListener, boolean z) {
            this.listeners.add(new WeakReference<>(valueListener));
            if (z) {
                valueListener.onValueChanged(this);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListeners(List<Property.ValueListener<Integer>> list) {
            Iterator<Property.ValueListener<Integer>> it = list.iterator();
            while (it.hasNext()) {
                addListener(it.next());
            }
        }

        @Override // com.nuance.connect.internal.Property
        public String getKey() {
            return this.key;
        }

        @Override // com.nuance.connect.internal.Property
        public int getOverrideFlags() {
            return this.overrideFlags;
        }

        @Override // com.nuance.connect.internal.Property
        public Property.Source getSource() {
            return this.source;
        }

        @Override // com.nuance.connect.internal.Property
        public Class<Integer> getTypeArgument() {
            return Integer.class;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.nuance.connect.internal.Property
        public Integer getValue() {
            return this.value;
        }

        @Override // com.nuance.connect.internal.Property
        public int getVerification() {
            return this.verification;
        }

        @Override // com.nuance.connect.internal.Property
        public void set(Object obj, Property.Source source) {
            helper(this.reference, obj, source);
        }

        @Override // com.nuance.connect.internal.Property
        public void setOverrideFlags(int i) {
            this.overrideFlags = i;
        }

        @Override // com.nuance.connect.internal.Property
        public void setValue(Integer num, Property.Source source) {
            if (((this.value == null || num == null || this.value.equals(num)) && (num == null || num.equals(this.value))) || !allowOverride(source)) {
                return;
            }
            this.value = num;
            this.source = source;
            save();
            notifyListeners();
        }

        public String toString() {
            return this.value != null ? String.valueOf(this.value) : "null";
        }

        @Override // com.nuance.connect.internal.Property
        public boolean verify(Object obj, Property.Source source) {
            if (this.verifier != null) {
                return this.verifier.verify(obj, source, this);
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class LongProperty extends GenericProperty implements Property<Long> {
        private String key;
        private final List<WeakReference<Property.ValueListener<Long>>> listeners;
        private int overrideFlags;
        private Property.Source source;
        private PersistentDataStore store;
        private Long value;
        private int verification;
        private Property.Verifier<Long> verifier;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class SimpleLongProperty implements Serializable {
            private static final long serialVersionUID = -7382535113982690727L;
            String source;
            Long value;

            SimpleLongProperty(Long l, Property.Source source) {
                this.value = l;
                this.source = source.name();
            }

            Property.Source getSource() {
                return Property.Source.from(this.source);
            }

            Long getValue() {
                return this.value;
            }
        }

        public LongProperty(String str, Long l) {
            this.verification = 1;
            this.source = Property.Source.DEFAULT;
            this.verifier = new DefaultLongVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.key = str;
            this.value = l;
        }

        public LongProperty(String str, Long l, PersistentDataStore persistentDataStore) {
            this.verification = 1;
            this.source = Property.Source.DEFAULT;
            this.verifier = new DefaultLongVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            loadPersistedData(l, Property.Source.DEFAULT);
        }

        public LongProperty(String str, Long l, PersistentDataStore persistentDataStore, int i, int i2, Property.Verifier<Long> verifier) {
            this.verification = 1;
            this.source = Property.Source.DEFAULT;
            this.verifier = new DefaultLongVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            this.verification = i;
            this.overrideFlags = i2;
            if (verifier != null) {
                this.verifier = verifier;
            }
            loadPersistedData(l, Property.Source.DEFAULT);
        }

        private boolean allowOverride(Property.Source source) {
            if (this.source.equals(Property.Source.CONNECT_DAT)) {
                return false;
            }
            if (Property.Source.CONNECT_DAT.equals(source) && (this.overrideFlags & 2) != 2) {
                return false;
            }
            if (!Property.Source.SERVER.equals(source) || (this.overrideFlags & 1) == 1) {
                return !Property.Source.DEFAULT.equals(source) || this.source.equals(Property.Source.DEFAULT);
            }
            return false;
        }

        private static <T> void helper(Property<T> property, Object obj, Property.Source source) {
            try {
                if (obj instanceof String) {
                    String str = (String) String.class.cast(obj);
                    if (property.getTypeArgument().equals(Long.class)) {
                        property.setValue(property.getTypeArgument().cast(Long.valueOf(Long.parseLong(str))), source);
                        return;
                    }
                    return;
                }
                if (obj instanceof Long) {
                    property.setValue(property.getTypeArgument().cast(obj), source);
                } else {
                    if (obj != null) {
                        throw new IllegalArgumentException("Invalid argument supplied for Property<Long>.set() " + obj.getClass());
                    }
                    property.setValue(null, source);
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid argument supplied for Property<Long>.set() ");
            }
        }

        private void loadPersistedData(Long l, Property.Source source) {
            SimpleLongProperty simpleLongProperty;
            if (this.store != null) {
                Object readObject = this.store.readObject(this.key);
                simpleLongProperty = readObject instanceof SimpleLongProperty ? (SimpleLongProperty) readObject : null;
            } else {
                simpleLongProperty = null;
            }
            if (simpleLongProperty != null) {
                this.value = simpleLongProperty.getValue();
                this.source = simpleLongProperty.getSource();
                return;
            }
            this.value = l;
            this.source = source;
            if (this.store == null || this.store.exists(this.key) || l == null) {
                return;
            }
            save();
        }

        private void notifyListeners() {
            Iterator<WeakReference<Property.ValueListener<Long>>> it = this.listeners.iterator();
            while (it.hasNext()) {
                Property.ValueListener<Long> valueListener = it.next().get();
                if (valueListener != null) {
                    valueListener.onValueChanged(this);
                }
            }
        }

        private void save() {
            SimpleLongProperty simpleLongProperty = new SimpleLongProperty(this.value, this.source);
            if (this.store != null) {
                this.store.saveObject(this.key, simpleLongProperty);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<Long> valueListener) {
            addListener(valueListener, false);
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<Long> valueListener, boolean z) {
            this.listeners.add(new WeakReference<>(valueListener));
            if (z) {
                valueListener.onValueChanged(this);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListeners(List<Property.ValueListener<Long>> list) {
            Iterator<Property.ValueListener<Long>> it = list.iterator();
            while (it.hasNext()) {
                addListener(it.next());
            }
        }

        @Override // com.nuance.connect.internal.Property
        public String getKey() {
            return this.key;
        }

        @Override // com.nuance.connect.internal.Property
        public int getOverrideFlags() {
            return this.overrideFlags;
        }

        @Override // com.nuance.connect.internal.Property
        public Property.Source getSource() {
            return this.source;
        }

        @Override // com.nuance.connect.internal.Property
        public Class<Long> getTypeArgument() {
            return Long.class;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.nuance.connect.internal.Property
        public Long getValue() {
            return this.value;
        }

        @Override // com.nuance.connect.internal.Property
        public int getVerification() {
            return this.verification;
        }

        @Override // com.nuance.connect.internal.Property
        public void set(Object obj, Property.Source source) {
            helper(this.reference, obj, source);
        }

        @Override // com.nuance.connect.internal.Property
        public void setOverrideFlags(int i) {
            this.overrideFlags = i;
        }

        @Override // com.nuance.connect.internal.Property
        public void setValue(Long l, Property.Source source) {
            if (((this.value == null || this.value.equals(l)) && (l == null || l.equals(this.value))) || !allowOverride(source)) {
                return;
            }
            this.value = l;
            this.source = source;
            save();
            notifyListeners();
        }

        public String toString() {
            return this.value != null ? String.valueOf(this.value) : "null";
        }

        @Override // com.nuance.connect.internal.Property
        public boolean verify(Object obj, Property.Source source) {
            if (this.verifier != null) {
                return this.verifier.verify(obj, source, this);
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class StringProperty extends GenericProperty implements Property<String> {
        private String key;
        private final List<WeakReference<Property.ValueListener<String>>> listeners;
        private int overrideFlags;
        private Property.Source source;
        private PersistentDataStore store;
        private String value;
        private int verification;
        private Property.Verifier<String> verifier;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class SimpleStringProperty implements Serializable {
            private static final long serialVersionUID = -5394360188502472341L;
            String source;
            String value;

            SimpleStringProperty(String str, Property.Source source) {
                this.value = str;
                this.source = source.name();
            }

            Property.Source getSource() {
                return Property.Source.from(this.source);
            }

            String getValue() {
                return this.value;
            }
        }

        public StringProperty(String str, String str2) {
            this.verification = 1;
            this.verifier = new DefaultStringVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.key = str;
            this.value = str2;
            this.source = Property.Source.DEFAULT;
        }

        public StringProperty(String str, String str2, PersistentDataStore persistentDataStore) {
            this.verification = 1;
            this.verifier = new DefaultStringVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            loadValue(str2, Property.Source.DEFAULT);
        }

        public StringProperty(String str, String str2, PersistentDataStore persistentDataStore, int i, int i2, Property.Verifier<String> verifier) {
            this.verification = 1;
            this.verifier = new DefaultStringVerifier();
            this.listeners = new ArrayList();
            this.reference = this;
            this.store = persistentDataStore;
            this.key = str;
            this.verification = i;
            this.overrideFlags = i2;
            if (verifier != null) {
                this.verifier = verifier;
            }
            loadValue(str2, Property.Source.DEFAULT);
        }

        private boolean allowOverride(Property.Source source) {
            if (this.source.equals(Property.Source.CONNECT_DAT)) {
                return false;
            }
            if (Property.Source.CONNECT_DAT.equals(source) && (this.overrideFlags & 2) != 2) {
                return false;
            }
            if (!Property.Source.SERVER.equals(source) || (this.overrideFlags & 1) == 1) {
                return !Property.Source.DEFAULT.equals(source) || this.source.equals(Property.Source.DEFAULT);
            }
            return false;
        }

        private static <T> void helper(Property<T> property, Object obj, Property.Source source) {
            try {
                property.setValue(property.getTypeArgument().cast(obj), source);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid argument supplied for Property<String>.set() " + obj.getClass());
            }
        }

        private void loadValue(String str, Property.Source source) {
            SimpleStringProperty simpleStringProperty;
            if (this.store != null) {
                Object readObject = this.store.readObject(this.key);
                simpleStringProperty = readObject instanceof SimpleStringProperty ? (SimpleStringProperty) readObject : null;
            } else {
                simpleStringProperty = null;
            }
            if (simpleStringProperty != null) {
                this.value = simpleStringProperty.getValue();
                this.source = simpleStringProperty.getSource();
                return;
            }
            this.value = str;
            this.source = source;
            if (this.store == null || this.store.exists(this.key) || str == null) {
                return;
            }
            save();
        }

        private void notifyListeners() {
            Iterator<WeakReference<Property.ValueListener<String>>> it = this.listeners.iterator();
            while (it.hasNext()) {
                Property.ValueListener<String> valueListener = it.next().get();
                if (valueListener != null) {
                    valueListener.onValueChanged(this);
                }
            }
        }

        private void save() {
            SimpleStringProperty simpleStringProperty = new SimpleStringProperty(this.value, this.source);
            if (this.store != null) {
                this.store.saveObject(this.key, simpleStringProperty);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<String> valueListener) {
            addListener(valueListener, false);
        }

        @Override // com.nuance.connect.internal.Property
        public void addListener(Property.ValueListener<String> valueListener, boolean z) {
            this.listeners.add(new WeakReference<>(valueListener));
            if (z) {
                valueListener.onValueChanged(this);
            }
        }

        @Override // com.nuance.connect.internal.Property
        public void addListeners(List<Property.ValueListener<String>> list) {
            Iterator<Property.ValueListener<String>> it = list.iterator();
            while (it.hasNext()) {
                addListener(it.next());
            }
        }

        @Override // com.nuance.connect.internal.Property
        public String getKey() {
            return this.key;
        }

        @Override // com.nuance.connect.internal.Property
        public int getOverrideFlags() {
            return this.overrideFlags;
        }

        @Override // com.nuance.connect.internal.Property
        public Property.Source getSource() {
            return this.source;
        }

        @Override // com.nuance.connect.internal.Property
        public Class<String> getTypeArgument() {
            return String.class;
        }

        @Override // com.nuance.connect.internal.Property
        public String getValue() {
            return this.value;
        }

        @Override // com.nuance.connect.internal.Property
        public int getVerification() {
            return this.verification;
        }

        @Override // com.nuance.connect.internal.Property
        public void set(Object obj, Property.Source source) {
            helper(this.reference, obj, source);
        }

        @Override // com.nuance.connect.internal.Property
        public void setOverrideFlags(int i) {
            this.overrideFlags = i;
        }

        @Override // com.nuance.connect.internal.Property
        public void setValue(String str, Property.Source source) {
            if (((this.value == null || this.value.equals(str)) && (str == null || str.equals(this.value))) || !allowOverride(source)) {
                return;
            }
            this.value = str;
            this.source = source;
            save();
            notifyListeners();
        }

        public String toString() {
            return this.value;
        }

        @Override // com.nuance.connect.internal.Property
        public boolean verify(Object obj, Property.Source source) {
            if (this.verifier != null) {
                return this.verifier.verify(obj, source, this);
            }
            return false;
        }
    }

    protected GenericProperty() {
    }
}
