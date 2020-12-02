package com.decentage.multitenancy.data.config;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EntityScan("com.decentage.multitenancy.data.entity")
@EnableJpaRepositories(basePackages = "com.decentage.multitenancy.data")
public class AppConfig {

    //TODO test multitenancy is disabled - done
    //TODO test cache + filters - looks like everything is ok
    //TODO change filters during one session. add Listener to ContextHolder - do we need it? I think no

    @Bean
    public TenantInterceptorsProperties interceptorsProperties() {
        return TenantInterceptorsProperties.create()
                .add(TenantInterceptorsProperties.TenantProperties.<TenantSupport>builder()
                        .supportType(TenantSupport.class)
                        .tenantFieldName("tenant")
                        .skipSigns(Collections.singletonList(Skip.SKIP_TENANT))
                        .build())
                .add(TenantInterceptorsProperties.TenantProperties.<ClientSupport>builder()
                        .supportType(ClientSupport.class)
                        .tenantFieldName("client")
                        .skipSigns(Arrays.asList(Skip.SKIP_TENANT, Skip.SKIP_CLIENT))
                        .build());
    }


}
