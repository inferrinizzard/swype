package com.nuance.connect.service.manager;

import android.annotation.SuppressLint;
import android.os.Message;
import android.util.Pair;
import com.nuance.connect.comm.Response;
import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.internal.Property;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.interfaces.CommandManager;
import com.nuance.connect.service.manager.interfaces.Manager;
import com.nuance.connect.service.manager.interfaces.MessageProcessor;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.VersionUtils;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ManagerRegistry {
    private ConnectClient client;
    private boolean oemIdChange;
    private VersionUtils.Version versionFrom;
    private VersionUtils.Version versionTo;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final List<Manager> managers = new ArrayList();
    Comparator<Manager> startOrderComparator = new Comparator<Manager>() { // from class: com.nuance.connect.service.manager.ManagerRegistry.1
        @Override // java.util.Comparator
        public int compare(Manager manager, Manager manager2) {
            if (manager.equals(manager2)) {
                return 0;
            }
            return manager.getDependentCount() == manager2.getDependentCount() ? manager.getManagerName().compareTo(manager2.getManagerName()) : manager.getDependentCount() > manager2.getDependentCount() ? -1 : 1;
        }
    };
    private boolean currentlyProcessing = false;
    private boolean validRegistry = false;
    private final GenericProperty.BooleanProperty idle = new GenericProperty.BooleanProperty(ManagerRegistry.class.getSimpleName(), false);
    private volatile boolean processingMessage = false;
    private boolean allManagersStarted = false;
    private boolean bPostStartComplete = false;

    @SuppressLint({"UseSparseArrays"})
    private final HashMap<Integer, WeakReference<MessageProcessor>> messageHandlerMap = new HashMap<>();
    private Property.BooleanValueListener valueListener = new Property.BooleanValueListener() { // from class: com.nuance.connect.service.manager.ManagerRegistry.2
        @Override // com.nuance.connect.internal.Property.ValueListener
        public void onValueChanged(Property<Boolean> property) {
            ManagerRegistry.this.checkIdle();
        }
    };

    private boolean add(Manager manager) {
        String managerName = manager.getManagerName();
        this.log.d("ManagerRegistry.add(): " + managerName);
        if (isRegistered(managerName)) {
            return true;
        }
        synchronized (this.managers) {
            this.managers.add(manager);
        }
        manager.getIdleProperty().addListener(this.valueListener);
        return true;
    }

    private boolean addManager(String str) {
        Manager manager = null;
        if (isRegistered(str)) {
            return true;
        }
        if (str.equals(ConfigurationManager.MANAGER_NAME)) {
            manager = new ConfigurationManager(this.client);
        } else if (str.equals(DeviceManager.MANAGER_NAME)) {
            manager = new DeviceManager(this.client);
        } else if (str.equals(SessionManager.MANAGER_NAME)) {
            manager = new SessionManager(this.client);
        } else if (str.equals(LanguageManager.MANAGER_NAME)) {
            manager = new LanguageManager(this.client);
        } else if (str.equals(AccountManager.MANAGER_NAME)) {
            manager = new AccountManager(this.client);
        } else if (str.equals(DlmSyncManager.MANAGER_NAME)) {
            manager = new DlmSyncManager(this.client);
        } else if (str.equals(CategoryManager.MANAGER_NAME)) {
            manager = new CategoryManager(this.client);
        } else if (str.equals("report")) {
            manager = new ReportingManager(this.client);
        } else if (str.equals(DocumentManager.MANAGER_NAME)) {
            manager = new DocumentManager(this.client);
        } else if (str.equals(GeoIpLocationManager.MANAGER_NAME)) {
            manager = new GeoIpLocationManager(this.client);
        } else {
            this.log.d("Unknown manager: " + str);
        }
        if (manager == null) {
            return false;
        }
        add(manager);
        String[] dependencies = manager.getDependencies();
        if (dependencies == null) {
            return true;
        }
        for (String str2 : dependencies) {
            addManager(str2);
        }
        return true;
    }

    private void calculateSortOrder() {
        ArrayList arrayList;
        ArrayList<Manager> arrayList2;
        this.log.d("calculateSortOrder()");
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            walkDependencyTree((Manager) it.next());
        }
        synchronized (this.managers) {
            Collections.sort(this.managers, this.startOrderComparator);
            arrayList2 = new ArrayList(this.managers);
        }
        this.log.d("final order()");
        for (Manager manager : arrayList2) {
            this.log.d(manager.getManagerName() + XMLResultsHandler.SEP_SPACE + manager.getDependentCount());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIdle() {
        ArrayList arrayList;
        boolean z;
        if (allStarted()) {
            if (!this.processingMessage) {
                synchronized (this.managers) {
                    arrayList = new ArrayList(this.managers);
                }
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    } else if (!((Manager) it.next()).getIdleProperty().getValue().booleanValue()) {
                        this.idle.setValue((Boolean) false, Property.Source.DEFAULT);
                        z = true;
                        break;
                    }
                }
            } else {
                this.idle.setValue((Boolean) false, Property.Source.DEFAULT);
                z = true;
            }
            if (!z) {
                this.idle.setValue((Boolean) true, Property.Source.DEFAULT);
            }
        }
        this.log.v("checkIdle started=" + allStarted() + " idle=" + this.idle.getValue());
    }

    private boolean isRegistered(String str) {
        boolean z;
        synchronized (this.managers) {
            Iterator<Manager> it = this.managers.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                if (it.next().getManagerName().equals(str)) {
                    z = true;
                    break;
                }
            }
        }
        return z;
    }

    private void registerMessages(MessageProcessor messageProcessor, int[] iArr) {
        this.log.d("RegisterMessages() " + messageProcessor.getClass());
        if (iArr != null) {
            for (int i : iArr) {
                this.messageHandlerMap.put(Integer.valueOf(i), new WeakReference<>(messageProcessor));
            }
        }
    }

    private void walkDependencyTree(Manager manager) {
        if (manager != null) {
            manager.incrementDependentCount();
            String[] dependencies = manager.getDependencies();
            if (dependencies != null) {
                for (String str : dependencies) {
                    walkDependencyTree(getManagerReference(str));
                }
            }
        }
    }

    public boolean addService(String str) {
        this.log.d("addService(" + str + ")");
        String managerForService = ManagerService.managerForService(str);
        if (managerForService == null) {
            return false;
        }
        addManager(managerForService);
        return false;
    }

    public boolean allStarted() {
        return this.allManagersStarted;
    }

    public boolean complete() {
        ArrayList<Manager> arrayList;
        this.log.d("ManagerRegistry.complete() Currently: [" + this.currentlyProcessing + "] valid: [" + this.validRegistry + "]");
        if (!this.currentlyProcessing) {
            return false;
        }
        this.log.d("ManagerRegistry.complete(): init happening");
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        for (Manager manager : arrayList) {
            manager.init();
            if (manager instanceof MessageProcessor) {
                registerMessages((MessageProcessor) manager, ((MessageProcessor) manager).getMessageIDs());
            }
        }
        calculateSortOrder();
        this.currentlyProcessing = false;
        this.validRegistry = true;
        return true;
    }

    public void deregister() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.managers.size()) {
                return;
            }
            Manager manager = this.managers.get(i2);
            if (manager != null) {
                manager.deregister();
            }
            i = i2 + 1;
        }
    }

    public void destroy() {
        ArrayList arrayList;
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= arrayList.size()) {
                return;
            }
            Manager manager = (Manager) arrayList.get(i2);
            if (manager != null) {
                manager.destroy();
            }
            i = i2 + 1;
        }
    }

    public void dispatchReceivedMessage(String str, String str2, Response response) {
        ArrayList<Manager> arrayList;
        CommandManager commandManager;
        List<Pair<String, String>> realTimeSubscriptions;
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        for (Manager manager : arrayList) {
            if ((manager instanceof CommandManager) && (realTimeSubscriptions = (commandManager = (CommandManager) manager).getRealTimeSubscriptions()) != null) {
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < realTimeSubscriptions.size()) {
                        if (((String) realTimeSubscriptions.get(i2).first).equals(str) && ((String) realTimeSubscriptions.get(i2).second).equals(str2)) {
                            commandManager.onResponse(response);
                        }
                        i = i2 + 1;
                    }
                }
            }
        }
    }

    public GenericProperty.BooleanProperty getIdleProperty() {
        return this.idle;
    }

    public Manager getManagerReference(String str) {
        synchronized (this.managers) {
            for (int i = 0; i < this.managers.size(); i++) {
                Manager manager = this.managers.get(i);
                if (manager != null && manager.getManagerName().equals(str)) {
                    return manager;
                }
            }
            return null;
        }
    }

    public Manager getManagerReferenceByClass(String str) {
        ArrayList<Manager> arrayList;
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        for (Manager manager : arrayList) {
            if (manager.getClass().toString().equals(str)) {
                return manager;
            }
        }
        return null;
    }

    public int getMinimumPollInterval() {
        ArrayList arrayList;
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        int intValue = this.client.getInteger(ConnectConfiguration.ConfigProperty.DEFAULT_POLLING_INTERVAL_NO_FEATURES).intValue();
        Iterator it = arrayList.iterator();
        while (true) {
            int i = intValue;
            if (!it.hasNext()) {
                return i;
            }
            Manager manager = (Manager) it.next();
            intValue = manager.getManagerPollInterval() < i ? manager.getManagerPollInterval() : i;
        }
    }

    public Manager getNextPendingManager() {
        ArrayList<Manager> arrayList;
        if (!this.validRegistry) {
            return null;
        }
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        for (Manager manager : arrayList) {
            if (manager.getManagerStartState() == AbstractCommandManager.ManagerState.STARTING) {
                return null;
            }
            if (manager.getManagerStartState() == AbstractCommandManager.ManagerState.DISABLED) {
                return manager;
            }
        }
        this.allManagersStarted = true;
        checkIdle();
        return null;
    }

    public boolean handleMessage(Message message) {
        MessageProcessor messageProcessor;
        Logger.getTrace().enterMethod("handleMessage");
        this.processingMessage = true;
        checkIdle();
        try {
            if (this.messageHandlerMap.containsKey(Integer.valueOf(message.what)) && this.messageHandlerMap.get(Integer.valueOf(message.what)) != null && (messageProcessor = this.messageHandlerMap.get(Integer.valueOf(message.what)).get()) != null) {
                if (messageProcessor.onHandleMessage(message)) {
                    return true;
                }
            }
            return false;
        } finally {
            Logger.getTrace().exitMethod("handleMessage");
            this.processingMessage = false;
            checkIdle();
        }
    }

    public boolean isCurrentlyProcessing() {
        return this.currentlyProcessing;
    }

    public boolean isValid() {
        return this.validRegistry;
    }

    public void postStart() {
        ArrayList arrayList;
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((Manager) it.next()).postStart();
        }
        versionUpgrade();
        this.bPostStartComplete = true;
    }

    public boolean rebind() {
        ArrayList arrayList;
        if (!this.validRegistry) {
            return false;
        }
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((Manager) it.next()).rebind();
        }
        return true;
    }

    public void setup(ConnectClient connectClient, boolean z) {
        this.client = connectClient;
        this.currentlyProcessing = true;
        this.validRegistry = false;
        this.allManagersStarted = false;
        this.bPostStartComplete = false;
        synchronized (this.managers) {
            this.managers.clear();
        }
    }

    public void versionUpdate(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        this.versionFrom = version;
        this.versionTo = version2;
        this.oemIdChange = z;
        if (this.bPostStartComplete) {
            versionUpgrade();
        }
    }

    protected void versionUpgrade() {
        ArrayList arrayList;
        if (this.versionFrom == null && this.versionTo == null) {
            return;
        }
        synchronized (this.managers) {
            arrayList = new ArrayList(this.managers);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((Manager) it.next()).onUpgrade(this.versionFrom, this.versionTo, this.oemIdChange);
        }
        this.versionFrom = null;
        this.versionTo = null;
    }
}
