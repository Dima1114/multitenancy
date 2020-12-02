package com.decentage.multitenancy.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "spting.multitenancy")
public class MultiTenancyProperties {

    private boolean enable;
}
