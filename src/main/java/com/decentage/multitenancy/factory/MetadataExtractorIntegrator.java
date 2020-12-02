package com.decentage.multitenancy.factory;

import com.decentage.multitenancy.model.MultiTenantSupport;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import java.util.HashMap;

@RequiredArgsConstructor
public class MetadataExtractorIntegrator implements Integrator {

    private final TenantInterceptorsProperties interceptorProperties;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        if (sessionFactory instanceof MultiTenantSessionFactoryImpl) {
            val factory = (MultiTenantSessionFactoryImpl) sessionFactory;
            interceptorProperties.getTenantProperties().forEach(props -> addFilters(factory, metadata, props));
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }

    private void addFilters(MultiTenantSessionFactoryImpl factory, Metadata metadata,
                            TenantInterceptorsProperties.TenantProperties<? extends MultiTenantSupport> properties) {
        val filterName = "filter" + properties.getSupportType().getSimpleName();
        val filterCondition = properties.getTenantFieldName() + " = :" + properties.getTenantFieldName();

        val params = new HashMap<String, Type>();
        params.put(properties.getTenantFieldName(), StringType.INSTANCE);
        val filterDef = new FilterDefinition(filterName, "", params);

        factory.addFilterDefinition(filterName, filterDef);

        metadata.getEntityBindings().stream()
                .filter(pc -> properties.getSupportType().isAssignableFrom(pc.getMappedClass()))
                .forEach(pc -> pc.addFilter(filterName, filterCondition, true, new HashMap<>(), new HashMap<>()));

        metadata.getCollectionBindings().stream()
                .filter(c -> properties.getSupportType().isAssignableFrom(c.getElement().getType().getReturnedClass()))
                .forEach(c -> {
                    if(ManyToOne.class.isAssignableFrom(c.getElement().getClass())){
                        c.addManyToManyFilter(filterName, filterCondition, true, new HashMap<>(), new HashMap<>());
                    }else {
                        c.addFilter(filterName, filterCondition, true, new HashMap<>(), new HashMap<>());
                    }
                });

        MetadataContextHolder.put(properties.getSupportType(), filterName, properties.getTenantFieldName(), properties.getSkipSigns());
    }
}
