package com.decentage.multitenancy.factory;

import com.decentage.multitenancy.model.MultiTenantSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.UtilityClass;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
class MetadataContextHolder {

    private static final Map<Class<? extends MultiTenantSupport>, FilterInformation> metadataMap = new HashMap<>();

    static void put(Class<? extends MultiTenantSupport> type, String filterName, String filterParameter, List<String> skipSigns) {
        metadataMap.put(type, new FilterInformation(filterName, filterParameter, skipSigns));
    }

    static FilterInformation get(Class<? extends MultiTenantSupport> type) {
        return metadataMap.get(type);
    }

    static Map<Class<? extends MultiTenantSupport>, FilterInformation> getMetadata() {
        return metadataMap;
    }

    @Data
    @AllArgsConstructor
    static class FilterInformation {
        private String filterName;
        private String filterParameter;
        private List<String> skipSigns;
    }
}
