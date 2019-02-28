package com.salesmanager.catalog.business.integration.core.listener;

import com.salesmanager.catalog.business.integration.core.adapter.MerchantStoreInfoAdapter;
import com.salesmanager.catalog.business.integration.core.repository.MerchantStoreInfoRepository;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MerchantStoreCoreEventListener {

    private MerchantStoreInfoRepository merchantStoreInfoRepository;
    private final MerchantStoreInfoAdapter merchantStoreInfoAdapter;

    @Autowired
    public MerchantStoreCoreEventListener(MerchantStoreInfoRepository merchantStoreInfoRepository, MerchantStoreInfoAdapter merchantStoreInfoAdapter) {
        this.merchantStoreInfoRepository = merchantStoreInfoRepository;
        this.merchantStoreInfoAdapter = merchantStoreInfoAdapter;
    }

    @KafkaListener(topics = "merchant_store", containerFactory = "merchantStoreKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMerchantStoreEvent(MerchantStoreDTO storeDTO) {
        if (storeDTO != null) {
            switch (storeDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    this.merchantStoreInfoAdapter.createOrUpdateMerchantStoreInfo(storeDTO);
                    break;
                case DELETED:
                    merchantStoreInfoRepository.delete(storeDTO.getId());
                    break;
            }

        }
    }

}
