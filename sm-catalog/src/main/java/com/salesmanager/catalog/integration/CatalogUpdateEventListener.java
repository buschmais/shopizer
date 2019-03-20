package com.salesmanager.catalog.integration;

import com.salesmanager.catalog.api.dto.AbstractCatalogCrudDTO;
import com.salesmanager.catalog.business.repository.product.ProductRepository;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.attribute.ProductOption;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import com.salesmanager.catalog.model.product.description.ProductDescription;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.salesmanager.catalog.api.dto.AbstractCatalogCrudDTO.*;


@Component
public class CatalogUpdateEventListener implements PostUpdateEventListener {

    private final ProductRepository productRepository;

    private final KafkaTemplate<String, AbstractCatalogCrudDTO> kafkaTemplate;

    @Autowired
    public CatalogUpdateEventListener(ProductRepository productRepository, KafkaTemplate<String, AbstractCatalogCrudDTO> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof Product) {
            Product product = (Product) event.getEntity();
            kafkaTemplate.send("product", (AbstractCatalogCrudDTO) product.toDTO().setEventType(EventType.UPDATE));
        } else if (event.getEntity() instanceof ProductAttribute) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the attribute directly
            Product product = ((ProductAttribute) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            kafkaTemplate.send("product", (AbstractCatalogCrudDTO) product.toDTO().setEventType(EventType.UPDATE));
        } else if (event.getEntity() instanceof ProductDescription) {
            // we see the product as an aggregate root and thus publish the change of the product instead of the description directly
            Product product = ((ProductDescription) event.getEntity()).getProduct();
            product = productRepository.findOne(product.getId());
            kafkaTemplate.send("product", (AbstractCatalogCrudDTO) product.toDTO().setEventType(EventType.UPDATE));
        } else if (event.getEntity() instanceof ProductOption) {
            ProductOption productOption = (ProductOption) event.getEntity();
            kafkaTemplate.send("product_option", (AbstractCatalogCrudDTO) productOption.toDTO().setEventType(EventType.UPDATE));
        } else if (event.getEntity() instanceof ProductOptionValue) {
            ProductOptionValue productOptionValue = (ProductOptionValue) event.getEntity();
            kafkaTemplate.send("product_option_value", (AbstractCatalogCrudDTO) productOptionValue.toDTO().setEventType(EventType.UPDATE));
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
