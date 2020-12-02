package com.decentage.multitenancy.data.repository;

import com.decentage.multitenancy.data.entity.TenantClientDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantClientDomainRepository extends JpaRepository<TenantClientDomain, Long> {
}
