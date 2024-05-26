package com.google.api.services.gmail;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import com.google.api.services.gmail.model.AutoForwarding;
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Filter;
import com.google.api.services.gmail.model.ForwardingAddress;
import com.google.api.services.gmail.model.ImapSettings;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListDraftsResponse;
import com.google.api.services.gmail.model.ListFiltersResponse;
import com.google.api.services.gmail.model.ListForwardingAddressesResponse;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListSendAsResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.ModifyThreadRequest;
import com.google.api.services.gmail.model.PopSettings;
import com.google.api.services.gmail.model.Profile;
import com.google.api.services.gmail.model.Thread;
import com.google.api.services.gmail.model.VacationSettings;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;
import java.io.IOException;
import java.math.BigInteger;

/* loaded from: classes.dex */
public class Gmail extends AbstractGoogleJsonClient {
    public static final String DEFAULT_BASE_URL = "https://www.googleapis.com/gmail/v1/users/";
    public static final String DEFAULT_ROOT_URL = "https://www.googleapis.com/";
    public static final String DEFAULT_SERVICE_PATH = "gmail/v1/users/";

    static {
        boolean z = GoogleUtils.MAJOR_VERSION.intValue() == 1 && GoogleUtils.MINOR_VERSION.intValue() >= 15;
        Object[] objArr = {GoogleUtils.VERSION};
        if (z) {
        } else {
            throw new IllegalStateException(Preconditions.format("You are currently running with version %s of google-api-client. You need at least version 1.15 of google-api-client to run version 1.22.0 of the Gmail API library.", objArr));
        }
    }

    public Gmail(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(httpTransport, jsonFactory, httpRequestInitializer));
    }

    Gmail(Builder builder) {
        super(builder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.api.client.googleapis.services.AbstractGoogleClient
    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
        super.initialize(abstractGoogleClientRequest);
    }

    public Users users() {
        return new Users();
    }

    /* loaded from: classes.dex */
    public class Users {
        public Users() {
        }

        public GetProfile getProfile(String str) throws IOException {
            GetProfile getProfile = new GetProfile(str);
            Gmail.this.initialize(getProfile);
            return getProfile;
        }

        /* loaded from: classes.dex */
        public class GetProfile extends GmailRequest<Profile> {
            private static final String REST_PATH = "{userId}/profile";

            @Key
            private String userId;

            protected GetProfile(String str) {
                super(Gmail.this, "GET", REST_PATH, null, Profile.class);
                this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
            }

            @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }

            @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setAlt */
            public GmailRequest<Profile> setAlt2(String str) {
                return (GetProfile) super.setAlt2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setFields */
            public GmailRequest<Profile> setFields2(String str) {
                return (GetProfile) super.setFields2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setKey */
            public GmailRequest<Profile> setKey2(String str) {
                return (GetProfile) super.setKey2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setOauthToken */
            public GmailRequest<Profile> setOauthToken2(String str) {
                return (GetProfile) super.setOauthToken2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setPrettyPrint */
            public GmailRequest<Profile> setPrettyPrint2(Boolean bool) {
                return (GetProfile) super.setPrettyPrint2(bool);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setQuotaUser */
            public GmailRequest<Profile> setQuotaUser2(String str) {
                return (GetProfile) super.setQuotaUser2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setUserIp */
            public GmailRequest<Profile> setUserIp2(String str) {
                return (GetProfile) super.setUserIp2(str);
            }

            public String getUserId() {
                return this.userId;
            }

            public GetProfile setUserId(String str) {
                this.userId = str;
                return this;
            }

            @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
            public GetProfile set(String str, Object obj) {
                return (GetProfile) super.set(str, obj);
            }
        }

        public Stop stop(String str) throws IOException {
            Stop stop = new Stop(str);
            Gmail.this.initialize(stop);
            return stop;
        }

        /* loaded from: classes.dex */
        public class Stop extends GmailRequest<Void> {
            private static final String REST_PATH = "{userId}/stop";

            @Key
            private String userId;

            protected Stop(String str) {
                super(Gmail.this, "POST", REST_PATH, null, Void.class);
                this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setAlt */
            public GmailRequest<Void> setAlt2(String str) {
                return (Stop) super.setAlt2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setFields */
            public GmailRequest<Void> setFields2(String str) {
                return (Stop) super.setFields2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setKey */
            public GmailRequest<Void> setKey2(String str) {
                return (Stop) super.setKey2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setOauthToken */
            public GmailRequest<Void> setOauthToken2(String str) {
                return (Stop) super.setOauthToken2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setPrettyPrint */
            public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                return (Stop) super.setPrettyPrint2(bool);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setQuotaUser */
            public GmailRequest<Void> setQuotaUser2(String str) {
                return (Stop) super.setQuotaUser2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setUserIp */
            public GmailRequest<Void> setUserIp2(String str) {
                return (Stop) super.setUserIp2(str);
            }

            public String getUserId() {
                return this.userId;
            }

            public Stop setUserId(String str) {
                this.userId = str;
                return this;
            }

            @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
            public Stop set(String str, Object obj) {
                return (Stop) super.set(str, obj);
            }
        }

        public Watch watch(String str, WatchRequest watchRequest) throws IOException {
            Watch watch = new Watch(str, watchRequest);
            Gmail.this.initialize(watch);
            return watch;
        }

        /* loaded from: classes.dex */
        public class Watch extends GmailRequest<WatchResponse> {
            private static final String REST_PATH = "{userId}/watch";

            @Key
            private String userId;

            protected Watch(String str, WatchRequest watchRequest) {
                super(Gmail.this, "POST", REST_PATH, watchRequest, WatchResponse.class);
                this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setAlt */
            public GmailRequest<WatchResponse> setAlt2(String str) {
                return (Watch) super.setAlt2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setFields */
            public GmailRequest<WatchResponse> setFields2(String str) {
                return (Watch) super.setFields2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setKey */
            public GmailRequest<WatchResponse> setKey2(String str) {
                return (Watch) super.setKey2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setOauthToken */
            public GmailRequest<WatchResponse> setOauthToken2(String str) {
                return (Watch) super.setOauthToken2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setPrettyPrint */
            public GmailRequest<WatchResponse> setPrettyPrint2(Boolean bool) {
                return (Watch) super.setPrettyPrint2(bool);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setQuotaUser */
            public GmailRequest<WatchResponse> setQuotaUser2(String str) {
                return (Watch) super.setQuotaUser2(str);
            }

            @Override // com.google.api.services.gmail.GmailRequest
            /* renamed from: setUserIp */
            public GmailRequest<WatchResponse> setUserIp2(String str) {
                return (Watch) super.setUserIp2(str);
            }

            public String getUserId() {
                return this.userId;
            }

            public Watch setUserId(String str) {
                this.userId = str;
                return this;
            }

            @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
            public Watch set(String str, Object obj) {
                return (Watch) super.set(str, obj);
            }
        }

        public Drafts drafts() {
            return new Drafts();
        }

        /* loaded from: classes.dex */
        public class Drafts {
            public Drafts() {
            }

            public Create create(String str, Draft draft) throws IOException {
                Create create = new Create(str, draft);
                Gmail.this.initialize(create);
                return create;
            }

            public Create create(String str, Draft draft, AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                Create create = new Create(str, draft, abstractInputStreamContent);
                Gmail.this.initialize(create);
                return create;
            }

            /* loaded from: classes.dex */
            public class Create extends GmailRequest<Draft> {
                private static final String REST_PATH = "{userId}/drafts";

                @Key
                private String userId;

                protected Create(String str, Draft draft) {
                    super(Gmail.this, "POST", REST_PATH, draft, Draft.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                protected Create(String str, Draft draft, AbstractInputStreamContent abstractInputStreamContent) {
                    super(Gmail.this, "POST", "/upload/" + Gmail.this.getServicePath() + REST_PATH, draft, Draft.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    initializeMediaUpload(abstractInputStreamContent);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setAlt2(String str) {
                    return (Create) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setFields2(String str) {
                    return (Create) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setKey2(String str) {
                    return (Create) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setOauthToken2(String str) {
                    return (Create) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setPrettyPrint2(Boolean bool) {
                    return (Create) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setQuotaUser2(String str) {
                    return (Create) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp, reason: merged with bridge method [inline-methods] */
                public GmailRequest<Draft> setUserIp2(String str) {
                    return (Create) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Create setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Create set(String str, Object obj) {
                    return (Create) super.set(str, obj);
                }
            }

            public Delete delete(String str, String str2) throws IOException {
                Delete delete = new Delete(str, str2);
                Gmail.this.initialize(delete);
                return delete;
            }

            /* loaded from: classes.dex */
            public class Delete extends GmailRequest<Void> {
                private static final String REST_PATH = "{userId}/drafts/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Delete(String str, String str2) {
                    super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Void> setAlt2(String str) {
                    return (Delete) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Void> setFields2(String str) {
                    return (Delete) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Void> setKey2(String str) {
                    return (Delete) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Void> setOauthToken2(String str) {
                    return (Delete) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                    return (Delete) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Void> setQuotaUser2(String str) {
                    return (Delete) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Void> setUserIp2(String str) {
                    return (Delete) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Delete setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Delete setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Delete set(String str, Object obj) {
                    return (Delete) super.set(str, obj);
                }
            }

            public Get get(String str, String str2) throws IOException {
                Get get = new Get(str, str2);
                Gmail.this.initialize(get);
                return get;
            }

            /* loaded from: classes.dex */
            public class Get extends GmailRequest<Draft> {
                private static final String REST_PATH = "{userId}/drafts/{id}";

                @Key
                private String format;

                @Key
                private String id;

                @Key
                private String userId;

                protected Get(String str, String str2) {
                    super(Gmail.this, "GET", REST_PATH, null, Draft.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Draft> setAlt2(String str) {
                    return (Get) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Draft> setFields2(String str) {
                    return (Get) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Draft> setKey2(String str) {
                    return (Get) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Draft> setOauthToken2(String str) {
                    return (Get) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Draft> setPrettyPrint2(Boolean bool) {
                    return (Get) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Draft> setQuotaUser2(String str) {
                    return (Get) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Draft> setUserIp2(String str) {
                    return (Get) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Get setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Get setId(String str) {
                    this.id = str;
                    return this;
                }

                public String getFormat() {
                    return this.format;
                }

                public Get setFormat(String str) {
                    this.format = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Get set(String str, Object obj) {
                    return (Get) super.set(str, obj);
                }
            }

            public List list(String str) throws IOException {
                List list = new List(str);
                Gmail.this.initialize(list);
                return list;
            }

            /* loaded from: classes.dex */
            public class List extends GmailRequest<ListDraftsResponse> {
                private static final String REST_PATH = "{userId}/drafts";

                @Key
                private Boolean includeSpamTrash;

                @Key
                private Long maxResults;

                @Key
                private String pageToken;

                @Key
                private String q;

                @Key
                private String userId;

                protected List(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, ListDraftsResponse.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ListDraftsResponse> setAlt2(String str) {
                    return (List) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ListDraftsResponse> setFields2(String str) {
                    return (List) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ListDraftsResponse> setKey2(String str) {
                    return (List) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ListDraftsResponse> setOauthToken2(String str) {
                    return (List) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ListDraftsResponse> setPrettyPrint2(Boolean bool) {
                    return (List) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ListDraftsResponse> setQuotaUser2(String str) {
                    return (List) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ListDraftsResponse> setUserIp2(String str) {
                    return (List) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public List setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public Boolean getIncludeSpamTrash() {
                    return this.includeSpamTrash;
                }

                public List setIncludeSpamTrash(Boolean bool) {
                    this.includeSpamTrash = bool;
                    return this;
                }

                public boolean isIncludeSpamTrash() {
                    if (this.includeSpamTrash == null || this.includeSpamTrash == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.includeSpamTrash.booleanValue();
                }

                public Long getMaxResults() {
                    return this.maxResults;
                }

                public List setMaxResults(Long l) {
                    this.maxResults = l;
                    return this;
                }

                public String getPageToken() {
                    return this.pageToken;
                }

                public List setPageToken(String str) {
                    this.pageToken = str;
                    return this;
                }

                public String getQ() {
                    return this.q;
                }

                public List setQ(String str) {
                    this.q = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public List set(String str, Object obj) {
                    return (List) super.set(str, obj);
                }
            }

            public Send send(String str, Draft draft) throws IOException {
                Send send = new Send(str, draft);
                Gmail.this.initialize(send);
                return send;
            }

            public Send send(String str, Draft draft, AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                Send send = new Send(str, draft, abstractInputStreamContent);
                Gmail.this.initialize(send);
                return send;
            }

            /* loaded from: classes.dex */
            public class Send extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/drafts/send";

                @Key
                private String userId;

                protected Send(String str, Draft draft) {
                    super(Gmail.this, "POST", REST_PATH, draft, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                protected Send(String str, Draft draft, AbstractInputStreamContent abstractInputStreamContent) {
                    super(Gmail.this, "POST", "/upload/" + Gmail.this.getServicePath() + REST_PATH, draft, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    initializeMediaUpload(abstractInputStreamContent);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Send) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Send) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Send) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Send) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Send) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Send) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Send) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Send setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Send set(String str, Object obj) {
                    return (Send) super.set(str, obj);
                }
            }

            public Update update(String str, String str2, Draft draft) throws IOException {
                Update update = new Update(str, str2, draft);
                Gmail.this.initialize(update);
                return update;
            }

            public Update update(String str, String str2, Draft draft, AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                Update update = new Update(str, str2, draft, abstractInputStreamContent);
                Gmail.this.initialize(update);
                return update;
            }

            /* loaded from: classes.dex */
            public class Update extends GmailRequest<Draft> {
                private static final String REST_PATH = "{userId}/drafts/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Update(String str, String str2, Draft draft) {
                    super(Gmail.this, "PUT", REST_PATH, draft, Draft.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                protected Update(String str, String str2, Draft draft, AbstractInputStreamContent abstractInputStreamContent) {
                    super(Gmail.this, "PUT", "/upload/" + Gmail.this.getServicePath() + REST_PATH, draft, Draft.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                    initializeMediaUpload(abstractInputStreamContent);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Draft> setAlt2(String str) {
                    return (Update) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Draft> setFields2(String str) {
                    return (Update) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Draft> setKey2(String str) {
                    return (Update) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Draft> setOauthToken2(String str) {
                    return (Update) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Draft> setPrettyPrint2(Boolean bool) {
                    return (Update) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Draft> setQuotaUser2(String str) {
                    return (Update) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Draft> setUserIp2(String str) {
                    return (Update) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Update setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Update setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Update set(String str, Object obj) {
                    return (Update) super.set(str, obj);
                }
            }
        }

        public History history() {
            return new History();
        }

        /* loaded from: classes.dex */
        public class History {
            public History() {
            }

            public List list(String str) throws IOException {
                List list = new List(str);
                Gmail.this.initialize(list);
                return list;
            }

            /* loaded from: classes.dex */
            public class List extends GmailRequest<ListHistoryResponse> {
                private static final String REST_PATH = "{userId}/history";

                @Key
                private String labelId;

                @Key
                private Long maxResults;

                @Key
                private String pageToken;

                @Key
                private BigInteger startHistoryId;

                @Key
                private String userId;

                protected List(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, ListHistoryResponse.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ListHistoryResponse> setAlt2(String str) {
                    return (List) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ListHistoryResponse> setFields2(String str) {
                    return (List) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ListHistoryResponse> setKey2(String str) {
                    return (List) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ListHistoryResponse> setOauthToken2(String str) {
                    return (List) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ListHistoryResponse> setPrettyPrint2(Boolean bool) {
                    return (List) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ListHistoryResponse> setQuotaUser2(String str) {
                    return (List) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ListHistoryResponse> setUserIp2(String str) {
                    return (List) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public List setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getLabelId() {
                    return this.labelId;
                }

                public List setLabelId(String str) {
                    this.labelId = str;
                    return this;
                }

                public Long getMaxResults() {
                    return this.maxResults;
                }

                public List setMaxResults(Long l) {
                    this.maxResults = l;
                    return this;
                }

                public String getPageToken() {
                    return this.pageToken;
                }

                public List setPageToken(String str) {
                    this.pageToken = str;
                    return this;
                }

                public BigInteger getStartHistoryId() {
                    return this.startHistoryId;
                }

                public List setStartHistoryId(BigInteger bigInteger) {
                    this.startHistoryId = bigInteger;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public List set(String str, Object obj) {
                    return (List) super.set(str, obj);
                }
            }
        }

        public Labels labels() {
            return new Labels();
        }

        /* loaded from: classes.dex */
        public class Labels {
            public Labels() {
            }

            public Create create(String str, Label label) throws IOException {
                Create create = new Create(str, label);
                Gmail.this.initialize(create);
                return create;
            }

            /* loaded from: classes.dex */
            public class Create extends GmailRequest<Label> {
                private static final String REST_PATH = "{userId}/labels";

                @Key
                private String userId;

                protected Create(String str, Label label) {
                    super(Gmail.this, "POST", REST_PATH, label, Label.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getLabelListVisibility(), "Label.getLabelListVisibility()");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getMessageListVisibility(), "Label.getMessageListVisibility()");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getName(), "Label.getName()");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Label> setAlt2(String str) {
                    return (Create) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Label> setFields2(String str) {
                    return (Create) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Label> setKey2(String str) {
                    return (Create) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Label> setOauthToken2(String str) {
                    return (Create) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Label> setPrettyPrint2(Boolean bool) {
                    return (Create) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Label> setQuotaUser2(String str) {
                    return (Create) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Label> setUserIp2(String str) {
                    return (Create) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Create setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Create set(String str, Object obj) {
                    return (Create) super.set(str, obj);
                }
            }

            public Delete delete(String str, String str2) throws IOException {
                Delete delete = new Delete(str, str2);
                Gmail.this.initialize(delete);
                return delete;
            }

            /* loaded from: classes.dex */
            public class Delete extends GmailRequest<Void> {
                private static final String REST_PATH = "{userId}/labels/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Delete(String str, String str2) {
                    super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Void> setAlt2(String str) {
                    return (Delete) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Void> setFields2(String str) {
                    return (Delete) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Void> setKey2(String str) {
                    return (Delete) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Void> setOauthToken2(String str) {
                    return (Delete) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                    return (Delete) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Void> setQuotaUser2(String str) {
                    return (Delete) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Void> setUserIp2(String str) {
                    return (Delete) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Delete setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Delete setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Delete set(String str, Object obj) {
                    return (Delete) super.set(str, obj);
                }
            }

            public Get get(String str, String str2) throws IOException {
                Get get = new Get(str, str2);
                Gmail.this.initialize(get);
                return get;
            }

            /* loaded from: classes.dex */
            public class Get extends GmailRequest<Label> {
                private static final String REST_PATH = "{userId}/labels/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Get(String str, String str2) {
                    super(Gmail.this, "GET", REST_PATH, null, Label.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Label> setAlt2(String str) {
                    return (Get) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Label> setFields2(String str) {
                    return (Get) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Label> setKey2(String str) {
                    return (Get) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Label> setOauthToken2(String str) {
                    return (Get) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Label> setPrettyPrint2(Boolean bool) {
                    return (Get) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Label> setQuotaUser2(String str) {
                    return (Get) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Label> setUserIp2(String str) {
                    return (Get) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Get setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Get setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Get set(String str, Object obj) {
                    return (Get) super.set(str, obj);
                }
            }

            public List list(String str) throws IOException {
                List list = new List(str);
                Gmail.this.initialize(list);
                return list;
            }

            /* loaded from: classes.dex */
            public class List extends GmailRequest<ListLabelsResponse> {
                private static final String REST_PATH = "{userId}/labels";

                @Key
                private String userId;

                protected List(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, ListLabelsResponse.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ListLabelsResponse> setAlt2(String str) {
                    return (List) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ListLabelsResponse> setFields2(String str) {
                    return (List) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ListLabelsResponse> setKey2(String str) {
                    return (List) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ListLabelsResponse> setOauthToken2(String str) {
                    return (List) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ListLabelsResponse> setPrettyPrint2(Boolean bool) {
                    return (List) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ListLabelsResponse> setQuotaUser2(String str) {
                    return (List) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ListLabelsResponse> setUserIp2(String str) {
                    return (List) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public List setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public List set(String str, Object obj) {
                    return (List) super.set(str, obj);
                }
            }

            public Patch patch(String str, String str2, Label label) throws IOException {
                Patch patch = new Patch(str, str2, label);
                Gmail.this.initialize(patch);
                return patch;
            }

            /* loaded from: classes.dex */
            public class Patch extends GmailRequest<Label> {
                private static final String REST_PATH = "{userId}/labels/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Patch(String str, String str2, Label label) {
                    super(Gmail.this, HttpMethods.PATCH, REST_PATH, label, Label.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Label> setAlt2(String str) {
                    return (Patch) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Label> setFields2(String str) {
                    return (Patch) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Label> setKey2(String str) {
                    return (Patch) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Label> setOauthToken2(String str) {
                    return (Patch) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Label> setPrettyPrint2(Boolean bool) {
                    return (Patch) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Label> setQuotaUser2(String str) {
                    return (Patch) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Label> setUserIp2(String str) {
                    return (Patch) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Patch setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Patch setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Patch set(String str, Object obj) {
                    return (Patch) super.set(str, obj);
                }
            }

            public Update update(String str, String str2, Label label) throws IOException {
                Update update = new Update(str, str2, label);
                Gmail.this.initialize(update);
                return update;
            }

            /* loaded from: classes.dex */
            public class Update extends GmailRequest<Label> {
                private static final String REST_PATH = "{userId}/labels/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Update(String str, String str2, Label label) {
                    super(Gmail.this, "PUT", REST_PATH, label, Label.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getId(), "Label.getId()");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getLabelListVisibility(), "Label.getLabelListVisibility()");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getMessageListVisibility(), "Label.getMessageListVisibility()");
                    checkRequiredParameter(label, "content");
                    checkRequiredParameter(label.getName(), "Label.getName()");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Label> setAlt2(String str) {
                    return (Update) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Label> setFields2(String str) {
                    return (Update) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Label> setKey2(String str) {
                    return (Update) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Label> setOauthToken2(String str) {
                    return (Update) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Label> setPrettyPrint2(Boolean bool) {
                    return (Update) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Label> setQuotaUser2(String str) {
                    return (Update) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Label> setUserIp2(String str) {
                    return (Update) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Update setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Update setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Update set(String str, Object obj) {
                    return (Update) super.set(str, obj);
                }
            }
        }

        public Messages messages() {
            return new Messages();
        }

        /* loaded from: classes.dex */
        public class Messages {
            public Messages() {
            }

            public BatchDelete batchDelete(String str, BatchDeleteMessagesRequest batchDeleteMessagesRequest) throws IOException {
                BatchDelete batchDelete = new BatchDelete(str, batchDeleteMessagesRequest);
                Gmail.this.initialize(batchDelete);
                return batchDelete;
            }

            /* loaded from: classes.dex */
            public class BatchDelete extends GmailRequest<Void> {
                private static final String REST_PATH = "{userId}/messages/batchDelete";

                @Key
                private String userId;

                protected BatchDelete(String str, BatchDeleteMessagesRequest batchDeleteMessagesRequest) {
                    super(Gmail.this, "POST", REST_PATH, batchDeleteMessagesRequest, Void.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Void> setAlt2(String str) {
                    return (BatchDelete) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Void> setFields2(String str) {
                    return (BatchDelete) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Void> setKey2(String str) {
                    return (BatchDelete) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Void> setOauthToken2(String str) {
                    return (BatchDelete) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                    return (BatchDelete) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Void> setQuotaUser2(String str) {
                    return (BatchDelete) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Void> setUserIp2(String str) {
                    return (BatchDelete) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public BatchDelete setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public BatchDelete set(String str, Object obj) {
                    return (BatchDelete) super.set(str, obj);
                }
            }

            public Delete delete(String str, String str2) throws IOException {
                Delete delete = new Delete(str, str2);
                Gmail.this.initialize(delete);
                return delete;
            }

            /* loaded from: classes.dex */
            public class Delete extends GmailRequest<Void> {
                private static final String REST_PATH = "{userId}/messages/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Delete(String str, String str2) {
                    super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Void> setAlt2(String str) {
                    return (Delete) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Void> setFields2(String str) {
                    return (Delete) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Void> setKey2(String str) {
                    return (Delete) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Void> setOauthToken2(String str) {
                    return (Delete) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                    return (Delete) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Void> setQuotaUser2(String str) {
                    return (Delete) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Void> setUserIp2(String str) {
                    return (Delete) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Delete setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Delete setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Delete set(String str, Object obj) {
                    return (Delete) super.set(str, obj);
                }
            }

            public Get get(String str, String str2) throws IOException {
                Get get = new Get(str, str2);
                Gmail.this.initialize(get);
                return get;
            }

            /* loaded from: classes.dex */
            public class Get extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages/{id}";

                @Key
                private String format;

                @Key
                private String id;

                @Key
                private java.util.List<String> metadataHeaders;

                @Key
                private String userId;

                protected Get(String str, String str2) {
                    super(Gmail.this, "GET", REST_PATH, null, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Get) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Get) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Get) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Get) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Get) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Get) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Get) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Get setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Get setId(String str) {
                    this.id = str;
                    return this;
                }

                public String getFormat() {
                    return this.format;
                }

                public Get setFormat(String str) {
                    this.format = str;
                    return this;
                }

                public java.util.List<String> getMetadataHeaders() {
                    return this.metadataHeaders;
                }

                public Get setMetadataHeaders(java.util.List<String> list) {
                    this.metadataHeaders = list;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Get set(String str, Object obj) {
                    return (Get) super.set(str, obj);
                }
            }

            public GmailImport gmailImport(String str, Message message) throws IOException {
                GmailImport gmailImport = new GmailImport(str, message);
                Gmail.this.initialize(gmailImport);
                return gmailImport;
            }

            public GmailImport gmailImport(String str, Message message, AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                GmailImport gmailImport = new GmailImport(str, message, abstractInputStreamContent);
                Gmail.this.initialize(gmailImport);
                return gmailImport;
            }

            /* loaded from: classes.dex */
            public class GmailImport extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages/import";

                @Key
                private Boolean deleted;

                @Key
                private String internalDateSource;

                @Key
                private Boolean neverMarkSpam;

                @Key
                private Boolean processForCalendar;

                @Key
                private String userId;

                protected GmailImport(String str, Message message) {
                    super(Gmail.this, "POST", REST_PATH, message, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                protected GmailImport(String str, Message message, AbstractInputStreamContent abstractInputStreamContent) {
                    super(Gmail.this, "POST", "/upload/" + Gmail.this.getServicePath() + REST_PATH, message, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    initializeMediaUpload(abstractInputStreamContent);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (GmailImport) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (GmailImport) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (GmailImport) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (GmailImport) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (GmailImport) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (GmailImport) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (GmailImport) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public GmailImport setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public Boolean getDeleted() {
                    return this.deleted;
                }

                public GmailImport setDeleted(Boolean bool) {
                    this.deleted = bool;
                    return this;
                }

                public boolean isDeleted() {
                    if (this.deleted == null || this.deleted == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.deleted.booleanValue();
                }

                public String getInternalDateSource() {
                    return this.internalDateSource;
                }

                public GmailImport setInternalDateSource(String str) {
                    this.internalDateSource = str;
                    return this;
                }

                public Boolean getNeverMarkSpam() {
                    return this.neverMarkSpam;
                }

                public GmailImport setNeverMarkSpam(Boolean bool) {
                    this.neverMarkSpam = bool;
                    return this;
                }

                public boolean isNeverMarkSpam() {
                    if (this.neverMarkSpam == null || this.neverMarkSpam == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.neverMarkSpam.booleanValue();
                }

                public Boolean getProcessForCalendar() {
                    return this.processForCalendar;
                }

                public GmailImport setProcessForCalendar(Boolean bool) {
                    this.processForCalendar = bool;
                    return this;
                }

                public boolean isProcessForCalendar() {
                    if (this.processForCalendar == null || this.processForCalendar == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.processForCalendar.booleanValue();
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public GmailImport set(String str, Object obj) {
                    return (GmailImport) super.set(str, obj);
                }
            }

            public Insert insert(String str, Message message) throws IOException {
                Insert insert = new Insert(str, message);
                Gmail.this.initialize(insert);
                return insert;
            }

            public Insert insert(String str, Message message, AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                Insert insert = new Insert(str, message, abstractInputStreamContent);
                Gmail.this.initialize(insert);
                return insert;
            }

            /* loaded from: classes.dex */
            public class Insert extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages";

                @Key
                private Boolean deleted;

                @Key
                private String internalDateSource;

                @Key
                private String userId;

                protected Insert(String str, Message message) {
                    super(Gmail.this, "POST", REST_PATH, message, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    checkRequiredParameter(message, "content");
                    checkRequiredParameter(message.getRaw(), "Message.getRaw()");
                }

                protected Insert(String str, Message message, AbstractInputStreamContent abstractInputStreamContent) {
                    super(Gmail.this, "POST", "/upload/" + Gmail.this.getServicePath() + REST_PATH, message, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    initializeMediaUpload(abstractInputStreamContent);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Insert) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Insert) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Insert) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Insert) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Insert) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Insert) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Insert) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Insert setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public Boolean getDeleted() {
                    return this.deleted;
                }

                public Insert setDeleted(Boolean bool) {
                    this.deleted = bool;
                    return this;
                }

                public boolean isDeleted() {
                    if (this.deleted == null || this.deleted == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.deleted.booleanValue();
                }

                public String getInternalDateSource() {
                    return this.internalDateSource;
                }

                public Insert setInternalDateSource(String str) {
                    this.internalDateSource = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Insert set(String str, Object obj) {
                    return (Insert) super.set(str, obj);
                }
            }

            public List list(String str) throws IOException {
                List list = new List(str);
                Gmail.this.initialize(list);
                return list;
            }

            /* loaded from: classes.dex */
            public class List extends GmailRequest<ListMessagesResponse> {
                private static final String REST_PATH = "{userId}/messages";

                @Key
                private Boolean includeSpamTrash;

                @Key
                private java.util.List<String> labelIds;

                @Key
                private Long maxResults;

                @Key
                private String pageToken;

                @Key
                private String q;

                @Key
                private String userId;

                protected List(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, ListMessagesResponse.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ListMessagesResponse> setAlt2(String str) {
                    return (List) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ListMessagesResponse> setFields2(String str) {
                    return (List) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ListMessagesResponse> setKey2(String str) {
                    return (List) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ListMessagesResponse> setOauthToken2(String str) {
                    return (List) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ListMessagesResponse> setPrettyPrint2(Boolean bool) {
                    return (List) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ListMessagesResponse> setQuotaUser2(String str) {
                    return (List) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ListMessagesResponse> setUserIp2(String str) {
                    return (List) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public List setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public Boolean getIncludeSpamTrash() {
                    return this.includeSpamTrash;
                }

                public List setIncludeSpamTrash(Boolean bool) {
                    this.includeSpamTrash = bool;
                    return this;
                }

                public boolean isIncludeSpamTrash() {
                    if (this.includeSpamTrash == null || this.includeSpamTrash == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.includeSpamTrash.booleanValue();
                }

                public java.util.List<String> getLabelIds() {
                    return this.labelIds;
                }

                public List setLabelIds(java.util.List<String> list) {
                    this.labelIds = list;
                    return this;
                }

                public Long getMaxResults() {
                    return this.maxResults;
                }

                public List setMaxResults(Long l) {
                    this.maxResults = l;
                    return this;
                }

                public String getPageToken() {
                    return this.pageToken;
                }

                public List setPageToken(String str) {
                    this.pageToken = str;
                    return this;
                }

                public String getQ() {
                    return this.q;
                }

                public List setQ(String str) {
                    this.q = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public List set(String str, Object obj) {
                    return (List) super.set(str, obj);
                }
            }

            public Modify modify(String str, String str2, ModifyMessageRequest modifyMessageRequest) throws IOException {
                Modify modify = new Modify(str, str2, modifyMessageRequest);
                Gmail.this.initialize(modify);
                return modify;
            }

            /* loaded from: classes.dex */
            public class Modify extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages/{id}/modify";

                @Key
                private String id;

                @Key
                private String userId;

                protected Modify(String str, String str2, ModifyMessageRequest modifyMessageRequest) {
                    super(Gmail.this, "POST", REST_PATH, modifyMessageRequest, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Modify) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Modify) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Modify) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Modify) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Modify) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Modify) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Modify) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Modify setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Modify setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Modify set(String str, Object obj) {
                    return (Modify) super.set(str, obj);
                }
            }

            public Send send(String str, Message message) throws IOException {
                Send send = new Send(str, message);
                Gmail.this.initialize(send);
                return send;
            }

            public Send send(String str, Message message, AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                Send send = new Send(str, message, abstractInputStreamContent);
                Gmail.this.initialize(send);
                return send;
            }

            /* loaded from: classes.dex */
            public class Send extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages/send";

                @Key
                private String userId;

                protected Send(String str, Message message) {
                    super(Gmail.this, "POST", REST_PATH, message, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    checkRequiredParameter(message, "content");
                    checkRequiredParameter(message.getRaw(), "Message.getRaw()");
                }

                protected Send(String str, Message message, AbstractInputStreamContent abstractInputStreamContent) {
                    super(Gmail.this, "POST", "/upload/" + Gmail.this.getServicePath() + REST_PATH, message, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    initializeMediaUpload(abstractInputStreamContent);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Send) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Send) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Send) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Send) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Send) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Send) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Send) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Send setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Send set(String str, Object obj) {
                    return (Send) super.set(str, obj);
                }
            }

            public Trash trash(String str, String str2) throws IOException {
                Trash trash = new Trash(str, str2);
                Gmail.this.initialize(trash);
                return trash;
            }

            /* loaded from: classes.dex */
            public class Trash extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages/{id}/trash";

                @Key
                private String id;

                @Key
                private String userId;

                protected Trash(String str, String str2) {
                    super(Gmail.this, "POST", REST_PATH, null, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Trash) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Trash) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Trash) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Trash) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Trash) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Trash) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Trash) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Trash setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Trash setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Trash set(String str, Object obj) {
                    return (Trash) super.set(str, obj);
                }
            }

            public Untrash untrash(String str, String str2) throws IOException {
                Untrash untrash = new Untrash(str, str2);
                Gmail.this.initialize(untrash);
                return untrash;
            }

            /* loaded from: classes.dex */
            public class Untrash extends GmailRequest<Message> {
                private static final String REST_PATH = "{userId}/messages/{id}/untrash";

                @Key
                private String id;

                @Key
                private String userId;

                protected Untrash(String str, String str2) {
                    super(Gmail.this, "POST", REST_PATH, null, Message.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Message> setAlt2(String str) {
                    return (Untrash) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Message> setFields2(String str) {
                    return (Untrash) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Message> setKey2(String str) {
                    return (Untrash) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Message> setOauthToken2(String str) {
                    return (Untrash) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Message> setPrettyPrint2(Boolean bool) {
                    return (Untrash) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Message> setQuotaUser2(String str) {
                    return (Untrash) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Message> setUserIp2(String str) {
                    return (Untrash) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Untrash setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Untrash setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Untrash set(String str, Object obj) {
                    return (Untrash) super.set(str, obj);
                }
            }

            public Attachments attachments() {
                return new Attachments();
            }

            /* loaded from: classes.dex */
            public class Attachments {
                public Attachments() {
                }

                public Get get(String str, String str2, String str3) throws IOException {
                    Get get = new Get(str, str2, str3);
                    Gmail.this.initialize(get);
                    return get;
                }

                /* loaded from: classes.dex */
                public class Get extends GmailRequest<MessagePartBody> {
                    private static final String REST_PATH = "{userId}/messages/{messageId}/attachments/{id}";

                    @Key
                    private String id;

                    @Key
                    private String messageId;

                    @Key
                    private String userId;

                    protected Get(String str, String str2, String str3) {
                        super(Gmail.this, "GET", REST_PATH, null, MessagePartBody.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.messageId = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter messageId must be specified.");
                        this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str3, "Required parameter id must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<MessagePartBody> setAlt2(String str) {
                        return (Get) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<MessagePartBody> setFields2(String str) {
                        return (Get) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<MessagePartBody> setKey2(String str) {
                        return (Get) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<MessagePartBody> setOauthToken2(String str) {
                        return (Get) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<MessagePartBody> setPrettyPrint2(Boolean bool) {
                        return (Get) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<MessagePartBody> setQuotaUser2(String str) {
                        return (Get) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<MessagePartBody> setUserIp2(String str) {
                        return (Get) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Get setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getMessageId() {
                        return this.messageId;
                    }

                    public Get setMessageId(String str) {
                        this.messageId = str;
                        return this;
                    }

                    public String getId() {
                        return this.id;
                    }

                    public Get setId(String str) {
                        this.id = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Get set(String str, Object obj) {
                        return (Get) super.set(str, obj);
                    }
                }
            }
        }

        public Settings settings() {
            return new Settings();
        }

        /* loaded from: classes.dex */
        public class Settings {
            public Settings() {
            }

            public GetAutoForwarding getAutoForwarding(String str) throws IOException {
                GetAutoForwarding getAutoForwarding = new GetAutoForwarding(str);
                Gmail.this.initialize(getAutoForwarding);
                return getAutoForwarding;
            }

            /* loaded from: classes.dex */
            public class GetAutoForwarding extends GmailRequest<AutoForwarding> {
                private static final String REST_PATH = "{userId}/settings/autoForwarding";

                @Key
                private String userId;

                protected GetAutoForwarding(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, AutoForwarding.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<AutoForwarding> setAlt2(String str) {
                    return (GetAutoForwarding) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<AutoForwarding> setFields2(String str) {
                    return (GetAutoForwarding) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<AutoForwarding> setKey2(String str) {
                    return (GetAutoForwarding) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<AutoForwarding> setOauthToken2(String str) {
                    return (GetAutoForwarding) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<AutoForwarding> setPrettyPrint2(Boolean bool) {
                    return (GetAutoForwarding) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<AutoForwarding> setQuotaUser2(String str) {
                    return (GetAutoForwarding) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<AutoForwarding> setUserIp2(String str) {
                    return (GetAutoForwarding) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public GetAutoForwarding setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public GetAutoForwarding set(String str, Object obj) {
                    return (GetAutoForwarding) super.set(str, obj);
                }
            }

            public GetImap getImap(String str) throws IOException {
                GetImap getImap = new GetImap(str);
                Gmail.this.initialize(getImap);
                return getImap;
            }

            /* loaded from: classes.dex */
            public class GetImap extends GmailRequest<ImapSettings> {
                private static final String REST_PATH = "{userId}/settings/imap";

                @Key
                private String userId;

                protected GetImap(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, ImapSettings.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ImapSettings> setAlt2(String str) {
                    return (GetImap) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ImapSettings> setFields2(String str) {
                    return (GetImap) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ImapSettings> setKey2(String str) {
                    return (GetImap) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ImapSettings> setOauthToken2(String str) {
                    return (GetImap) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ImapSettings> setPrettyPrint2(Boolean bool) {
                    return (GetImap) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ImapSettings> setQuotaUser2(String str) {
                    return (GetImap) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ImapSettings> setUserIp2(String str) {
                    return (GetImap) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public GetImap setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public GetImap set(String str, Object obj) {
                    return (GetImap) super.set(str, obj);
                }
            }

            public GetPop getPop(String str) throws IOException {
                GetPop getPop = new GetPop(str);
                Gmail.this.initialize(getPop);
                return getPop;
            }

            /* loaded from: classes.dex */
            public class GetPop extends GmailRequest<PopSettings> {
                private static final String REST_PATH = "{userId}/settings/pop";

                @Key
                private String userId;

                protected GetPop(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, PopSettings.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<PopSettings> setAlt2(String str) {
                    return (GetPop) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<PopSettings> setFields2(String str) {
                    return (GetPop) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<PopSettings> setKey2(String str) {
                    return (GetPop) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<PopSettings> setOauthToken2(String str) {
                    return (GetPop) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<PopSettings> setPrettyPrint2(Boolean bool) {
                    return (GetPop) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<PopSettings> setQuotaUser2(String str) {
                    return (GetPop) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<PopSettings> setUserIp2(String str) {
                    return (GetPop) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public GetPop setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public GetPop set(String str, Object obj) {
                    return (GetPop) super.set(str, obj);
                }
            }

            public GetVacation getVacation(String str) throws IOException {
                GetVacation getVacation = new GetVacation(str);
                Gmail.this.initialize(getVacation);
                return getVacation;
            }

            /* loaded from: classes.dex */
            public class GetVacation extends GmailRequest<VacationSettings> {
                private static final String REST_PATH = "{userId}/settings/vacation";

                @Key
                private String userId;

                protected GetVacation(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, VacationSettings.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<VacationSettings> setAlt2(String str) {
                    return (GetVacation) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<VacationSettings> setFields2(String str) {
                    return (GetVacation) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<VacationSettings> setKey2(String str) {
                    return (GetVacation) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<VacationSettings> setOauthToken2(String str) {
                    return (GetVacation) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<VacationSettings> setPrettyPrint2(Boolean bool) {
                    return (GetVacation) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<VacationSettings> setQuotaUser2(String str) {
                    return (GetVacation) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<VacationSettings> setUserIp2(String str) {
                    return (GetVacation) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public GetVacation setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public GetVacation set(String str, Object obj) {
                    return (GetVacation) super.set(str, obj);
                }
            }

            public UpdateAutoForwarding updateAutoForwarding(String str, AutoForwarding autoForwarding) throws IOException {
                UpdateAutoForwarding updateAutoForwarding = new UpdateAutoForwarding(str, autoForwarding);
                Gmail.this.initialize(updateAutoForwarding);
                return updateAutoForwarding;
            }

            /* loaded from: classes.dex */
            public class UpdateAutoForwarding extends GmailRequest<AutoForwarding> {
                private static final String REST_PATH = "{userId}/settings/autoForwarding";

                @Key
                private String userId;

                protected UpdateAutoForwarding(String str, AutoForwarding autoForwarding) {
                    super(Gmail.this, "PUT", REST_PATH, autoForwarding, AutoForwarding.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<AutoForwarding> setAlt2(String str) {
                    return (UpdateAutoForwarding) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<AutoForwarding> setFields2(String str) {
                    return (UpdateAutoForwarding) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<AutoForwarding> setKey2(String str) {
                    return (UpdateAutoForwarding) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<AutoForwarding> setOauthToken2(String str) {
                    return (UpdateAutoForwarding) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<AutoForwarding> setPrettyPrint2(Boolean bool) {
                    return (UpdateAutoForwarding) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<AutoForwarding> setQuotaUser2(String str) {
                    return (UpdateAutoForwarding) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<AutoForwarding> setUserIp2(String str) {
                    return (UpdateAutoForwarding) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public UpdateAutoForwarding setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public UpdateAutoForwarding set(String str, Object obj) {
                    return (UpdateAutoForwarding) super.set(str, obj);
                }
            }

            public UpdateImap updateImap(String str, ImapSettings imapSettings) throws IOException {
                UpdateImap updateImap = new UpdateImap(str, imapSettings);
                Gmail.this.initialize(updateImap);
                return updateImap;
            }

            /* loaded from: classes.dex */
            public class UpdateImap extends GmailRequest<ImapSettings> {
                private static final String REST_PATH = "{userId}/settings/imap";

                @Key
                private String userId;

                protected UpdateImap(String str, ImapSettings imapSettings) {
                    super(Gmail.this, "PUT", REST_PATH, imapSettings, ImapSettings.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ImapSettings> setAlt2(String str) {
                    return (UpdateImap) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ImapSettings> setFields2(String str) {
                    return (UpdateImap) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ImapSettings> setKey2(String str) {
                    return (UpdateImap) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ImapSettings> setOauthToken2(String str) {
                    return (UpdateImap) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ImapSettings> setPrettyPrint2(Boolean bool) {
                    return (UpdateImap) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ImapSettings> setQuotaUser2(String str) {
                    return (UpdateImap) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ImapSettings> setUserIp2(String str) {
                    return (UpdateImap) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public UpdateImap setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public UpdateImap set(String str, Object obj) {
                    return (UpdateImap) super.set(str, obj);
                }
            }

            public UpdatePop updatePop(String str, PopSettings popSettings) throws IOException {
                UpdatePop updatePop = new UpdatePop(str, popSettings);
                Gmail.this.initialize(updatePop);
                return updatePop;
            }

            /* loaded from: classes.dex */
            public class UpdatePop extends GmailRequest<PopSettings> {
                private static final String REST_PATH = "{userId}/settings/pop";

                @Key
                private String userId;

                protected UpdatePop(String str, PopSettings popSettings) {
                    super(Gmail.this, "PUT", REST_PATH, popSettings, PopSettings.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<PopSettings> setAlt2(String str) {
                    return (UpdatePop) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<PopSettings> setFields2(String str) {
                    return (UpdatePop) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<PopSettings> setKey2(String str) {
                    return (UpdatePop) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<PopSettings> setOauthToken2(String str) {
                    return (UpdatePop) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<PopSettings> setPrettyPrint2(Boolean bool) {
                    return (UpdatePop) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<PopSettings> setQuotaUser2(String str) {
                    return (UpdatePop) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<PopSettings> setUserIp2(String str) {
                    return (UpdatePop) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public UpdatePop setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public UpdatePop set(String str, Object obj) {
                    return (UpdatePop) super.set(str, obj);
                }
            }

            public UpdateVacation updateVacation(String str, VacationSettings vacationSettings) throws IOException {
                UpdateVacation updateVacation = new UpdateVacation(str, vacationSettings);
                Gmail.this.initialize(updateVacation);
                return updateVacation;
            }

            /* loaded from: classes.dex */
            public class UpdateVacation extends GmailRequest<VacationSettings> {
                private static final String REST_PATH = "{userId}/settings/vacation";

                @Key
                private String userId;

                protected UpdateVacation(String str, VacationSettings vacationSettings) {
                    super(Gmail.this, "PUT", REST_PATH, vacationSettings, VacationSettings.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<VacationSettings> setAlt2(String str) {
                    return (UpdateVacation) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<VacationSettings> setFields2(String str) {
                    return (UpdateVacation) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<VacationSettings> setKey2(String str) {
                    return (UpdateVacation) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<VacationSettings> setOauthToken2(String str) {
                    return (UpdateVacation) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<VacationSettings> setPrettyPrint2(Boolean bool) {
                    return (UpdateVacation) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<VacationSettings> setQuotaUser2(String str) {
                    return (UpdateVacation) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<VacationSettings> setUserIp2(String str) {
                    return (UpdateVacation) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public UpdateVacation setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public UpdateVacation set(String str, Object obj) {
                    return (UpdateVacation) super.set(str, obj);
                }
            }

            public Filters filters() {
                return new Filters();
            }

            /* loaded from: classes.dex */
            public class Filters {
                public Filters() {
                }

                public Create create(String str, Filter filter) throws IOException {
                    Create create = new Create(str, filter);
                    Gmail.this.initialize(create);
                    return create;
                }

                /* loaded from: classes.dex */
                public class Create extends GmailRequest<Filter> {
                    private static final String REST_PATH = "{userId}/settings/filters";

                    @Key
                    private String userId;

                    protected Create(String str, Filter filter) {
                        super(Gmail.this, "POST", REST_PATH, filter, Filter.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<Filter> setAlt2(String str) {
                        return (Create) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<Filter> setFields2(String str) {
                        return (Create) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<Filter> setKey2(String str) {
                        return (Create) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<Filter> setOauthToken2(String str) {
                        return (Create) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<Filter> setPrettyPrint2(Boolean bool) {
                        return (Create) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<Filter> setQuotaUser2(String str) {
                        return (Create) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<Filter> setUserIp2(String str) {
                        return (Create) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Create setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Create set(String str, Object obj) {
                        return (Create) super.set(str, obj);
                    }
                }

                public Delete delete(String str, String str2) throws IOException {
                    Delete delete = new Delete(str, str2);
                    Gmail.this.initialize(delete);
                    return delete;
                }

                /* loaded from: classes.dex */
                public class Delete extends GmailRequest<Void> {
                    private static final String REST_PATH = "{userId}/settings/filters/{id}";

                    @Key
                    private String id;

                    @Key
                    private String userId;

                    protected Delete(String str, String str2) {
                        super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<Void> setAlt2(String str) {
                        return (Delete) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<Void> setFields2(String str) {
                        return (Delete) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<Void> setKey2(String str) {
                        return (Delete) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<Void> setOauthToken2(String str) {
                        return (Delete) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                        return (Delete) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<Void> setQuotaUser2(String str) {
                        return (Delete) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<Void> setUserIp2(String str) {
                        return (Delete) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Delete setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getId() {
                        return this.id;
                    }

                    public Delete setId(String str) {
                        this.id = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Delete set(String str, Object obj) {
                        return (Delete) super.set(str, obj);
                    }
                }

                public Get get(String str, String str2) throws IOException {
                    Get get = new Get(str, str2);
                    Gmail.this.initialize(get);
                    return get;
                }

                /* loaded from: classes.dex */
                public class Get extends GmailRequest<Filter> {
                    private static final String REST_PATH = "{userId}/settings/filters/{id}";

                    @Key
                    private String id;

                    @Key
                    private String userId;

                    protected Get(String str, String str2) {
                        super(Gmail.this, "GET", REST_PATH, null, Filter.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<Filter> setAlt2(String str) {
                        return (Get) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<Filter> setFields2(String str) {
                        return (Get) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<Filter> setKey2(String str) {
                        return (Get) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<Filter> setOauthToken2(String str) {
                        return (Get) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<Filter> setPrettyPrint2(Boolean bool) {
                        return (Get) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<Filter> setQuotaUser2(String str) {
                        return (Get) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<Filter> setUserIp2(String str) {
                        return (Get) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Get setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getId() {
                        return this.id;
                    }

                    public Get setId(String str) {
                        this.id = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Get set(String str, Object obj) {
                        return (Get) super.set(str, obj);
                    }
                }

                public List list(String str) throws IOException {
                    List list = new List(str);
                    Gmail.this.initialize(list);
                    return list;
                }

                /* loaded from: classes.dex */
                public class List extends GmailRequest<ListFiltersResponse> {
                    private static final String REST_PATH = "{userId}/settings/filters";

                    @Key
                    private String userId;

                    protected List(String str) {
                        super(Gmail.this, "GET", REST_PATH, null, ListFiltersResponse.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<ListFiltersResponse> setAlt2(String str) {
                        return (List) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<ListFiltersResponse> setFields2(String str) {
                        return (List) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<ListFiltersResponse> setKey2(String str) {
                        return (List) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<ListFiltersResponse> setOauthToken2(String str) {
                        return (List) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<ListFiltersResponse> setPrettyPrint2(Boolean bool) {
                        return (List) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<ListFiltersResponse> setQuotaUser2(String str) {
                        return (List) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<ListFiltersResponse> setUserIp2(String str) {
                        return (List) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public List setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public List set(String str, Object obj) {
                        return (List) super.set(str, obj);
                    }
                }
            }

            public ForwardingAddresses forwardingAddresses() {
                return new ForwardingAddresses();
            }

            /* loaded from: classes.dex */
            public class ForwardingAddresses {
                public ForwardingAddresses() {
                }

                public Create create(String str, ForwardingAddress forwardingAddress) throws IOException {
                    Create create = new Create(str, forwardingAddress);
                    Gmail.this.initialize(create);
                    return create;
                }

                /* loaded from: classes.dex */
                public class Create extends GmailRequest<ForwardingAddress> {
                    private static final String REST_PATH = "{userId}/settings/forwardingAddresses";

                    @Key
                    private String userId;

                    protected Create(String str, ForwardingAddress forwardingAddress) {
                        super(Gmail.this, "POST", REST_PATH, forwardingAddress, ForwardingAddress.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<ForwardingAddress> setAlt2(String str) {
                        return (Create) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<ForwardingAddress> setFields2(String str) {
                        return (Create) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<ForwardingAddress> setKey2(String str) {
                        return (Create) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<ForwardingAddress> setOauthToken2(String str) {
                        return (Create) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<ForwardingAddress> setPrettyPrint2(Boolean bool) {
                        return (Create) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<ForwardingAddress> setQuotaUser2(String str) {
                        return (Create) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<ForwardingAddress> setUserIp2(String str) {
                        return (Create) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Create setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Create set(String str, Object obj) {
                        return (Create) super.set(str, obj);
                    }
                }

                public Delete delete(String str, String str2) throws IOException {
                    Delete delete = new Delete(str, str2);
                    Gmail.this.initialize(delete);
                    return delete;
                }

                /* loaded from: classes.dex */
                public class Delete extends GmailRequest<Void> {
                    private static final String REST_PATH = "{userId}/settings/forwardingAddresses/{forwardingEmail}";

                    @Key
                    private String forwardingEmail;

                    @Key
                    private String userId;

                    protected Delete(String str, String str2) {
                        super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.forwardingEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter forwardingEmail must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<Void> setAlt2(String str) {
                        return (Delete) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<Void> setFields2(String str) {
                        return (Delete) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<Void> setKey2(String str) {
                        return (Delete) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<Void> setOauthToken2(String str) {
                        return (Delete) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                        return (Delete) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<Void> setQuotaUser2(String str) {
                        return (Delete) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<Void> setUserIp2(String str) {
                        return (Delete) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Delete setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getForwardingEmail() {
                        return this.forwardingEmail;
                    }

                    public Delete setForwardingEmail(String str) {
                        this.forwardingEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Delete set(String str, Object obj) {
                        return (Delete) super.set(str, obj);
                    }
                }

                public Get get(String str, String str2) throws IOException {
                    Get get = new Get(str, str2);
                    Gmail.this.initialize(get);
                    return get;
                }

                /* loaded from: classes.dex */
                public class Get extends GmailRequest<ForwardingAddress> {
                    private static final String REST_PATH = "{userId}/settings/forwardingAddresses/{forwardingEmail}";

                    @Key
                    private String forwardingEmail;

                    @Key
                    private String userId;

                    protected Get(String str, String str2) {
                        super(Gmail.this, "GET", REST_PATH, null, ForwardingAddress.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.forwardingEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter forwardingEmail must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<ForwardingAddress> setAlt2(String str) {
                        return (Get) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<ForwardingAddress> setFields2(String str) {
                        return (Get) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<ForwardingAddress> setKey2(String str) {
                        return (Get) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<ForwardingAddress> setOauthToken2(String str) {
                        return (Get) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<ForwardingAddress> setPrettyPrint2(Boolean bool) {
                        return (Get) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<ForwardingAddress> setQuotaUser2(String str) {
                        return (Get) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<ForwardingAddress> setUserIp2(String str) {
                        return (Get) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Get setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getForwardingEmail() {
                        return this.forwardingEmail;
                    }

                    public Get setForwardingEmail(String str) {
                        this.forwardingEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Get set(String str, Object obj) {
                        return (Get) super.set(str, obj);
                    }
                }

                public List list(String str) throws IOException {
                    List list = new List(str);
                    Gmail.this.initialize(list);
                    return list;
                }

                /* loaded from: classes.dex */
                public class List extends GmailRequest<ListForwardingAddressesResponse> {
                    private static final String REST_PATH = "{userId}/settings/forwardingAddresses";

                    @Key
                    private String userId;

                    protected List(String str) {
                        super(Gmail.this, "GET", REST_PATH, null, ListForwardingAddressesResponse.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<ListForwardingAddressesResponse> setAlt2(String str) {
                        return (List) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<ListForwardingAddressesResponse> setFields2(String str) {
                        return (List) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<ListForwardingAddressesResponse> setKey2(String str) {
                        return (List) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<ListForwardingAddressesResponse> setOauthToken2(String str) {
                        return (List) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<ListForwardingAddressesResponse> setPrettyPrint2(Boolean bool) {
                        return (List) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<ListForwardingAddressesResponse> setQuotaUser2(String str) {
                        return (List) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<ListForwardingAddressesResponse> setUserIp2(String str) {
                        return (List) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public List setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public List set(String str, Object obj) {
                        return (List) super.set(str, obj);
                    }
                }
            }

            public SendAs sendAs() {
                return new SendAs();
            }

            /* loaded from: classes.dex */
            public class SendAs {
                public SendAs() {
                }

                public Create create(String str, com.google.api.services.gmail.model.SendAs sendAs) throws IOException {
                    Create create = new Create(str, sendAs);
                    Gmail.this.initialize(create);
                    return create;
                }

                /* loaded from: classes.dex */
                public class Create extends GmailRequest<com.google.api.services.gmail.model.SendAs> {
                    private static final String REST_PATH = "{userId}/settings/sendAs";

                    @Key
                    private String userId;

                    protected Create(String str, com.google.api.services.gmail.model.SendAs sendAs) {
                        super(Gmail.this, "POST", REST_PATH, sendAs, com.google.api.services.gmail.model.SendAs.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setAlt2(String str) {
                        return (Create) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setFields2(String str) {
                        return (Create) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setKey2(String str) {
                        return (Create) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setOauthToken2(String str) {
                        return (Create) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setPrettyPrint2(Boolean bool) {
                        return (Create) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setQuotaUser2(String str) {
                        return (Create) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setUserIp2(String str) {
                        return (Create) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Create setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Create set(String str, Object obj) {
                        return (Create) super.set(str, obj);
                    }
                }

                public Delete delete(String str, String str2) throws IOException {
                    Delete delete = new Delete(str, str2);
                    Gmail.this.initialize(delete);
                    return delete;
                }

                /* loaded from: classes.dex */
                public class Delete extends GmailRequest<Void> {
                    private static final String REST_PATH = "{userId}/settings/sendAs/{sendAsEmail}";

                    @Key
                    private String sendAsEmail;

                    @Key
                    private String userId;

                    protected Delete(String str, String str2) {
                        super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.sendAsEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter sendAsEmail must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<Void> setAlt2(String str) {
                        return (Delete) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<Void> setFields2(String str) {
                        return (Delete) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<Void> setKey2(String str) {
                        return (Delete) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<Void> setOauthToken2(String str) {
                        return (Delete) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                        return (Delete) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<Void> setQuotaUser2(String str) {
                        return (Delete) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<Void> setUserIp2(String str) {
                        return (Delete) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Delete setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getSendAsEmail() {
                        return this.sendAsEmail;
                    }

                    public Delete setSendAsEmail(String str) {
                        this.sendAsEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Delete set(String str, Object obj) {
                        return (Delete) super.set(str, obj);
                    }
                }

                public Get get(String str, String str2) throws IOException {
                    Get get = new Get(str, str2);
                    Gmail.this.initialize(get);
                    return get;
                }

                /* loaded from: classes.dex */
                public class Get extends GmailRequest<com.google.api.services.gmail.model.SendAs> {
                    private static final String REST_PATH = "{userId}/settings/sendAs/{sendAsEmail}";

                    @Key
                    private String sendAsEmail;

                    @Key
                    private String userId;

                    protected Get(String str, String str2) {
                        super(Gmail.this, "GET", REST_PATH, null, com.google.api.services.gmail.model.SendAs.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.sendAsEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter sendAsEmail must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setAlt2(String str) {
                        return (Get) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setFields2(String str) {
                        return (Get) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setKey2(String str) {
                        return (Get) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setOauthToken2(String str) {
                        return (Get) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setPrettyPrint2(Boolean bool) {
                        return (Get) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setQuotaUser2(String str) {
                        return (Get) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setUserIp2(String str) {
                        return (Get) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Get setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getSendAsEmail() {
                        return this.sendAsEmail;
                    }

                    public Get setSendAsEmail(String str) {
                        this.sendAsEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Get set(String str, Object obj) {
                        return (Get) super.set(str, obj);
                    }
                }

                public List list(String str) throws IOException {
                    List list = new List(str);
                    Gmail.this.initialize(list);
                    return list;
                }

                /* loaded from: classes.dex */
                public class List extends GmailRequest<ListSendAsResponse> {
                    private static final String REST_PATH = "{userId}/settings/sendAs";

                    @Key
                    private String userId;

                    protected List(String str) {
                        super(Gmail.this, "GET", REST_PATH, null, ListSendAsResponse.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }

                    @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<ListSendAsResponse> setAlt2(String str) {
                        return (List) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<ListSendAsResponse> setFields2(String str) {
                        return (List) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<ListSendAsResponse> setKey2(String str) {
                        return (List) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<ListSendAsResponse> setOauthToken2(String str) {
                        return (List) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<ListSendAsResponse> setPrettyPrint2(Boolean bool) {
                        return (List) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<ListSendAsResponse> setQuotaUser2(String str) {
                        return (List) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<ListSendAsResponse> setUserIp2(String str) {
                        return (List) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public List setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public List set(String str, Object obj) {
                        return (List) super.set(str, obj);
                    }
                }

                public Patch patch(String str, String str2, com.google.api.services.gmail.model.SendAs sendAs) throws IOException {
                    Patch patch = new Patch(str, str2, sendAs);
                    Gmail.this.initialize(patch);
                    return patch;
                }

                /* loaded from: classes.dex */
                public class Patch extends GmailRequest<com.google.api.services.gmail.model.SendAs> {
                    private static final String REST_PATH = "{userId}/settings/sendAs/{sendAsEmail}";

                    @Key
                    private String sendAsEmail;

                    @Key
                    private String userId;

                    protected Patch(String str, String str2, com.google.api.services.gmail.model.SendAs sendAs) {
                        super(Gmail.this, HttpMethods.PATCH, REST_PATH, sendAs, com.google.api.services.gmail.model.SendAs.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.sendAsEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter sendAsEmail must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setAlt2(String str) {
                        return (Patch) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setFields2(String str) {
                        return (Patch) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setKey2(String str) {
                        return (Patch) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setOauthToken2(String str) {
                        return (Patch) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setPrettyPrint2(Boolean bool) {
                        return (Patch) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setQuotaUser2(String str) {
                        return (Patch) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setUserIp2(String str) {
                        return (Patch) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Patch setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getSendAsEmail() {
                        return this.sendAsEmail;
                    }

                    public Patch setSendAsEmail(String str) {
                        this.sendAsEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Patch set(String str, Object obj) {
                        return (Patch) super.set(str, obj);
                    }
                }

                public Update update(String str, String str2, com.google.api.services.gmail.model.SendAs sendAs) throws IOException {
                    Update update = new Update(str, str2, sendAs);
                    Gmail.this.initialize(update);
                    return update;
                }

                /* loaded from: classes.dex */
                public class Update extends GmailRequest<com.google.api.services.gmail.model.SendAs> {
                    private static final String REST_PATH = "{userId}/settings/sendAs/{sendAsEmail}";

                    @Key
                    private String sendAsEmail;

                    @Key
                    private String userId;

                    protected Update(String str, String str2, com.google.api.services.gmail.model.SendAs sendAs) {
                        super(Gmail.this, "PUT", REST_PATH, sendAs, com.google.api.services.gmail.model.SendAs.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.sendAsEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter sendAsEmail must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setAlt2(String str) {
                        return (Update) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setFields2(String str) {
                        return (Update) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setKey2(String str) {
                        return (Update) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setOauthToken2(String str) {
                        return (Update) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setPrettyPrint2(Boolean bool) {
                        return (Update) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setQuotaUser2(String str) {
                        return (Update) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<com.google.api.services.gmail.model.SendAs> setUserIp2(String str) {
                        return (Update) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Update setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getSendAsEmail() {
                        return this.sendAsEmail;
                    }

                    public Update setSendAsEmail(String str) {
                        this.sendAsEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Update set(String str, Object obj) {
                        return (Update) super.set(str, obj);
                    }
                }

                public Verify verify(String str, String str2) throws IOException {
                    Verify verify = new Verify(str, str2);
                    Gmail.this.initialize(verify);
                    return verify;
                }

                /* loaded from: classes.dex */
                public class Verify extends GmailRequest<Void> {
                    private static final String REST_PATH = "{userId}/settings/sendAs/{sendAsEmail}/verify";

                    @Key
                    private String sendAsEmail;

                    @Key
                    private String userId;

                    protected Verify(String str, String str2) {
                        super(Gmail.this, "POST", REST_PATH, null, Void.class);
                        this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                        this.sendAsEmail = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter sendAsEmail must be specified.");
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setAlt */
                    public GmailRequest<Void> setAlt2(String str) {
                        return (Verify) super.setAlt2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setFields */
                    public GmailRequest<Void> setFields2(String str) {
                        return (Verify) super.setFields2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setKey */
                    public GmailRequest<Void> setKey2(String str) {
                        return (Verify) super.setKey2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setOauthToken */
                    public GmailRequest<Void> setOauthToken2(String str) {
                        return (Verify) super.setOauthToken2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setPrettyPrint */
                    public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                        return (Verify) super.setPrettyPrint2(bool);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setQuotaUser */
                    public GmailRequest<Void> setQuotaUser2(String str) {
                        return (Verify) super.setQuotaUser2(str);
                    }

                    @Override // com.google.api.services.gmail.GmailRequest
                    /* renamed from: setUserIp */
                    public GmailRequest<Void> setUserIp2(String str) {
                        return (Verify) super.setUserIp2(str);
                    }

                    public String getUserId() {
                        return this.userId;
                    }

                    public Verify setUserId(String str) {
                        this.userId = str;
                        return this;
                    }

                    public String getSendAsEmail() {
                        return this.sendAsEmail;
                    }

                    public Verify setSendAsEmail(String str) {
                        this.sendAsEmail = str;
                        return this;
                    }

                    @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                    public Verify set(String str, Object obj) {
                        return (Verify) super.set(str, obj);
                    }
                }
            }
        }

        public Threads threads() {
            return new Threads();
        }

        /* loaded from: classes.dex */
        public class Threads {
            public Threads() {
            }

            public Delete delete(String str, String str2) throws IOException {
                Delete delete = new Delete(str, str2);
                Gmail.this.initialize(delete);
                return delete;
            }

            /* loaded from: classes.dex */
            public class Delete extends GmailRequest<Void> {
                private static final String REST_PATH = "{userId}/threads/{id}";

                @Key
                private String id;

                @Key
                private String userId;

                protected Delete(String str, String str2) {
                    super(Gmail.this, "DELETE", REST_PATH, null, Void.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Void> setAlt2(String str) {
                    return (Delete) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Void> setFields2(String str) {
                    return (Delete) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Void> setKey2(String str) {
                    return (Delete) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Void> setOauthToken2(String str) {
                    return (Delete) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Void> setPrettyPrint2(Boolean bool) {
                    return (Delete) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Void> setQuotaUser2(String str) {
                    return (Delete) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Void> setUserIp2(String str) {
                    return (Delete) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Delete setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Delete setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Delete set(String str, Object obj) {
                    return (Delete) super.set(str, obj);
                }
            }

            public Get get(String str, String str2) throws IOException {
                Get get = new Get(str, str2);
                Gmail.this.initialize(get);
                return get;
            }

            /* loaded from: classes.dex */
            public class Get extends GmailRequest<Thread> {
                private static final String REST_PATH = "{userId}/threads/{id}";

                @Key
                private String format;

                @Key
                private String id;

                @Key
                private java.util.List<String> metadataHeaders;

                @Key
                private String userId;

                protected Get(String str, String str2) {
                    super(Gmail.this, "GET", REST_PATH, null, Thread.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Thread> setAlt2(String str) {
                    return (Get) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Thread> setFields2(String str) {
                    return (Get) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Thread> setKey2(String str) {
                    return (Get) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Thread> setOauthToken2(String str) {
                    return (Get) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Thread> setPrettyPrint2(Boolean bool) {
                    return (Get) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Thread> setQuotaUser2(String str) {
                    return (Get) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Thread> setUserIp2(String str) {
                    return (Get) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Get setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Get setId(String str) {
                    this.id = str;
                    return this;
                }

                public String getFormat() {
                    return this.format;
                }

                public Get setFormat(String str) {
                    this.format = str;
                    return this;
                }

                public java.util.List<String> getMetadataHeaders() {
                    return this.metadataHeaders;
                }

                public Get setMetadataHeaders(java.util.List<String> list) {
                    this.metadataHeaders = list;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Get set(String str, Object obj) {
                    return (Get) super.set(str, obj);
                }
            }

            public List list(String str) throws IOException {
                List list = new List(str);
                Gmail.this.initialize(list);
                return list;
            }

            /* loaded from: classes.dex */
            public class List extends GmailRequest<ListThreadsResponse> {
                private static final String REST_PATH = "{userId}/threads";

                @Key
                private Boolean includeSpamTrash;

                @Key
                private java.util.List<String> labelIds;

                @Key
                private Long maxResults;

                @Key
                private String pageToken;

                @Key
                private String q;

                @Key
                private String userId;

                protected List(String str) {
                    super(Gmail.this, "GET", REST_PATH, null, ListThreadsResponse.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override // com.google.api.client.googleapis.services.AbstractGoogleClientRequest
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<ListThreadsResponse> setAlt2(String str) {
                    return (List) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<ListThreadsResponse> setFields2(String str) {
                    return (List) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<ListThreadsResponse> setKey2(String str) {
                    return (List) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<ListThreadsResponse> setOauthToken2(String str) {
                    return (List) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<ListThreadsResponse> setPrettyPrint2(Boolean bool) {
                    return (List) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<ListThreadsResponse> setQuotaUser2(String str) {
                    return (List) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<ListThreadsResponse> setUserIp2(String str) {
                    return (List) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public List setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public Boolean getIncludeSpamTrash() {
                    return this.includeSpamTrash;
                }

                public List setIncludeSpamTrash(Boolean bool) {
                    this.includeSpamTrash = bool;
                    return this;
                }

                public boolean isIncludeSpamTrash() {
                    if (this.includeSpamTrash == null || this.includeSpamTrash == Data.NULL_BOOLEAN) {
                        return false;
                    }
                    return this.includeSpamTrash.booleanValue();
                }

                public java.util.List<String> getLabelIds() {
                    return this.labelIds;
                }

                public List setLabelIds(java.util.List<String> list) {
                    this.labelIds = list;
                    return this;
                }

                public Long getMaxResults() {
                    return this.maxResults;
                }

                public List setMaxResults(Long l) {
                    this.maxResults = l;
                    return this;
                }

                public String getPageToken() {
                    return this.pageToken;
                }

                public List setPageToken(String str) {
                    this.pageToken = str;
                    return this;
                }

                public String getQ() {
                    return this.q;
                }

                public List setQ(String str) {
                    this.q = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public List set(String str, Object obj) {
                    return (List) super.set(str, obj);
                }
            }

            public Modify modify(String str, String str2, ModifyThreadRequest modifyThreadRequest) throws IOException {
                Modify modify = new Modify(str, str2, modifyThreadRequest);
                Gmail.this.initialize(modify);
                return modify;
            }

            /* loaded from: classes.dex */
            public class Modify extends GmailRequest<Thread> {
                private static final String REST_PATH = "{userId}/threads/{id}/modify";

                @Key
                private String id;

                @Key
                private String userId;

                protected Modify(String str, String str2, ModifyThreadRequest modifyThreadRequest) {
                    super(Gmail.this, "POST", REST_PATH, modifyThreadRequest, Thread.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Thread> setAlt2(String str) {
                    return (Modify) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Thread> setFields2(String str) {
                    return (Modify) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Thread> setKey2(String str) {
                    return (Modify) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Thread> setOauthToken2(String str) {
                    return (Modify) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Thread> setPrettyPrint2(Boolean bool) {
                    return (Modify) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Thread> setQuotaUser2(String str) {
                    return (Modify) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Thread> setUserIp2(String str) {
                    return (Modify) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Modify setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Modify setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Modify set(String str, Object obj) {
                    return (Modify) super.set(str, obj);
                }
            }

            public Trash trash(String str, String str2) throws IOException {
                Trash trash = new Trash(str, str2);
                Gmail.this.initialize(trash);
                return trash;
            }

            /* loaded from: classes.dex */
            public class Trash extends GmailRequest<Thread> {
                private static final String REST_PATH = "{userId}/threads/{id}/trash";

                @Key
                private String id;

                @Key
                private String userId;

                protected Trash(String str, String str2) {
                    super(Gmail.this, "POST", REST_PATH, null, Thread.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Thread> setAlt2(String str) {
                    return (Trash) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Thread> setFields2(String str) {
                    return (Trash) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Thread> setKey2(String str) {
                    return (Trash) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Thread> setOauthToken2(String str) {
                    return (Trash) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Thread> setPrettyPrint2(Boolean bool) {
                    return (Trash) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Thread> setQuotaUser2(String str) {
                    return (Trash) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Thread> setUserIp2(String str) {
                    return (Trash) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Trash setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Trash setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Trash set(String str, Object obj) {
                    return (Trash) super.set(str, obj);
                }
            }

            public Untrash untrash(String str, String str2) throws IOException {
                Untrash untrash = new Untrash(str, str2);
                Gmail.this.initialize(untrash);
                return untrash;
            }

            /* loaded from: classes.dex */
            public class Untrash extends GmailRequest<Thread> {
                private static final String REST_PATH = "{userId}/threads/{id}/untrash";

                @Key
                private String id;

                @Key
                private String userId;

                protected Untrash(String str, String str2) {
                    super(Gmail.this, "POST", REST_PATH, null, Thread.class);
                    this.userId = (String) com.google.api.client.util.Preconditions.checkNotNull(str, "Required parameter userId must be specified.");
                    this.id = (String) com.google.api.client.util.Preconditions.checkNotNull(str2, "Required parameter id must be specified.");
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setAlt */
                public GmailRequest<Thread> setAlt2(String str) {
                    return (Untrash) super.setAlt2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setFields */
                public GmailRequest<Thread> setFields2(String str) {
                    return (Untrash) super.setFields2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setKey */
                public GmailRequest<Thread> setKey2(String str) {
                    return (Untrash) super.setKey2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setOauthToken */
                public GmailRequest<Thread> setOauthToken2(String str) {
                    return (Untrash) super.setOauthToken2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setPrettyPrint */
                public GmailRequest<Thread> setPrettyPrint2(Boolean bool) {
                    return (Untrash) super.setPrettyPrint2(bool);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setQuotaUser */
                public GmailRequest<Thread> setQuotaUser2(String str) {
                    return (Untrash) super.setQuotaUser2(str);
                }

                @Override // com.google.api.services.gmail.GmailRequest
                /* renamed from: setUserIp */
                public GmailRequest<Thread> setUserIp2(String str) {
                    return (Untrash) super.setUserIp2(str);
                }

                public String getUserId() {
                    return this.userId;
                }

                public Untrash setUserId(String str) {
                    this.userId = str;
                    return this;
                }

                public String getId() {
                    return this.id;
                }

                public Untrash setId(String str) {
                    this.id = str;
                    return this;
                }

                @Override // com.google.api.services.gmail.GmailRequest, com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest, com.google.api.client.googleapis.services.AbstractGoogleClientRequest, com.google.api.client.util.GenericData
                public Untrash set(String str, Object obj) {
                    return (Untrash) super.set(str, obj);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class Builder extends AbstractGoogleJsonClient.Builder {
        public Builder(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
            super(httpTransport, jsonFactory, Gmail.DEFAULT_ROOT_URL, Gmail.DEFAULT_SERVICE_PATH, httpRequestInitializer, false);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Gmail build() {
            return new Gmail(this);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setRootUrl(String str) {
            return (Builder) super.setRootUrl(str);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setServicePath(String str) {
            return (Builder) super.setServicePath(str);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setHttpRequestInitializer(HttpRequestInitializer httpRequestInitializer) {
            return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setApplicationName(String str) {
            return (Builder) super.setApplicationName(str);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setSuppressPatternChecks(boolean z) {
            return (Builder) super.setSuppressPatternChecks(z);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setSuppressRequiredParameterChecks(boolean z) {
            return (Builder) super.setSuppressRequiredParameterChecks(z);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setSuppressAllChecks(boolean z) {
            return (Builder) super.setSuppressAllChecks(z);
        }

        public final Builder setGmailRequestInitializer(GmailRequestInitializer gmailRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer((GoogleClientRequestInitializer) gmailRequestInitializer);
        }

        @Override // com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder, com.google.api.client.googleapis.services.AbstractGoogleClient.Builder
        public final Builder setGoogleClientRequestInitializer(GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
    }
}
