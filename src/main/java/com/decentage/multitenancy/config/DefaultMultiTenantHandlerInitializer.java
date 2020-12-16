package com.decentage.multitenancy.config;

import com.decentage.multitenancy.model.DefaultTenantSupport;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("spting.multitenancy.enable")
@AutoConfigureOrder
public class DefaultMultiTenantHandlerInitializer {

    @Bean
    @ConditionalOnMissingBean
    public TenantInterceptorsProperties interceptorsProperties() {
        return TenantInterceptorsProperties.create()
                .add(TenantInterceptorsProperties.TenantProperties.<DefaultTenantSupport>builder()
                        .supportType(DefaultTenantSupport.class)
                        .tenantFieldName("tenant")
                        .autoInsert(false)
                        .build()
                );
    }
}
