package com.decentage.multitenancy.utils;

import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.model.MultiTenantSupport;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.Method;
import java.util.Optional;

@UtilityClass
public class TenantExtractorUtil {

    public <T extends MultiTenantSupport> Method findGetter(Class<T> supportType, String tenantFieldName) {
        val getterName = "get" + StringUtils.capitalize(tenantFieldName);
        return Optional.ofNullable(BeanUtils.findDeclaredMethod(supportType, getterName))
                .orElseThrow(() -> new MultiTenantSupportException(
                        String.format("Provided type '%s' does not contain Getter method with name '%s'" +
                                        "for provided field name '%s'",
                                supportType.getName(), getterName, tenantFieldName)));
    }

    public <T extends MultiTenantSupport> Method findSetter(Class<T> supportType, String tenantFieldName) {
        val setterName = "set" + StringUtils.capitalize(tenantFieldName);
        return Optional.ofNullable(BeanUtils.findDeclaredMethod(supportType, setterName, String.class))
                .orElseThrow(() -> new MultiTenantSupportException(
                        String.format("Provided type '%s' does not contain Setter method with name '%s'" +
                                        "for provided field name '%s'",
                                supportType.getName(), setterName, tenantFieldName)));
    }

    @SneakyThrows
    public <T extends MultiTenantSupport> String getTenant(Object entity, Class<T> supportType, Method getter) {
        T tenantSupportEntity = supportType.cast(entity);
        return (String) getter.invoke(tenantSupportEntity);
    }

    @SneakyThrows
    public <T extends MultiTenantSupport> void setTenant(Object entity, String tenant,
                                                         Class<T> supportType, Method setter) {
        T tenantSupportEntity = supportType.cast(entity);
        setter.invoke(tenantSupportEntity, tenant);
    }
}
