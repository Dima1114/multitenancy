package com.decentage.multitenancy.factory;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;

public class MultiTenantSessionFactoryBuilderFactory implements SessionFactoryBuilderFactory {

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor metadata,
                                                          SessionFactoryBuilderImplementor defaultBuilder) {
        return new MultiTenantSessionFactoryBuilder(metadata, defaultBuilder);
    }
}
