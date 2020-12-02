package com.decentage.multitenancy.translator;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import java.util.Map;

public class MultiTenantQueryTranslatorImpl extends QueryTranslatorImpl {

    public MultiTenantQueryTranslatorImpl(String queryIdentifier,
                                          String query,
                                          Map enabledFilters,
                                          SessionFactoryImplementor factory) {
        super(queryIdentifier, query, enabledFilters, factory);
    }

    public MultiTenantQueryTranslatorImpl(String queryIdentifier,
                                          String query,
                                          Map enabledFilters,
                                          SessionFactoryImplementor factory,
                                          EntityGraphQueryHint entityGraphQueryHint) {
        super(queryIdentifier, query, enabledFilters, factory, entityGraphQueryHint);
    }

    @Override
    public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) {
        //TODO handle multi tenant updates somehow
        return super.executeUpdate(queryParameters, session);
    }
}
