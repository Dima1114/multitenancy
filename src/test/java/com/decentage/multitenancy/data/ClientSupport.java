package com.decentage.multitenancy.data;

import com.decentage.multitenancy.model.MultiTenantSupport;

public interface ClientSupport extends MultiTenantSupport {

    void setClient(Object client);

    Object getClient();
}
