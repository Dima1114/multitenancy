package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantClientDomainElement;
import com.decentage.multitenancy.data.entity.TenantDomainElement;
import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class SaveEntityCollectionsTest extends BaseMultiTenantTest {

    @Test
    void shouldCascadeSaveCollection() {

        //given
        val element = getTenantElement(0, TENANT, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result.getElements())
                .hasSize(1)
                .extracting(TenantDomainElement::getName, TenantDomainElement::getTenant)
                .containsExactly(tuple("tenant_element_0", TENANT));
    }

    @Test
    void shouldFailToCascadeSaveCollectionWithDifferentTenantDescriptor() {

        //given
        val element = getTenantElement(0, TENANT_WRONG, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You don`t have permission to perform Multi Tenant action." +
                                " Object tenant is '%s', User tenant is '%s'.", TENANT_WRONG, TENANT));
    }

    @Test
    void shouldFailToAddNewCollectionElementWithDifferentTenantDescriptor() {

        //given
        val element = getTenantElement(0, TENANT, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);
        tenantDomainRepository.save(tenant);

        //when
        val newElement = getTenantElement(1, TENANT_WRONG, tenant);
        tenant.getElements().add(newElement);
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You don`t have permission to perform Multi Tenant action." +
                                " Object tenant is '%s', User tenant is '%s'.", TENANT_WRONG, TENANT));
    }

    @Test
    void shouldCascadeSaveMultiTenantCollection() {

        //given
        val element = getTenantClientElement(0, TENANT, CLIENT, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result.getMultiTenantElements())
                .hasSize(1)
                .extracting(TenantClientDomainElement::getName,
                        TenantClientDomainElement::getTenant,
                        TenantClientDomainElement::getClient)
                .containsExactly(tuple("tenant_element_0", TENANT, CLIENT));
    }

    @Test
    void shouldFailToCascadeSaveMultiTenantCollectionWithDifferentTenantDescriptor() {

        //given
        val element = getTenantClientElement(0, TENANT, CLIENT_WRONG, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You don`t have permission to perform Multi Tenant action." +
                                " Object tenant is '%s', User tenant is '%s'.", CLIENT_WRONG, CLIENT));
    }

    @Test
    void shouldCascadeSaveMultiTenantCollectionWithDifferentTenantDescriptorAndSkipPermission() {

        //given
        val element = getTenantClientElement(0, TENANT, CLIENT, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result.getMultiTenantElements())
                .hasSize(1)
                .extracting(TenantClientDomainElement::getName,
                        TenantClientDomainElement::getTenant,
                        TenantClientDomainElement::getClient)
                .containsExactly(tuple("tenant_element_0", TENANT, CLIENT));
    }

    @Test
    void shouldCascadeSaveCollectionWithSkipPermission() {

        //given
        val element = getTenantElement(0, TENANT_WRONG, null);
        val tenant = getTenant(0, TENANT, element);
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);

        //when
        val result = assertThrows(TransactionSystemException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .getRootCause()
                .hasMessageContaining(
                        String.format("You can`t perform cascade operations with multiple tenants: %s, %s",
                                TENANT, TENANT_WRONG));
    }
}
