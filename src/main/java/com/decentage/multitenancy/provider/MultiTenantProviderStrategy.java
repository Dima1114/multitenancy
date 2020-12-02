package com.decentage.multitenancy.provider;

import com.decentage.multitenancy.model.MultiTenantSupport;

public interface MultiTenantProviderStrategy {

    void clearContext(Class<? extends MultiTenantSupport> type);

    Object getContext(Class<? extends MultiTenantSupport> type);

    void setContext(Object context, Class<? extends MultiTenantSupport> type);
}
