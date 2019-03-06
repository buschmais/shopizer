package com.salesmanager.catalog.presentation.controller.product;

import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.category.CategoryService;
import com.salesmanager.catalog.business.service.product.PricingService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.business.service.product.attribute.ProductAttributeService;
import com.salesmanager.catalog.business.service.product.relationship.ProductRelationshipService;
import com.salesmanager.catalog.business.service.product.review.ProductReviewService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.presentation.controller.ControllerConstants;
import com.salesmanager.catalog.presentation.util.CatalogImageFilePathUtils;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.attribute.ProductOptionDescription;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValueDescription;
import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.catalog.model.product.relationship.ProductRelationship;
import com.salesmanager.catalog.model.product.relationship.ProductRelationshipType;
import com.salesmanager.catalog.model.product.review.ProductReview;
import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.util.DateUtil;
import com.salesmanager.catalog.business.integration.core.dto.LanguageDTO;
import com.salesmanager.catalog.business.integration.core.dto.MerchantStoreDTO;
import com.salesmanager.common.presentation.constants.Constants;
import com.salesmanager.catalog.presentation.model.product.ReadableProduct;
import com.salesmanager.catalog.presentation.model.product.ReadableProductPrice;
import com.salesmanager.catalog.presentation.model.product.ReadableProductReview;
import com.salesmanager.common.presentation.model.Breadcrumb;
import com.salesmanager.common.presentation.model.PageInformation;
import com.salesmanager.catalog.presentation.populator.catalog.ReadableFinalPricePopulator;
import com.salesmanager.catalog.presentation.populator.catalog.ReadableProductPopulator;
import com.salesmanager.catalog.presentation.populator.catalog.ReadableProductReviewPopulator;
import com.salesmanager.catalog.presentation.model.store.Attribute;
import com.salesmanager.catalog.presentation.model.store.AttributeValue;
import com.salesmanager.catalog.presentation.util.BreadcrumbsUtils;
import com.salesmanager.common.presentation.util.LabelUtils;
import com.salesmanager.catalog.presentation.util.PageBuilderUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;



/**
 * Populates the product details page
 * @author Carl Samson
 *
 */
@Controller
@RequestMapping("/shop/product")
public class ShopProductController {
	
	@Inject
	private ProductService productService;
	
	@Inject
	private ProductAttributeService productAttributeService;
	
	@Inject
	private ProductRelationshipService productRelationshipService;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	private ProductReviewService productReviewService;
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private CategoryService categoryService;
	
	@Inject
	private BreadcrumbsUtils breadcrumbsUtils;
	
	@Autowired
	private CatalogImageFilePathUtils imageUtils;

	@Autowired
	private MerchantStoreInfoService merchantStoreInfoService;

	@Autowired
	private LanguageInfoService languageInfoService;

	@Value("#{catalogEhCacheManager.getCache('catalogCache')}")
	private Cache catalogCache;

	private static final Logger LOG = LoggerFactory.getLogger(ShopProductController.class);

	@RequestMapping("/featured")
	public String displayFeaturedItems(Model model, HttpServletRequest request) throws ServiceException, ConversionException {

		MerchantStoreDTO storeDTO = (MerchantStoreDTO) request.getAttribute(Constants.MERCHANT_STORE_DTO);
		MerchantStoreInfo store = this.merchantStoreInfoService.findbyCode(storeDTO.getCode());
		LanguageDTO languageDTO = (LanguageDTO) request.getAttribute("LANGUAGE_DTO");
		LanguageInfo language = this.languageInfoService.findbyCode(languageDTO.getCode());

		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);

		//featured items
		List<ProductRelationship> relationships = productRelationshipService.getByType(store, ProductRelationshipType.FEATURED_ITEM, language);
		List<ReadableProduct> featuredItems = new ArrayList<ReadableProduct>();
		Date today = new Date();
		for(ProductRelationship relationship : relationships) {
			Product product = relationship.getRelatedProduct();
			if(product.isAvailable() && DateUtil.dateBeforeEqualsDate(product.getDateAvailable(), today)) {
				ReadableProduct proxyProduct = populator.populate(product, new ReadableProduct(), store, language);
				featuredItems.add(proxyProduct);
			}
		}

		model.addAttribute("featuredItems", featuredItems);

		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Product.featured).append(".").append(store.getStoreTemplate());

		return template.toString();
	}

	/**
	 * Display product details with reference to caller page
	 * @param friendlyUrl
	 * @param ref
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{friendlyUrl}.html/ref={ref}")
	public String displayProductWithReference(@PathVariable final String friendlyUrl, @PathVariable final String ref, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		return display(ref, friendlyUrl, model, request, response, locale);
	}
	

	/**
	 * Display product details no reference
	 * @param friendlyUrl
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{friendlyUrl}.html")
	public String displayProduct(@PathVariable final String friendlyUrl, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		return display(null, friendlyUrl, model, request, response, locale);
	}


	public String display(final String reference, final String friendlyUrl, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {


		MerchantStoreDTO storeDTO = (MerchantStoreDTO) request.getAttribute(Constants.MERCHANT_STORE_DTO);
		MerchantStoreInfo store = this.merchantStoreInfoService.findbyCode(storeDTO.getCode());
		LanguageDTO languageDTO = (LanguageDTO) request.getAttribute("LANGUAGE_DTO");
		LanguageInfo language = this.languageInfoService.findbyCode(languageDTO.getCode());
		
		Product product = productService.getBySeUrl(store, friendlyUrl, locale);
				
		if(product==null) {
			return PageBuilderUtils.build404(store);
		}
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		
		ReadableProduct productProxy = populator.populate(product, new ReadableProduct(), store, language);

		//meta information
		PageInformation pageInformation = new PageInformation();
		pageInformation.setPageDescription(productProxy.getDescription().getMetaDescription());
		pageInformation.setPageKeywords(productProxy.getDescription().getKeyWords());
		pageInformation.setPageTitle(productProxy.getDescription().getTitle());
		pageInformation.setPageUrl(productProxy.getDescription().getFriendlyUrl());
		
		request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);
		
		Breadcrumb breadCrumb = breadcrumbsUtils.buildProductBreadcrumb(reference, productProxy, store, language, request.getContextPath());
		request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
		request.setAttribute(Constants.BREADCRUMB, breadCrumb);
		

		
		StringBuilder relatedItemsCacheKey = new StringBuilder();
		relatedItemsCacheKey
		.append(store.getId())
		.append("_")
		.append(Constants.RELATEDITEMS_CACHE_KEY)
		.append("-")
		.append(language.getCode());
		
		StringBuilder relatedItemsMissed = new StringBuilder();
		relatedItemsMissed
		.append(relatedItemsCacheKey.toString())
		.append(Constants.MISSED_CACHE_KEY);
		
		Map<Long,List<ReadableProduct>> relatedItemsMap = null;
		List<ReadableProduct> relatedItems = null;
		
		if(store.isUseCache()) {

			//get from the cache
			Cache.ValueWrapper wrapper = catalogCache.get(relatedItemsCacheKey.toString());
			relatedItemsMap = wrapper != null ? (Map<Long,List<ReadableProduct>>) wrapper.get() : null;
			if(relatedItemsMap==null) {
				//get from missed cache
				//Boolean missedContent = (Boolean)cache.getFromCache(relatedItemsMissed.toString());

				//if(missedContent==null) {
					relatedItems = relatedItems(store, product, language);
					if(relatedItems!=null) {
						relatedItemsMap = new HashMap<Long,List<ReadableProduct>>();
						relatedItemsMap.put(product.getId(), relatedItems);
						catalogCache.put(relatedItemsMap, relatedItemsCacheKey.toString());
					} else {
						//cache.putInCache(new Boolean(true), relatedItemsMissed.toString());
					}
				//}
			} else {
				relatedItems = relatedItemsMap.get(product.getId());
			}
		} else {
			relatedItems = relatedItems(store, product, language);
		}
		
		model.addAttribute("relatedProducts",relatedItems);	
		Set<ProductAttribute> attributes = product.getAttributes();
		

		
		//split read only and options
		Map<Long,Attribute> readOnlyAttributes = null;
		Map<Long,Attribute> selectableOptions = null;
		
		if(!CollectionUtils.isEmpty(attributes)) {
						
			for(ProductAttribute attribute : attributes) {
				Attribute attr = null;
				AttributeValue attrValue = new AttributeValue();
				ProductOptionValue optionValue = attribute.getProductOptionValue();
				
				if(attribute.getAttributeDisplayOnly()==true) {//read only attribute
					if(readOnlyAttributes==null) {
						readOnlyAttributes = new TreeMap<Long,Attribute>();
					}
					attr = readOnlyAttributes.get(attribute.getProductOption().getId());
					if(attr==null) {
						attr = createAttribute(attribute, language);
					}
					if(attr!=null) {
						readOnlyAttributes.put(attribute.getProductOption().getId(), attr);
						attr.setReadOnlyValue(attrValue);
					}
				} else {//selectable option
					if(selectableOptions==null) {
						selectableOptions = new TreeMap<Long,Attribute>();
					}
					attr = selectableOptions.get(attribute.getProductOption().getId());
					if(attr==null) {
						attr = createAttribute(attribute, language);
					}
					if(attr!=null) {
						selectableOptions.put(attribute.getProductOption().getId(), attr);
					}
				}
				
				
				
				attrValue.setDefaultAttribute(attribute.getAttributeDefault());
				attrValue.setId(attribute.getId());//id of the attribute
				attrValue.setLanguage(language.getCode());
				if(attribute.getProductAttributePrice()!=null && attribute.getProductAttributePrice().doubleValue()>0) {
					String formatedPrice = pricingService.getDisplayAmount(attribute.getProductAttributePrice(), store);
					attrValue.setPrice(formatedPrice);
				}
				
				if(!StringUtils.isBlank(attribute.getProductOptionValue().getProductOptionValueImage())) {
					attrValue.setImage(imageUtils.buildProductPropertyImageUtils(store, attribute.getProductOptionValue().getProductOptionValueImage()));
				}
				attrValue.setSortOrder(0);
				if(attribute.getProductOptionSortOrder()!=null) {
					attrValue.setSortOrder(attribute.getProductOptionSortOrder().intValue());
				}
				
				List<ProductOptionValueDescription> descriptions = optionValue.getDescriptionsSettoList();
				ProductOptionValueDescription description = null;
				if(descriptions!=null && descriptions.size()>0) {
					description = descriptions.get(0);
					if(descriptions.size()>1) {
						for(ProductOptionValueDescription optionValueDescription : descriptions) {
							if(optionValueDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
								description = optionValueDescription;
								break;
							}
						}
					}
				}
				attrValue.setName(description.getName());
				attrValue.setDescription(description.getDescription());
				List<AttributeValue> attrs = attr.getValues();
				if(attrs==null) {
					attrs = new ArrayList<AttributeValue>();
					attr.setValues(attrs);
				}
				attrs.add(attrValue);
			}
			
		}
		
		

		List<ProductReview> reviews = productReviewService.getByProduct(product, language);
		if(!CollectionUtils.isEmpty(reviews)) {
			List<ReadableProductReview> revs = new ArrayList<ReadableProductReview>();
			ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
			for(ProductReview review : reviews) {
				ReadableProductReview rev = new ReadableProductReview();
				reviewPopulator.populate(review, rev, store, language);
				revs.add(rev);
			}
			model.addAttribute("reviews", revs);
		}
		
		List<Attribute> attributesList = null;
		if(readOnlyAttributes!=null) {
			attributesList = new ArrayList<Attribute>(readOnlyAttributes.values());
		}
		
		List<Attribute> optionsList = null;
		if(selectableOptions!=null) {
			optionsList = new ArrayList<Attribute>(selectableOptions.values());
			//order attributes by sort order
			for(Attribute attr : optionsList) {
				Collections.sort(attr.getValues(), new Comparator<AttributeValue>(){
				     public int compare(AttributeValue o1, AttributeValue o2){
					         if(o1.getSortOrder()== o2.getSortOrder())
					             return 0;
					         return o1.getSortOrder() < o2.getSortOrder() ? -1 : 1;
				    	
				     }
				});
			}
		}
		
		model.addAttribute("attributes", attributesList);
		model.addAttribute("options", optionsList);
			
		model.addAttribute("product", productProxy);

		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Product.product).append(".").append(store.getStoreTemplate());

		return template.toString();
	}
	
    @RequestMapping(value={"/{productId}/calculatePrice.json"}, method=RequestMethod.POST)
	public @ResponseBody
	ReadableProductPrice calculatePrice(@RequestParam(value="attributeIds[]") Long[] attributeIds, @PathVariable final Long productId, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) throws Exception {


		MerchantStoreDTO storeDTO = (MerchantStoreDTO) request.getAttribute(Constants.MERCHANT_STORE_DTO);
		MerchantStoreInfo store = this.merchantStoreInfoService.findbyCode(storeDTO.getCode());
		LanguageDTO languageDTO = (LanguageDTO) request.getAttribute("LANGUAGE_DTO");
		LanguageInfo language = this.languageInfoService.findbyCode(languageDTO.getCode());
		
		
		Product product = productService.getById(productId);
		
		@SuppressWarnings("unchecked")
		List<Long> ids = new ArrayList<Long>(Arrays.asList(attributeIds));
		List<ProductAttribute> attributes = productAttributeService.getByAttributeIds(store, product, ids);      
		
		for(ProductAttribute attribute : attributes) {
			if(attribute.getProduct().getId().longValue()!=productId.longValue()) {
				return null;
			}
		}
		
		FinalPrice price = pricingService.calculateProductPrice(product, attributes);
    	ReadableProductPrice readablePrice = new ReadableProductPrice();
    	ReadableFinalPricePopulator populator = new ReadableFinalPricePopulator();
    	populator.setPricingService(pricingService);
    	populator.populate(price, readablePrice, store, language);
    	return readablePrice;
    	
    }
	
	private Attribute createAttribute(ProductAttribute productAttribute, LanguageInfo language) {
		
		Attribute attribute = new Attribute();
		attribute.setId(productAttribute.getProductOption().getId());//attribute of the option
		attribute.setType(productAttribute.getProductOption().getProductOptionType());
		List<ProductOptionDescription> descriptions = productAttribute.getProductOption().getDescriptionsSettoList();
		ProductOptionDescription description = null;
		if(descriptions!=null && descriptions.size()>0) {
			description = descriptions.get(0);
			if(descriptions.size()>1) {
				for(ProductOptionDescription optionDescription : descriptions) {
					if(optionDescription.getLanguage().getId().intValue()==language.getId().intValue()) {
						description = optionDescription;
						break;
					}
				}
			}
		}
		
		if(description==null) {
			return null;
		}
		
		attribute.setType(productAttribute.getProductOption().getProductOptionType());
		attribute.setLanguage(language.getCode());
		attribute.setName(description.getName());
		attribute.setCode(productAttribute.getProductOption().getCode());

		
		return attribute;
		
	}
	
	private List<ReadableProduct> relatedItems(MerchantStoreInfo store, Product product, LanguageInfo language) throws Exception {
		
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		
		List<ProductRelationship> relatedItems = productRelationshipService.getByType(store, product, ProductRelationshipType.RELATED_ITEM);
		if(relatedItems!=null && relatedItems.size()>0) {
			List<ReadableProduct> items = new ArrayList<ReadableProduct>();
			for(ProductRelationship relationship : relatedItems) {
				Product relatedProduct = relationship.getRelatedProduct();
				ReadableProduct proxyProduct = populator.populate(relatedProduct, new ReadableProduct(), store, language);
				items.add(proxyProduct);
			}
			return items;
		}
		
		return null;
	}
	


}
