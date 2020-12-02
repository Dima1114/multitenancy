package com.decentage.multitenancy;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.Skip;
import com.decentage.multitenancy.data.TenantSupport;
import com.decentage.multitenancy.data.entity.ClientDomain;
import com.decentage.multitenancy.data.entity.TenantClientDomain;
import com.decentage.multitenancy.data.entity.TenantClientDomainElement;
import com.decentage.multitenancy.data.entity.TenantDomain;
import com.decentage.multitenancy.data.entity.TenantDomainElement;
import com.decentage.multitenancy.data.entity.TenantSingleDomain;
import com.decentage.multitenancy.data.repository.ClientDomainRepository;
import com.decentage.multitenancy.data.repository.TenantClientDomainElementRepository;
import com.decentage.multitenancy.data.repository.TenantClientDomainRepository;
import com.decentage.multitenancy.data.repository.TenantDomainRepository;
import com.decentage.multitenancy.data.repository.TenantSingleDomainRepository;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionTemplate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public abstract class BaseMultiTenantTest {

    protected static final String TENANT = "ten1";
    protected static final String TENANT_WRONG = "ten2";
    protected static final String CLIENT = "cl1";
    protected static final String CLIENT_WRONG = "cl2";

    @Autowired
    protected ClientDomainRepository clientDomainRepository;
    @Autowired
    protected TenantDomainRepository tenantDomainRepository;
    @Autowired
    protected TenantClientDomainRepository tenantClientDomainRepository;
    @Autowired
    protected TenantSingleDomainRepository tenantSingleDomainRepository;
    @Autowired
    protected TenantClientDomainElementRepository tenantClientDomainElementRepository;


    @Autowired
    protected TransactionTemplate txTemplate;

    @AfterEach
    void cleanUpContext() {
        MultiTenantContextHolder.setContext(Skip.SKIP_TENANT, TenantSupport.class);
        MultiTenantContextHolder.setContext(Skip.SKIP_CLIENT, ClientSupport.class);
        tenantDomainRepository.deleteAll();
        clientDomainRepository.deleteAll();

        MultiTenantContextHolder.clearContext(TenantSupport.class);
        MultiTenantContextHolder.clearContext(ClientSupport.class);
    }

    protected <T> T doInTransaction(Supplier<T> operation) {
        return txTemplate.execute(status -> operation.get());
    }

    protected void doInTransaction(Runnable operation) {
        txTemplate.execute(status -> {
            operation.run();
            return null;
        });
    }

    protected ClientDomain getClient(int name, String client) {
        return ClientDomain.builder()
                .name("client_" + name)
                .client(client)
                .build();
    }

    protected TenantDomain getTenant(int name, String tenant) {
        return TenantDomain.builder()
                .name("tenant_" + name)
                .tenant(tenant)
                .build();
    }

    protected TenantDomain getTenant(int name, String tenant, TenantDomainElement... elements) {
        val entity = getTenant(name, tenant);
        entity.setElements(new ArrayList<>(Arrays.asList(elements)));

        Arrays.stream(elements).forEach(element -> element.setTenantDomain(entity));

        return entity;
    }

    protected TenantDomain getTenant(int name, String tenant, TenantClientDomainElement... elements) {
        val entity = getTenant(name, tenant);
        entity.setMultiTenantElements(new ArrayList<>(Arrays.asList(elements)));

        Arrays.stream(elements).forEach(element -> element.setTenantDomain(entity));

        return entity;
    }

    protected TenantSingleDomain getTenantSingle(int name, String tenant, TenantDomain tenantDomain) {
        return TenantSingleDomain.builder()
                .name("tenant_single_" + name)
                .tenant(tenant)
                .tenantDomain(tenantDomain)
                .build();
    }

    protected TenantDomainElement getTenantElement(int name, String tenant, TenantDomain tenantDomain) {
        return TenantDomainElement.builder()
                .name("tenant_element_" + name)
                .tenant(tenant)
                .tenantDomain(tenantDomain)
                .build();
    }

    protected TenantClientDomainElement getTenantClientElement(int name, String tenant, String client, TenantDomain tenantDomain) {
        return TenantClientDomainElement.builder()
                .name("tenant_element_" + name)
                .tenant(tenant)
                .client(client)
                .tenantDomain(tenantDomain)
                .build();
    }

    protected TenantClientDomain getTenantClient(int name, String tenant, String client) {
        return TenantClientDomain.builder()
                .name("tenant_client_" + name)
                .tenant(tenant)
                .client(client)
                .build();
    }

    protected TenantDomain saveTenant(int name, String tenant) {
        var tenantDomain = getTenant(name, tenant);

        MultiTenantContextHolder.setContext(tenant, TenantSupport.class);
        tenantDomain = tenantDomainRepository.save(tenantDomain);
        MultiTenantContextHolder.clearContext(TenantSupport.class);

        return tenantDomain;
    }

    protected TenantDomain saveTenant(int name, String tenant, TenantDomainElement... elements) {
        val entity = getTenant(name, tenant);
        entity.setElements(new ArrayList<>(Arrays.asList(elements)));

        Arrays.stream(elements).forEach(element -> element.setTenantDomain(entity));

        MultiTenantContextHolder.setContext(tenant, TenantSupport.class);
        tenantDomainRepository.save(entity);
        MultiTenantContextHolder.clearContext(TenantSupport.class);

        return entity;
    }

    protected TenantClientDomain saveTenantClient(int name, String tenant, String client) {
        var entity = getTenantClient(name, tenant, client);

        MultiTenantContextHolder.setContext(tenant, TenantSupport.class);
        MultiTenantContextHolder.setContext(client, ClientSupport.class);
        entity = tenantClientDomainRepository.save(entity);
        MultiTenantContextHolder.clearContext(TenantSupport.class);
        MultiTenantContextHolder.clearContext(ClientSupport.class);

        return entity;
    }

    public <T> Specification<T> isEqual(String name, Object value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(resolvePath(root, name), value);
    }

    private static Path<?> resolvePath(Root<?> root, String value) {
        String[] pathArray = value.split("\\.");
        Path<?> path = root;
        for (String s : pathArray) {
            path = path.get(s);
        }
        return path;
    }
}
