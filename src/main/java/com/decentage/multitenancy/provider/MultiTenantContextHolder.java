package com.decentage.multitenancy.provider;

import com.decentage.multitenancy.model.MultiTenantSupport;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MultiTenantContextHolder {

    private static MultiTenantProviderStrategy strategy = new ThreadLocalMultiTenantProviderStrategy();

    public void initialize(MultiTenantProviderStrategy strategyProvider) {
        //set strategy type
        strategy = strategyProvider;
    }

    public void clearContext(Class<? extends MultiTenantSupport> type) {
        strategy.clearContext(type);
    }

    public String getContext(Class<? extends MultiTenantSupport> type) {
        return (String) strategy.getContext(type);
    }

    public void setContext(String context, Class<? extends MultiTenantSupport> type) {
        strategy.setContext(context, type);
    }
}
