package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.config.AppConfig;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
public class CacheTest extends BaseMultiTenantTest {

    public static final String TENANT_CACHE = "tenantDomainList";

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCacheListResultWithCurrentTenant() {

        saveTenant(0, TENANT);
        saveTenant(0, TENANT_WRONG);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //should cache result
        tenantDomainRepository.findAllByName("tenant_0");

        //when
        val cachedResult = tenantDomainRepository.findAllByName("tenant_0");

        //then
        assertThat(cachedResult)
                .hasSize(1)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(tuple("tenant_0", TENANT));

    }

    @Test
    @SuppressWarnings("all")
    void shouldCacheListInTenantCache() {

        saveTenant(0, TENANT);
        saveTenant(0, TENANT_WRONG);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //should cache result
        tenantDomainRepository.findAllByName("tenant_0");

        //when
        val cache = cacheManager.getCache(TENANT_CACHE);

        //then
        assertThat(cache.get("tenant_0").get())
                .isNotNull()
                .asList()
                .hasSize(1)
                .extracting(o -> ((TenantDomain) o).getName(), o -> ((TenantDomain) o).getTenant())
                .containsExactly(tuple("tenant_0", TENANT));

    }

    @Test
    @SuppressWarnings("all")
    void shouldCacheListInTenantCacheUsingCurrentContextAsKey() {

        saveTenant(0, TENANT);
        saveTenant(0, TENANT_WRONG);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //should cache result
        tenantDomainRepository.getByName("tenant_0");

        //when
        val cache = cacheManager.getCache(TENANT_CACHE);

        //then
        assertThat(cache.get(Arrays.asList("tenant_0", TENANT)).get())
                .isNotNull()
                .asList()
                .hasSize(1)
                .extracting(o -> ((TenantDomain) o).getName(), o -> ((TenantDomain) o).getTenant())
                .containsExactly(tuple("tenant_0", TENANT));

    }

    @TestConfiguration
    @EnableCaching
    public static class CacheConfig {
        @Bean
        public CacheManager cacheManager() {
            val cacheManager = new SimpleCacheManager();
            val cacheList = new ArrayList<Cache>();
            cacheList.add(new ConcurrentMapCache(TENANT_CACHE));
            //so like that you can create as many as you want
            cacheManager.setCaches(cacheList);
            return cacheManager;
        }
    }
}
