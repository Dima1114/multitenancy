package com.decentage.multitenancy.config;

import com.decentage.multitenancy.factory.MultiTenantSessionFactoryImpl;
import com.decentage.multitenancy.utils.AutowireUtil;
import lombok.val;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManagerFactory;

@Component
@ConditionalOnProperty("spting.multitenancy.enable")
public class AppContextReadyListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        contextRefreshedEvent.getSource();
        val factory = (MultiTenantSessionFactoryImpl) AutowireUtil.getBean(EntityManagerFactory.class)
                .unwrap(SessionFactoryImpl.class);
        factory.setInitialized(true);
    }
}
