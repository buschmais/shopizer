package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.event.product.ProductCreatedEvent;
import com.salesmanager.catalog.model.product.Product;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;


@Component
public class CatalogCreatedEventListener implements PostInsertEventListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductCreatedEvent(product.toDTO()));
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
