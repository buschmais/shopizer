package com.salesmanager.core.integration;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.salesmanager.core.integration.AbstractCoreCrudDTO.*;


@Component
public class DeletedEventListener implements PostDeleteEventListener {

    @Autowired
    @Qualifier("coreKafkaTemplate")
    private KafkaTemplate<String, AbstractCoreDTO> kafkaTemplate;

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof MerchantStore) {
            MerchantStore store = ((MerchantStore) event.getEntity());
            kafkaTemplate.send("merchant_store", store.toDTO().setEventType(EventType.DELETED));
        } else if (event.getEntity() instanceof Language) {
            Language language = ((Language) event.getEntity());
            kafkaTemplate.send("language", language.toDTO().setEventType(EventType.DELETED));
        } else if (event.getEntity() instanceof Customer) {
            Customer customer = ((Customer) event.getEntity());
            kafkaTemplate.send("customer", customer.toDTO().setEventType(EventType.DELETED));
        } else if (event.getEntity() instanceof TaxClass) {
            TaxClass taxClass = ((TaxClass) event.getEntity());
            kafkaTemplate.send("tax_class", taxClass.toDTO().setEventType(EventType.DELETED));
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

}
