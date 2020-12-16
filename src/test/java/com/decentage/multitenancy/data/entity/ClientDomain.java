package com.decentage.multitenancy.data.entity;

import com.decentage.multitenancy.data.ClientSupport;
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
public class ClientDomain implements ClientSupport {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String client;

    @Override
    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String getClient() {
        return client;
    }
}
