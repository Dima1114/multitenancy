package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.ClientSupport;
import com.decentage.multitenancy.data.TenantSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantClientDomain implements TenantSupport, ClientSupport {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String tenant;

    private String client;

    @Override
    public void setClient(String client) {
        this.client = client;
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
