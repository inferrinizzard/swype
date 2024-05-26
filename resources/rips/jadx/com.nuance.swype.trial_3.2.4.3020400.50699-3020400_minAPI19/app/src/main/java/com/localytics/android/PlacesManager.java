package com.localytics.android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import com.facebook.internal.ServerProtocol;
import com.localytics.android.BaseProvider;
import com.localytics.android.Localytics;
import com.localytics.android.PlacesCampaign;
import com.localytics.android.Region;
import com.nuance.connect.comm.MessageAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PlacesManager extends BasePushManager {
    private static final String PLACES_PUSH_OPENED_EVENT = "Localytics Places Push Opened";
    private static final String PLACES_PUSH_RECEIVED_EVENT = "Localytics Places Push Received";

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlacesManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler) {
        super(localyticsDao, marketingHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _processMarketingObject(boolean successful, Map<String, Object> marketingMap, Map<String, Object> config) {
        if (successful) {
            try {
                if (marketingMap != null) {
                    List<MarketingMessage> marketingMessages = new ArrayList<>();
                    Object placesObject = marketingMap.get("places");
                    if (placesObject != null) {
                        for (Map<String, Object> obj : JsonHelper.toList((JSONArray) JsonHelper.toJSON(placesObject))) {
                            marketingMessages.add(new MarketingMessage(obj));
                        }
                    }
                    _removeDeactivatedCampaigns(marketingMessages);
                    for (MarketingMessage marketingMessage : marketingMessages) {
                        _savePlacesCampaign(marketingMessage, config);
                    }
                    return;
                }
                _removeDeactivatedCampaigns(new LinkedList());
            } catch (JSONException e) {
                Localytics.Log.e("JSONException", e);
            }
        }
    }

    private long _savePlacesCampaign(MarketingMessage marketingMessage, Map<String, Object> config) {
        if (!_validatePlacesMarketingMessage(marketingMessage)) {
            Localytics.Log.e(String.format("places campaign is invalid:\n%s", marketingMessage.toString()));
            return 0L;
        }
        long campaignId = JsonHelper.getSafeLongFromMap(marketingMessage, "campaign_id");
        if (_hasMessageBeenDisplayed(campaignId) && !Constants.isTestModeEnabled()) {
            Localytics.Log.e(String.format("No update needed. Places campaign has already displayed\n\t campaignID = %d", Long.valueOf(campaignId)));
            return 0L;
        }
        long localVersion = 0;
        Cursor cursorCampaign = null;
        try {
            cursorCampaign = this.mProvider.query("places_campaigns", new String[]{ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION}, String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)}, null);
            if (cursorCampaign.moveToFirst()) {
                localVersion = cursorCampaign.getLong(cursorCampaign.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION));
            }
            if (localVersion > 0) {
                Localytics.Log.w(String.format("Places campaign already exists for this campaign\n\t campaignID = %d", Long.valueOf(campaignId)));
                long remoteVersion = JsonHelper.getSafeLongFromMap(marketingMessage, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION);
                if (localVersion >= remoteVersion) {
                    Localytics.Log.w(String.format("No update needed. Campaign version has not been updated\n\t version: %d", Long.valueOf(localVersion)));
                    return 0L;
                }
            } else {
                Localytics.Log.w("Places campaign not found. Creating a new one.");
            }
            ContentValues values = _parsePlacesMarketingMessage(marketingMessage, config);
            long campaignId2 = this.mProvider.replace("places_campaigns", values);
            if (campaignId2 == -1) {
                Localytics.Log.e(String.format("Failed to replace places campaign %d", Long.valueOf(campaignId2)));
                return -1L;
            }
            if (campaignId2 > 0) {
                _saveGeofenceTriggers(campaignId2, JsonHelper.getSafeListFromMap(marketingMessage, "triggering_geofences"));
                _saveTriggerEvents(campaignId2, JsonHelper.getSafeListFromMap(marketingMessage, "triggering_events"));
                _saveCampaignAttributes(campaignId2, JsonHelper.getSafeMapFromMap(marketingMessage, "attributes"));
                return campaignId2;
            }
            return campaignId2;
        } finally {
            if (cursorCampaign != null) {
                cursorCampaign.close();
            }
        }
    }

    private boolean _validatePlacesMarketingMessage(MarketingMessage marketingMessage) {
        long campaignId = JsonHelper.getSafeLongFromMap(marketingMessage, "campaign_id");
        long creativeId = JsonHelper.getSafeLongFromMap(marketingMessage, "ab");
        long version = JsonHelper.getSafeLongFromMap(marketingMessage, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION);
        long expiration = JsonHelper.getSafeLongFromMap(marketingMessage, "expiration");
        String ruleName = JsonHelper.getSafeStringFromMap(marketingMessage, "rule_name");
        List<Object> triggeringGeofences = JsonHelper.getSafeListFromMap(marketingMessage, "triggering_geofences");
        List<Object> triggeringEvents = JsonHelper.getSafeListFromMap(marketingMessage, "triggering_events");
        long now = this.mLocalyticsDao.getCurrentTimeMillis() / 1000;
        return campaignId > 0 && creativeId > 0 && version > 0 && ruleName != null && triggeringGeofences != null && triggeringGeofences.size() > 0 && triggeringEvents != null && triggeringEvents.size() > 0 && (expiration > now || Constants.isTestModeEnabled());
    }

    private boolean _hasMessageBeenDisplayed(long campaignId) {
        Cursor cursorDisplayed = null;
        try {
            cursorDisplayed = this.mProvider.query("places_campaigns_displayed", new String[]{"campaign_id"}, String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)}, null);
            boolean displayed = cursorDisplayed.moveToFirst();
            return displayed;
        } finally {
            if (cursorDisplayed != null) {
                cursorDisplayed.close();
            }
        }
    }

    private ContentValues _parsePlacesMarketingMessage(MarketingMessage marketingMessage, Map<String, Object> config) {
        int schemaVersion;
        ContentValues values = new ContentValues();
        values.put("campaign_id", Long.valueOf(JsonHelper.getSafeLongFromMap(marketingMessage, "campaign_id")));
        values.put("creative_id", Long.valueOf(JsonHelper.getSafeLongFromMap(marketingMessage, "ab")));
        values.put("creative_type", JsonHelper.getSafeStringFromMap(marketingMessage, "creative_type"));
        values.put("expiration", Long.valueOf(JsonHelper.getSafeLongFromMap(marketingMessage, "expiration")));
        values.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, Long.valueOf(JsonHelper.getSafeLongFromMap(marketingMessage, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
        values.put("ab_test", JsonHelper.getSafeStringFromMap(marketingMessage, "ab"));
        values.put("rule_name", JsonHelper.getSafeStringFromMap(marketingMessage, "rule_name"));
        values.put("control_group", Integer.valueOf(JsonHelper.getSafeIntegerFromMap(marketingMessage, "control_group")));
        values.put("message", JsonHelper.getSafeStringFromMap(marketingMessage, "message"));
        values.put("sound_filename", JsonHelper.getSafeStringFromMap(marketingMessage, "sound_filename"));
        if (config != null && (schemaVersion = JsonHelper.getSafeIntegerFromMap(config, "schema_version")) > 0) {
            values.put("schema_version", Integer.valueOf(schemaVersion));
        }
        return values;
    }

    private void _saveGeofenceTriggers(long campaignId, List<Object> triggeringPlaceIds) {
        this.mProvider.remove("places_campaigns_geofence_triggers", String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)});
        if (triggeringPlaceIds != null) {
            for (Object obj : triggeringPlaceIds) {
                ContentValues values = new ContentValues();
                values.put("place_id", Long.valueOf(obj.toString()));
                values.put("campaign_id", Long.valueOf(campaignId));
                this.mProvider.insert("places_campaigns_geofence_triggers", values);
            }
        }
    }

    private void _saveTriggerEvents(long campaignId, List<Object> triggeringEvents) {
        this.mProvider.remove("places_campaigns_events", String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)});
        if (triggeringEvents != null) {
            for (Object obj : triggeringEvents) {
                ContentValues values = new ContentValues();
                values.put("event", obj.toString());
                values.put("campaign_id", Long.valueOf(campaignId));
                this.mProvider.insert("places_campaigns_events", values);
            }
        }
    }

    private void _saveCampaignAttributes(long campaignId, Map<String, Object> attributes) {
        if (attributes != null) {
            try {
                for (String key : attributes.keySet()) {
                    ContentValues values = new ContentValues(attributes.size() + 1);
                    values.put("key", key);
                    values.put("value", attributes.get(key).toString());
                    values.put("campaign_id", Long.valueOf(campaignId));
                    if (this.mProvider.insert("places_campaign_attributes", values) <= 0) {
                        Localytics.Log.e(String.format("Failed to insert attributes for places campaign id %d", Long.valueOf(campaignId)));
                    }
                }
            } catch (ClassCastException e) {
                Localytics.Log.e(String.format("Cannot parse places attributes data: %s", attributes.toString()));
            }
        }
    }

    private void _removeDeactivatedCampaigns(List<MarketingMessage> marketingMessages) {
        if (marketingMessages.size() > 0) {
            String inClause = BaseProvider.buildSqlInClause(marketingMessages, new BaseProvider.InClauseBuilder<MarketingMessage>() { // from class: com.localytics.android.PlacesManager.1
                @Override // com.localytics.android.BaseProvider.InClauseBuilder
                public Object getValue(MarketingMessage object) {
                    return Long.valueOf(JsonHelper.getSafeLongFromMap(object, "campaign_id"));
                }
            });
            this.mProvider.remove("places_campaigns", String.format("%s NOT IN %s", "campaign_id", inClause), null);
        } else {
            this.mProvider.remove("places_campaigns", null, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _triggerRegions(List<Region> regions, Region.Event event) {
        for (Region region : regions) {
            if (_circularRegionTrigger(region, event)) {
                return true;
            }
        }
        return false;
    }

    private boolean _circularRegionTrigger(Region region, Region.Event event) {
        PlacesCampaign campaign;
        if (region instanceof CircularRegion) {
            for (Long campaignId : _getDisplayableCampaignIdsFromGeofencePlaceId(region.getPlaceId())) {
                if (_triggerEventMatchesCampaign(event.toString(), campaignId.longValue()) && (campaign = _placesCampaignForRegionEvent(campaignId.longValue(), region, event)) != null) {
                    if (this.mLocalyticsDao.areNotificationsDisabled()) {
                        Localytics.Log.w("Got places push notification while push is disabled.");
                    } else {
                        boolean allowedByListener = true;
                        synchronized (this.mMarketingHandler) {
                            MessagingListener devListener = this.mMarketingHandler.mListeners.getDevListener();
                            if (devListener != null) {
                                allowedByListener = devListener.localyticsShouldShowPlacesPushNotification(campaign);
                            }
                        }
                        if (allowedByListener && _setCampaignAsDisplayed(campaignId.longValue()) > 0) {
                            _tagPushReceived(campaign);
                            if (_hasMessage(campaign.getMessage()) && !campaign.isControlGroup()) {
                                _showNotificationForCampaign(campaign);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    List<Long> _getDisplayableCampaignIdsFromGeofencePlaceId(long placeId) {
        List<Long> campaignIds = new ArrayList<>();
        Cursor cursor = null;
        try {
            String displayColumn = String.format("%s.%s", "places_campaigns_displayed", "campaign_id");
            String campaignIdValue = String.format("%s.%s", "places_campaigns_geofence_triggers", "campaign_id");
            String rawSql = String.format("SELECT %s FROM %s WHERE %s > ? AND %s IN (SELECT %s FROM %s NATURAL LEFT OUTER JOIN %s WHERE %s IS NULL AND %s = ? ORDER BY %s);", "campaign_id", "places_campaigns", "expiration", "campaign_id", campaignIdValue, "places_campaigns_geofence_triggers", "places_campaigns_displayed", displayColumn, "place_id", campaignIdValue);
            cursor = this.mProvider.mDb.rawQuery(rawSql, new String[]{Long.toString(this.mLocalyticsDao.getCurrentTimeMillis() / 1000), Long.toString(placeId)});
            while (cursor.moveToNext()) {
                campaignIds.add(Long.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow("campaign_id"))));
            }
            return campaignIds;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean _triggerEventMatchesCampaign(String triggerEvent, long campaignId) {
        for (String event : _getTriggeringEventsFromCampaignId(campaignId)) {
            if (triggerEvent.equalsIgnoreCase(event)) {
                return true;
            }
        }
        return false;
    }

    private List<String> _getTriggeringEventsFromCampaignId(long campaignId) {
        List<String> events = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("places_campaigns_events", null, String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)}, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                events.add(cursor.getString(cursor.getColumnIndexOrThrow("event")));
            }
            return events;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private PlacesCampaign _placesCampaignForRegionEvent(long campaignId, Region region, Region.Event event) {
        Cursor cursor = null;
        try {
            long now = this.mLocalyticsDao.getCurrentTimeMillis() / 1000;
            cursor = this.mProvider.query("places_campaigns", null, String.format("%s = ? AND %s > ?", "campaign_id", "expiration"), new String[]{Long.toString(campaignId), Long.toString(now)}, null);
            if (cursor.moveToFirst()) {
                PlacesCampaign build = new PlacesCampaign.Builder().setCampaignId(cursor.getLong(cursor.getColumnIndexOrThrow("campaign_id"))).setRuleName(cursor.getString(cursor.getColumnIndexOrThrow("rule_name"))).setCreativeId(cursor.getLong(cursor.getColumnIndexOrThrow("creative_id"))).setCreativeType(cursor.getString(cursor.getColumnIndexOrThrow("creative_type"))).setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message"))).setSoundFilename(cursor.getString(cursor.getColumnIndexOrThrow("sound_filename"))).setRegion(region).setControlGroup(cursor.getInt(cursor.getColumnIndexOrThrow("control_group")) != 0).setAbTest(cursor.getString(cursor.getColumnIndexOrThrow("ab_test"))).setVersion(cursor.getLong(cursor.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION))).setSchemaVersion(cursor.getInt(cursor.getColumnIndexOrThrow("schema_version"))).setTriggerEvent(event).setAttributes(_getCampaignAttributes(campaignId)).build();
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Map<String, String> _getCampaignAttributes(long campaignId) {
        Map<String, String> attributes = new HashMap<>();
        if (campaignId > 0) {
            Cursor cursor = null;
            try {
                cursor = this.mProvider.query("places_campaign_attributes", null, String.format("%s = ?", "campaign_id"), new String[]{Long.toString(campaignId)}, null);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    attributes.put(cursor.getString(cursor.getColumnIndexOrThrow("key")), cursor.getString(cursor.getColumnIndexOrThrow("value")));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return attributes;
    }

    private boolean _tagPushReceived(PlacesCampaign campaign) {
        String message = campaign.getMessage();
        String creativeType = campaign.getCreativeType();
        campaign.setCreativeType(_creativeTypeForMessage(creativeType, message));
        Map<String, String> extraAttributes = new HashMap<>(1);
        Region region = campaign.getRegion();
        if (region != null) {
            extraAttributes.put("Localytics Place ID", Long.toString(region.getPlaceId()));
            extraAttributes.put("Region Identifier", region.getUniqueId());
            extraAttributes.put("Region Type", region.getType());
            extraAttributes.putAll(region.getAttributes());
        }
        return _tagPushReceived(PLACES_PUSH_RECEIVED_EVENT, message, campaign.getCampaignId(), String.valueOf(campaign.getCreativeId()), String.valueOf(campaign.getSchemaVersion()), creativeType, 0, 0, extraAttributes);
    }

    private void _showNotificationForCampaign(PlacesCampaign campaign) {
        Bundle extras = new Bundle();
        extras.putParcelable("places_campaign", campaign);
        _showPushNotification(campaign.getMessage(), campaign.getSoundFilename(), campaign.getCampaignId(), campaign, extras);
    }

    private long _setCampaignAsDisplayed(long campaignId) {
        ContentValues values = new ContentValues();
        values.put("campaign_id", Long.valueOf(campaignId));
        return this.mProvider.insert("places_campaigns_displayed", values);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handlePushNotificationOpened(Intent intent) {
        PlacesCampaign campaign;
        if (intent != null && intent.getExtras() != null && (campaign = (PlacesCampaign) intent.getExtras().getParcelable("places_campaign")) != null) {
            long campaignId = campaign.getCampaignId();
            long creativeId = campaign.getCreativeId();
            if (campaignId > 0 && creativeId > 0) {
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put("Campaign ID", String.valueOf(campaignId));
                attributes.put("Creative ID", String.valueOf(creativeId));
                attributes.put("Creative Type", campaign.getCreativeType());
                attributes.put("Action", "Click");
                attributes.put("Schema Version - Client", MessageAPI.DEVICE_ID);
                attributes.put("Schema Version - Server", String.valueOf(campaign.getSchemaVersion()));
                Region region = campaign.getRegion();
                if (region != null) {
                    attributes.put("Localytics Place ID", Long.toString(region.getPlaceId()));
                    attributes.put("Region Identifier", region.getUniqueId());
                    attributes.put("Region Type", region.getType());
                    attributes.putAll(region.getAttributes());
                }
                this.mLocalyticsDao.tagEvent(PLACES_PUSH_OPENED_EVENT, attributes);
                this.mLocalyticsDao.upload();
            }
            intent.removeExtra("places_campaign");
        }
    }
}
