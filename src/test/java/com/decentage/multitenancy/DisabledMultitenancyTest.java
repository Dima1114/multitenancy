package com.decentage.multitenancy;

import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spting.multitenancy.enable=false"})
class DisabledMultitenancyTest extends BaseMultiTenantTest {

    @Test
    void shouldSaveTenantEntityWithoutTenant() {

        //given
        val tenant = getTenant(0, null);
        MultiTenantContextHolder.clearContext(TenantSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly("tenant_0", null);

    }

    @Test
    void shouldGetTenantEntityWithoutTenant() {

        //given
        val tenant = saveTenant(0, null);
        MultiTenantContextHolder.clearContext(TenantSupport.class);

        //when
        val result = tenantDomainRepository.findById(tenant.getId());

        //then
        assertThat(result.get())
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly("tenant_0", null);

    }
}
