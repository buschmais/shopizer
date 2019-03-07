package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.event.product.ProductDeleteEvent;
import com.salesmanager.catalog.api.event.product.ProductOptionDeleteEvent;
import com.salesmanager.catalog.api.event.product.ProductOptionValueDeleteEvent;
import com.salesmanager.catalog.api.event.product.ProductUpdateEvent;
import com.salesmanager.catalog.business.repository.product.ProductRepository;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.attribute.ProductOption;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import com.salesmanager.catalog.model.product.description.ProductDescription;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;


@Component
public class CatalogDeleteEventListener implements PostDeleteEventListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    private final ProductRepository productRepository;

    @Autowired
    public CatalogDeleteEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductDeleteEvent(product.toDTO()));
        } else if (event.getEntity() instanceof ProductAttribute) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the attribute directly
            Product product = ((ProductAttribute) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            applicationEventPublisher.publishEvent(new ProductUpdateEvent(product.toDTO()));
        } else if (event.getEntity() instanceof ProductDescription) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the description directly
            Product product = ((ProductDescription) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            applicationEventPublisher.publishEvent(new ProductUpdateEvent(product.toDTO()));
        }else if (event.getEntity() instanceof ProductOption) {
            ProductOption productOption = (ProductOption) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionDeleteEvent(productOption.toDTO()));
        } else if (event.getEntity() instanceof ProductOptionValue) {
            ProductOptionValue productOptionValue = (ProductOptionValue) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionValueDeleteEvent(productOptionValue.toDTO()));
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
