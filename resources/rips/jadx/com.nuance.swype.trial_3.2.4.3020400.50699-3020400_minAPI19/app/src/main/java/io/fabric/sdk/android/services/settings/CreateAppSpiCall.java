package io.fabric.sdk.android.services.settings;

import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequestFactory;

/* loaded from: classes.dex */
public final class CreateAppSpiCall extends AbstractAppSpiCall {
    @Override // io.fabric.sdk.android.services.settings.AbstractAppSpiCall
    public final /* bridge */ /* synthetic */ boolean invoke(AppRequestData x0) {
        return super.invoke(x0);
    }

    public CreateAppSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.POST);
    }
}
