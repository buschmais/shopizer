package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import com.salesmanager.catalog.api.event.product.ProductOptionValueCreateEvent;
import com.salesmanager.catalog.business.repository.product.ProductRepository;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.attribute.ProductOption;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import com.salesmanager.catalog.model.product.description.ProductDescription;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.salesmanager.catalog.api.dto.AbstractCatalogCrudDTO.*;


@Component
public class CatalogCreateEventListener implements PostInsertEventListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    private final ProductRepository productRepository;

    private final KafkaTemplate<String, AbstractCatalogDTO> kafkaTemplate;

    @Autowired
    public CatalogCreateEventListener(ProductRepository productRepository, KafkaTemplate<String, AbstractCatalogDTO> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            kafkaTemplate.send("product", product.toDTO().setEventType(EventType.CREATE));
        } else if (event.getEntity() instanceof ProductAttribute) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the attribute directly
            Product product = ((ProductAttribute) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            kafkaTemplate.send("product", product.toDTO().setEventType(EventType.UPDATE));
        } else if (event.getEntity() instanceof ProductDescription) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the description directly
            Product product = ((ProductDescription) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            kafkaTemplate.send("product", product.toDTO().setEventType(EventType.UPDATE));
        } else if (event.getEntity() instanceof ProductOption) {
            ProductOption productOption = (ProductOption) event.getEntity();
            kafkaTemplate.send("product_option", productOption.toDTO().setEventType(EventType.CREATE));
        } else if (event.getEntity() instanceof ProductOptionValue) {
            ProductOptionValue productOptionValue = (ProductOptionValue) event.getEntity();
            applicationEventPublisher.publishEvent(new ProductOptionValueCreateEvent(productOptionValue.toDTO()));
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
