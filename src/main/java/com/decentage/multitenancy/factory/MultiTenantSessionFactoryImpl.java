package com.decentage.multitenancy.factory;

import lombok.val;
import org.hibernate.Session;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.util.ReflectionUtils;
import java.util.Map;

public class MultiTenantSessionFactoryImpl extends SessionFactoryImpl {

    private boolean isInitialized = false;

    public MultiTenantSessionFactoryImpl(MetadataImplementor metadata,
                                         SessionFactoryOptions options,
                                         QueryPlanCache.QueryPlanCreator queryPlanCacheFunction) {
        super(metadata, options, queryPlanCacheFunction);
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    @SuppressWarnings("all")
    public void addFilterDefinition(String name, FilterDefinition filterDef) {
        val filtersField = ReflectionUtils.findField(getClass().getSuperclass(), "filters");
        filtersField.setAccessible(true);
        val filters = (Map<String, FilterDefinition>) ReflectionUtils.getField(filtersField, this);
        filters.put(name, filterDef);
    }

    @Override
    public Session createEntityManager() {
        val session = super.createEntityManager();
        addFilters(session);
        return session;
    }

    @Override
    public Session openSession() {
        val session = super.openSession();
        addFilters(session);
        session.addEventListeners();
        return session;
    }

    private void addFilters(Session session) {
        if (this.isInitialized) {
            MetadataContextHolder.getMetadata()
                    .forEach((type, filter) -> MultiTenantFilterUtil.enableTenantFilter(session, type, filter));
        }
    }


}
