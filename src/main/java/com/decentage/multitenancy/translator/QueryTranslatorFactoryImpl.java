package com.decentage.multitenancy.translator;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class QueryTranslatorFactoryImpl implements QueryTranslatorFactory {

    @Override
    public QueryTranslator createQueryTranslator(
            String queryIdentifier,
            String queryString,
            Map filters,
            SessionFactoryImplementor factory,
            EntityGraphQueryHint entityGraphQueryHint) {
        return new MultiTenantQueryTranslatorImpl(queryIdentifier, queryString, filters, factory, entityGraphQueryHint);
    }

    @Override
    public FilterTranslator createFilterTranslator(
            String queryIdentifier,
            String queryString,
            Map filters,
            SessionFactoryImplementor factory) {
        return new MultiTenantQueryTranslatorImpl(queryIdentifier, queryString, filters, factory);
    }
}
