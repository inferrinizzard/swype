package com.google.api.client.googleapis.json;

import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.Strings;
import java.io.IOException;

/* loaded from: classes.dex */
public final class GoogleJsonResponseException extends HttpResponseException {
    private final transient GoogleJsonError details;

    private GoogleJsonResponseException(HttpResponseException.Builder builder, GoogleJsonError details) {
        super(builder);
        this.details = details;
    }

    public static GoogleJsonResponseException from(JsonFactory jsonFactory, HttpResponse response) {
        HttpResponseException.Builder builder = new HttpResponseException.Builder(response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
        Preconditions.checkNotNull(jsonFactory);
        GoogleJsonError details = null;
        String detailString = null;
        try {
            if (response.isSuccessStatusCode() || !HttpMediaType.equalsIgnoreParameters(Json.MEDIA_TYPE, response.getContentType()) || response.getContent() == null) {
                detailString = response.parseAsString();
            } else {
                JsonParser parser = null;
                try {
                    try {
                        parser = jsonFactory.createJsonParser(response.getContent());
                        JsonToken currentToken = parser.getCurrentToken();
                        if (currentToken == null) {
                            currentToken = parser.nextToken();
                        }
                        if (currentToken != null) {
                            parser.skipToKey("error");
                            if (parser.getCurrentToken() != JsonToken.END_OBJECT) {
                                details = (GoogleJsonError) parser.parseAndClose(GoogleJsonError.class);
                                detailString = details.toPrettyString();
                            }
                        }
                        if (parser == null) {
                            response.ignore();
                        } else if (details == null) {
                            parser.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (parser == null) {
                            response.ignore();
                        } else if (details == null) {
                            parser.close();
                        }
                    }
                } catch (Throwable th) {
                    if (parser == null) {
                        response.ignore();
                    } else if (details == null) {
                        parser.close();
                    }
                    throw th;
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        StringBuilder message = HttpResponseException.computeMessageBuffer(response);
        if (!Strings.isNullOrEmpty(detailString)) {
            message.append(StringUtils.LINE_SEPARATOR).append(detailString);
            builder.setContent(detailString);
        }
        builder.setMessage(message.toString());
        return new GoogleJsonResponseException(builder, details);
    }
}
