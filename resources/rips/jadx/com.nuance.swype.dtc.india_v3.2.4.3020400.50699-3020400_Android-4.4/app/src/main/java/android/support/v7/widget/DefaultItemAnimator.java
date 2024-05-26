package android.support.v7.widget;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class DefaultItemAnimator extends SimpleItemAnimator {
    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MoveInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder holder;
        public int toX;
        public int toY;

        /* synthetic */ MoveInfo(RecyclerView.ViewHolder x0, int x1, int x2, int x3, int x4, byte b) {
            this(x0, x1, x2, x3, x4);
        }

        private MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ChangeInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder newHolder;
        public RecyclerView.ViewHolder oldHolder;
        public int toX;
        public int toY;

        /* synthetic */ ChangeInfo(RecyclerView.ViewHolder x0, RecyclerView.ViewHolder x1, int x2, int x3, int x4, int x5, byte b) {
            this(x0, x1, x2, x3, x4, x5);
        }

        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public final String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    @Override // android.support.v7.widget.RecyclerView.ItemAnimator
    public final void runPendingAnimations() {
        long removeDuration;
        long moveDuration;
        long changeDuration;
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (removalsPending || movesPending || additionsPending || changesPending) {
            Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                final RecyclerView.ViewHolder holder = it.next();
                final ViewPropertyAnimatorCompat animate = ViewCompat.animate(holder.itemView);
                this.mRemoveAnimations.add(holder);
                animate.setDuration(this.mRemoveDuration).alpha(0.0f).setListener(new VpaListenerAdapter() { // from class: android.support.v7.widget.DefaultItemAnimator.4
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super((byte) 0);
                    }

                    @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                    public final void onAnimationStart(View view) {
                    }

                    @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                    public final void onAnimationEnd(View view) {
                        animate.setListener(null);
                        ViewCompat.setAlpha(view, 1.0f);
                        DefaultItemAnimator.this.dispatchAnimationFinished(holder);
                        DefaultItemAnimator.this.mRemoveAnimations.remove(holder);
                        DefaultItemAnimator.this.dispatchFinishedWhenDone();
                    }
                }).start();
            }
            this.mPendingRemovals.clear();
            if (movesPending) {
                final ArrayList<MoveInfo> moves = new ArrayList<>();
                moves.addAll(this.mPendingMoves);
                this.mMovesList.add(moves);
                this.mPendingMoves.clear();
                Runnable mover = new Runnable() { // from class: android.support.v7.widget.DefaultItemAnimator.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Iterator it2 = moves.iterator();
                        while (it2.hasNext()) {
                            MoveInfo moveInfo = (MoveInfo) it2.next();
                            final DefaultItemAnimator defaultItemAnimator = DefaultItemAnimator.this;
                            final RecyclerView.ViewHolder viewHolder = moveInfo.holder;
                            int i = moveInfo.fromX;
                            int i2 = moveInfo.fromY;
                            int i3 = moveInfo.toX;
                            int i4 = moveInfo.toY;
                            View view = viewHolder.itemView;
                            final int i5 = i3 - i;
                            final int i6 = i4 - i2;
                            if (i5 != 0) {
                                ViewCompat.animate(view).translationX(0.0f);
                            }
                            if (i6 != 0) {
                                ViewCompat.animate(view).translationY(0.0f);
                            }
                            final ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(view);
                            defaultItemAnimator.mMoveAnimations.add(viewHolder);
                            animate2.setDuration(defaultItemAnimator.mMoveDuration).setListener(new VpaListenerAdapter() { // from class: android.support.v7.widget.DefaultItemAnimator.6
                                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                {
                                    super((byte) 0);
                                }

                                @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                public final void onAnimationStart(View view2) {
                                }

                                @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                public final void onAnimationCancel(View view2) {
                                    if (i5 != 0) {
                                        ViewCompat.setTranslationX(view2, 0.0f);
                                    }
                                    if (i6 != 0) {
                                        ViewCompat.setTranslationY(view2, 0.0f);
                                    }
                                }

                                @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                public final void onAnimationEnd(View view2) {
                                    animate2.setListener(null);
                                    DefaultItemAnimator.this.dispatchAnimationFinished(viewHolder);
                                    DefaultItemAnimator.this.mMoveAnimations.remove(viewHolder);
                                    DefaultItemAnimator.this.dispatchFinishedWhenDone();
                                }
                            }).start();
                        }
                        moves.clear();
                        DefaultItemAnimator.this.mMovesList.remove(moves);
                    }
                };
                if (removalsPending) {
                    ViewCompat.postOnAnimationDelayed(moves.get(0).holder.itemView, mover, this.mRemoveDuration);
                } else {
                    mover.run();
                }
            }
            if (changesPending) {
                final ArrayList<ChangeInfo> changes = new ArrayList<>();
                changes.addAll(this.mPendingChanges);
                this.mChangesList.add(changes);
                this.mPendingChanges.clear();
                Runnable changer = new Runnable() { // from class: android.support.v7.widget.DefaultItemAnimator.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        Iterator it2 = changes.iterator();
                        while (it2.hasNext()) {
                            final ChangeInfo change = (ChangeInfo) it2.next();
                            final DefaultItemAnimator defaultItemAnimator = DefaultItemAnimator.this;
                            RecyclerView.ViewHolder viewHolder = change.oldHolder;
                            View view = viewHolder == null ? null : viewHolder.itemView;
                            RecyclerView.ViewHolder viewHolder2 = change.newHolder;
                            final View view2 = viewHolder2 != null ? viewHolder2.itemView : null;
                            if (view != null) {
                                final ViewPropertyAnimatorCompat duration = ViewCompat.animate(view).setDuration(defaultItemAnimator.mChangeDuration);
                                defaultItemAnimator.mChangeAnimations.add(change.oldHolder);
                                duration.translationX(change.toX - change.fromX);
                                duration.translationY(change.toY - change.fromY);
                                duration.alpha(0.0f).setListener(new VpaListenerAdapter() { // from class: android.support.v7.widget.DefaultItemAnimator.7
                                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                    {
                                        super((byte) 0);
                                    }

                                    @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                    public final void onAnimationStart(View view3) {
                                    }

                                    @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                    public final void onAnimationEnd(View view3) {
                                        duration.setListener(null);
                                        ViewCompat.setAlpha(view3, 1.0f);
                                        ViewCompat.setTranslationX(view3, 0.0f);
                                        ViewCompat.setTranslationY(view3, 0.0f);
                                        DefaultItemAnimator.this.dispatchAnimationFinished(change.oldHolder);
                                        DefaultItemAnimator.this.mChangeAnimations.remove(change.oldHolder);
                                        DefaultItemAnimator.this.dispatchFinishedWhenDone();
                                    }
                                }).start();
                            }
                            if (view2 != null) {
                                final ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(view2);
                                defaultItemAnimator.mChangeAnimations.add(change.newHolder);
                                animate2.translationX(0.0f).translationY(0.0f).setDuration(defaultItemAnimator.mChangeDuration).alpha(1.0f).setListener(new VpaListenerAdapter() { // from class: android.support.v7.widget.DefaultItemAnimator.8
                                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                    {
                                        super((byte) 0);
                                    }

                                    @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                    public final void onAnimationStart(View view3) {
                                    }

                                    @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                    public final void onAnimationEnd(View view3) {
                                        animate2.setListener(null);
                                        ViewCompat.setAlpha(view2, 1.0f);
                                        ViewCompat.setTranslationX(view2, 0.0f);
                                        ViewCompat.setTranslationY(view2, 0.0f);
                                        DefaultItemAnimator.this.dispatchAnimationFinished(change.newHolder);
                                        DefaultItemAnimator.this.mChangeAnimations.remove(change.newHolder);
                                        DefaultItemAnimator.this.dispatchFinishedWhenDone();
                                    }
                                }).start();
                            }
                        }
                        changes.clear();
                        DefaultItemAnimator.this.mChangesList.remove(changes);
                    }
                };
                if (removalsPending) {
                    ViewCompat.postOnAnimationDelayed(changes.get(0).oldHolder.itemView, changer, this.mRemoveDuration);
                } else {
                    changer.run();
                }
            }
            if (additionsPending) {
                final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
                additions.addAll(this.mPendingAdditions);
                this.mAdditionsList.add(additions);
                this.mPendingAdditions.clear();
                Runnable adder = new Runnable() { // from class: android.support.v7.widget.DefaultItemAnimator.3
                    @Override // java.lang.Runnable
                    public final void run() {
                        Iterator it2 = additions.iterator();
                        while (it2.hasNext()) {
                            final RecyclerView.ViewHolder holder2 = (RecyclerView.ViewHolder) it2.next();
                            final DefaultItemAnimator defaultItemAnimator = DefaultItemAnimator.this;
                            final ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(holder2.itemView);
                            defaultItemAnimator.mAddAnimations.add(holder2);
                            animate2.alpha(1.0f).setDuration(defaultItemAnimator.mAddDuration).setListener(new VpaListenerAdapter() { // from class: android.support.v7.widget.DefaultItemAnimator.5
                                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                {
                                    super((byte) 0);
                                }

                                @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                public final void onAnimationStart(View view) {
                                }

                                @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                public final void onAnimationCancel(View view) {
                                    ViewCompat.setAlpha(view, 1.0f);
                                }

                                @Override // android.support.v7.widget.DefaultItemAnimator.VpaListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                                public final void onAnimationEnd(View view) {
                                    animate2.setListener(null);
                                    DefaultItemAnimator.this.dispatchAnimationFinished(holder2);
                                    DefaultItemAnimator.this.mAddAnimations.remove(holder2);
                                    DefaultItemAnimator.this.dispatchFinishedWhenDone();
                                }
                            }).start();
                        }
                        additions.clear();
                        DefaultItemAnimator.this.mAdditionsList.remove(additions);
                    }
                };
                if (removalsPending || movesPending || changesPending) {
                    if (!removalsPending) {
                        removeDuration = 0;
                    } else {
                        removeDuration = this.mRemoveDuration;
                    }
                    if (!movesPending) {
                        moveDuration = 0;
                    } else {
                        moveDuration = this.mMoveDuration;
                    }
                    if (!changesPending) {
                        changeDuration = 0;
                    } else {
                        changeDuration = this.mChangeDuration;
                    }
                    long totalDelay = removeDuration + Math.max(moveDuration, changeDuration);
                    ViewCompat.postOnAnimationDelayed(additions.get(0).itemView, adder, totalDelay);
                    return;
                }
                adder.run();
            }
        }
    }

    @Override // android.support.v7.widget.SimpleItemAnimator
    public final boolean animateRemove(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        this.mPendingRemovals.add(holder);
        return true;
    }

    @Override // android.support.v7.widget.SimpleItemAnimator
    public final boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        ViewCompat.setAlpha(holder.itemView, 0.0f);
        this.mPendingAdditions.add(holder);
        return true;
    }

    @Override // android.support.v7.widget.SimpleItemAnimator
    public final boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        int fromX2 = (int) (fromX + ViewCompat.getTranslationX(holder.itemView));
        int fromY2 = (int) (fromY + ViewCompat.getTranslationY(holder.itemView));
        resetAnimation(holder);
        int deltaX = toX - fromX2;
        int deltaY = toY - fromY2;
        if (deltaX != 0 || deltaY != 0) {
            if (deltaX != 0) {
                ViewCompat.setTranslationX(view, -deltaX);
            }
            if (deltaY != 0) {
                ViewCompat.setTranslationY(view, -deltaY);
            }
            this.mPendingMoves.add(new MoveInfo(holder, fromX2, fromY2, toX, toY, (byte) 0));
            return true;
        }
        dispatchAnimationFinished(holder);
        return false;
    }

    @Override // android.support.v7.widget.SimpleItemAnimator
    public final boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        if (oldHolder == newHolder) {
            return animateMove(oldHolder, fromX, fromY, toX, toY);
        }
        float prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView);
        float prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView);
        float prevAlpha = ViewCompat.getAlpha(oldHolder.itemView);
        resetAnimation(oldHolder);
        int deltaX = (int) ((toX - fromX) - prevTranslationX);
        int deltaY = (int) ((toY - fromY) - prevTranslationY);
        ViewCompat.setTranslationX(oldHolder.itemView, prevTranslationX);
        ViewCompat.setTranslationY(oldHolder.itemView, prevTranslationY);
        ViewCompat.setAlpha(oldHolder.itemView, prevAlpha);
        if (newHolder != null) {
            resetAnimation(newHolder);
            ViewCompat.setTranslationX(newHolder.itemView, -deltaX);
            ViewCompat.setTranslationY(newHolder.itemView, -deltaY);
            ViewCompat.setAlpha(newHolder.itemView, 0.0f);
        }
        this.mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY, (byte) 0));
        return true;
    }

    private void endChangeAnimation(List<ChangeInfo> infoList, RecyclerView.ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                infoList.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder == item) {
            changeInfo.oldHolder = null;
        } else {
            return false;
        }
        ViewCompat.setAlpha(item.itemView, 1.0f);
        ViewCompat.setTranslationX(item.itemView, 0.0f);
        ViewCompat.setTranslationY(item.itemView, 0.0f);
        dispatchAnimationFinished(item);
        return true;
    }

    @Override // android.support.v7.widget.RecyclerView.ItemAnimator
    public final void endAnimation(RecyclerView.ViewHolder item) {
        View view = item.itemView;
        ViewCompat.animate(view).cancel();
        for (int i = this.mPendingMoves.size() - 1; i >= 0; i--) {
            if (this.mPendingMoves.get(i).holder == item) {
                ViewCompat.setTranslationY(view, 0.0f);
                ViewCompat.setTranslationX(view, 0.0f);
                dispatchAnimationFinished(item);
                this.mPendingMoves.remove(i);
            }
        }
        endChangeAnimation(this.mPendingChanges, item);
        if (this.mPendingRemovals.remove(item)) {
            ViewCompat.setAlpha(view, 1.0f);
            dispatchAnimationFinished(item);
        }
        if (this.mPendingAdditions.remove(item)) {
            ViewCompat.setAlpha(view, 1.0f);
            dispatchAnimationFinished(item);
        }
        for (int i2 = this.mChangesList.size() - 1; i2 >= 0; i2--) {
            ArrayList<ChangeInfo> changes = this.mChangesList.get(i2);
            endChangeAnimation(changes, item);
            if (changes.isEmpty()) {
                this.mChangesList.remove(i2);
            }
        }
        for (int i3 = this.mMovesList.size() - 1; i3 >= 0; i3--) {
            ArrayList<MoveInfo> moves = this.mMovesList.get(i3);
            int j = moves.size() - 1;
            while (true) {
                if (j < 0) {
                    break;
                }
                if (moves.get(j).holder != item) {
                    j--;
                } else {
                    ViewCompat.setTranslationY(view, 0.0f);
                    ViewCompat.setTranslationX(view, 0.0f);
                    dispatchAnimationFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        this.mMovesList.remove(i3);
                    }
                }
            }
        }
        for (int i4 = this.mAdditionsList.size() - 1; i4 >= 0; i4--) {
            ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i4);
            if (additions.remove(item)) {
                ViewCompat.setAlpha(view, 1.0f);
                dispatchAnimationFinished(item);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(i4);
                }
            }
        }
        this.mRemoveAnimations.remove(item);
        this.mAddAnimations.remove(item);
        this.mChangeAnimations.remove(item);
        this.mMoveAnimations.remove(item);
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        endAnimation(holder);
    }

    @Override // android.support.v7.widget.RecyclerView.ItemAnimator
    public final boolean isRunning() {
        return (this.mPendingAdditions.isEmpty() && this.mPendingChanges.isEmpty() && this.mPendingMoves.isEmpty() && this.mPendingRemovals.isEmpty() && this.mMoveAnimations.isEmpty() && this.mRemoveAnimations.isEmpty() && this.mAddAnimations.isEmpty() && this.mChangeAnimations.isEmpty() && this.mMovesList.isEmpty() && this.mAdditionsList.isEmpty() && this.mChangesList.isEmpty()) ? false : true;
    }

    final void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    @Override // android.support.v7.widget.RecyclerView.ItemAnimator
    public final void endAnimations() {
        for (int i = this.mPendingMoves.size() - 1; i >= 0; i--) {
            MoveInfo item = this.mPendingMoves.get(i);
            View view = item.holder.itemView;
            ViewCompat.setTranslationY(view, 0.0f);
            ViewCompat.setTranslationX(view, 0.0f);
            dispatchAnimationFinished(item.holder);
            this.mPendingMoves.remove(i);
        }
        for (int i2 = this.mPendingRemovals.size() - 1; i2 >= 0; i2--) {
            dispatchAnimationFinished(this.mPendingRemovals.get(i2));
            this.mPendingRemovals.remove(i2);
        }
        for (int i3 = this.mPendingAdditions.size() - 1; i3 >= 0; i3--) {
            RecyclerView.ViewHolder item2 = this.mPendingAdditions.get(i3);
            ViewCompat.setAlpha(item2.itemView, 1.0f);
            dispatchAnimationFinished(item2);
            this.mPendingAdditions.remove(i3);
        }
        for (int i4 = this.mPendingChanges.size() - 1; i4 >= 0; i4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(i4));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            for (int i5 = this.mMovesList.size() - 1; i5 >= 0; i5--) {
                ArrayList<MoveInfo> moves = this.mMovesList.get(i5);
                for (int j = moves.size() - 1; j >= 0; j--) {
                    MoveInfo moveInfo = moves.get(j);
                    View view2 = moveInfo.holder.itemView;
                    ViewCompat.setTranslationY(view2, 0.0f);
                    ViewCompat.setTranslationX(view2, 0.0f);
                    dispatchAnimationFinished(moveInfo.holder);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        this.mMovesList.remove(moves);
                    }
                }
            }
            for (int i6 = this.mAdditionsList.size() - 1; i6 >= 0; i6--) {
                ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i6);
                for (int j2 = additions.size() - 1; j2 >= 0; j2--) {
                    RecyclerView.ViewHolder item3 = additions.get(j2);
                    ViewCompat.setAlpha(item3.itemView, 1.0f);
                    dispatchAnimationFinished(item3);
                    additions.remove(j2);
                    if (additions.isEmpty()) {
                        this.mAdditionsList.remove(additions);
                    }
                }
            }
            for (int i7 = this.mChangesList.size() - 1; i7 >= 0; i7--) {
                ArrayList<ChangeInfo> changes = this.mChangesList.get(i7);
                for (int j3 = changes.size() - 1; j3 >= 0; j3--) {
                    endChangeAnimationIfNecessary(changes.get(j3));
                    if (changes.isEmpty()) {
                        this.mChangesList.remove(changes);
                    }
                }
            }
            cancelAll(this.mRemoveAnimations);
            cancelAll(this.mMoveAnimations);
            cancelAll(this.mAddAnimations);
            cancelAll(this.mChangeAnimations);
            dispatchAnimationsFinished();
        }
    }

    private static void cancelAll(List<RecyclerView.ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            ViewCompat.animate(viewHolders.get(i).itemView).cancel();
        }
    }

    @Override // android.support.v7.widget.RecyclerView.ItemAnimator
    public final boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }

    /* loaded from: classes.dex */
    private static class VpaListenerAdapter implements ViewPropertyAnimatorListener {
        private VpaListenerAdapter() {
        }

        /* synthetic */ VpaListenerAdapter(byte b) {
            this();
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationStart(View view) {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationEnd(View view) {
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationCancel(View view) {
        }
    }
}
