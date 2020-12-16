package com.decentage.multitenancy;

import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.exception.MultiTenantSupportException;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SetTenantTest extends BaseMultiTenantTest {

    @Test
    void shouldSetTenantAutomatically() {

        //given
        val tenant = getTenant(0, null);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = tenantDomainRepository.save(tenant);

        //then
        assertThat(result)
                .extracting(TenantDomain::getName, TenantDomain::getTenant)
                .containsExactly("tenant_0", TENANT);
    }

    @Test
    void shouldNotSetSkipSignAsTenantAndThrowException() {

        //given
        val tenant = getTenant(0, null);
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);

        //when
        val result = assertThrows(MultiTenantSupportException.class, () -> tenantDomainRepository.save(tenant));

        //then
        assertThat(result)
                .hasMessageContaining(
                        String.format("You can`t have entity of type '%s' providing skip Sign as '%s' field value",
                                TenantSupport.class.getName(), "tenant"));
    }
}
