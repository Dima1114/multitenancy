package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.data.entity.TenantDomainElement;
import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SelectEntitiesTest extends BaseMultiTenantTest {

    @BeforeEach
    void setUp() {
        saveTenant(0, TENANT, getTenantElement(0, TENANT, null));
        saveTenant(0, TENANT_WRONG, getTenantElement(0, TENANT_WRONG, null));

        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);
    }

    @Test
    void shouldSelectOnlyOwnTenantEntities() {

        //given
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.findAll();

        //then
        assertThat(result)
                .hasSize(1)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(tuple("tenant_0", TENANT));
    }

    @Test
    void shouldSelectOnlyOwnTenantEntitiesUsingCustomRepositoryMethod() {

        //given
        val tenant1 = saveTenant(1, TENANT);
        saveTenant(1, TENANT);
        saveTenant(1, TENANT_WRONG);
        saveTenant(2, TENANT_WRONG);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.findAllByName(tenant1.getName());

        //then
        assertThat(result)
                .hasSize(2)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(
                        tuple(tenant1.getName(), tenant1.getTenant()),
                        tuple(tenant1.getName(), tenant1.getTenant()));
    }

    @Test
    void shouldSelectAllTenantEntitiesUsingCustomRepositoryMethodAndSkipSign() {

        //given
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.findAllByName("tenant_0");

        //then
        assertThat(result)
                .hasSize(2)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactlyInAnyOrder(
                        tuple("tenant_0", TENANT),
                        tuple("tenant_0", TENANT_WRONG));
    }

    @Test
    void shouldSelectOnlyOwnTenantEntitiesUsingCustomQueryMethod() {

        //given
        val tenant1 = saveTenant(1, TENANT);
        saveTenant(1, TENANT);
        saveTenant(1, TENANT_WRONG);
        saveTenant(2, TENANT_WRONG);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.getByName(tenant1.getName());

        //then
        assertThat(result)
                .hasSize(2)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(
                        tuple(tenant1.getName(), tenant1.getTenant()),
                        tuple(tenant1.getName(), tenant1.getTenant()));
    }

    @Test
    void shouldSelectAllOwnTenantEntitiesUsingCustomQueryMethodAndSkipSign() {

        //given
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.getByName("tenant_0");

        //then
        assertThat(result)
                .hasSize(2)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactlyInAnyOrder(
                        tuple("tenant_0", TENANT),
                        tuple("tenant_0", TENANT_WRONG));
    }

    @Test
    void shouldSelectOnlyOwnTenantEntitiesAndItsCollectionUsingCustomRepositoryMethod() {

        //given
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.findAllByName("tenant_0");

        //then
        assertThat(result)
                .hasSize(1)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(
                        tuple("tenant_0", TENANT));

        assertThat(result.get(0).getElements())
                .hasSize(1)
                .extracting(TenantDomainElement::getName, TenantDomainElement::getTenant)
                .containsExactly(
                        tuple("tenant_element_0", TENANT));
    }

    @Test
    void shouldSelectOnlyOwnTenantEntitiesUsingSpecificationFilter() {

        //given
        Specification<TenantDomain> spec = Specification.where(isEqual("name", "tenant_0"));
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.findAll(spec);

        //then
        assertThat(result)
                .hasSize(1)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(
                        tuple("tenant_0", TENANT));
    }

    @Test
    void shouldSelectOnlyAllTenantEntitiesUsingSpecificationFilterWithSkipSign() {

        //given
        Specification<TenantDomain> spec = Specification.where(isEqual("name", "tenant_0"));
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.findAll(spec);

        //then
        assertThat(result)
                .hasSize(2)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly(
                        tuple("tenant_0", TENANT),
                        tuple("tenant_0", TENANT_WRONG));
    }

    @Test
    void shouldFailToGetWrongTenantEntityById() {

        //given
        val tenant = saveTenant(1, TENANT);
        MultiTenantContextHolder.setContext(TENANT_WRONG, TenantSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.findById(tenant.getId()));

        //then
        assertThat(result)
                .hasMessageContaining(String.format("You don`t have permission to perform Multi Tenant action." +
                        " Object tenant is '%s', User tenant is '%s'.", TENANT, TENANT_WRONG));
    }
}
