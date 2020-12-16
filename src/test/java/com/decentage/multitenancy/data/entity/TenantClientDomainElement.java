package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.ClientSupport;
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
public class TenantClientDomainElement implements TenantSupport, ClientSupport {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private TenantDomain tenantDomain;

    private String tenant;

    private String client;

    @Override
    public void setClient(String client) {
        this.client = (String) client;
    }

    @Override
    public String getClient() {
        return client;
    }

    @Override
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getTenant() {
        return tenant;
    }
}
