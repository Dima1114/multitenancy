package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantClientDomainElement;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
class SelectOneToManyEntityTest extends BaseMultiTenantTest {

    @Test
    void shouldSelectOnlyOwnTenantsElementsInRelatedCollection() {

        //given
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        val ownElement = getTenantClientElement(0, TENANT, CLIENT, null);
        val wrongElement = getTenantClientElement(1, TENANT, CLIENT_WRONG, null);
        val tenant = getTenant(0, TENANT, ownElement, wrongElement);
        tenantDomainRepository.save(tenant);

        //when
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);
        val result = tenantDomainRepository.findById(tenant.getId());

        //then
        assertThat(result.get().getMultiTenantElements())
                .hasSize(1)
                .extracting(TenantClientDomainElement::getName, TenantClientDomainElement::getClient)
                .containsExactly(tuple(ownElement.getName(), ownElement.getClient()));
    }
}
