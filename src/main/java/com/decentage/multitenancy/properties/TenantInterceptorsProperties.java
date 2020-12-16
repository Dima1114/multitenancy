package com.decentage.multitenancy.properties;

import com.decentage.multitenancy.model.MultiTenantSupport;
import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TenantInterceptorsProperties {

    private List<TenantProperties<?>> tenantProperties = new ArrayList<>();

    public static TenantInterceptorsProperties create() {
        return new TenantInterceptorsProperties();
    }

    public TenantInterceptorsProperties add(TenantProperties<?> properties) {
        tenantProperties.add(properties);
        return this;
    }

    @Builder
    @Getter
    public static class TenantProperties<T extends MultiTenantSupport> {
        private Class<T> supportType;
        private String tenantFieldName;
        private List<String> skipSigns;
        private boolean autoInsert;
    }
}
