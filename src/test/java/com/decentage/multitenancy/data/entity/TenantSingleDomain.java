package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.TenantSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSingleDomain implements TenantSupport {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String tenant;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "single")
    private TenantDomain tenantDomain;

    @Override
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getTenant() {
        return tenant;
    }
}
