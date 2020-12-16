package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.TenantSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDomainElement implements TenantSupport {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private TenantDomain tenantDomain;

    private String tenant;

    @Override
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getTenant() {
        return tenant;
    }
}
