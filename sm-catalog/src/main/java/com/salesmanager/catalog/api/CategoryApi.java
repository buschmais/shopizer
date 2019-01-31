package com.salesmanager.catalog.api;

import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.integration.language.LanguageDTO;

public interface CategoryApi {

    BreadcrumbItem getBreadcrumbItemForLocale(Long categoryId, LanguageDTO languageDTO);

}
