package com.salesmanager.catalog.presentation.filter;

import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.presentation.controller.category.facade.CategoryFacade;
import com.salesmanager.catalog.presentation.model.category.ReadableCategory;
import com.salesmanager.common.presentation.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CatalogFilter extends HandlerInterceptorAdapter {

    @Autowired
    private MerchantStoreInfoService merchantStoreInfoService;

    @Autowired
    private LanguageInfoService languageInfoService;

    @Autowired
    private CategoryFacade categoryFacade;

    @Value("#{catalogEhCacheManager.getCache('catalogCache')}")
    private Cache catalogCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String storeCode = (String) request.getSession().getAttribute(Constants.MERCHANT_STORE_CODE);
        MerchantStoreInfo store = this.merchantStoreInfoService.findbyCode(storeCode);

        String languageCode = (String) request.getSession().getAttribute(Constants.LANGUAGE_CODE);
        LanguageInfo language = languageInfoService.findbyCode(languageCode);

        /******* Top Categories ********/
        //this.getTopCategories(store, language, request);
        this.setTopCategories(store, language, request);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void setTopCategories(MerchantStoreInfo store, LanguageInfo language, HttpServletRequest request) throws Exception {

        StringBuilder categoriesKey = new StringBuilder();
        categoriesKey
                .append(store.getId())
                .append("_")
                .append(Constants.CATEGORIES_CACHE_KEY)
                .append("-")
                .append(language.getCode());

        StringBuilder categoriesKeyMissed = new StringBuilder();
        categoriesKeyMissed
                .append(categoriesKey.toString())
                .append(Constants.MISSED_CACHE_KEY);


        //language code - List of category
        Map<String, List<ReadableCategory>> objects = null;
        List<ReadableCategory> loadedCategories = null;

        if(store.isUseCache()) {
            Cache.ValueWrapper vw = catalogCache.get(categoriesKey.toString());
            objects = vw != null ? (Map<String, List<ReadableCategory>>) vw.get() : null;

            if(objects==null) {
                //load categories
                loadedCategories = categoryFacade.getCategoryHierarchy(store, 0, language, null);//null filter
                objects = new ConcurrentHashMap<String, List<ReadableCategory>>();
                objects.put(language.getCode(), loadedCategories);
                catalogCache.put(categoriesKey.toString(), objects);

            } else {
                loadedCategories = objects.get(language.getCode());
            }

        } else {
            loadedCategories = categoryFacade.getCategoryHierarchy(store, 0, language, null);//null filter
        }

        if(loadedCategories!=null) {
            request.setAttribute(Constants.REQUEST_TOP_CATEGORIES, loadedCategories);
        }

    }
}
