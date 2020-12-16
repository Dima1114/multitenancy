package com.decentage.multitenancy.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.type.Type;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TenantInterceptor extends EmptyInterceptor {

    private final List<TenantInterceptorHandler<?>> tenantHandlers;
    private final List<TenantInsertHandler<?>> insertHandlers;

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        val modified = insertHandlers.stream()
                .map(handler -> handler.setTenant(entity, state, propertyNames))
                .collect(Collectors.toList())
                .contains(true);
        tenantHandlers.forEach(handler -> handler.checkPermissions(entity));
        return modified;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        tenantHandlers.forEach(handler ->
                handler.checkMultiStatePermissions(entity, currentState, previousState, propertyNames));
        return false;
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        tenantHandlers.forEach(handler -> handler.checkStatePermissions(entity, state, propertyNames));
        return super.onLoad(entity, id, state, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        tenantHandlers.forEach(handler -> handler.checkPermissions(entity));
    }

    @Override
    public void onCollectionRemove(Object collection, Serializable key) {
        Object owner = ((AbstractPersistentCollection) collection).getOwner();
        tenantHandlers.forEach(handler ->
                handler.checkCollectionPermissions((AbstractPersistentCollection) collection, owner));
        super.onCollectionRemove(collection, key);
    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) {
        Object owner = ((AbstractPersistentCollection) collection).getOwner();
        tenantHandlers.forEach(handler ->
                handler.checkCollectionPermissions((AbstractPersistentCollection) collection, owner));
        super.onCollectionRecreate(collection, key);
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) {
        Object owner = ((AbstractPersistentCollection) collection).getOwner();
        tenantHandlers.forEach(handler ->
                handler.checkCollectionPermissions((AbstractPersistentCollection) collection, owner));
        super.onCollectionUpdate(collection, key);
    }

    @Override
    public void preFlush(Iterator entities) {
        super.preFlush(entities);
    }

    @Override
    public String onPrepareStatement(String sql) {
        return super.onPrepareStatement(sql);
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        super.beforeTransactionCompletion(tx);
    }
}
