package com.salesmanager.catalog.integration;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Component
public class HibernateCatalogListenerConfigurer {

    @PersistenceUnit(unitName = "catalogEntityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    private CatalogCreatedEventListener catalogCreatedEventListener;

    private CatalogDeletedEventListener catalogDeletedEventListener;

    private CatalogUpdatedEventListener catalogUpdatedEventListener;

    @Autowired
    public HibernateCatalogListenerConfigurer(CatalogCreatedEventListener catalogCreatedEventListener, CatalogDeletedEventListener catalogDeletedEventListener,
                                              CatalogUpdatedEventListener catalogUpdatedEventListener) {
        this.catalogCreatedEventListener = catalogCreatedEventListener;
        this.catalogDeletedEventListener = catalogDeletedEventListener;
        this.catalogUpdatedEventListener = catalogUpdatedEventListener;
    }


    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(catalogCreatedEventListener);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(catalogDeletedEventListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(catalogUpdatedEventListener);
    }
}