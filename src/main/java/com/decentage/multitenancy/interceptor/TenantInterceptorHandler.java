package com.decentage.multitenancy.interceptor;

import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.model.MultiTenantSupport;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class TenantInterceptorHandler<T extends MultiTenantSupport> {

    private Class<T> supportType;
    private String tenantFieldName;
    private List<String> skipSigns;
    private Method getter;

    public TenantInterceptorHandler(TenantInterceptorsProperties.TenantProperties<T> properties) {
        this.supportType = properties.getSupportType();
        this.tenantFieldName = properties.getTenantFieldName();
        this.skipSigns = properties.getSkipSigns() != null ? properties.getSkipSigns() : Collections.emptyList();
        this.getter = findGetter(supportType);
    }

    public void checkPermissions(Object entity) {
        checkRelatedPermissions(entity, null);
    }

    private void checkRelatedPermissions(Object entity, Class<?> relatedClass) {
        if (supportType.isAssignableFrom(entity.getClass())) {
            Object entityTenant = getTenant(entity);
            Object tenant = MultiTenantContextHolder.getContext(supportType);
            verifyTenant(tenant, entityTenant);
            checkPermissionsForRelatedObjects(entity, relatedClass);
        }
    }

    public void checkStatePermissions(Object entity, Object[] state, String[] propertyNames) {
        if (supportType.isAssignableFrom(entity.getClass())) {
            Object entityTenant = getTenantFromFieldsArray(state, propertyNames);
            Object tenant = MultiTenantContextHolder.getContext(supportType);
            verifyTenant(tenant, entityTenant);
            checkPermissionsForRelatedObjectsOfStateArray(entity, state);
        }
    }

    public void checkMultiStatePermissions(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
        if (supportType.isAssignableFrom(entity.getClass())) {
            Object currentTenant = getTenantFromFieldsArray(currentState, propertyNames);
            Object previousTenant = getTenantFromFieldsArray(previousState, propertyNames);
            Object tenant = MultiTenantContextHolder.getContext(supportType);
            verifyTenant(tenant, previousTenant);
            verifyTenant(tenant, currentTenant);
            verifyTenantIsUnchanged(previousTenant, currentTenant);
            checkPermissionsForRelatedObjectsOfStateArray(entity, currentState);
        }
    }

    @SuppressWarnings("unchecked")
    public void checkCollectionPermissions(AbstractPersistentCollection collection, Object owner) {
        if (supportType.isAssignableFrom(owner.getClass())) {
            Object ownerTenant = getTenant(owner);
            ((Collection) collection).forEach(it -> checkElementPermissions(ownerTenant, it));
        }
    }

    private void checkElementPermissions(Object tenant, Object element) {
        if (supportType.isAssignableFrom(element.getClass())) {
            Object elementTenant = getTenant(element);
            if (!Objects.equals(tenant, elementTenant)) {
                throw new MultiTenantSupportException(
                        String.format("You can`t perform cascade operations with multiple tenants: %s, %s", tenant, elementTenant));
            }
            checkPermissionsForRelatedObjects(element, tenant.getClass());
        }
    }

    private boolean shouldSkip(Object tenant) {
        return ObjectUtils.isNotEmpty(tenant) &&
                skipSigns.stream().anyMatch(sign -> Objects.equals(sign, tenant));
    }

    private void verifyTenant(Object userTenant, Object entityTenant) {
        if (ObjectUtils.isEmpty(entityTenant)) {
            throw new MultiTenantSupportException(
                    String.format("You can`t have entity of type '%s' without providing '%s' field value",
                            supportType.getName(), tenantFieldName));
        }
        if (!shouldSkip(userTenant) && !Objects.equals(entityTenant, userTenant)) {
            throw new MultiTenantSupportException(
                    String.format("You don`t have permission to perform Multi Tenant action." +
                            " Object tenant is '%s', User tenant is '%s'.", entityTenant, userTenant));
        }
    }

    private void verifyTenantIsUnchanged(Object previousTenant, Object currentTenant) {
        if (!Objects.equals(previousTenant, currentTenant)) {
            throw new MultiTenantSupportException(
                    String.format("You can`t change saved tenant during update." +
                            " Previous tenant is '%s', Current tenant is '%s'.", previousTenant, currentTenant));
        }
    }

    @SneakyThrows
    private Object getTenant(Object entity) {
        T tenantSupportEntity = supportType.cast(entity);
        return getter.invoke(tenantSupportEntity);
    }

    private Object getTenantFromFieldsArray(Object[] state, String[] propertyNames) {
        return IntStream.range(0, propertyNames.length)
                .filter(i -> propertyNames[i].equals(tenantFieldName))
                .mapToObj(i -> state[i])
                .findFirst()
                .orElseThrow(() -> new MultiTenantSupportException(
                        String.format("Can not find field '%s' in entity that implements '%s'",
                                tenantFieldName, supportType.getName())));
    }

    private void checkPermissionsForRelatedObjects(Object entity, Class<?> relatedClass) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> supportType.isAssignableFrom(field.getType()))
                .filter(field -> !field.getType().equals(relatedClass))
                .peek(field -> field.setAccessible(true))
                .map(field -> ReflectionUtils.getField(field, entity))
                .filter(Objects::nonNull)
                .filter(field -> !(field instanceof HibernateProxy))
                .forEach(field -> this.checkRelatedPermissions(field, entity.getClass()));
    }

    private void checkPermissionsForRelatedObjectsOfStateArray(Object entity, Object[] state) {
        Arrays.stream(state)
                .filter(Objects::nonNull)
                .filter(field -> !(field instanceof HibernateProxy))
                .filter(field -> supportType.isAssignableFrom(field.getClass()))
                .forEach(field -> this.checkRelatedPermissions(field, entity.getClass()));
    }

    private Method findGetter(Class<T> supportType) {
        val getterName = "get" + StringUtils.capitalize(tenantFieldName);
        return Optional.ofNullable(BeanUtils.findDeclaredMethod(supportType, getterName))
                .orElseThrow(() -> new MultiTenantSupportException(
                        String.format("Provided type '%s' does not contain Getter method with name '%s'" +
                                        "for provided field name '%s'",
                                supportType.getName(), getterName, tenantFieldName)));
    }
}
