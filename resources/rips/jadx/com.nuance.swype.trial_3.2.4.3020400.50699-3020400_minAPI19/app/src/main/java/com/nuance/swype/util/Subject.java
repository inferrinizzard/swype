package com.nuance.swype.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public final class Subject {
    private final Set<Observer> observers = new HashSet();

    public final void addObserver(Observer o) {
        this.observers.add(o);
    }

    public final void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    public final void doNotify() {
        Iterator<Observer> it = this.observers.iterator();
        while (it.hasNext()) {
            it.next().update();
        }
    }
}
