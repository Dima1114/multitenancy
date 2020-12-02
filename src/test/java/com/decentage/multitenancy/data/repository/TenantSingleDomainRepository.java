package com.decentage.multitenancy.data.repository;

import com.decentage.multitenancy.data.entity.TenantSingleDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantSingleDomainRepository extends JpaRepository<TenantSingleDomain, Long>,
        JpaSpecificationExecutor<TenantSingleDomain> {
}
