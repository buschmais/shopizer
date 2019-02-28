package com.salesmanager.catalog.business.integration.core.listener;

import com.salesmanager.catalog.business.integration.core.adapter.MerchantStoreInfoAdapter;
import com.salesmanager.catalog.business.integration.core.repository.TaxClassInfoRepository;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.integration.core.TaxClassInfo;
import com.salesmanager.core.integration.tax.TaxClassDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TaxClassCoreEventListener {

    private TaxClassInfoRepository taxClassInfoRepository;
    private final MerchantStoreInfoAdapter merchantStoreInfoAdapter;

    @Autowired
    public TaxClassCoreEventListener(TaxClassInfoRepository taxClassInfoRepository, MerchantStoreInfoAdapter merchantStoreInfoAdapter) {
        this.taxClassInfoRepository = taxClassInfoRepository;
        this.merchantStoreInfoAdapter = merchantStoreInfoAdapter;
    }

    @KafkaListener(topics = "tax_class", containerFactory = "taxClassKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleTaxClassEvent(TaxClassDTO taxClassDTO) {
        if (taxClassDTO != null) {
            switch (taxClassDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    MerchantStoreInfo merchantStoreInfo = this.merchantStoreInfoAdapter.findOrRequest(taxClassDTO.getMerchantStoreCode());
                    TaxClassInfo taxClass = new TaxClassInfo(
                            taxClassDTO.getId(),
                            taxClassDTO.getCode(),
                            merchantStoreInfo
                    );
                    this.taxClassInfoRepository.save(taxClass);
                    break;
                case DELETED:
                    this.taxClassInfoRepository.delete(taxClassDTO.getId());
                    break;
            }
        }
    }

}
