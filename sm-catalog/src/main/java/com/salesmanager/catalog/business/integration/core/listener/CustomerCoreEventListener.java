package com.salesmanager.catalog.business.integration.core.listener;

import com.salesmanager.catalog.business.integration.core.adapter.LanguageInfoAdapter;
import com.salesmanager.catalog.business.integration.core.adapter.MerchantStoreInfoAdapter;
import com.salesmanager.catalog.business.integration.core.repository.CustomerInfoRepository;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.model.integration.core.CustomerInfo;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.business.integration.core.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomerCoreEventListener {

    private final CustomerInfoRepository customerInfoRepository;
    private final MerchantStoreInfoAdapter merchantStoreInfoAdapter;
    private final LanguageInfoAdapter languageInfoAdapter;
    private final LanguageInfoService languageInfoService;

    @Autowired
    public CustomerCoreEventListener(CustomerInfoRepository customerInfoRepository, MerchantStoreInfoAdapter merchantStoreInfoAdapter, LanguageInfoAdapter languageInfoAdapter, LanguageInfoService languageInfoService) {
        this.customerInfoRepository = customerInfoRepository;
        this.merchantStoreInfoAdapter = merchantStoreInfoAdapter;
        this.languageInfoAdapter = languageInfoAdapter;
        this.languageInfoService = languageInfoService;
    }

    @KafkaListener(topics = "customer", containerFactory = "customerKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleCustomerCreateEvent(CustomerDTO customerDTO) {
        if (customerDTO != null) {
            switch (customerDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    MerchantStoreInfo merchantStoreInfo = this.merchantStoreInfoAdapter.findOrRequest(customerDTO.getMerchantStoreCode());
                    LanguageInfo languageInfo = this.languageInfoAdapter.findOrRequet(customerDTO.getDefaultLanguageCode());
                    CustomerInfo customer = new CustomerInfo(
                            customerDTO.getId(),
                            customerDTO.getNick(),
                            customerDTO.getFirstName(),
                            customerDTO.getLastName(),
                            merchantStoreInfo,
                            languageInfo
                    );
                    this.customerInfoRepository.save(customer);
                    break;
                case DELETED:
                    this.customerInfoRepository.delete(customerDTO.getId());
                    break;
            }
        }
    }


}
