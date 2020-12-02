package com.decentage.multitenancy;

import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Disabled("figure out how to handle multitenancy in modifying query")
class CustomQueryTest extends BaseMultiTenantTest {

    @Test
    void shouldNotUpdate() {

        //given
        val tenant = saveTenant(0, TENANT);
        MultiTenantContextHolder.setContext(TENANT, TenantSupport.class);

        //when
        val result = assertThrows(TransactionSystemException.class,
                () -> tenantDomainRepository.updateTenantDomain(tenant.getId(), TENANT_WRONG));

        //then
        assertThat(result)
                .hasMessageContaining(String.format("You don`t have permission to perform Multi Tenant action." +
                        " Object tenant is '%s', User tenant is '%s'.", TENANT, TENANT_WRONG));
    }
}
