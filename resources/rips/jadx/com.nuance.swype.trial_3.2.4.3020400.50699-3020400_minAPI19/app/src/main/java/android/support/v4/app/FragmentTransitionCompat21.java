package android.support.v4.app;

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class FragmentTransitionCompat21 {

    /* loaded from: classes.dex */
    public static class EpicenterView {
        public View epicenter;
    }

    /* loaded from: classes.dex */
    public interface ViewRetriever {
        View getView();
    }

    public static Object cloneTransition(Object transition) {
        if (transition != null) {
            return ((Transition) transition).clone();
        }
        return transition;
    }

    public static void excludeTarget(Object transitionObject, View view, boolean exclude) {
        ((Transition) transitionObject).excludeTarget(view, exclude);
    }

    public static void setEpicenter(Object transitionObject, View view) {
        Transition transition = (Transition) transitionObject;
        final Rect epicenter = getBoundsOnScreen(view);
        transition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.support.v4.app.FragmentTransitionCompat21.1
            @Override // android.transition.Transition.EpicenterCallback
            public final Rect onGetEpicenter(Transition transition2) {
                return epicenter;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void excludeViews(Transition transition, Transition fromTransition, ArrayList<View> views, boolean exclude) {
        if (transition != null) {
            int viewCount = fromTransition == null ? 0 : views.size();
            for (int i = 0; i < viewCount; i++) {
                transition.excludeTarget(views.get(i), exclude);
            }
        }
    }

    public static void excludeSharedElementViews(Object enterTransitionObj, Object exitTransitionObj, Object sharedElementTransitionObj, ArrayList<View> views, boolean exclude) {
        Transition enterTransition = (Transition) enterTransitionObj;
        Transition exitTransition = (Transition) exitTransitionObj;
        Transition sharedElementTransition = (Transition) sharedElementTransitionObj;
        excludeViews(enterTransition, sharedElementTransition, views, exclude);
        excludeViews(exitTransition, sharedElementTransition, views, exclude);
    }

    public static void setSharedElementTargets(Object transitionObj, View nonExistentView, Map<String, View> namedViews, ArrayList<View> sharedElementTargets) {
        TransitionSet transition = (TransitionSet) transitionObj;
        sharedElementTargets.clear();
        sharedElementTargets.addAll(namedViews.values());
        List<View> views = transition.getTargets();
        views.clear();
        int count = sharedElementTargets.size();
        for (int i = 0; i < count; i++) {
            View view = sharedElementTargets.get(i);
            int size = views.size();
            if (!containedBeforeIndex(views, view, size)) {
                views.add(view);
                for (int i2 = size; i2 < views.size(); i2++) {
                    View view2 = views.get(i2);
                    if (view2 instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) view2;
                        int childCount = viewGroup.getChildCount();
                        for (int i3 = 0; i3 < childCount; i3++) {
                            View childAt = viewGroup.getChildAt(i3);
                            if (!containedBeforeIndex(views, childAt, size)) {
                                views.add(childAt);
                            }
                        }
                    }
                }
            }
        }
        sharedElementTargets.add(nonExistentView);
        addTargets(transition, sharedElementTargets);
    }

    private static boolean containedBeforeIndex(List<View> views, View view, int maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            if (views.get(i) == view) {
                return true;
            }
        }
        return false;
    }

    static Rect getBoundsOnScreen(View view) {
        Rect epicenter = new Rect();
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        epicenter.set(loc[0], loc[1], loc[0] + view.getWidth(), loc[1] + view.getHeight());
        return epicenter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void captureTransitioningViews(ArrayList<View> transitioningViews, View view) {
        if (view.getVisibility() == 0) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                if (viewGroup.isTransitionGroup()) {
                    transitioningViews.add(viewGroup);
                    return;
                }
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = viewGroup.getChildAt(i);
                    captureTransitioningViews(transitioningViews, child);
                }
                return;
            }
            transitioningViews.add(view);
        }
    }

    public static void findNamedViews(Map<String, View> namedViews, View view) {
        if (view.getVisibility() == 0) {
            String transitionName = view.getTransitionName();
            if (transitionName != null) {
                namedViews.put(transitionName, view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = viewGroup.getChildAt(i);
                    findNamedViews(namedViews, child);
                }
            }
        }
    }

    public static void removeTargets(Object transitionObject, ArrayList<View> views) {
        List<View> targets;
        Transition transition = (Transition) transitionObject;
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (int i = 0; i < numTransitions; i++) {
                removeTargets(set.getTransitionAt(i), views);
            }
            return;
        }
        if (!hasSimpleTarget(transition) && (targets = transition.getTargets()) != null && targets.size() == views.size() && targets.containsAll(views)) {
            for (int i2 = views.size() - 1; i2 >= 0; i2--) {
                transition.removeTarget(views.get(i2));
            }
        }
    }

    public static void addTargets(Object transitionObject, ArrayList<View> views) {
        Transition transition = (Transition) transitionObject;
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (int i = 0; i < numTransitions; i++) {
                addTargets(set.getTransitionAt(i), views);
            }
            return;
        }
        if (!hasSimpleTarget(transition) && isNullOrEmpty(transition.getTargets())) {
            int numViews = views.size();
            for (int i2 = 0; i2 < numViews; i2++) {
                transition.addTarget(views.get(i2));
            }
        }
    }

    private static boolean hasSimpleTarget(Transition transition) {
        return (isNullOrEmpty(transition.getTargetIds()) && isNullOrEmpty(transition.getTargetNames()) && isNullOrEmpty(transition.getTargetTypes())) ? false : true;
    }

    private static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }
}
