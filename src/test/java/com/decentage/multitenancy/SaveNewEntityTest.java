package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantClientDomain;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SaveNewEntityTest extends BaseMultiTenantTest {

    @Test
    void shouldSaveEntityWithTenantField() {

        //given
        val tenant = getTenant(0, TENANT);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly("tenant_0", TENANT);
    }

    @Test
    void shouldFailToSaveEntityWithTenantField() {

        //given
        val tenant = getTenant(0, TENANT);
        MultiTenantContextHolder.setContext(TENANT_WRONG, TenantSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining("You don`t have permission to perform Multi Tenant action.");
    }

    @Test
    void shouldSaveEntityWithTenantAndClientField() {

        //given
        val tenant = getTenantClient(0, TENANT, CLIENT);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        //when
        val result = tenantClientDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantClientDomain::getName, TenantClientDomain::getTenant, TenantClientDomain::getClient)
                .containsExactly("tenant_client_0", TENANT, CLIENT);
    }

    @Test
    void shouldFailToSaveEntityWithTenantAndClientField() {

        //given
        val tenant = getTenantClient(0, TENANT, CLIENT);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT_WRONG, ClientSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantClientDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining("You don`t have permission to perform Multi Tenant action.");
    }

    @Test
    void shouldFailToSaveEntityWithoutTenantDescriptor() {

        //given
        val tenant = getTenant(0, null);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You can`t have entity of type '%s' without providing '%s' field value",
                        TenantSupport.class.getName(), "tenant"));
    }

    @Test
    void shouldSaveEntityWithTenantDescriptorAndSkipPermissions() {

        //given
        val tenant = getTenant(0, TENANT);
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly("tenant_0", TENANT);
    }
}
