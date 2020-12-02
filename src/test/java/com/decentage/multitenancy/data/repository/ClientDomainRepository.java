package com.decentage.multitenancy.data.repository;

import com.decentage.multitenancy.data.entity.ClientDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDomainRepository extends JpaRepository<ClientDomain, Long> {
}
