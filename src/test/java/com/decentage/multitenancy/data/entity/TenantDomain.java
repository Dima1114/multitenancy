package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.TenantSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDomain implements TenantSupport {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String tenant;

    private int count = 1;

    @OneToMany(mappedBy = "tenantDomain", cascade = CascadeType.ALL)
    private List<TenantDomainElement> elements;

    @OneToMany(mappedBy = "tenantDomain", cascade = CascadeType.ALL)
    private List<TenantClientDomainElement> multiTenantElements;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "single_id", referencedColumnName = "id")
    private TenantSingleDomain single;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "td_tcd",
            joinColumns = @JoinColumn(name = "td_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tcd_id", referencedColumnName = "id"))
//    @Filters({
//            @Filter(name = "filterTenantSupport", condition = "tenant = :tenant"),
//            @Filter(name = "filterClientSupport", condition = "client = :client")})
    private Set<TenantClientDomain> tenantClients;

    @Override
    public void setTenant(String tenant) {
        this.tenant = (String) tenant;
    }

    @Override
    public String getTenant() {
        return tenant;
    }
}
