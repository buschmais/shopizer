package com.salesmanager.core.business.services.catalog;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.business.integration.catalog.adapter.CategoryInfoAdapter;
import com.salesmanager.core.model.reference.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryInfoService {

    private final CategoryInfoAdapter categoryInfoAdapter;

    @Autowired
    public CategoryInfoService(CategoryInfoAdapter categoryInfoAdapter) {
        this.categoryInfoAdapter = categoryInfoAdapter;
    }


    public BreadcrumbItem getBreadcrumbItemForLocale(Long id, Language language) throws ServiceException {
        return this.categoryInfoAdapter.getCategoryBreadcrumbItem(id, language.getCode());
    }
}
