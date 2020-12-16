package com.decentage.multitenancy.interceptor;

import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.model.MultiTenantSupport;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import com.decentage.multitenancy.utils.TenantExtractorUtil;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class TenantInsertHandler<T extends MultiTenantSupport> {

    private Class<T> supportType;
    private String tenantFieldName;
    private List<String> skipSigns;
    private boolean autoInsert;
    private Method getter;
    private Method setter;

    public TenantInsertHandler(TenantInterceptorsProperties.TenantProperties<T> properties) {
        this.supportType = properties.getSupportType();
        this.tenantFieldName = properties.getTenantFieldName();
        this.skipSigns = properties.getSkipSigns() != null ? properties.getSkipSigns() : Collections.emptyList();
        this.autoInsert = properties.isAutoInsert();
        this.getter = TenantExtractorUtil.findGetter(this.supportType, this.tenantFieldName);
        this.setter = TenantExtractorUtil.findSetter(this.supportType, this.tenantFieldName);
    }

    public boolean setTenant(Object entity, Object[] state, String[] propertyNames) {
        if (!autoInsert) {
            return false;
        }

        if (supportType.isAssignableFrom(entity.getClass())) {
            var tenant = TenantExtractorUtil.getTenant(entity, supportType, getter);
            if (!shouldSkip(tenant)) {
                tenant = MultiTenantContextHolder.getContext(supportType);
                val tenantPosition = findTenantPosition(propertyNames);
                state[tenantPosition] = tenant;
                TenantExtractorUtil.setTenant(entity, tenant, supportType, setter);
                return true;
            }
        }
        return false;
    }

    private boolean shouldSkip(Object tenant) {
        return ObjectUtils.isNotEmpty(tenant) ||
                skipSigns.stream().anyMatch(sign -> Objects.equals(sign, tenant));
    }

    private int findTenantPosition(String[] propertyNames) {
        return IntStream.range(0, propertyNames.length)
                .filter(i -> propertyNames[i].equals(tenantFieldName))
                .findFirst()
                .orElseThrow(() -> new MultiTenantSupportException(
                        String.format("Can not find field '%s' in entity that implements '%s'",
                                tenantFieldName, supportType.getName())));
    }

}
