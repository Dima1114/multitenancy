package com.decentage.multitenancy.data;

import com.decentage.multitenancy.model.MultiTenantSupport;

public interface TenantSupport extends MultiTenantSupport {

    void setTenant(String tenant);

    String getTenant();
}
