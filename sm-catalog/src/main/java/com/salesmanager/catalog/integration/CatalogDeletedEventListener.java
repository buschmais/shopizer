package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.dto.AbstractCatalogCrudDTO;
import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.salesmanager.catalog.api.dto.AbstractCatalogCrudDTO.*;


@Component
public class CatalogDeletedEventListener implements PostDeleteEventListener {

    private final ProductRepository productRepository;

    private final KafkaTemplate<String, AbstractCatalogDTO> kafkaTemplate;

    @Autowired
    public CatalogDeletedEventListener(ProductRepository productRepository, KafkaTemplate<String, AbstractCatalogDTO> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            kafkaTemplate.send("product", product.toDTO().setEventType(EventType.DELETED));
        } else if (event.getEntity() instanceof ProductAttribute) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the attribute directly
            Product product = ((ProductAttribute) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            kafkaTemplate.send("product", product.toDTO().setEventType(AbstractCatalogCrudDTO.EventType.UPDATED));
        } else if (event.getEntity() instanceof ProductDescription) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the description directly
            Product product = ((ProductDescription) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            kafkaTemplate.send("product", product.toDTO().setEventType(AbstractCatalogCrudDTO.EventType.UPDATED));
        }else if (event.getEntity() instanceof ProductOption) {
            ProductOption productOption = (ProductOption) event.getEntity();
            kafkaTemplate.send("product_option", productOption.toDTO().setEventType(AbstractCatalogCrudDTO.EventType.DELETED));
        } else if (event.getEntity() instanceof ProductOptionValue) {
            ProductOptionValue productOptionValue = (ProductOptionValue) event.getEntity();
            kafkaTemplate.send("product_option_value", productOptionValue.toDTO().setEventType(AbstractCatalogCrudDTO.EventType.DELETED));
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
