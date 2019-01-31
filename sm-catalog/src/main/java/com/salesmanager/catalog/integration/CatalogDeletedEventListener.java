package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.event.product.ProductDeletedEvent;
import com.salesmanager.catalog.api.event.product.ProductOptionDeletedEvent;
import com.salesmanager.catalog.api.event.product.ProductOptionValueDeletedEvent;
import com.salesmanager.catalog.api.event.product.ProductUpdatedEvent;
import com.salesmanager.catalog.business.repository.product.ProductRepository;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.attribute.ProductOption;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;


@Component
public class CatalogDeletedEventListener implements PostDeleteEventListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    private final ProductRepository productRepository;

    @Autowired
    public CatalogDeletedEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductDeletedEvent(product.toDTO()));
        } else if (event.getEntity() instanceof ProductAttribute) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the attribute directly
            Product product = ((ProductAttribute) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            applicationEventPublisher.publishEvent(new ProductUpdatedEvent(product.toDTO()));
        } else if (event.getEntity() instanceof ProductOption) {
            ProductOption productOption = (ProductOption) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionDeletedEvent(productOption.toDTO()));
        } else if (event.getEntity() instanceof ProductOptionValue) {
            ProductOptionValue productOptionValue = (ProductOptionValue) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionValueDeletedEvent(productOptionValue.toDTO()));
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
