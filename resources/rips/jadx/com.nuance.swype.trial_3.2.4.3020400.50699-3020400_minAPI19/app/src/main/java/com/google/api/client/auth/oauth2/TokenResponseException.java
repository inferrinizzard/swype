package com.google.api.client.auth.oauth2;

import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.Strings;
import java.io.IOException;

/* loaded from: classes.dex */
public final class TokenResponseException extends HttpResponseException {
    final transient TokenErrorResponse details;

    private TokenResponseException(HttpResponseException.Builder builder, TokenErrorResponse details) {
        super(builder);
        this.details = details;
    }

    public static TokenResponseException from(JsonFactory jsonFactory, HttpResponse response) {
        HttpResponseException.Builder builder = new HttpResponseException.Builder(response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
        Preconditions.checkNotNull(jsonFactory);
        TokenErrorResponse details = null;
        String detailString = null;
        String contentType = response.getContentType();
        try {
            if (!response.isSuccessStatusCode() && contentType != null && response.getContent() != null && HttpMediaType.equalsIgnoreParameters(Json.MEDIA_TYPE, contentType)) {
                details = (TokenErrorResponse) new JsonObjectParser(jsonFactory).parseAndClose(response.getContent(), response.getContentCharset(), TokenErrorResponse.class);
                detailString = details.toPrettyString();
            } else {
                detailString = response.parseAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder message = HttpResponseException.computeMessageBuffer(response);
        if (!Strings.isNullOrEmpty(detailString)) {
            message.append(StringUtils.LINE_SEPARATOR).append(detailString);
            builder.setContent(detailString);
        }
        builder.setMessage(message.toString());
        return new TokenResponseException(builder, details);
    }
}
