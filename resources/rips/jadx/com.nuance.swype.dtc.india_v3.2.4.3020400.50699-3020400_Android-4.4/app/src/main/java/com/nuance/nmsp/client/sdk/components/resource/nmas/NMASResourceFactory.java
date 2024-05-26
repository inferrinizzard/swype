package com.nuance.nmsp.client.sdk.components.resource.nmas;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.resource.internal.nmas.PDXQueryResult;

/* loaded from: classes.dex */
public class NMASResourceFactory {
    private static final LogFactory.Log a = LogFactory.getLog(NMASResourceFactory.class);

    private NMASResourceFactory() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x00c6, code lost:            throw new java.lang.IllegalArgumentException("Parameter type: " + r0.getType() + " not allowed. ");     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResource createNMASResource(com.nuance.nmsp.client.sdk.components.resource.common.Manager r4, com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceListener r5, java.util.Vector r6, java.lang.String r7) {
        /*
            com.nuance.nmsp.client.sdk.common.oem.api.LogFactory$Log r0 = com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory.a
            boolean r0 = r0.isDebugEnabled()
            if (r0 == 0) goto L10
            com.nuance.nmsp.client.sdk.common.oem.api.LogFactory$Log r0 = com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory.a
            java.lang.String r1 = "createNMASResource"
            r0.debug(r1)
        L10:
            if (r4 != 0) goto L23
            com.nuance.nmsp.client.sdk.common.oem.api.LogFactory$Log r0 = com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory.a
            java.lang.String r1 = "manager is null"
            r0.error(r1)
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "manager can not be null!"
            r0.<init>(r1)
            throw r0
        L23:
            if (r5 != 0) goto L36
            com.nuance.nmsp.client.sdk.common.oem.api.LogFactory$Log r0 = com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory.a
            java.lang.String r1 = "nmasListener is null"
            r0.error(r1)
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "nmasListener can not be null!"
            r0.<init>(r1)
            throw r0
        L36:
            if (r6 == 0) goto Lcc
            r0 = 0
            r1 = r0
        L3a:
            int r0 = r6.size()
            if (r1 >= r0) goto Lcc
            java.lang.Object r0 = r6.elementAt(r1)
            com.nuance.nmsp.client.sdk.components.general.Parameter r0 = (com.nuance.nmsp.client.sdk.components.general.Parameter) r0
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.APP
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.NSS
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.SLOG
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.NSSLOG
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.GWLOG
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.SVSP
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.SIP
            if (r2 == r3) goto L86
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r2 = r0.getType()
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = com.nuance.nmsp.client.sdk.components.general.Parameter.Type.SDP
            if (r2 != r3) goto Lc7
        L86:
            com.nuance.nmsp.client.sdk.common.oem.api.LogFactory$Log r1 = com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory.a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "IllegalArgumentException Parameter type: "
            r2.<init>(r3)
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r3 = r0.getType()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = " not allowed. "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.error(r2)
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "Parameter type: "
            r2.<init>(r3)
            com.nuance.nmsp.client.sdk.components.general.Parameter$Type r0 = r0.getType()
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r2 = " not allowed. "
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r0 = r0.toString()
            r1.<init>(r0)
            throw r1
        Lc7:
            int r0 = r1 + 1
            r1 = r0
            goto L3a
        Lcc:
            com.nuance.nmsp.client.sdk.components.resource.internal.nmas.NMASResourceImpl r0 = new com.nuance.nmsp.client.sdk.components.resource.internal.nmas.NMASResourceImpl
            com.nuance.nmsp.client.sdk.components.resource.internal.common.ManagerImpl r4 = (com.nuance.nmsp.client.sdk.components.resource.internal.common.ManagerImpl) r4
            r0.<init>(r4, r5, r6, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory.createNMASResource(com.nuance.nmsp.client.sdk.components.resource.common.Manager, com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceListener, java.util.Vector, java.lang.String):com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResource");
    }

    public static QueryResult createQueryResult(byte[] bArr) {
        return new PDXQueryResult(bArr);
    }
}
