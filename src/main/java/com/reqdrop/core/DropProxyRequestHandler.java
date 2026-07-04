package com.reqdrop.core;

import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import com.reqdrop.model.DropLogEntry;
import com.reqdrop.model.DropRule;

import java.util.Optional;

/** Drops Proxy requests that match an enabled rule; fails open on any error. */
public final class DropProxyRequestHandler implements ProxyRequestHandler {

    private final RuleStore store;
    private final DropLog dropLog;
    private final Logging logging;

    public DropProxyRequestHandler(RuleStore store, DropLog dropLog, Logging logging) {
        this.store = store;
        this.dropLog = dropLog;
        this.logging = logging;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        try {
            if (!store.isEnabled()) {
                return ProxyRequestReceivedAction.continueWith(interceptedRequest);
            }
            String host = interceptedRequest.httpService().host();
            String path = interceptedRequest.pathWithoutQuery();
            Optional<DropRule> match = store.firstMatch(host, path);
            if (match.isPresent()) {
                DropRule rule = match.get();
                store.incrementHit(rule.id());
                dropLog.add(new DropLogEntry(
                        System.currentTimeMillis(),
                        interceptedRequest.method(),
                        host,
                        interceptedRequest.url(),
                        rule.displayLabel()));
                return ProxyRequestReceivedAction.drop();
            }
            return ProxyRequestReceivedAction.continueWith(interceptedRequest);
        } catch (RuntimeException e) {
            // Fail open: never drop a request because of an internal error.
            logging.logToError("ReqDrop: error evaluating request, passing through: " + e);
            return ProxyRequestReceivedAction.continueWith(interceptedRequest);
        }
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}
