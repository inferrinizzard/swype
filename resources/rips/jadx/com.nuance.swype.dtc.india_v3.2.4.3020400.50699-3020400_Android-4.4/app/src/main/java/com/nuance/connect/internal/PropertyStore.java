package com.nuance.connect.internal;

import com.nuance.connect.internal.Property;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class PropertyStore {
    private final HashMap<String, Property<?>> properties = new HashMap<>();
    private final HashMap<String, List<WeakReference<Property.ValueListener<?>>>> listeners = new HashMap<>();

    private static <T> void add(Property<T> property, Property.ValueListener<T> valueListener) {
        if (property.getTypeArgument().equals(valueListener.getTypeArgument())) {
            property.addListener(valueListener, true);
        }
    }

    private static void addHelper(Property<?> property, Property.ValueListener<?> valueListener) {
        if (property == null || valueListener == null) {
            return;
        }
        if (Boolean.class.equals(property.getTypeArgument()) && Boolean.class.equals(valueListener.getTypeArgument())) {
            add(property, valueListener);
            return;
        }
        if (Integer.class.equals(property.getTypeArgument()) && Integer.class.equals(valueListener.getTypeArgument())) {
            add(property, valueListener);
            return;
        }
        if (String.class.equals(property.getTypeArgument()) && String.class.equals(valueListener.getTypeArgument())) {
            add(property, valueListener);
        } else if (Long.class.equals(property.getTypeArgument()) && Long.class.equals(valueListener.getTypeArgument())) {
            add(property, valueListener);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Property<Boolean> getBooleanHelper(Property<?> property) {
        if (property == 0 || !Boolean.class.equals(property.getTypeArgument())) {
            return null;
        }
        return property;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Property<Integer> getIntegerHelper(Property<?> property) {
        if (property == 0 || !Integer.class.equals(property.getTypeArgument())) {
            return null;
        }
        return property;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Property<Long> getLongHelper(Property<?> property) {
        if (property == 0 || !Long.class.equals(property.getTypeArgument())) {
            return null;
        }
        return property;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Property<String> getStringHelper(Property<?> property) {
        if (property == 0 || !String.class.equals(property.getTypeArgument())) {
            return null;
        }
        return property;
    }

    public void addListener(String str, Property.ValueListener<?> valueListener) {
        Property<?> property = this.properties.get(str);
        if (property != null) {
            addHelper(property, valueListener);
            return;
        }
        List<WeakReference<Property.ValueListener<?>>> list = this.listeners.get(str);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(new WeakReference<>(valueListener));
        this.listeners.put(str, list);
    }

    public Boolean getBoolean(String str) {
        Property<Boolean> booleanHelper = getBooleanHelper(this.properties.get(str));
        return booleanHelper != null ? booleanHelper.getValue() : Boolean.FALSE;
    }

    public Integer getInteger(String str) {
        Property<Integer> integerHelper = getIntegerHelper(this.properties.get(str));
        if (integerHelper != null) {
            return integerHelper.getValue();
        }
        return null;
    }

    public Long getLong(String str) {
        Property<Long> longHelper = getLongHelper(this.properties.get(str));
        if (longHelper != null) {
            return longHelper.getValue();
        }
        return null;
    }

    public Property<?> getProperty(String str) {
        return this.properties.get(str);
    }

    public String getString(String str) {
        Property<String> stringHelper = getStringHelper(this.properties.get(str));
        if (stringHelper != null) {
            return stringHelper.getValue();
        }
        return null;
    }

    public Set<String> keySet() {
        return this.properties.keySet();
    }

    public void setProperty(Property<?> property) {
        Property<?> property2 = this.properties.get(property.getKey());
        if (property2 != null) {
            property2.set(property.getValue(), property.getSource());
            return;
        }
        this.properties.put(property.getKey(), property);
        if (this.listeners.get(property.getKey()) != null) {
            Iterator<WeakReference<Property.ValueListener<?>>> it = this.listeners.get(property.getKey()).iterator();
            while (it.hasNext()) {
                addHelper(property, it.next().get());
            }
        }
        this.listeners.remove(property.getKey());
    }
}
