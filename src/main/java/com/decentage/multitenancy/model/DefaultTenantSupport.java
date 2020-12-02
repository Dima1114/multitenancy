package com.decentage.multitenancy.model;

public interface DefaultTenantSupport extends MultiTenantSupport {

    void setTenant(Object tenant);

    Object getTenant();
}
