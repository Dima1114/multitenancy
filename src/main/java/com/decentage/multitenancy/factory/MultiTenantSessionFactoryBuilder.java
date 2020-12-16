package com.decentage.multitenancy.factory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.internal.SessionFactoryBuilderImpl;
import org.hibernate.boot.internal.SessionFactoryOptionsBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.bytecode.internal.SessionFactoryObserverForBytecodeEnhancer;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;

public class MultiTenantSessionFactoryBuilder extends SessionFactoryBuilderImpl {

    private final MetadataImplementor metadata;

    public MultiTenantSessionFactoryBuilder(MetadataImplementor metadata,
                                            SessionFactoryBuilderImplementor sessionFactoryBuilderImplementor) {
        super(metadata, optionsBuilder(sessionFactoryBuilderImplementor));
        this.metadata = metadata;
    }

    @Override
    public SessionFactory build() {
        metadata.validate();
        final StandardServiceRegistry serviceRegistry = metadata.getMetadataBuildingOptions().getServiceRegistry();
        BytecodeProvider bytecodeProvider = serviceRegistry.getService(BytecodeProvider.class);
        addSessionFactoryObservers(new SessionFactoryObserverForBytecodeEnhancer(bytecodeProvider));
        return new MultiTenantSessionFactoryImpl(metadata, buildSessionFactoryOptions());
    }

    private static SessionFactoryOptionsBuilder optionsBuilder(SessionFactoryBuilderImplementor sessionFactoryBuilderImplementor) {
        Field field = ReflectionUtils.findField(sessionFactoryBuilderImplementor.getClass(), "optionsBuilder");
        field.setAccessible(true);
        return (SessionFactoryOptionsBuilder) ReflectionUtils.getField(field, sessionFactoryBuilderImplementor);
    }
}
