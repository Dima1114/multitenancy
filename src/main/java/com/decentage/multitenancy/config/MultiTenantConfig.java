package com.decentage.multitenancy.config;

import com.decentage.multitenancy.interceptor.TenantInterceptor;
import com.decentage.multitenancy.interceptor.TenantInterceptorHandler;
import com.decentage.multitenancy.properties.TenantInterceptorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty("spting.multitenancy.enable")
@RequiredArgsConstructor
@EnableConfigurationProperties
@AutoConfigureOrder(1)
public class MultiTenantConfig {

    private final TenantInterceptorsProperties interceptorProperties;

    @Bean
    public List<TenantInterceptorHandler<?>> interceptorHandlers() {
        return interceptorProperties.getTenantProperties()
                .stream()
                .map(props -> new TenantInterceptorHandler<>(props))
                .collect(Collectors.toList());
    }

    @Bean
    public TenantInterceptor multiTenantInterceptor() {
        return new TenantInterceptor(interceptorHandlers());
    }
}
