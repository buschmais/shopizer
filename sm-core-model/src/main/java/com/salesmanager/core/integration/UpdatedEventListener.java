package com.salesmanager.core.integration;

import com.salesmanager.common.model.integration.AbstractCrudDTO;
import com.salesmanager.core.integration.dto.AbstractCoreCrudDTO;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.salesmanager.common.model.integration.AbstractCrudDTO.*;

@Component
public class UpdatedEventListener implements PostUpdateEventListener {

    @Autowired
    @Qualifier("coreKafkaTemplate")
    private KafkaTemplate<String, AbstractCoreCrudDTO> kafkaTemplate;

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof MerchantStore) {
            MerchantStore store = ((MerchantStore) event.getEntity());
            kafkaTemplate.send("merchantStore", (AbstractCoreCrudDTO) store.toDTO().setEventType(EventType.UPDATED));
        } else if (event.getEntity() instanceof Language) {
            Language language = ((Language) event.getEntity());
            kafkaTemplate.send("language", (AbstractCoreCrudDTO) language.toDTO().setEventType(EventType.UPDATED));
        } else if (event.getEntity() instanceof Customer) {
            Customer customer = ((Customer) event.getEntity());
            kafkaTemplate.send("customer", (AbstractCoreCrudDTO) customer.toDTO().setEventType(EventType.UPDATED));
        } else if (event.getEntity() instanceof TaxClass) {
            TaxClass taxClass = ((TaxClass) event.getEntity());
            kafkaTemplate.send("taxCkass", (AbstractCoreCrudDTO) taxClass.toDTO().setEventType(EventType.UPDATED));
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

}
