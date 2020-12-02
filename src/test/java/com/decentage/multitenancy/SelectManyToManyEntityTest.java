package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantClientDomain;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class SelectManyToManyEntityTest extends BaseMultiTenantTest {

    private Long id;

    @BeforeEach
    void setUp() {
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);

        val ownTC = getTenantClient(0, TENANT, CLIENT);
        val wrongTC = getTenantClient(1, TENANT, CLIENT_WRONG);
        val tenant = getTenant(0, TENANT);
        tenant.setTenantClients(Set.of(ownTC, wrongTC));
        id = tenantDomainRepository.save(tenant).getId();
    }

    @Test
    void shouldSelectOnlyOwnTenantRelatedCollection() {

        //given
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        //when
        val result = tenantDomainRepository.findById(id);

        //then
        assertThat(result.get().getTenantClients())
                .hasSize(1)
                .extracting(TenantClientDomain::getName,
                        TenantClientDomain::getTenant,
                        TenantClientDomain::getClient)
                .containsExactly(tuple("tenant_client_0", TENANT, CLIENT));
    }

    @Test
    void shouldSelectOnlyOwnTenantRelatedCollectionInTransaction() {

        //given
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        doInTransaction(() -> {
            //when
            val result = tenantDomainRepository.findById(id);

            //then
            assertThat(result.get().getTenantClients())
                    .hasSize(1)
                    .extracting(TenantClientDomain::getName,
                            TenantClientDomain::getTenant,
                            TenantClientDomain::getClient)
                    .containsExactly(tuple("tenant_client_0", TENANT, CLIENT))
            ;
        });
    }

    @Test
    void shouldSelectOnlyOwnTenantRelatedCollectionInTransactionWithLazyLoad() {

        //given
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(CLIENT, ClientSupport.class);

        doInTransaction(() -> {
            //when
            val result = tenantDomainRepository.findByIdWithoutGraph(id);

            //then
            assertThat(result.getTenantClients())
                    .hasSize(1)
                    .extracting(TenantClientDomain::getName,
                            TenantClientDomain::getTenant,
                            TenantClientDomain::getClient)
                    .containsExactly(tuple("tenant_client_0", TENANT, CLIENT))
            ;
        });
    }
}
