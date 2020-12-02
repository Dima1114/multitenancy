package com.decentage.multitenancy.factory;

import com.decentage.multitenancy.model.MultiTenantSupport;
import com.decentage.multitenancy.provider.MultiTenantContextHolder;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Session;
import java.util.List;
import java.util.Objects;

@UtilityClass
class MultiTenantFilterUtil {

    void enableTenantFilter(Session session,
                            Class<? extends MultiTenantSupport> type,
                            MetadataContextHolder.FilterInformation filter) {
        Object tenant = MultiTenantContextHolder.getContext(type);
        if (!shouldSkip(tenant, filter.getSkipSigns())) {
            session.enableFilter(filter.getFilterName())
                    .setParameter(filter.getFilterParameter(), tenant);
        }
    }

    boolean shouldSkip(Object tenant, List<String> skipSigns) {
        return ObjectUtils.isNotEmpty(tenant) &&
                skipSigns.stream().anyMatch(sign -> Objects.equals(sign, tenant));
    }
}
