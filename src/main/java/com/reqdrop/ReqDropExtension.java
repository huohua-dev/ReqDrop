package com.reqdrop;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.reqdrop.core.DropLog;
import com.reqdrop.core.DropProxyRequestHandler;
import com.reqdrop.core.RuleStore;
import com.reqdrop.persist.MontoyaKeyValueStore;
import com.reqdrop.persist.RulePersistence;
import com.reqdrop.ui.ReqDropTab;

import javax.swing.SwingUtilities;

public class ReqDropExtension implements BurpExtension {

    private static final int DROP_LOG_CAPACITY = 2000;

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("ReqDrop");
        Logging logging = api.logging();

        MontoyaKeyValueStore persistence = new MontoyaKeyValueStore(api.persistence().extensionData());
        RuleStore store = new RuleStore(logging::logToError);

        // Load persisted state BEFORE attaching the save listener to avoid an echo write.
        RulePersistence.Loaded loaded = RulePersistence.load(persistence);
        store.setRules(loaded.rules());
        store.setEnabled(loaded.masterEnabled());

        store.addChangeListener(() -> {
            try {
                RulePersistence.save(persistence, store.isEnabled(), store.rules());
            } catch (RuntimeException e) {
                logging.logToError("ReqDrop: failed to save rules: " + e);
            }
        });

        DropLog dropLog = new DropLog(DROP_LOG_CAPACITY);
        api.proxy().registerRequestHandler(new DropProxyRequestHandler(store, dropLog, logging));

        SwingUtilities.invokeLater(() -> {
            ReqDropTab tab = new ReqDropTab(store, dropLog);
            api.userInterface().applyThemeToComponent(tab.component());
            api.userInterface().registerSuiteTab("ReqDrop", tab.component());
        });

        logging.logToOutput("ReqDrop loaded (" + loaded.rules().size() + " rule(s), master "
                + (loaded.masterEnabled() ? "enabled" : "disabled") + ").");
    }
}
