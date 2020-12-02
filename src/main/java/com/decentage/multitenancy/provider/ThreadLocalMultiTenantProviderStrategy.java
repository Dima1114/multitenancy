package com.decentage.multitenancy.provider;

import com.decentage.multitenancy.model.MultiTenantSupport;
import lombok.val;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThreadLocalMultiTenantProviderStrategy implements MultiTenantProviderStrategy {

    private static final ThreadLocal<Map<Class<? extends MultiTenantSupport>, Object>> tenantHolder = new ThreadLocal<>();

    @Override
    public void clearContext(Class<? extends MultiTenantSupport> type) {
        Optional.ofNullable(tenantHolder.get())
                .ifPresent(tenantMap -> tenantMap.remove(type));
    }

    @Override
    public Object getContext(Class<? extends MultiTenantSupport> type) {
        return Optional.ofNullable(tenantHolder.get())
                .map(tenantMap -> tenantMap.get(type))
                .orElse(null);
    }

    @Override
    public void setContext(Object context, Class<? extends MultiTenantSupport> type) {
        Optional.ofNullable(tenantHolder.get())
                .ifPresentOrElse(
                        contextMap -> contextMap.put(type, context),
                        () -> {
                            val contextMap = new HashMap<Class<? extends MultiTenantSupport>, Object>();
                            contextMap.put(type, context);
                            tenantHolder.set(contextMap);
                        });
    }

}
