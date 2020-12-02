package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.TenantSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    public void setTenant(Object tenant) {
        this.tenant = (String) tenant;
    }

    @Override
    public Object getTenant() {
        return tenant;
    }
}
