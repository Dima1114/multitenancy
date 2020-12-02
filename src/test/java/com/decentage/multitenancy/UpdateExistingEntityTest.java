package com.decentage.multitenancy;

import com.decentage.multitenancy.config.MultiTenantConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UpdateExistingEntityTest extends BaseMultiTenantTest {

    @Test
    void shouldUpdateExistingTenant() {
        //given
        val tenant = saveTenant(0, TENANT);
        tenant.setName("New tenant name");

        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly("New tenant name", TENANT);
    }

    @Test
    void shouldUpdateEntityWithTenantAndClientFields() {

        //given
        val tenant = saveTenantClient(0, TENANT, CLIENT);
        tenant.setName("new_name");
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        //when
        val result = tenantClientDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantClientDomain::getName, TenantClientDomain::getTenant, TenantClientDomain::getClient)
                .containsExactly("new_name", TENANT, CLIENT);
    }

    @Test
    void shouldFailToUpdateExistingTenant() {
        //given
        val tenant = saveTenant(0, TENANT);
        tenant.setName("New tenant name");

        MultiTenantContextHolder.setContext(TENANT_WRONG, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(String.format("You don`t have permission to perform Multi Tenant action." +
                        " Object tenant is '%s', User tenant is '%s'.", TENANT, TENANT_WRONG));
    }

    @Test
    void shouldFailToUpdateEntityWithTenantAndWrongClientField() {

        //given
        val tenant = saveTenantClient(0, TENANT, CLIENT);
        tenant.setName("new_name");
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT_WRONG, ClientSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantClientDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You don`t have permission to perform Multi Tenant action." +
                                " Object tenant is '%s', User tenant is '%s'.", CLIENT, CLIENT_WRONG));
    }

    @Test
    void shouldFailToChangeTenantDescriptorForExistingTenantEntity() {
        //given
        val tenant = saveTenant(0, TENANT);
        tenant.setTenant(TENANT_WRONG);

        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //when
        val result = assertThrows(TransactionSystemException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .getRootCause()
                .hasMessageContaining(String.format("You don`t have permission to perform Multi Tenant action." +
                        " Object tenant is '%s', User tenant is '%s'.", TENANT_WRONG, TENANT));
    }

    @Test
    void shouldFailToChangeExistingTenantEntityDescriptorToOwn() {
        //given
        val tenant = saveTenant(0, TENANT);
        tenant.setTenant(TENANT_WRONG);

        MultiTenantContextHolder.setContext(TENANT_WRONG, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You don`t have permission to perform Multi Tenant action." +
                                " Object tenant is '%s', User tenant is '%s'.", TENANT, TENANT_WRONG));
    }

    @Test
    void shouldFailToUpdateChangeExistingTenantDescriptorWithSkipPermission() {
        //given
        val tenant = saveTenant(0, TENANT);
        tenant.setTenant(TENANT_WRONG);

        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //when
        val result = assertThrows(TransactionSystemException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .getRootCause()
                .hasMessageContaining(
                        String.format("You can`t change saved tenant during update." +
                                " Previous tenant is '%s', Current tenant is '%s'.", TENANT, TENANT_WRONG));
    }
}
