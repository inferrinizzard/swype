package com.localytics.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import com.localytics.android.CreativeManager;
import com.localytics.android.Localytics;
import com.localytics.android.Region;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class LocalyticsManager implements LocalyticsDao {
    private static final LocalyticsManager INSTANCE = new LocalyticsManager();
    private static int mActivityCounter = 0;
    private static boolean mIsAutoIntegrate = false;
    private Context mAppContext;
    private String mAppKey;
    private HandlerWrapper mHandlerWrapper;
    private Proxy mProxy;
    private boolean mUseNewCreativeLocation;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static LocalyticsManager getInstance() {
        return INSTANCE;
    }

    LocalyticsManager() {
    }

    @TargetApi(14)
    public void autoIntegrate(Application application, String localyticsKey) {
        application.registerActivityLifecycleCallbacks(new LocalyticsActivityLifecycleCallbacks(application, localyticsKey));
    }

    public synchronized void integrate(Context context, String localyticsKey) {
        if (this.mHandlerWrapper == null) {
            if (BuildConfig.APPLICATION_ID.equals(context.getPackageName()) && !context.getClass().getName().equals("android.test.IsolatedContext") && !context.getClass().getName().equals("android.test.RenamingDelegatingContext")) {
                throw new IllegalArgumentException(String.format("context.getPackageName() returned %s", context.getPackageName()));
            }
            boolean isRenamingDelegatingContext = context.getClass().getName().equals("android.test.RenamingDelegatingContext");
            if (!isRenamingDelegatingContext && (context instanceof Activity)) {
                throw new IllegalStateException("Activity context use is not supported. You must call integrate() or registerActivityLifecycleCallbacks() from your Application class (see integration guide). If migrating from 3.0, see https://support.localytics.com/Android_SDK_Migration_3.0_to_3.x");
            }
            if (!isRenamingDelegatingContext) {
                context = context.getApplicationContext();
            }
            this.mAppContext = context;
            this.mAppKey = localyticsKey;
            if (TextUtils.isEmpty(this.mAppKey)) {
                this.mAppKey = DatapointHelper.getLocalyticsAppKeyOrNull(this.mAppContext);
            }
            if (TextUtils.isEmpty(this.mAppKey)) {
                throw new IllegalArgumentException("App key must be declared in a meta data tag in AndroidManifest.xml or passed into integrate(final Context context, final String localyticsKey) or new LocalyticsActivityLifecycleCallbacks(final Context context, final String localyticsKey) (see integration guide).");
            }
            try {
                PackageInfo packageInfo = this.mAppContext.getPackageManager().getPackageInfo(this.mAppContext.getPackageName(), 3);
                if (classInManifest(packageInfo.receivers, "com.google.android.gms.gcm.GcmReceiver") && !classInManifest(packageInfo.activities, PushTrackingActivity.class.getName())) {
                    Localytics.Log.w("PushTrackingActivity is not declared in AndroidManifest.xml (see integration guide).");
                }
            } catch (PackageManager.NameNotFoundException e) {
                Localytics.Log.w(e);
            }
            AnalyticsHandler analyticsHandler = new AnalyticsHandler(this, getHandlerThread(AnalyticsHandler.class.getSimpleName()).getLooper());
            MarketingHandler marketingHandler = new MarketingHandler(this, getHandlerThread(MarketingHandler.class.getSimpleName()).getLooper());
            ProfileHandler profileHandler = new ProfileHandler(this, getHandlerThread(ProfileHandler.class.getSimpleName()).getLooper());
            ManifestHandler manifestHandler = new ManifestHandler(this, getHandlerThread(ManifestHandler.class.getSimpleName()).getLooper());
            LocationHandler locationHandler = new LocationHandler(this, getHandlerThread(LocationHandler.class.getSimpleName()).getLooper());
            this.mHandlerWrapper = new HandlerWrapper(analyticsHandler, marketingHandler, profileHandler, manifestHandler, locationHandler);
            analyticsHandler.addListener(marketingHandler);
            analyticsHandler.addListener(manifestHandler);
            manifestHandler.addListener(marketingHandler);
            manifestHandler.addListener(locationHandler);
            locationHandler.addListener(analyticsHandler);
            locationHandler.addListener(manifestHandler);
            locationHandler.addListener(marketingHandler);
        }
    }

    @Override // com.localytics.android.LocalyticsDao
    public Context getAppContext() {
        return this.mAppContext;
    }

    @Override // com.localytics.android.LocalyticsDao
    public String getAppKey() {
        return this.mAppKey;
    }

    @Override // com.localytics.android.LocalyticsDao
    public void upload() {
        if (!TextUtils.isEmpty(this.mAppKey)) {
            Future<String> customerIdFuture = getCustomerIdFuture();
            getProfileHandler().upload(customerIdFuture);
            getAnalyticsHandler().upload(customerIdFuture);
        }
    }

    public void setOptedOut(boolean newOptOut) {
        getAnalyticsHandler().setOptedOut(newOptOut);
    }

    public boolean isOptedOut() {
        return getAnalyticsHandler().isOptedOut();
    }

    public void openSession() {
        getAnalyticsHandler().openSession();
    }

    public void closeSession() {
        getMarketingHandler().tagDismissForInboxDetailFragments();
        getAnalyticsHandler().closeSession();
    }

    @Override // com.localytics.android.LocalyticsDao
    public void tagEvent(String eventName) {
        tagEvent(eventName, null, 0L);
    }

    @Override // com.localytics.android.LocalyticsDao
    public void tagEvent(String eventName, Map<String, String> attributes) {
        tagEvent(eventName, attributes, 0L);
    }

    @Override // com.localytics.android.LocalyticsDao
    public void tagEvent(String eventName, Map<String, String> attributes, long customerValueIncrease) {
        getAnalyticsHandler().tagEvent(eventName, attributes, customerValueIncrease);
    }

    public void tagPurchased(String itemName, String itemId, String itemType, Long itemPrice, Map<String, String> attributes) {
        getAnalyticsHandler().tagPurchased(itemName, itemId, itemType, itemPrice, attributes);
    }

    public void tagAddedToCart(String itemName, String itemId, String itemType, Long itemPrice, Map<String, String> attributes) {
        getAnalyticsHandler().tagAddedToCart(itemName, itemId, itemType, itemPrice, attributes);
    }

    public void tagStartedCheckout(Long totalPrice, Long itemCount, Map<String, String> attributes) {
        getAnalyticsHandler().tagStartedCheckout(totalPrice, itemCount, attributes);
    }

    public void tagCompletedCheckout(Long totalPrice, Long itemCount, Map<String, String> attributes) {
        getAnalyticsHandler().tagCompletedCheckout(totalPrice, itemCount, attributes);
    }

    public void tagContentViewed(String contentName, String contentId, String contentType, Map<String, String> attributes) {
        getAnalyticsHandler().tagContentViewed(contentName, contentId, contentType, attributes);
    }

    public void tagSearched(String query, String contentType, Long resultCount, Map<String, String> attributes) {
        getAnalyticsHandler().tagSearched(query, contentType, resultCount, attributes);
    }

    public void tagShared(String contentName, String contentId, String contentType, String method, Map<String, String> attributes) {
        getAnalyticsHandler().tagShared(contentName, contentId, contentType, method, attributes);
    }

    public void tagContentRated(String contentName, String contentId, String contentType, Long rating, Map<String, String> attributes) {
        getAnalyticsHandler().tagContentRated(contentName, contentId, contentType, rating, attributes);
    }

    public void tagCustomerRegistered(Customer customer, String method, Map<String, String> attributes) {
        if (customer != null) {
            setCustomer(customer);
        }
        getAnalyticsHandler().tagRegistered(method, attributes);
    }

    public void tagCustomerLoggedIn(Customer customer, String method, Map<String, String> attributes) {
        if (customer != null) {
            setCustomer(customer);
        }
        getAnalyticsHandler().tagLoggedIn(method, attributes);
    }

    public void tagCustomerLoggedOut(Map<String, String> attributes) {
        setCustomerId(null);
        getAnalyticsHandler().tagLoggedOut(attributes);
    }

    public void tagInvited(String method, Map<String, String> attributes) {
        getAnalyticsHandler().tagInvited(method, attributes);
    }

    public void tagScreen(String screen) {
        getAnalyticsHandler().tagScreen(screen);
    }

    @Override // com.localytics.android.LocalyticsDao
    public void setCustomDimension(int dimension, String value) {
        getAnalyticsHandler().setCustomDimension(dimension, value);
    }

    @Override // com.localytics.android.LocalyticsDao
    public String getCustomDimension(int dimension) {
        return getAnalyticsHandler().getCustomDimension(dimension);
    }

    public void setAnalyticsListener(AnalyticsListener listener) {
        getAnalyticsHandler().setDeveloperListener(listener);
    }

    @Override // com.localytics.android.LocalyticsDao
    public Map<String, String> getCachedIdentifiers() {
        return getAnalyticsHandler().getCachedIdentifiers();
    }

    @Override // com.localytics.android.LocalyticsDao
    public Map<Integer, String> getCachedCustomDimensions() {
        return getAnalyticsHandler().getCachedCustomDimensions();
    }

    public void setProfileAttribute(String attributeName, long attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().setProfileAttribute(attributeName, attributeValue, scope.getScope());
    }

    public void setProfileAttribute(String attributeName, long attributeValue) {
        setProfileAttribute(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void setProfileAttribute(String attributeName, long[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().setProfileAttribute(attributeName, attributeValue, scope.getScope());
    }

    public void setProfileAttribute(String attributeName, long[] attributeValue) {
        setProfileAttribute(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void setProfileAttribute(String attributeName, String attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().setProfileAttribute(attributeName, attributeValue, scope.getScope());
    }

    public void setProfileAttribute(String attributeName, String attributeValue) {
        setProfileAttribute(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void setProfileAttribute(String attributeName, String[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().setProfileAttribute(attributeName, attributeValue, scope.getScope());
    }

    public void setProfileAttribute(String attributeName, String[] attributeValue) {
        setProfileAttribute(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void setProfileAttribute(String attributeName, Date attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().setProfileAttribute(attributeName, attributeValue, scope.getScope());
    }

    public void setProfileAttribute(String attributeName, Date attributeValue) {
        setProfileAttribute(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void setProfileAttribute(String attributeName, Date[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().setProfileAttribute(attributeName, attributeValue, scope.getScope());
    }

    public void setProfileAttribute(String attributeName, Date[] attributeValue) {
        setProfileAttribute(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void addProfileAttributesToSet(String attributeName, long[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().addProfileAttributesToSet(attributeName, attributeValue, scope.getScope());
    }

    public void addProfileAttributesToSet(String attributeName, long[] attributeValue) {
        addProfileAttributesToSet(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void addProfileAttributesToSet(String attributeName, String[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().addProfileAttributesToSet(attributeName, attributeValue, scope.getScope());
    }

    public void addProfileAttributesToSet(String attributeName, String[] attributeValue) {
        addProfileAttributesToSet(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void addProfileAttributesToSet(String attributeName, Date[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().addProfileAttributesToSet(attributeName, attributeValue, scope.getScope());
    }

    public void addProfileAttributesToSet(String attributeName, Date[] attributeValue) {
        addProfileAttributesToSet(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void removeProfileAttributesFromSet(String attributeName, long[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().removeProfileAttributesFromSet(attributeName, attributeValue, scope.getScope());
    }

    public void removeProfileAttributesFromSet(String attributeName, long[] attributeValue) {
        removeProfileAttributesFromSet(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void removeProfileAttributesFromSet(String attributeName, String[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().removeProfileAttributesFromSet(attributeName, attributeValue, scope.getScope());
    }

    public void removeProfileAttributesFromSet(String attributeName, String[] attributeValue) {
        removeProfileAttributesFromSet(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void removeProfileAttributesFromSet(String attributeName, Date[] attributeValue, Localytics.ProfileScope scope) {
        getProfileHandler().removeProfileAttributesFromSet(attributeName, attributeValue, scope.getScope());
    }

    public void removeProfileAttributesFromSet(String attributeName, Date[] attributeValue) {
        removeProfileAttributesFromSet(attributeName, attributeValue, Localytics.ProfileScope.APPLICATION);
    }

    public void incrementProfileAttribute(String attributeName, long incrementValue, Localytics.ProfileScope scope) {
        getProfileHandler().incrementProfileAttribute(attributeName, incrementValue, scope.getScope());
    }

    public void incrementProfileAttribute(String attributeName, long incrementValue) {
        incrementProfileAttribute(attributeName, incrementValue, Localytics.ProfileScope.APPLICATION);
    }

    public void decrementProfileAttribute(String attributeName, long decrementValue, Localytics.ProfileScope scope) {
        getProfileHandler().incrementProfileAttribute(attributeName, (-1) * decrementValue, scope.getScope());
    }

    public void decrementProfileAttribute(String attributeName, long decrementValue) {
        decrementProfileAttribute(attributeName, decrementValue, Localytics.ProfileScope.APPLICATION);
    }

    public void deleteProfileAttribute(String attributeName, Localytics.ProfileScope scope) {
        getProfileHandler().deleteProfileAttribute(attributeName, scope.getScope());
    }

    public void deleteProfileAttribute(String attributeName) {
        deleteProfileAttribute(attributeName, Localytics.ProfileScope.APPLICATION);
    }

    void setCustomer(Customer customer) {
        if (customer.getCustomerId() != null) {
            setCustomerId(customer.getCustomerId());
        }
        if (customer.getFirstName() != null) {
            setSpecialCustomerIdentifierAndAttribute("first_name", customer.getFirstName());
        }
        if (customer.getLastName() != null) {
            setSpecialCustomerIdentifierAndAttribute("last_name", customer.getLastName());
        }
        if (customer.getFullName() != null) {
            setSpecialCustomerIdentifierAndAttribute("full_name", customer.getFullName());
        }
        if (customer.getEmailAddress() != null) {
            setSpecialCustomerIdentifierAndAttribute("email", customer.getEmailAddress());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSpecialCustomerIdentifierAndAttribute(String key, String value) {
        String attributeKey = "$" + key;
        if (!TextUtils.isEmpty(value)) {
            setProfileAttribute(attributeKey, value, Localytics.ProfileScope.ORGANIZATION);
        } else {
            deleteProfileAttribute(attributeKey, Localytics.ProfileScope.ORGANIZATION);
        }
        setIdentifier(key, value);
    }

    public void setInAppMessageDisplayActivity(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("attached activity cannot be null");
        }
        if (Build.VERSION.SDK_INT >= 11) {
            getMarketingHandler().setFragmentManager(activity.getFragmentManager());
            if (isTestModeEnabled()) {
                getMarketingHandler().showMarketingTest();
            }
        }
    }

    public void clearInAppMessageDisplayActivity() {
        getMarketingHandler().setFragmentManager(null);
    }

    public void triggerInAppMessage(String triggerName, Map<String, String> attributes) {
        getMarketingHandler().displayInAppMessage(triggerName, attributes);
    }

    public void dismissCurrentInAppMessage() {
        Runnable dismissInAppRunnable = new Runnable() { // from class: com.localytics.android.LocalyticsManager.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    LocalyticsManager.this.getMarketingHandler().dismissCurrentInAppMessage();
                } catch (Exception e) {
                    Localytics.Log.e("Exception while dismissing current in-app", e);
                }
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) {
            dismissInAppRunnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(dismissInAppRunnable);
        }
    }

    public void registerPush(String senderId) {
        getAnalyticsHandler().registerPush(senderId, 0L);
    }

    public void registerPush(String senderId, long delay) {
        getAnalyticsHandler().registerPush(senderId, delay);
    }

    public void retrieveTokenFromInstanceId() {
        getAnalyticsHandler().retrieveTokenFromInstanceId();
    }

    @Override // com.localytics.android.LocalyticsDao
    public String getPushRegistrationId() {
        return getAnalyticsHandler().getPushRegistrationID();
    }

    public void setPushRegistrationId(String registrationId) {
        getAnalyticsHandler().setPushRegistrationId(registrationId);
    }

    public void setNotificationsDisabled(boolean disable) {
        getAnalyticsHandler().setNotificationsDisabled(disable);
    }

    @Override // com.localytics.android.LocalyticsDao
    public boolean areNotificationsDisabled() {
        return getAnalyticsHandler().areNotificationsDisabled();
    }

    public void handlePushNotificationOpened(Intent intent) {
        getMarketingHandler().handlePushNotificationOpened(intent);
    }

    public void tagPushReceivedEvent(Bundle data) {
        getMarketingHandler().tagPushReceivedEvent(data);
    }

    public void handleTestMode(Intent intent) {
        getMarketingHandler().handleTestModeIntent(intent);
    }

    @Override // com.localytics.android.LocalyticsDao
    public void setTestModeEnabled(boolean enabled) {
        if (Constants.isTestModeEnabled() != enabled) {
            Constants.setTestModeEnabled(enabled);
            if (enabled) {
                getMarketingHandler().showMarketingTest();
            }
        }
    }

    public boolean isTestModeEnabled() {
        return Constants.isTestModeEnabled();
    }

    public void setInAppMessageDismissButtonImage(Resources resources, int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap source = BitmapFactory.decodeResource(resources, id, options);
        if (source == null) {
            Localytics.Log.w("Cannot load the new dismiss button image. Is the resource id corrected?");
            getMarketingHandler().setDismissButtonImage(null);
            return;
        }
        float maxSide = TypedValue.applyDimension(1, 40.0f, resources.getDisplayMetrics());
        Bitmap newImage = scaleDownBitmap(source, maxSide);
        if (newImage != source) {
            source.recycle();
        }
        getMarketingHandler().setDismissButtonImage(newImage);
    }

    public void setInAppMessageDismissButtonImage(Resources resources, Bitmap image) {
        Bitmap newImage = null;
        if (image != null) {
            float maxSide = TypedValue.applyDimension(1, 40.0f, resources.getDisplayMetrics());
            newImage = scaleDownBitmap(image, maxSide);
            if (newImage == image) {
                newImage = image.copy(Bitmap.Config.ARGB_8888, false);
            }
        }
        getMarketingHandler().setDismissButtonImage(newImage);
    }

    public void setMessagingListener(MessagingListener listener) {
        getMarketingHandler().setDeveloperListener(listener);
    }

    public List<InboxCampaign> getInboxCampaigns() {
        return getMarketingHandler().getInboxCampaigns();
    }

    public void refreshInboxCampaigns(InboxRefreshListener callback) {
        if (getMarketingHandler().canRefreshInbox()) {
            getMarketingHandler().setInboxRefreshCallback(callback);
            getManifestHandler().refreshManifest();
        } else {
            getMarketingHandler().getInboxCampaignsAsync(callback);
        }
    }

    public void setInboxCampaignRead(long campaignId, boolean read) {
        getMarketingHandler().setInboxCampaignRead(campaignId, read);
    }

    public int getInboxCampaignsUnreadCount() {
        return getMarketingHandler().getInboxCampaignsUnreadCount();
    }

    public void setInboxDetailFragmentDisplaying(Object fragment, boolean displaying) {
        getMarketingHandler().setInboxDetailFragmentDisplaying(fragment, displaying);
    }

    public void downloadInboxThumbnails(List<InboxCampaign> campaigns) {
        getMarketingHandler().downloadInboxThumbnails(campaigns);
    }

    public void priorityDownloadCreative(InboxCampaign campaign, CreativeManager.FirstDownloadedCallback firstDownloadedCallback) {
        getMarketingHandler().priorityDownloadCreative(campaign, firstDownloadedCallback);
    }

    public void setLocationMonitoringEnabled(boolean enabled) {
        if (!PlayServicesUtils.isLocationAvailable()) {
            throw new UnsupportedOperationException("You must include the Play Services Location dependency to use this API.");
        }
        getLocationHandler().setLocationMonitoringEnabled(enabled);
    }

    public boolean isLocationMonitoringEnabled() {
        return getLocationHandler().isLocationMonitoringEnabled();
    }

    public void triggerRegion(Region region, Region.Event event) {
        getLocationHandler().triggerRegion(region, event);
    }

    public void triggerRegions(List<Region> regions, Region.Event event) {
        getLocationHandler().triggerRegions(regions, event);
    }

    @Override // com.localytics.android.LocalyticsDao
    public void stoppedMonitoringAllGeofences() {
        getLocationHandler().stoppedMonitoringAllGeofences();
    }

    public List<CircularRegion> getGeofencesToMonitor(double latitude, double longitude) {
        return getLocationHandler().getGeofencesToMonitor(latitude, longitude);
    }

    public void setLocationListener(LocationListener listener) {
        getLocationHandler().setDeveloperListener(listener);
    }

    public void setIdentifier(String key, String value) {
        getAnalyticsHandler().setIdentifier(key, value);
    }

    public void setCustomerId(String customerId) {
        setIdentifier("customer_id", customerId);
    }

    public String getCustomerId() {
        return getIdentifier("customer_id");
    }

    public String getIdentifier(String key) {
        return getAnalyticsHandler().getIdentifier(key);
    }

    public void setLocation(Location location) {
        getAnalyticsHandler().setLocation(location);
        getManifestHandler().locationUpdated();
    }

    @Override // com.localytics.android.LocalyticsDao
    public String getInstallationId() {
        return getAnalyticsHandler().getInstallationId();
    }

    public void setInAppMessageDismissButtonLocation(Localytics.InAppMessageDismissButtonLocation buttonLocation) {
        getMarketingHandler().setInAppDismissButtonLocation(buttonLocation);
    }

    public Localytics.InAppMessageDismissButtonLocation getInAppMessageDismissButtonLocation() {
        return getMarketingHandler().getInAppDismissButtonLocation();
    }

    public void setReferrerId(String referrerId) {
        getAnalyticsHandler().setReferrerId(referrerId);
    }

    public void handleNotificationReceived(Bundle data) {
        getMarketingHandler().handleNotificationReceived(data);
    }

    @Override // com.localytics.android.LocalyticsDao
    public Future<String> getCustomerIdFuture() {
        return getAnalyticsHandler().getCustomerIdFuture();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void incrementActivityCounter() {
        mActivityCounter++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void decrementActivityCounter() {
        mActivityCounter--;
    }

    @Override // com.localytics.android.LocalyticsDao
    public boolean isAppInForeground() {
        return mActivityCounter > 0;
    }

    @Override // com.localytics.android.LocalyticsDao
    public boolean isAutoIntegrate() {
        return mIsAutoIntegrate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIsAutoIntegrate(boolean autoIntegrate) {
        mIsAutoIntegrate = autoIntegrate;
    }

    private AnalyticsHandler getAnalyticsHandler() {
        if (this.mHandlerWrapper == null) {
            throw new LocalyticsNotInitializedException();
        }
        return this.mHandlerWrapper.analyticsHandler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MarketingHandler getMarketingHandler() {
        if (this.mHandlerWrapper == null) {
            throw new LocalyticsNotInitializedException();
        }
        return this.mHandlerWrapper.marketingHandler;
    }

    private ProfileHandler getProfileHandler() {
        if (this.mHandlerWrapper == null) {
            throw new LocalyticsNotInitializedException();
        }
        return this.mHandlerWrapper.profileHandler;
    }

    private ManifestHandler getManifestHandler() {
        if (this.mHandlerWrapper == null) {
            throw new LocalyticsNotInitializedException();
        }
        return this.mHandlerWrapper.manifestHandler;
    }

    private LocationHandler getLocationHandler() {
        if (this.mHandlerWrapper == null) {
            throw new LocalyticsNotInitializedException();
        }
        return this.mHandlerWrapper.locationHandler;
    }

    private boolean classInManifest(ActivityInfo[] infoArray, String className) {
        if (infoArray != null) {
            for (ActivityInfo activityInfo : infoArray) {
                if (activityInfo.name.equalsIgnoreCase(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    private HandlerThread getHandlerThread(String name) {
        HandlerThread thread = new HandlerThread(name, 10);
        thread.start();
        return thread;
    }

    private Bitmap scaleDownBitmap(Bitmap source, float maxSide) {
        Bitmap scaledDown;
        int largerSide = Math.max(source.getWidth(), source.getHeight());
        float aspectRatio = source.getWidth() / source.getHeight();
        if (largerSide <= maxSide) {
            return source;
        }
        if (source.getWidth() >= source.getHeight()) {
            scaledDown = Bitmap.createScaledBitmap(source, (int) maxSide, (int) ((maxSide / aspectRatio) + 0.5f), true);
        } else {
            scaledDown = Bitmap.createScaledBitmap(source, (int) ((maxSide * aspectRatio) + 0.5f), (int) maxSide, true);
        }
        if (scaledDown == null) {
            Localytics.Log.w("Cannot scale down the new dismiss button image.");
            return scaledDown;
        }
        return scaledDown;
    }

    @Override // com.localytics.android.LocalyticsDao
    public int getAndroidVersionInt() {
        return Build.VERSION.SDK_INT;
    }

    @Override // com.localytics.android.LocalyticsDao
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override // com.localytics.android.LocalyticsDao
    public Calendar getCalendar() {
        return Calendar.getInstance();
    }

    @Override // com.localytics.android.LocalyticsDao
    public String getTimeStringForSQLite() {
        return "now";
    }

    @Override // com.localytics.android.LocalyticsDao
    public Proxy getProxy() {
        return this.mProxy;
    }

    public void setProxy(Proxy proxy) {
        this.mProxy = proxy;
    }

    @Override // com.localytics.android.LocalyticsDao
    public void useNewCreativeLocation(boolean useNewLocation) {
        this.mUseNewCreativeLocation = useNewLocation;
    }

    @Override // com.localytics.android.LocalyticsDao
    public boolean isUsingNewCreativeLocation() {
        return this.mUseNewCreativeLocation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class HandlerWrapper {
        AnalyticsHandler analyticsHandler;
        LocationHandler locationHandler;
        ManifestHandler manifestHandler;
        MarketingHandler marketingHandler;
        ProfileHandler profileHandler;

        private HandlerWrapper(AnalyticsHandler analyticsHandler, MarketingHandler marketingHandler, ProfileHandler profileHandler, ManifestHandler manifestHandler, LocationHandler locationHandler) {
            this.analyticsHandler = analyticsHandler;
            this.marketingHandler = marketingHandler;
            this.profileHandler = profileHandler;
            this.manifestHandler = manifestHandler;
            this.locationHandler = locationHandler;
        }
    }

    /* loaded from: classes.dex */
    public static final class LocalyticsNotInitializedException extends RuntimeException {
        private LocalyticsNotInitializedException() {
            super("You must first initialize Localytics");
        }
    }
}
