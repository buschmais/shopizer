package com.salesmanager.catalog.api;

import com.salesmanager.catalog.model.category.Category;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.integration.language.LanguageDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;

import java.util.List;

public interface CategoryApi {

    BreadcrumbItem getBreadcrumbItemForLocale(Long categoryId, LanguageDTO languageDTO);

    List<Category> listByDepth(MerchantStoreDTO store, int depth, LanguageDTO language);

    Category getByLanguage(long categoryId, LanguageDTO language);

}
