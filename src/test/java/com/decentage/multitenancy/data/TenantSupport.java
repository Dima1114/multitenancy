package com.decentage.multitenancy.data;

import com.decentage.multitenancy.model.MultiTenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

public interface TenantSupport extends MultiTenantSupport {

    void setTenant(Object tenant);

    Object getTenant();
}
