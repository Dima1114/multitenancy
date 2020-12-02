package com.decentage.multitenancy.config;

import com.decentage.multitenancy.interceptor.TenantInterceptor;
import com.decentage.multitenancy.factory.MetadataExtractorIntegrator;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import com.decentage.multitenancy.translator.QueryTranslatorFactoryImpl;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.Map;

@Component
@ConditionalOnProperty("spting.multitenancy.enable")
@RequiredArgsConstructor
@AutoConfigureOrder(2)
public class HibernateInterceptorRegistration implements HibernatePropertiesCustomizer {

    private final TenantInterceptor tenantInterceptor;
    private final TenantInterceptorsProperties interceptorProperties;
    private final QueryTranslatorFactoryImpl translatorFactory;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", tenantInterceptor);
        hibernateProperties.put("hibernate.query.factory_class", translatorFactory);
//        hibernateProperties.put("hibernate.current_session_context_class", "thread"); //not safe
        hibernateProperties.put("hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(
                        new MetadataExtractorIntegrator(interceptorProperties)));
//        );
    }
}
