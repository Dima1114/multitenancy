package com.decentage.multitenancy.data.repository;

import com.decentage.multitenancy.data.entity.TenantClientDomainElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantClientDomainElementRepository extends JpaRepository<TenantClientDomainElement, Long> {
}
