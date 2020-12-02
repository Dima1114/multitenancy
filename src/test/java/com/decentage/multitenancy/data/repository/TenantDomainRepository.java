package com.decentage.multitenancy.data.repository;

import com.decentage.multitenancy.data.entity.TenantDomain;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static com.decentage.multitenancy.CacheTest.TENANT_CACHE;

@Repository
public interface TenantDomainRepository extends JpaRepository<TenantDomain, Long>, JpaSpecificationExecutor<TenantDomain> {

    @Query("update TenantDomain t set t.tenant = ?2 where t.id = ?1")
    @Modifying
    @Transactional
    void updateTenantDomain(Long id, String tenant);

    @EntityGraph(attributePaths = "elements")
    @Cacheable(value = TENANT_CACHE)
    List<TenantDomain> findAllByName(String name);

    @EntityGraph(attributePaths = "elements")
    @Query("from TenantDomain t where t.name = ?1")
    @Cacheable(value = TENANT_CACHE, key = "{#name,  T(com.decentage.multitenancy.provider.MultiTenantContextHolder).getContext(T(com.decentage.multitenancy.data.TenantSupport))}")
    List<TenantDomain> getByName(String name);

    @Override
    @EntityGraph(attributePaths = {"multiTenantElements", "tenantClients"})
    Optional<TenantDomain> findById(Long aLong);

    @Query("from TenantDomain t where t.id = ?1")
    TenantDomain findByIdWithoutGraph(Long id);
}
