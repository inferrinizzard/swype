package com.localytics.android;

import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Constants {
    static final String AB_ATTRIBUTE = "ab";
    static final String ACTION_CLICK = "click";
    static final String ACTION_CONTROL = "control";
    static final String ACTION_DISMISS = "X";
    static final String ACTION_THUMBNAIL_DOWNLOADED = "com.localytics.intent.action.THUMBNAIL_DOWNLOADED";
    static final String ADID_STRING = "adid";
    static final String AMP_KEY = "amp";
    static final long ANALYTICS_MAX_DB_SIZE_IN_MEGABYTES = 10;
    static final String ATTRIBUTES = "attributes";
    static final String ATTRIBUTES_KEY = "attributes";
    static final String BLACKOUT_RULES_KEY = "blackout_rules";
    static final String CAMPAIGN_ID_KEY = "campaign_id";
    static final String CONDITIONS_KEY = "conditions";
    static final String CONFIG_KEY = "config";
    static final String COUNT_KEY = "count";
    static final String CREATIVE_DIR = "creative";
    static final String CUSTOMER_EMAIL = "email";
    static final String CUSTOMER_FIRST_NAME = "first_name";
    static final String CUSTOMER_FULL_NAME = "full_name";
    static final String CUSTOMER_ID = "customer_id";
    static final String CUSTOMER_LAST_NAME = "last_name";
    static final String CUSTOM_DIMENSION_DB_KEY = "custom_dimension_";
    static final String CUSTOM_DIMENSION_UPLOAD_KEY = "c";
    static final String DATES_KEY = "dates";
    static final String DAYS_KEY = "days";
    static final double DB_VACUUM_THRESHOLD = 0.8d;
    static final String DEFAULT_ANALYTICS_HOST = "analytics.localytics.com";
    static final int DEFAULT_GEOFENCES_MONITORING_LIMIT = 20;
    static final String DEFAULT_HTML_PAGE = "index.html";
    static final long DEFAULT_INBOX_THROTTLE_MILLIS = 240000;
    static final String DEFAULT_IN_APP_ZIP_PAGE = "amp_rule_%d.zip";
    static final String DEFAULT_MARKETING_HOST = "analytics.localytics.com";
    static final long DEFAULT_SESSION_EXPIRATION_SECONDS = 15;
    static final String DEFAULT_THUMBNAIL = "inbox_%d.png";
    static final String DEVICE_BOTH = "both";
    static final String DEVICE_PHONE = "phone";
    static final String DEVICE_TABLET = "tablet";
    static final float DISMISS_BUTTON_SIDE = 40.0f;
    static final String DISPLAY_EVENTS_KEY = "display_events";
    static final String DISPLAY_FREQUENCIES_KEY = "display_frequencies";
    static final String END_KEY = "end";
    static final String ENTER_EVENT = "enter";
    static final String EVENT_FORMAT = "%s:%s";
    static final String EXIT_EVENT = "exit";
    static final String FREQUENCY_CAPPING_KEY = "frequency_capping";
    static final String GEOFENCES = "geofences";
    static final String HEIGHT_KEY = "height";
    static final String IGNORE_GLOBAL_KEY = "ignore_global";
    static final String INAPP_AB_KEY = "ampAB";
    static final String INAPP_ACTION_KEY = "ampAction";
    static final String INAPP_CAMPAIGN_ID_KEY = "ampCampaignId";
    static final String INAPP_CAMPAIGN_KEY = "ampCampaign";
    static final String INAPP_EVENT_NAME_KEY = "ampView";
    static final String INAPP_TYPE_KEY = "type";
    static final String INBOX_ACTION_KEY = "Action";
    static final String INBOX_CAMPAIGN_ID_KEY = "Campaign ID";
    static final String INBOX_CREATIVE_ASSETS = "inbox_creative_assets_%d";
    static final String INBOX_CREATIVE_ASSETS_ZIP = "inbox_creative_assets_%d.zip";
    static final String INBOX_CREATIVE_ID_KEY = "Creative ID";
    static final String INBOX_EVENT_NAME_KEY = "Localytics Inbox Message Viewed";
    static final String INBOX_KEY = "inboxes";
    static final String INBOX_THROTTLE_KEY = "inbox_throttle";
    static final String INBOX_TYPE_KEY = "Type";
    static final double INVALID_LAT_LNG = 360.0d;
    static final String IN_APP_CREATIVE_ASSETS = "marketing_rule_%d";
    static final boolean IS_DEVICE_IDENTIFIER_UPLOADED = true;
    static final boolean IS_EXCEPTION_SUPPRESSION_ENABLED = true;
    static final boolean IS_PARAMETER_CHECKING_ENABLED = false;
    static final String KEY_BASE_PATH = "base_path";
    static final String KEY_CREATIVE_URL = "creative_url";
    static final String KEY_DISPLAY_HEIGHT = "display_height";
    static final String KEY_DISPLAY_WIDTH = "display_width";
    static final String KEY_DOWNLOAD_URL = "download_url";
    static final String KEY_HTML_URL = "html_url";
    static final String KEY_LOCAL_FILE_LOCATION = "local_file_location";
    static final String KEY_ZIP_NAME = "zip_name";
    static final String LL_DEEP_LINK_URL = "ll_deep_link_url";
    static final String LL_KEY = "ll";
    static final String LL_PUBLIC_MESSAGE_KEY = "ll_public_message";
    static final String LL_SOUND_FILENAME_KEY = "ll_sound_filename";
    static final String LOCALYTICS_DIR = ".localytics";
    static final String LOCALYTICS_METADATA_APP_KEY = "LOCALYTICS_APP_KEY";
    static final String LOCALYTICS_METADATA_ROLLUP_KEY = "LOCALYTICS_ROLLUP_KEY";
    static final String LOCALYTICS_PACKAGE_NAME = "com.localytics.android";
    static final String LOCALYTICS_PLACE_ID = "Localytics Place ID";
    static final long LOCATION_MANIFEST_UPDATE_INTERVAL_MILLIS = 86400000;
    static final long LOCATION_MAX_DB_SIZE_IN_MEGABYTES = 10;
    static final String LOG_TAG = "Localytics";
    static final long MANIFEST_MAX_DB_SIZE_IN_MEGABYTES = 1;
    static final String MARKETING_ACTION_STRING = "ampAction";
    static final int MARKETING_CLIENT_SCHEMA_VERSION = 5;
    static final String MARKETING_FIRST_RUN_TRIGGER = "AMP First Run";
    static final long MARKETING_MAX_DB_SIZE_IN_MEGABYTES = 10;
    static final String MARKETING_START_TRIGGER = "AMP Loaded";
    static final String MARKETING_UPGRADE_TRIGGER = "AMP upgrade";
    static final int MAXIMUM_ROWS_PER_UPLOAD = 100;
    static final int MAX_CUSTOM_DIMENSIONS = 20;
    static final String MAX_DISPLAY_COUNT_KEY = "max_display_count";
    static final int MAX_NAME_LENGTH = 128;
    static final int MAX_NUMBER_OF_UPLOAD_RETRIES = 3;
    static final int MAX_NUM_ATTRIBUTES = 50;
    static final int MAX_VALUE_LENGTH = 255;
    static final String NO_LITERAL = "No";
    static final int NUMBER_OF_EOF_ATTEMPTS = 3;
    static final String OPEN_EVENT = "open";
    static final String OPEN_EXTERNAL = "ampExternalOpen";
    static final String PERMISSIONS_NOT_GRANTED = "Permissions Not Granted";
    static final String PHONE_SIZE_KEY = "phone_size";
    static final String PLACES_CAMPAIGN_KEY = "places_campaign";
    static final String PLACES_DATA_LAST_MODIFIED = "places_data_last_modified";
    static final String PLACES_DATA_URL = "places_data_url";
    static final String PLACES_KEY = "places";
    static final String PLACE_ENTERED_EVENT = "Localytics Place Entered";
    static final String PLACE_VISITED_EVENT = "Localytics Place Visited";
    static final long PROFILES_MAX_DB_SIZE_IN_MEGABYTES = 1;
    static final String PROTOCOL_FILE = "file";
    static final String PROTOCOL_HTTP = "http";
    static final String PROTOCOL_HTTPS = "https";
    static final String PUSH_REGISTERED_EVENT = "Localytics Push Registered";
    static final String REGION_DWELL_TIME = "Dwell Time (minutes)";
    static final String REGION_IDENTIFIER = "Region Identifier";
    static final String REGION_TYPE = "Region Type";
    static final String SANDBOX_ANALYTICS_HOST = "queuer-elb.sandbox53.localytics.com";
    static final String SANDBOX_MANIFEST_HOST = "manifest.sandbox53.localytics.com";
    static final String SANDBOX_MARKETING_HOST = "queuer-elb.sandbox53.localytics.com";
    static final String SANDBOX_PROFILES_HOST = "profile-api.sandbox53.localytics.com";
    static final String SCHEMA_VERSION_CLIENT_ATTRIBUTE = "Schema Version - Client";
    static final String SCHEMA_VERSION_SERVER_ATTRIBUTE = "Schema Version - Server";
    static final String SERVER_SCHEMA_VERSION_KEY = "schema_version";
    static final long SESSION_START_MARKETING_MESSAGE_DELAY = 5000;
    static final String SPECIAL_PROFILE_ATTRIBUTE_PREFIX = "$";
    static final String STANDARD_EVENT_ADDED_TO_CART = "Localytics Added To Cart";
    static final String STANDARD_EVENT_ATTR_CONTENT_ID = "Content ID";
    static final String STANDARD_EVENT_ATTR_CONTENT_NAME = "Content Name";
    static final String STANDARD_EVENT_ATTR_CONTENT_RATING = "Content Rating";
    static final String STANDARD_EVENT_ATTR_CONTENT_TYPE = "Content Type";
    static final String STANDARD_EVENT_ATTR_ITEM_COUNT = "Item Count";
    static final String STANDARD_EVENT_ATTR_ITEM_ID = "Item ID";
    static final String STANDARD_EVENT_ATTR_ITEM_NAME = "Item Name";
    static final String STANDARD_EVENT_ATTR_ITEM_PRICE = "Item Price";
    static final String STANDARD_EVENT_ATTR_ITEM_TYPE = "Item Type";
    static final String STANDARD_EVENT_ATTR_METHOD = "Method Name";
    static final String STANDARD_EVENT_ATTR_RESULT_COUNT = "Search Result Count";
    static final String STANDARD_EVENT_ATTR_SEARCH_QUERY = "Search Query";
    static final String STANDARD_EVENT_ATTR_TOTAL_PRICE = "Total Price";
    static final String STANDARD_EVENT_COMPLETED_CHECKOUT = "Localytics Completed Checkout";
    static final String STANDARD_EVENT_CONTENT_RATED = "Localytics Content Rated";
    static final String STANDARD_EVENT_CONTENT_VIEWED = "Localytics Content Viewed";
    static final String STANDARD_EVENT_INVITED = "Localytics Invited";
    static final String STANDARD_EVENT_LOGGED_IN = "Localytics Logged In";
    static final String STANDARD_EVENT_LOGGED_OUT = "Localytics Logged Out";
    static final String STANDARD_EVENT_PURCHASED = "Localytics Purchased";
    static final String STANDARD_EVENT_REGISTERED = "Localytics Registered";
    static final String STANDARD_EVENT_SEARCHED = "Localytics Searched";
    static final String STANDARD_EVENT_SHARED = "Localytics Shared";
    static final String STANDARD_EVENT_STARTED_CHECKOUT = "Localytics Started Checkout";
    static final String START_KEY = "start";
    static final String TABLET_SIZE_KEY = "tablet_size";
    static final String TEST_MODE_UPDATE_DATA = "Test Mode Update Data";
    static final String THUMBNAILS_DIR = "thumbnails";
    static final String TIMES_KEY = "times";
    static final String TRIGGERING_EVENTS_KEY = "triggering_events";
    static final String TRIGGERING_GEOFENCES_KEY = "triggering_geofences";
    static final String TYPE_CONTROL = "Control";
    static final String TYPE_IN_APP = "In-App";
    static final String TYPE_IN_BOX = "Inbox";
    static final long UPLOAD_BACKOFF = 10000;
    static final String USER_TYPE = "type";
    static final String USER_TYPE_ANONYMOUS = "anonymous";
    static final String USER_TYPE_KNOWN = "known";
    static final boolean USE_EXTERNAL_DIRECTORY = false;
    static final String WEEKDAYS_KEY = "weekdays";
    static final String WIDTH_KEY = "width";
    static final String WIFI_ENABLED = "Wifi Enabled";
    static final String WIFI_MANAGER_NULL = "Wifi Manager is Null";
    static final String YES_LITERAL = "Yes";
    static final String DEFAULT_LOCALYTICS_CLIENT_LIBRARY_VERSION = "androida_4.0.1";
    static String LOCALYTICS_CLIENT_LIBRARY_VERSION = DEFAULT_LOCALYTICS_CLIENT_LIBRARY_VERSION;
    static final int CURRENT_API_LEVEL = DatapointHelper.getApiLevel();
    private static final AtomicBoolean sTestModeEnabled = new AtomicBoolean(false);
    static final long BYTES_IN_A_MEGABYTE = (long) Math.pow(1024.0d, 2.0d);
    static long SESSION_EXPIRATION = 15000;
    static boolean IS_LOGGING_ENABLED = false;
    static boolean USE_HTTPS = true;
    static String ANALYTICS_HOST = "analytics.localytics.com";
    static String MARKETING_HOST = "analytics.localytics.com";
    static final String DEFAULT_PROFILES_HOST = "profile.localytics.com";
    static String PROFILES_HOST = DEFAULT_PROFILES_HOST;
    static final String DEFAULT_MANIFEST_HOST = "manifest.localytics.com";
    static String MANIFEST_HOST = DEFAULT_MANIFEST_HOST;
    static int GEOFENCES_MONITORING_LIMIT = 20;
    static final long DEFAULT_MAX_REGION_DWELL_TIME_MILLIS = 604800000;
    static long MAX_REGION_DWELL_TIME_MILLIS = DEFAULT_MAX_REGION_DWELL_TIME_MILLIS;
    static final long DEFAULT_MIN_REGION_DWELL_TIME_MILLIS = 30000;
    static long MIN_REGION_DWELL_TIME_MILLIS = DEFAULT_MIN_REGION_DWELL_TIME_MILLIS;
    static final long DEFAULT_REGION_THROTTLE_CUTOFF_TIME_MILLIS = 1800000;
    static long REGION_THROTTLE_CUTOFF_TIME_MILLIS = DEFAULT_REGION_THROTTLE_CUTOFF_TIME_MILLIS;
    static Boolean IGNORE_STANDARD_EVENT_CLV = false;

    Constants() {
        throw new UnsupportedOperationException("This class is non-instantiable");
    }

    public static boolean isTestModeEnabled() {
        return sTestModeEnabled.get();
    }

    public static void setTestModeEnabled(boolean enabled) {
        sTestModeEnabled.set(enabled);
    }

    public static String getHTTPPreface() {
        Object[] objArr = new Object[1];
        objArr[0] = USE_HTTPS ? PROTOCOL_HTTPS : PROTOCOL_HTTP;
        return String.format("%s://", objArr);
    }
}
