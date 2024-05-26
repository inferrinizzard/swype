package com.localytics.android;

/* loaded from: classes.dex */
public class Customer {
    private final String mCustomerId;
    private final String mEmailAddress;
    private final String mFirstName;
    private final String mFullName;
    private final String mLastName;

    private Customer(Builder builder) {
        this.mCustomerId = builder.mCustomerId;
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mFullName = builder.mFullName;
        this.mEmailAddress = builder.mEmailAddress;
    }

    public String getCustomerId() {
        return this.mCustomerId;
    }

    public String getFirstName() {
        return this.mFirstName;
    }

    public String getLastName() {
        return this.mLastName;
    }

    public String getFullName() {
        return this.mFullName;
    }

    public String getEmailAddress() {
        return this.mEmailAddress;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private String mCustomerId = null;
        private String mFirstName = null;
        private String mLastName = null;
        private String mFullName = null;
        private String mEmailAddress = null;

        public Builder setCustomerId(String customerId) {
            this.mCustomerId = customerId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.mFirstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.mLastName = lastName;
            return this;
        }

        public Builder setFullName(String fullName) {
            this.mFullName = fullName;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.mEmailAddress = emailAddress;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
