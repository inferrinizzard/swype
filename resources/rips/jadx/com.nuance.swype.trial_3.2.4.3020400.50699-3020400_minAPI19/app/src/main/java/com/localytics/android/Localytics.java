package com.localytics.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import com.localytics.android.CreativeManager;
import com.localytics.android.Region;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class Localytics {
    static final String ANALYTICS_HOST_KEY = "analytics_host";
    static final String IGNORE_STANDARD_EVENT_CLV = "ignore_standard_event_clv";
    static final String MANIFEST_HOST_KEY = "manifest_host";
    static final String MAX_MONITORING_REGIONS_KEY = "max_monitoring_regions";
    static final String MAX_REGION_DWELL_TIME_KEY = "max_region_dwell_time";
    static final String MESSAGING_HOST_KEY = "messaging_host";
    static final String MIN_REGION_DWELL_TIME_KEY = "min_region_dwell_time";
    static final String PLUGIN_LIBRARY_KEY = "plugin_library";
    static final String PROFILES_HOST_KEY = "profiles_host";
    static final String REGION_THROTTLE_TIME_KEY = "region_throttle_time";
    static final String SESSION_TIMEOUT_KEY = "session_timeout";
    static final String USE_HTTPS_KEY = "use_https";
    static final String USE_SANDBOX_KEY = "use_sandbox";
    private static final Set<String> optionsNumberKeys = new HashSet<String>() { // from class: com.localytics.android.Localytics.1
        {
            add(Localytics.SESSION_TIMEOUT_KEY);
            add(Localytics.MAX_MONITORING_REGIONS_KEY);
            add(Localytics.REGION_THROTTLE_TIME_KEY);
            add(Localytics.MIN_REGION_DWELL_TIME_KEY);
            add(Localytics.MAX_REGION_DWELL_TIME_KEY);
        }
    };
    private static final Set<String> optionStringKeys = new HashSet<String>() { // from class: com.localytics.android.Localytics.2
        {
            add(Localytics.ANALYTICS_HOST_KEY);
            add(Localytics.MESSAGING_HOST_KEY);
            add(Localytics.PROFILES_HOST_KEY);
            add(Localytics.MANIFEST_HOST_KEY);
            add(Localytics.PLUGIN_LIBRARY_KEY);
        }
    };
    private static final Set<String> optionBooleanKeys = new HashSet<String>() { // from class: com.localytics.android.Localytics.3
        {
            add(Localytics.USE_SANDBOX_KEY);
            add(Localytics.USE_HTTPS_KEY);
            add(Localytics.IGNORE_STANDARD_EVENT_CLV);
        }
    };

    /* loaded from: classes.dex */
    public enum InAppMessageDismissButtonLocation {
        LEFT,
        RIGHT
    }

    /* loaded from: classes.dex */
    public enum ProfileScope {
        ORGANIZATION("org"),
        APPLICATION("app");

        private final String scope;

        ProfileScope(String sc) {
            this.scope = sc;
        }

        public final String getScope() {
            return this.scope;
        }
    }

    public static void integrate(Context context) {
        integrate(context, null);
    }

    public static void integrate(Context context, String localyticsKey) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        LocalyticsManager.getInstance().integrate(context, localyticsKey);
    }

    public static void autoIntegrate(Application application) {
        autoIntegrate(application, null);
    }

    public static void autoIntegrate(Application application, String localyticsKey) {
        if (application == null) {
            throw new IllegalArgumentException("application cannot be null");
        }
        LocalyticsManager.getInstance().autoIntegrate(application, localyticsKey);
    }

    public static void upload() {
        LocalyticsManager.getInstance().upload();
    }

    static boolean isAutoIntegrate() {
        return LocalyticsManager.getInstance().isAutoIntegrate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setIsAutoIntegrate(boolean autoIntegrate) {
        LocalyticsManager.getInstance().setIsAutoIntegrate(autoIntegrate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAppInForeground() {
        return LocalyticsManager.getInstance().isAppInForeground();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void incrementActivityCounter() {
        LocalyticsManager.getInstance().incrementActivityCounter();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void decrementActivityCounter() {
        LocalyticsManager.getInstance().decrementActivityCounter();
    }

    public static void setOptedOut(boolean newOptOut) {
        LocalyticsManager.getInstance().setOptedOut(newOptOut);
    }

    public static boolean isOptedOut() {
        return LocalyticsManager.getInstance().isOptedOut();
    }

    public static void openSession() {
        LocalyticsManager.getInstance().openSession();
    }

    public static void closeSession() {
        LocalyticsManager.getInstance().closeSession();
    }

    public static void tagEvent(String eventName) {
        tagEvent(eventName, null, 0L);
    }

    public static void tagEvent(String eventName, Map<String, String> attributes) {
        tagEvent(eventName, attributes, 0L);
    }

    public static void tagEvent(String eventName, Map<String, String> attributes, long customerValueIncrease) {
        LocalyticsManager.getInstance().tagEvent(eventName, attributes, customerValueIncrease);
    }

    public static void tagPurchased(String itemName, String itemId, String itemType, Long itemPrice, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagPurchased(itemName, itemId, itemType, itemPrice, attributes);
    }

    public static void tagAddedToCart(String itemName, String itemId, String itemType, Long itemPrice, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagAddedToCart(itemName, itemId, itemType, itemPrice, attributes);
    }

    public static void tagStartedCheckout(Long totalPrice, Long itemCount, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagStartedCheckout(totalPrice, itemCount, attributes);
    }

    public static void tagCompletedCheckout(Long totalPrice, Long itemCount, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagCompletedCheckout(totalPrice, itemCount, attributes);
    }

    public static void tagContentViewed(String contentName, String contentId, String contentType, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagContentViewed(contentName, contentId, contentType, attributes);
    }

    public static void tagSearched(String queryText, String contentType, Long resultCount, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagSearched(queryText, contentType, resultCount, attributes);
    }

    public static void tagShared(String contentName, String contentId, String contentType, String methodName, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagShared(contentName, contentId, contentType, methodName, attributes);
    }

    public static void tagContentRated(String contentName, String contentId, String contentType, Long rating, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagContentRated(contentName, contentId, contentType, rating, attributes);
    }

    public static void tagCustomerRegistered(Customer customer, String methodName, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagCustomerRegistered(customer, methodName, attributes);
    }

    public static void tagCustomerLoggedIn(Customer customer, String methodName, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagCustomerLoggedIn(customer, methodName, attributes);
    }

    public static void tagCustomerLoggedOut(Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagCustomerLoggedOut(attributes);
    }

    public static void tagInvited(String methodName, Map<String, String> attributes) {
        LocalyticsManager.getInstance().tagInvited(methodName, attributes);
    }

    public static void tagScreen(String screen) {
        LocalyticsManager.getInstance().tagScreen(screen);
    }

    public static void setCustomDimension(int dimension, String value) {
        LocalyticsManager.getInstance().setCustomDimension(dimension, value);
    }

    public static String getCustomDimension(int dimension) {
        return LocalyticsManager.getInstance().getCustomDimension(dimension);
    }

    public static void setAnalyticsListener(AnalyticsListener listener) {
        LocalyticsManager.getInstance().setAnalyticsListener(listener);
    }

    public static void setProfileAttribute(String attributeName, long attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().setProfileAttribute(attributeName, attributeValue, scope);
    }

    public static void setProfileAttribute(String attributeName, long attributeValue) {
        setProfileAttribute(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void setProfileAttribute(String attributeName, long[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().setProfileAttribute(attributeName, attributeValue, scope);
    }

    public static void setProfileAttribute(String attributeName, long[] attributeValue) {
        setProfileAttribute(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void setProfileAttribute(String attributeName, String attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().setProfileAttribute(attributeName, attributeValue, scope);
    }

    public static void setProfileAttribute(String attributeName, String attributeValue) {
        setProfileAttribute(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void setProfileAttribute(String attributeName, String[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().setProfileAttribute(attributeName, attributeValue, scope);
    }

    public static void setProfileAttribute(String attributeName, String[] attributeValue) {
        setProfileAttribute(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void setProfileAttribute(String attributeName, Date attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().setProfileAttribute(attributeName, attributeValue, scope);
    }

    public static void setProfileAttribute(String attributeName, Date attributeValue) {
        setProfileAttribute(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void setProfileAttribute(String attributeName, Date[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().setProfileAttribute(attributeName, attributeValue, scope);
    }

    public static void setProfileAttribute(String attributeName, Date[] attributeValue) {
        setProfileAttribute(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void addProfileAttributesToSet(String attributeName, long[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().addProfileAttributesToSet(attributeName, attributeValue, scope);
    }

    public static void addProfileAttributesToSet(String attributeName, long[] attributeValue) {
        addProfileAttributesToSet(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void addProfileAttributesToSet(String attributeName, String[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().addProfileAttributesToSet(attributeName, attributeValue, scope);
    }

    public static void addProfileAttributesToSet(String attributeName, String[] attributeValue) {
        addProfileAttributesToSet(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void addProfileAttributesToSet(String attributeName, Date[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().addProfileAttributesToSet(attributeName, attributeValue, scope);
    }

    public static void addProfileAttributesToSet(String attributeName, Date[] attributeValue) {
        addProfileAttributesToSet(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void removeProfileAttributesFromSet(String attributeName, long[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().removeProfileAttributesFromSet(attributeName, attributeValue, scope);
    }

    public static void removeProfileAttributesFromSet(String attributeName, long[] attributeValue) {
        removeProfileAttributesFromSet(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void removeProfileAttributesFromSet(String attributeName, String[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().removeProfileAttributesFromSet(attributeName, attributeValue, scope);
    }

    public static void removeProfileAttributesFromSet(String attributeName, String[] attributeValue) {
        removeProfileAttributesFromSet(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void removeProfileAttributesFromSet(String attributeName, Date[] attributeValue, ProfileScope scope) {
        LocalyticsManager.getInstance().removeProfileAttributesFromSet(attributeName, attributeValue, scope);
    }

    public static void removeProfileAttributesFromSet(String attributeName, Date[] attributeValue) {
        removeProfileAttributesFromSet(attributeName, attributeValue, ProfileScope.APPLICATION);
    }

    public static void incrementProfileAttribute(String attributeName, long incrementValue, ProfileScope scope) {
        LocalyticsManager.getInstance().incrementProfileAttribute(attributeName, incrementValue, scope);
    }

    public static void incrementProfileAttribute(String attributeName, long incrementValue) {
        incrementProfileAttribute(attributeName, incrementValue, ProfileScope.APPLICATION);
    }

    public static void decrementProfileAttribute(String attributeName, long decrementValue, ProfileScope scope) {
        LocalyticsManager.getInstance().incrementProfileAttribute(attributeName, (-1) * decrementValue, scope);
    }

    public static void decrementProfileAttribute(String attributeName, long decrementValue) {
        decrementProfileAttribute(attributeName, decrementValue, ProfileScope.APPLICATION);
    }

    public static void deleteProfileAttribute(String attributeName, ProfileScope scope) {
        LocalyticsManager.getInstance().deleteProfileAttribute(attributeName, scope);
    }

    public static void deleteProfileAttribute(String attributeName) {
        deleteProfileAttribute(attributeName, ProfileScope.APPLICATION);
    }

    public static void setCustomerEmail(String email) {
        LocalyticsManager.getInstance().setSpecialCustomerIdentifierAndAttribute("email", email);
    }

    public static void setCustomerFirstName(String firstName) {
        LocalyticsManager.getInstance().setSpecialCustomerIdentifierAndAttribute("first_name", firstName);
    }

    public static void setCustomerLastName(String lastName) {
        LocalyticsManager.getInstance().setSpecialCustomerIdentifierAndAttribute("last_name", lastName);
    }

    public static void setCustomerFullName(String fullName) {
        LocalyticsManager.getInstance().setSpecialCustomerIdentifierAndAttribute("full_name", fullName);
    }

    @TargetApi(11)
    public static void setInAppMessageDisplayActivity(Activity activity) {
        LocalyticsManager.getInstance().setInAppMessageDisplayActivity(activity);
    }

    @TargetApi(11)
    public static void clearInAppMessageDisplayActivity() {
        LocalyticsManager.getInstance().clearInAppMessageDisplayActivity();
    }

    @TargetApi(11)
    public static void triggerInAppMessage(String triggerName) {
        triggerInAppMessage(triggerName, null);
    }

    @TargetApi(11)
    public static void triggerInAppMessage(String triggerName, Map<String, String> attributes) {
        LocalyticsManager.getInstance().triggerInAppMessage(triggerName, attributes);
    }

    @TargetApi(11)
    public static void dismissCurrentInAppMessage() {
        LocalyticsManager.getInstance().dismissCurrentInAppMessage();
    }

    public static void registerPush(String senderId) {
        LocalyticsManager.getInstance().registerPush(senderId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void retrieveTokenFromInstanceId() {
        LocalyticsManager.getInstance().retrieveTokenFromInstanceId();
    }

    public static String getPushRegistrationId() {
        return LocalyticsManager.getInstance().getPushRegistrationId();
    }

    public static void setPushRegistrationId(String registrationId) {
        LocalyticsManager.getInstance().setPushRegistrationId(registrationId);
    }

    public static void setNotificationsDisabled(boolean disable) {
        LocalyticsManager.getInstance().setNotificationsDisabled(disable);
    }

    public static boolean areNotificationsDisabled() {
        return LocalyticsManager.getInstance().areNotificationsDisabled();
    }

    public static void handlePushNotificationOpened(Intent intent) {
        LocalyticsManager.getInstance().handlePushNotificationOpened(intent);
    }

    public static void handlePushNotificationReceived(Bundle data) {
        LocalyticsManager.getInstance().tagPushReceivedEvent(data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void handleNotificationReceived(Bundle data) {
        LocalyticsManager.getInstance().handleNotificationReceived(data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setReferrerId(String referrerId) {
        LocalyticsManager.getInstance().setReferrerId(referrerId);
    }

    public static void handleTestMode(Intent intent) {
        LocalyticsManager.getInstance().handleTestMode(intent);
    }

    public static void setTestModeEnabled(boolean enabled) {
        LocalyticsManager.getInstance().setTestModeEnabled(enabled);
    }

    public static boolean isTestModeEnabled() {
        return LocalyticsManager.getInstance().isTestModeEnabled();
    }

    @TargetApi(11)
    public static void setInAppMessageDismissButtonImage(Resources resources, int id) {
        LocalyticsManager.getInstance().setInAppMessageDismissButtonImage(resources, id);
    }

    @TargetApi(11)
    public static void setInAppMessageDismissButtonImage(Resources resources, Bitmap image) {
        LocalyticsManager.getInstance().setInAppMessageDismissButtonImage(resources, image);
    }

    public static void setMessagingListener(MessagingListener listener) {
        LocalyticsManager.getInstance().setMessagingListener(listener);
    }

    public static List<InboxCampaign> getInboxCampaigns() {
        return LocalyticsManager.getInstance().getInboxCampaigns();
    }

    public static void refreshInboxCampaigns(InboxRefreshListener callback) {
        LocalyticsManager.getInstance().refreshInboxCampaigns(callback);
    }

    public static void setInboxCampaignRead(long campaignId, boolean read) {
        LocalyticsManager.getInstance().setInboxCampaignRead(campaignId, read);
    }

    public static int getInboxCampaignsUnreadCount() {
        return LocalyticsManager.getInstance().getInboxCampaignsUnreadCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setInboxDetailFragmentDisplaying(Object fragment, boolean displaying) {
        LocalyticsManager.getInstance().setInboxDetailFragmentDisplaying(fragment, displaying);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void downloadInboxThumbnails(List<InboxCampaign> campaigns) {
        LocalyticsManager.getInstance().downloadInboxThumbnails(campaigns);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void priorityDownloadCreative(InboxCampaign campaign, CreativeManager.FirstDownloadedCallback firstDownloadedCallback) {
        LocalyticsManager.getInstance().priorityDownloadCreative(campaign, firstDownloadedCallback);
    }

    public static void setLocationMonitoringEnabled(boolean enabled) {
        LocalyticsManager.getInstance().setLocationMonitoringEnabled(enabled);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isLocationMonitoringEnabled() {
        return LocalyticsManager.getInstance().isLocationMonitoringEnabled();
    }

    public static List<CircularRegion> getGeofencesToMonitor(double latitude, double longitude) {
        return LocalyticsManager.getInstance().getGeofencesToMonitor(latitude, longitude);
    }

    public static void triggerRegion(Region region, Region.Event event) {
        LocalyticsManager.getInstance().triggerRegion(region, event);
    }

    public static void triggerRegions(List<Region> regions, Region.Event event) {
        LocalyticsManager.getInstance().triggerRegions(regions, event);
    }

    public static void setLocationListener(LocationListener listener) {
        LocalyticsManager.getInstance().setLocationListener(listener);
    }

    public static void setIdentifier(String key, String value) {
        LocalyticsManager.getInstance().setIdentifier(key, value);
    }

    public static void setCustomerId(String customerId) {
        LocalyticsManager.getInstance().setCustomerId(customerId);
    }

    public static String getCustomerId() {
        return getIdentifier("customer_id");
    }

    public static String getIdentifier(String key) {
        return LocalyticsManager.getInstance().getIdentifier(key);
    }

    public static void setLocation(Location location) {
        LocalyticsManager.getInstance().setLocation(location);
    }

    public static void onActivityResume(Activity activity) {
        onActivityResume(activity, activity.getIntent());
    }

    private static void onActivityResume(Activity activity, Intent intent) {
        openSession();
        upload();
        setInAppMessageDisplayActivity(activity);
        handleTestMode(intent);
        handlePushNotificationOpened(intent);
    }

    public static void onActivityPaused(Activity activity) {
        dismissCurrentInAppMessage();
        clearInAppMessageDisplayActivity();
        closeSession();
        upload();
    }

    public static void onNewIntent(Activity activity, Intent intent) {
        onActivityResume(activity, intent);
    }

    public static void setOptions(HashMap<String, Object> options) {
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            setOption(entry.getKey(), entry.getValue());
        }
    }

    private static void setOption(String option, Object value) {
        if (optionsNumberKeys.contains(option)) {
            Number numberValue = null;
            if (value != null) {
                if (value instanceof Number) {
                    numberValue = (Number) value;
                } else {
                    Log.e("invalid value " + value + " (should be Number or null) for option " + option + " passed to setOptions()");
                    return;
                }
            }
            if (TextUtils.equals(option, SESSION_TIMEOUT_KEY)) {
                setSessionTimeoutInterval(numberValue == null ? 15L : numberValue.longValue());
                return;
            }
            if (TextUtils.equals(option, MAX_MONITORING_REGIONS_KEY)) {
                setRegionMonitoringLimit(numberValue == null ? 20 : numberValue.intValue());
                return;
            }
            if (TextUtils.equals(option, REGION_THROTTLE_TIME_KEY)) {
                setRegionThrottleTime(numberValue == null ? 1800000L : numberValue.longValue());
                return;
            } else if (TextUtils.equals(option, MIN_REGION_DWELL_TIME_KEY)) {
                setMinRegionDwellTime(numberValue == null ? 30000L : numberValue.longValue());
                return;
            } else {
                if (TextUtils.equals(option, MAX_REGION_DWELL_TIME_KEY)) {
                    setMaxRegionDwellTime(numberValue == null ? 604800000L : numberValue.longValue());
                    return;
                }
                return;
            }
        }
        if (optionBooleanKeys.contains(option)) {
            Boolean booleanValue = null;
            if (value != null) {
                if (value instanceof Boolean) {
                    booleanValue = (Boolean) value;
                } else {
                    Log.e("invalid value " + value + " (should be Boolean or null) for option " + option + " passed to setOptions()");
                    return;
                }
            }
            if (TextUtils.equals(option, USE_SANDBOX_KEY)) {
                setUseSandbox(booleanValue != null ? booleanValue.booleanValue() : false);
                return;
            } else if (TextUtils.equals(option, USE_HTTPS_KEY)) {
                setUseHTTPS(booleanValue == null ? true : booleanValue.booleanValue());
                return;
            } else {
                if (TextUtils.equals(option, IGNORE_STANDARD_EVENT_CLV)) {
                    setIgnoreStandardEventClv(Boolean.valueOf(booleanValue != null ? booleanValue.booleanValue() : false));
                    return;
                }
                return;
            }
        }
        if (optionStringKeys.contains(option)) {
            String stringValue = null;
            if (value != null) {
                if (value instanceof String) {
                    stringValue = (String) value;
                    if (stringValue.isEmpty()) {
                        stringValue = null;
                    }
                } else {
                    Log.e("invalid value " + value + " (should be String or null) for option " + option + " passed to setOptions()");
                    return;
                }
            }
            if (TextUtils.equals(option, ANALYTICS_HOST_KEY)) {
                if (stringValue == null) {
                    stringValue = "analytics.localytics.com";
                }
                setAnalyticsHost(stringValue);
                return;
            }
            if (TextUtils.equals(option, MESSAGING_HOST_KEY)) {
                if (stringValue == null) {
                    stringValue = "analytics.localytics.com";
                }
                setMessagingHost(stringValue);
                return;
            }
            if (TextUtils.equals(option, PROFILES_HOST_KEY)) {
                if (stringValue == null) {
                    stringValue = "profile.localytics.com";
                }
                setProfilesHost(stringValue);
                return;
            } else if (TextUtils.equals(option, MANIFEST_HOST_KEY)) {
                if (stringValue == null) {
                    stringValue = "manifest.localytics.com";
                }
                setManifestHost(stringValue);
                return;
            } else {
                if (TextUtils.equals(option, PLUGIN_LIBRARY_KEY)) {
                    StringBuilder version = new StringBuilder("androida_4.0.1");
                    if (stringValue != null) {
                        version.append(':');
                        version.append(stringValue);
                    }
                    setClientLibraryVersion(version.toString());
                    return;
                }
                return;
            }
        }
        Log.e("invalid key " + option + " passed to setOptions()");
    }

    public static void setLoggingEnabled(boolean enabled) {
        Constants.IS_LOGGING_ENABLED = enabled;
    }

    public static boolean isLoggingEnabled() {
        return Constants.IS_LOGGING_ENABLED;
    }

    static void setSessionTimeoutInterval(long seconds) {
        Constants.SESSION_EXPIRATION = 1000 * seconds;
    }

    static void setRegionMonitoringLimit(int monitoringLimit) {
        Constants.GEOFENCES_MONITORING_LIMIT = Math.max(0, Math.min(100, monitoringLimit));
    }

    static void setRegionThrottleTime(long seconds) {
        Constants.REGION_THROTTLE_CUTOFF_TIME_MILLIS = Math.max(0L, 1000 * seconds);
    }

    static void setMinRegionDwellTime(long seconds) {
        Constants.MIN_REGION_DWELL_TIME_MILLIS = Math.max(0L, 1000 * seconds);
    }

    static void setMaxRegionDwellTime(long seconds) {
        Constants.MAX_REGION_DWELL_TIME_MILLIS = Math.max(0L, 1000 * seconds);
    }

    public static String getInstallId() {
        return LocalyticsManager.getInstance().getInstallationId();
    }

    public static String getAppKey() {
        return LocalyticsManager.getInstance().getAppKey();
    }

    public static String getLibraryVersion() {
        return Constants.LOCALYTICS_CLIENT_LIBRARY_VERSION;
    }

    @TargetApi(11)
    public static void setInAppMessageDismissButtonLocation(InAppMessageDismissButtonLocation buttonLocation) {
        LocalyticsManager.getInstance().setInAppMessageDismissButtonLocation(buttonLocation);
    }

    @TargetApi(11)
    public static InAppMessageDismissButtonLocation getInAppMessageDismissButtonLocation() {
        return LocalyticsManager.getInstance().getInAppMessageDismissButtonLocation();
    }

    static void setUseHTTPS(boolean useHTTPS) {
        Constants.USE_HTTPS = useHTTPS;
    }

    static void setUseSandbox(boolean useSandbox) {
        if (useSandbox) {
            setAnalyticsHost("queuer-elb.sandbox53.localytics.com");
            setMessagingHost("queuer-elb.sandbox53.localytics.com");
            setProfilesHost("profile-api.sandbox53.localytics.com");
            setManifestHost("manifest.sandbox53.localytics.com");
            return;
        }
        setAnalyticsHost("analytics.localytics.com");
        setMessagingHost("analytics.localytics.com");
        setProfilesHost("profile.localytics.com");
        setManifestHost("manifest.localytics.com");
    }

    static void setAnalyticsHost(String host) {
        Constants.ANALYTICS_HOST = host;
    }

    static void setMessagingHost(String host) {
        Constants.MARKETING_HOST = host;
    }

    static void setManifestHost(String host) {
        Constants.MANIFEST_HOST = host;
    }

    static void setClientLibraryVersion(String library) {
        Constants.LOCALYTICS_CLIENT_LIBRARY_VERSION = library;
    }

    static void setIgnoreStandardEventClv(Boolean ignoreCLV) {
        Constants.IGNORE_STANDARD_EVENT_CLV = ignoreCLV;
    }

    static void setProfilesHost(String host) {
        Constants.PROFILES_HOST = host;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Log {
        Log() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int d(String msg) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.d("Localytics", msg);
            }
            return -1;
        }

        static int d(String msg, Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.d("Localytics", msg, tr);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int e(String msg) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.e("Localytics", msg);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int e(String msg, Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.e("Localytics", msg, tr);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int i(String msg) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.i("Localytics", msg);
            }
            return -1;
        }

        static int i(String msg, Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.i("Localytics", msg, tr);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int v(String msg, Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.v("Localytics", msg, tr);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int v(String msg) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.v("Localytics", msg);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int w(Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.w("Localytics", tr);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int w(String msg, Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.w("Localytics", msg, tr);
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int w(String msg) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.w("Localytics", msg);
            }
            return -1;
        }

        static int wtf(Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.wtf("Localytics", tr);
            }
            return -1;
        }

        static int wtf(String msg, Throwable tr) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.wtf("Localytics", msg, tr);
            }
            return -1;
        }

        static int wtf(String msg) {
            if (Constants.IS_LOGGING_ENABLED) {
                return android.util.Log.wtf("Localytics", msg);
            }
            return -1;
        }
    }
}
