package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.event.product.ProductCreatedEvent;
import com.salesmanager.catalog.api.event.product.ProductOptionCreatedEvent;
import com.salesmanager.catalog.api.event.product.ProductOptionValueCreatedEvent;
import com.salesmanager.catalog.api.event.product.ProductUpdatedEvent;
import com.salesmanager.catalog.business.repository.product.ProductRepository;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.attribute.ProductOption;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;


@Component
public class CatalogCreatedEventListener implements PostInsertEventListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    private final ProductRepository productRepository;

    @Autowired
    public CatalogCreatedEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductCreatedEvent(product.toDTO()));
        } else if (event.getEntity() instanceof ProductAttribute) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the attribute directly
            Product product = ((ProductAttribute) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            applicationEventPublisher.publishEvent(new ProductUpdatedEvent(product.toDTO()));
        } else if (event.getEntity() instanceof ProductOption) {
            ProductOption productOption = (ProductOption) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionCreatedEvent(productOption.toDTO()));
        } else if (event.getEntity() instanceof ProductOptionValue) {
            ProductOptionValue productOptionValue = (ProductOptionValue) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionValueCreatedEvent(productOptionValue.toDTO()));
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
