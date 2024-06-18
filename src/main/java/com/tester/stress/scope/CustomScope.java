package com.tester.stress.scope;

import jakarta.servlet.ServletContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomScope implements Scope {
    private static final ThreadLocal<Map<String, Object>> jmsScopedObjects = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Runnable>> jmsDestructionCallbacks = ThreadLocal.withInitial(HashMap::new);
    private final ServletContext servletContext;

    @NonNull
    @Override
    public Object get(@NonNull String name, @NonNull ObjectFactory<?> objectFactory) {
        log.atWarn().log("get, " + name);
        if (isHttpRequest()) {
            return getHttpScopedObject(name, objectFactory);
        } else {
            return jmsScopedObjects.get().computeIfAbsent(name, k -> objectFactory.getObject());
        }
    }

    @Override
    public Object remove(@NonNull String name) {
        log.atWarn().log("remove, " + name);
        if (isHttpRequest()) {
            return removeHttpScopedObject(name);
        } else {
            jmsDestructionCallbacks.get().remove(name);
            return jmsScopedObjects.get().remove(name);
        }
    }

    @Override
    public void registerDestructionCallback(@NonNull String name, @NonNull Runnable callback) {
        log.atWarn().log("registerDestructionCallback, " + name);
        if (isHttpRequest()) {
            registerHttpDestructionCallback(name, callback);
        } else {
            jmsDestructionCallbacks.get().put(name, callback);
        }
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return isHttpRequest() ? "http" : "jmsMessage";
    }

    private boolean isHttpRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    private Object getHttpScopedObject(String name, ObjectFactory<?> objectFactory) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        Map<String, Object> scopedObjects = getScopedObjects(requestAttributes);
        return scopedObjects.computeIfAbsent(name, k -> objectFactory.getObject());
    }

    private Object removeHttpScopedObject(String name) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        Map<String, Object> scopedObjects = getScopedObjects(requestAttributes);
        return scopedObjects.remove(name);
    }

    private void registerHttpDestructionCallback(String name, Runnable callback) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        Map<String, Runnable> destructionCallbacks = getDestructionCallbacks(requestAttributes);
        destructionCallbacks.put(name, callback);
    }

    private Map<String, Object> getScopedObjects(RequestAttributes requestAttributes) {
        Map<String, Object> scopedObjects = (Map<String, Object>) requestAttributes.getAttribute("customScope", RequestAttributes.SCOPE_REQUEST);
        if (scopedObjects == null) {
            scopedObjects = new HashMap<>();
            requestAttributes.setAttribute("customScope", scopedObjects, RequestAttributes.SCOPE_REQUEST);
        }
        return scopedObjects;
    }

    private Map<String, Runnable> getDestructionCallbacks(RequestAttributes requestAttributes) {
        Map<String, Runnable> destructionCallbacks = (Map<String, Runnable>) requestAttributes.getAttribute("customScope.destructionCallbacks", RequestAttributes.SCOPE_REQUEST);
        if (destructionCallbacks == null) {
            destructionCallbacks = new HashMap<>();
            requestAttributes.setAttribute("customScope.destructionCallbacks", destructionCallbacks, RequestAttributes.SCOPE_REQUEST);
        }
        return destructionCallbacks;
    }

    public void startJmsScope() {
        log.atError().log("startJmsScope");
        jmsScopedObjects.set(new HashMap<>());
        jmsDestructionCallbacks.set(new HashMap<>());
    }

    public void endJmsScope() {
        log.atError().log("endJmsScope");
        jmsDestructionCallbacks.get().values().forEach(Runnable::run);
        jmsDestructionCallbacks.remove();
        jmsScopedObjects.remove();
    }
}
