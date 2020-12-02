package com.decentage.multitenancy;

import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.data.entity.TenantSingleDomain;
import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SaveOneToOneEntityTest extends BaseMultiTenantTest {

    @Test
    void shouldSaveEntitiesWithTheSameTenant() {

        //given
        val tenant = getTenant(0, TENANT);
        val single = getTenantSingle(0, TENANT, tenant);
        tenant.setSingle(single);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(tenant.getName(), tenant.getTenant());
        assertThat(result.getSingle())
                .isNotNull()
                .extracting(TenantSingleDomain::getName, TenantSingleDomain::getTenant)
                .containsExactly(single.getName(), single.getTenant());
    }

    @Test
    void shouldNotSaveEntitiesWithDifferentTenant() {

        //given
        val tenant = getTenant(0, TENANT);
        val single = getTenantSingle(0, TENANT_WRONG, tenant);
        tenant.setSingle(single);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(String.format("You don`t have permission to perform Multi Tenant action." +
                        " Object tenant is '%s', User tenant is '%s'.", TENANT_WRONG, TENANT));
    }

    @Test
    void shouldNotSaveEntitiesWithDifferentTenantSeparately() {

        //given
        val tenant = getTenant(0, TENANT);
        val single = getTenantSingle(0, TENANT_WRONG, tenant);

        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        tenantDomainRepository.save(tenant);

        MultiTenantContextHolder.setContext(TENANT_WRONG, TenantSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantSingleDomainRepository.save(single));

        //then
        assertThat(result)
                .hasMessageContaining(String.format("You don`t have permission to perform Multi Tenant action." +
                        " Object tenant is '%s', User tenant is '%s'.", TENANT, TENANT_WRONG));
    }
}
