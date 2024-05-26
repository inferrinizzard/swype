package android.support.v4.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/* loaded from: classes.dex */
public abstract class AbsSavedState implements Parcelable {
    public final Parcelable mSuperState;
    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() { // from class: android.support.v4.view.AbsSavedState.1
    };
    public static final Parcelable.Creator<AbsSavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<AbsSavedState>() { // from class: android.support.v4.view.AbsSavedState.2
        @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
        public final /* bridge */ /* synthetic */ AbsSavedState[] newArray(int i) {
            return new AbsSavedState[i];
        }

        @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
        public final /* bridge */ /* synthetic */ AbsSavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
            if (parcel.readParcelable(classLoader) != null) {
                throw new IllegalStateException("superState must be null");
            }
            return AbsSavedState.EMPTY_STATE;
        }
    });

    /* synthetic */ AbsSavedState(byte b) {
        this();
    }

    private AbsSavedState() {
        this.mSuperState = null;
    }

    public AbsSavedState(Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        this.mSuperState = superState == EMPTY_STATE ? null : superState;
    }

    public AbsSavedState(Parcel source, ClassLoader loader) {
        Parcelable superState = source.readParcelable(loader);
        this.mSuperState = superState == null ? EMPTY_STATE : superState;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSuperState, flags);
    }
}
