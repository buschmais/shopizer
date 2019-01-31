package com.salesmanager.shop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.catalog.api.CategoryApi;
import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.common.presentation.model.Breadcrumb;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.common.presentation.model.BreadcrumbItemType;
import com.salesmanager.common.presentation.model.PageInformation;
import com.salesmanager.common.presentation.util.LabelUtils;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.MerchantConfig;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.model.system.MerchantConfigurationType;
import com.salesmanager.common.presentation.constants.Constants;
import com.salesmanager.shop.model.customer.Address;
import com.salesmanager.shop.model.customer.AnonymousCustomer;
import com.salesmanager.shop.utils.GeoLocationUtils;
import com.salesmanager.shop.utils.LanguageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet Filter implementation class StoreFilter
 */

public class StoreFilter extends HandlerInterceptorAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreFilter.class);
	
	private final static String STORE_REQUEST_PARAMETER = "store";

	
	@Inject
	private ContentService contentService;
	
	@Inject
	private CategoryApi categoryApi;

	@Inject
	protected ProductApi productApi;

	@Inject
	private MerchantStoreService merchantService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	
	@Inject
	private LanguageService languageService;

	@Inject
	private LabelUtils messages;
	
	@Inject
	private LanguageUtils languageUtils;
	
	@Inject
	private CacheUtils cache;
	
	@Inject
	private CoreConfiguration coreConfiguration;
	
	private final static String SERVICES_URL_PATTERN = "/services";
	private final static String REFERENCE_URL_PATTERN = "/reference";
	


    /**
     * Default constructor. 
     */
    public StoreFilter() {

    }
    
	   public boolean preHandle(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            Object handler) throws Exception {

			request.setCharacterEncoding("UTF-8");
			
			/**
			 * if url contains /services
			 * exit from here !
			 */
			//System.out.println("****** " + request.getRequestURL().toString());
			if(request.getRequestURL().toString().toLowerCase().contains(SERVICES_URL_PATTERN)
				|| request.getRequestURL().toString().toLowerCase().contains(REFERENCE_URL_PATTERN)	
			) {
				return true;
			}
			
			/*****
			 * where is my stuff
			 */
			//String currentPath = System.getProperty("user.dir");
			//System.out.println("*** user.dir ***" + currentPath);
			//LOGGER.debug("*** user.dir ***" + currentPath);

			try {

				/** merchant store **/
				MerchantStoreDTO storeDTO = (MerchantStoreDTO) request.getSession().getAttribute(Constants.MERCHANT_STORE_DTO);
	
				String storeCode = request.getParameter(STORE_REQUEST_PARAMETER);
				
				//remove link set from controllers for declaring active - inactive links
				request.removeAttribute(Constants.LINK_CODE);
				
				if(!StringUtils.isBlank(storeCode)) {
					if(storeDTO!=null) {
						if(!storeDTO.getCode().equals(storeCode)) {
							storeDTO = setMerchantStoreInSession(request, storeCode);
						}
					}else{ // when url sm-shop/shop is being loaded for first time store is null
						storeDTO = setMerchantStoreInSession(request, storeCode);
					}
				}

				if(storeDTO==null) {
					storeDTO = setMerchantStoreInSession(request, MerchantStore.DEFAULT_STORE);
				}
				MerchantStore store = merchantService.getMerchantStore(storeDTO.getCode());
				
				request.setAttribute(Constants.MERCHANT_STORE_DTO, storeDTO);
				request.setAttribute(Constants.MERCHANT_STORE, store);
				
				/** customer **/
				Customer customer = (Customer)request.getSession().getAttribute(Constants.CUSTOMER);
				if(customer!=null) {
					if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						request.getSession().removeAttribute(Constants.CUSTOMER);
					}
					if(!customer.isAnonymous()) {
			        	if(!request.isUserInRole("AUTH_CUSTOMER")) {
			        			request.removeAttribute(Constants.CUSTOMER);
				        }
					}
					request.setAttribute(Constants.CUSTOMER, customer);
				} 
				
				if(customer==null) {
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        	if(auth != null &&
			        		 request.isUserInRole("AUTH_CUSTOMER")) {
		        		customer = customerService.getByNick(auth.getName());
		        		if(customer!=null) {
		        			request.setAttribute(Constants.CUSTOMER, customer);
		        		}
			        } 
					
				}
				
				
				
				AnonymousCustomer anonymousCustomer =  (AnonymousCustomer)request.getSession().getAttribute(Constants.ANONYMOUS_CUSTOMER);
				if(anonymousCustomer==null) {
					
					Address address = null;
					try {
						
						String ipAddress = GeoLocationUtils.getClientIpAddress(request);
						com.salesmanager.core.model.common.Address geoAddress = customerService.getCustomerAddress(store, ipAddress);
						if(geoAddress!=null) {
							address = new Address();
							address.setCountry(geoAddress.getCountry());
							address.setCity(geoAddress.getCity());
							address.setZone(geoAddress.getZone());
							/** no postal code **/
							//address.setPostalCode(geoAddress.getPostalCode());
						}
					} catch(Exception ce) {
						LOGGER.error("Cannot get geo ip component ", ce);
					}
					
					if(address==null) {
						address = new Address();
						address.setCountry(store.getCountry().getIsoCode());
						if(store.getZone()!=null) {
							address.setZone(store.getZone().getCode());
						} else {
							address.setStateProvince(store.getStorestateprovince());
						}
						/** no postal code **/
						//address.setPostalCode(store.getStorepostalcode());
					}
					
					anonymousCustomer = new AnonymousCustomer();
					anonymousCustomer.setBilling(address);
					request.getSession().setAttribute(Constants.ANONYMOUS_CUSTOMER, anonymousCustomer);
				} else {
					request.setAttribute(Constants.ANONYMOUS_CUSTOMER, anonymousCustomer);
				}
				
				
				
				
				/** language & locale **/
				Language language = languageUtils.getRequestLanguage(request, response);
				request.setAttribute(Constants.LANGUAGE, language);
				request.setAttribute(Constants.LANGUAGE_DTO, language.toDTO());
				
				
				Locale locale = languageService.toLocale(language, store);
				request.setAttribute(Constants.LOCALE, locale);
				
				//Locale locale = LocaleContextHolder.getLocale();
				LocaleContextHolder.setLocale(locale);
				
				/** Breadcrumbs **/
				setBreadcrumb(request,locale);

				
				/**
				 * Get global objects
				 * Themes are built on a similar way displaying
				 * Header, Body and Footer
				 * Header and Footer are displayed on each page
				 * Some themes also contain side bars which may include
				 * similar emements
				 * 
				 * Elements from Header :
				 * - CMS links
				 * - Customer
				 * - Mini shopping cart
				 * - Store name / logo
				 * - Top categories
				 * - Search
				 * 
				 * Elements from Footer :
				 * - CMS links
				 * - Store address
				 * - Global payment information
				 * - Global shipping information
				 */
				

				//get from the cache first
				/**
				 * The cache for each object contains 2 objects, a Cache and a Missed-Cache
				 * Get objects from the cache
				 * If not null use those objects
				 * If null, get entry from missed-cache
				 * If missed-cache not null then nothing exist
				 * If missed-cache null, add missed-cache entry and load from the database
				 * If objects from database not null store in cache
				 */
				
				/******* CMS Objects ********/
				this.getContentObjects(store, language, request);
				
				/******* CMS Page names **********/
				this.getContentPageNames(store, language, request);
				
				/******* Default metatags *******/
				
				/**
				 * Title
				 * Description
				 * Keywords
				 */
				
				PageInformation pageInformation = new PageInformation();
				pageInformation.setPageTitle(store.getStorename());
				pageInformation.setPageDescription(store.getStorename());
				pageInformation.setPageKeywords(store.getStorename());
				
				
				@SuppressWarnings("unchecked")
				Map<String, ContentDescription> contents = (Map<String, ContentDescription>)request.getAttribute(Constants.REQUEST_CONTENT_OBJECTS);
				
				if(contents!=null) {
					//for(String key : contents.keySet()) {
						//List<ContentDescription> contentsList = contents.get(key);
						//for(Content content : contentsList) {
							//if(key.equals(Constants.CONTENT_LANDING_PAGE)) {
								
								//List<ContentDescription> descriptions = content.getDescriptions();
								ContentDescription contentDescription = contents.get(Constants.CONTENT_LANDING_PAGE);
								if(contentDescription!=null) {
								//for(ContentDescription contentDescription : descriptions) {
									//if(contentDescription.getLanguage().getCode().equals(language.getCode())) {
										pageInformation.setPageTitle(contentDescription.getName());
										pageInformation.setPageDescription(contentDescription.getMetatagDescription());
										pageInformation.setPageKeywords(contentDescription.getMetatagKeywords());
									//}
								}
							//}
						//}
					//}
				}
				
				request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);
				
				
				/******* Configuration objects  *******/
				
				/**
				 * SHOP configuration type
				 * Should contain
				 * - Different configuration flags
				 * - Google analytics
				 * - Facebook page
				 * - Twitter handle
				 * - Show customer login
				 * - ...
				 */
				
				this.getMerchantConfigurations(store,request);
				
				/******* Shopping Cart *********/
				
				String shoppingCarCode = (String)request.getSession().getAttribute(Constants.SHOPPING_CART);
				if(shoppingCarCode!=null) {
					request.setAttribute(Constants.REQUEST_SHOPPING_CART, shoppingCarCode);
				}
				

			
			} catch (Exception e) {
				LOGGER.error("Error in StoreFilter",e);
			}

			return true;
		   
	   }
	   
	   @SuppressWarnings("unchecked")
	   private void getMerchantConfigurations(MerchantStore store, HttpServletRequest request) throws Exception {
		   
	
			
			StringBuilder configKey = new StringBuilder();
			configKey
			.append(store.getId())
			.append("_")
			.append(Constants.CONFIG_CACHE_KEY);
			
			
			StringBuilder configKeyMissed = new StringBuilder();
			configKeyMissed
			.append(configKey.toString())
			.append(Constants.MISSED_CACHE_KEY);
			
			Map<String, Object> configs = null;
			
			if(store.isUseCache()) {
			
				//get from the cache
				configs = (Map<String, Object>) cache.getFromCache(configKey.toString());
				if(configs==null) {
					//get from missed cache
					//Boolean missedContent = (Boolean)cache.getFromCache(configKeyMissed.toString());

				   //if( missedContent==null) {
					    configs = this.getConfigurations(store);
						//put in cache
					    
					    if(configs!=null) {
					    	cache.putInCache(configs, configKey.toString());
					    } else {
					    	//put in missed cache
					    	//cache.putInCache(new Boolean(true), configKeyMissed.toString());
					    }
				   //}
				}

			} else {
				 configs = this.getConfigurations(store);
			}
			
			
			if(configs!=null && configs.size()>0) {
				request.setAttribute(Constants.REQUEST_CONFIGS, configs);
			}
		   
		   
	   }
	   
	   
		@SuppressWarnings("unchecked")
		private void getContentPageNames(MerchantStore store, Language language, HttpServletRequest request) throws Exception {
			   

				/**
				 * CMS links
				 * Those links are implemented as pages (Content)
				 * ContentDescription will provide attributes name for the
				 * label to be displayed and seUrl for the friendly url page
				 */
				
				//build the key
				/**
				 * The cache is kept as a Map<String,Object>
				 * The key is <MERCHANT_ID>_CONTENTPAGELOCALE
				 * The value is a List of Content object
				 */
				
				StringBuilder contentKey = new StringBuilder();
				contentKey
				.append(store.getId())
				.append("_")
				.append(Constants.CONTENT_PAGE_CACHE_KEY)
				.append("-")
				.append(language.getCode());
				
				StringBuilder contentKeyMissed = new StringBuilder();
				contentKeyMissed
				.append(contentKey.toString())
				.append(Constants.MISSED_CACHE_KEY);
				
				Map<String, List<ContentDescription>> contents = null;
				
				if(store.isUseCache()) {
				
					//get from the cache
					contents = (Map<String, List<ContentDescription>>) cache.getFromCache(contentKey.toString());
					

					if(contents==null) {
						//get from missed cache
						//Boolean missedContent = (Boolean)cache.getFromCache(contentKeyMissed.toString());

					
						//if(missedContent==null) {
						
							contents = this.getContentPagesNames(store, language);

							if(contents!=null) {
								//put in cache
								cache.putInCache(contents, contentKey.toString());
							
							} else {
								//put in missed cache
								//cache.putInCache(new Boolean(true), contentKeyMissed.toString());
							}
						//}		
				   } 
				} else {
					contents = this.getContentPagesNames(store, language);	
				}
				
				
				if(contents!=null && contents.size()>0) {
					List<ContentDescription> descriptions = contents.get(contentKey.toString());
					
					if(descriptions!=null) {
						request.setAttribute(Constants.REQUEST_CONTENT_PAGE_OBJECTS, descriptions);
					}
				}	   
	 }
	   
	@SuppressWarnings({ "unchecked"})
	private void getContentObjects(MerchantStore store, Language language, HttpServletRequest request) throws Exception {
		   

			/**
			 * CMS links
			 * Those links are implemented as pages (Content)
			 * ContentDescription will provide attributes name for the
			 * label to be displayed and seUrl for the friendly url page
			 */
			
			//build the key
			/**
			 * The cache is kept as a Map<String,Object>
			 * The key is CONTENT_<MERCHANT_ID>_<LOCALE>
			 * The value is a List of Content object
			 */
			
			StringBuilder contentKey = new StringBuilder();
			contentKey
			.append(store.getId())
			.append("_")
			.append(Constants.CONTENT_CACHE_KEY)
			.append("-")
			.append(language.getCode());
			
			StringBuilder contentKeyMissed = new StringBuilder();
			contentKeyMissed
			.append(contentKey.toString())
			.append(Constants.MISSED_CACHE_KEY);
			
			Map<String, List<Content>> contents = null;
			
			if(store.isUseCache()) {
			
				//get from the cache
				contents = (Map<String, List<Content>>) cache.getFromCache(contentKey.toString());

				
				if(contents==null) {

					//get from missed cache
					 //Boolean missedContent = (Boolean)cache.getFromCache(contentKeyMissed.toString());
					
					
					//if(missedContent==null) {
					
						contents = this.getContent(store, language);
						if(contents!=null && contents.size()>0) {
							//put in cache
							cache.putInCache(contents, contentKey.toString());
						} else {
							//put in missed cache
							//cache.putInCache(new Boolean(true), contentKeyMissed.toString());
						}
					//}		
						
				}
			} else {

				contents = this.getContent(store, language);	

			}
			
			
			
			if(contents!=null && contents.size()>0) {

					//request.setAttribute(Constants.REQUEST_CONTENT_OBJECTS, contents);
				
					List<Content> contentByStore = contents.get(contentKey.toString());
					if(!CollectionUtils.isEmpty(contentByStore)) {
						Map<String, ContentDescription> contentMap = new HashMap<String,ContentDescription>();
						for(Content content : contentByStore) {
							if(content.isVisible()) {
								contentMap.put(content.getCode(), content.getDescription());
							}
						}
						request.setAttribute(Constants.REQUEST_CONTENT_OBJECTS, contentMap);
					}

				
			}

		   
    }

	
	   private Map<String, List<ContentDescription>> getContentPagesNames(MerchantStore store, Language language) throws Exception {
		   
		   
		    Map<String, List<ContentDescription>> contents = new ConcurrentHashMap<String, List<ContentDescription>>();
		   
			//Get boxes and sections from the database
			List<ContentType> contentTypes = new ArrayList<ContentType>();
			contentTypes.add(ContentType.PAGE);

			
			List<ContentDescription> contentPages = contentService.listNameByType(contentTypes, store, language);
			
			if(contentPages!=null && contentPages.size()>0) {
				
				//create a Map<String,List<Content>
				for(ContentDescription content : contentPages) {


						Language lang = language;
						String key = new StringBuilder()
						.append(store.getId())
						.append("_")
						.append(Constants.CONTENT_PAGE_CACHE_KEY)
						.append("-")
						.append(lang.getCode()).toString();
						List<ContentDescription> contentList = null;
						if(contents==null || contents.size()==0) {
							contents = new HashMap<String, List<ContentDescription>>();
						}
						if(!contents.containsKey(key)) {
							contentList = new ArrayList<ContentDescription>();

							contents.put(key, contentList);
						} else {//get from key
							contentList = contents.get(key);
							if(contentList==null) {
								LOGGER.error("Cannot find content key in cache " + key);
								continue;
							}
						}
						contentList.add(content);
				}
			}
			return contents;
	   }
	   
	   private Map<String, List<Content>> getContent(MerchantStore store, Language language) throws Exception {
		   
		   
		   Map<String, List<Content>> contents = new ConcurrentHashMap<String, List<Content>>();
		   
			//Get boxes and sections from the database
			List<ContentType> contentTypes = new ArrayList<ContentType>();
			contentTypes.add(ContentType.BOX);
			contentTypes.add(ContentType.SECTION);
			
			List<Content> contentPages = contentService.listByType(contentTypes, store, language);
			
			if(contentPages!=null && contentPages.size()>0) {
				
				//create a Map<String,List<Content>
				for(Content content : contentPages) {
					if(content.isVisible()) {
						List<ContentDescription> descriptions = content.getDescriptions();
						for(ContentDescription contentDescription : descriptions) {
							Language lang = contentDescription.getLanguage();
							String key = new StringBuilder()
							.append(store.getId())
							.append("_")
							.append(Constants.CONTENT_CACHE_KEY)
							.append("-")
							.append(lang.getCode()).toString();
							List<Content> contentList = null;
							if(contents==null || contents.size()==0) {
								contents = new HashMap<String, List<Content>>();
							}
							if(!contents.containsKey(key)) {
								contentList = new ArrayList<Content>();
	
								contents.put(key, contentList);
							}else {//get from key
								contentList = contents.get(key);
								if(contentList==null) {
									LOGGER.error("Cannot find content key in cache " + key);
									continue;
								}
							}
							contentList.add(content);
						}
					}
				}
			}
			return contents;
	   }
	   
	   @SuppressWarnings("unused")
	private Map<String,Object> getConfigurations(MerchantStore store) {
		   
		   Map<String,Object> configs = new HashMap<String,Object>();
		   try {
			   
			   List<MerchantConfiguration> merchantConfiguration = merchantConfigurationService.listByType(MerchantConfigurationType.CONFIG, store);
			   
			   if(CollectionUtils.isEmpty(merchantConfiguration)) {
				   return configs;
			   }
			   
			   
			   for(MerchantConfiguration configuration : merchantConfiguration) {
				   configs.put(configuration.getKey(), configuration.getValue());
			   }
			   
			   configs.put(Constants.SHOP_SCHEME, coreConfiguration.getProperty(Constants.SHOP_SCHEME));
			   configs.put(Constants.FACEBOOK_APP_ID, coreConfiguration.getProperty(Constants.FACEBOOK_APP_ID));
			   
			   //get MerchantConfig
			   MerchantConfig merchantConfig = merchantConfigurationService.getMerchantConfig(store);
			   if(merchantConfig!=null) {
				   if(configs==null) {
					   configs = new HashMap<String,Object>();
				   }
				   
				   ObjectMapper m = new ObjectMapper();
				   @SuppressWarnings("unchecked")
				   Map<String,Object> props = m.convertValue(merchantConfig, Map.class);
				   
				   for(String key : props.keySet()) {
					   configs.put(key, props.get(key));
				   }
			   }
		   } catch (Exception e) {
			   LOGGER.error("Exception while getting configurations",e);
		   }
		   
		   return configs;
		   
	   }
	   
	   private void setBreadcrumb(HttpServletRequest request, Locale locale) {
		   
		   
		   
		   try {
			
				//breadcrumb
				Breadcrumb breadCrumb = (Breadcrumb) request.getSession().getAttribute(Constants.BREADCRUMB);
				Language language = (Language)request.getAttribute(Constants.LANGUAGE);
				if(breadCrumb==null) {
					breadCrumb = new Breadcrumb();
					breadCrumb.setLanguage(language.getCode());
					BreadcrumbItem item = this.getDefaultBreadcrumbItem(language, locale);
					breadCrumb.getBreadCrumbs().add(item);
				} else {
					
					//check language
					if(language.getCode().equals(breadCrumb.getLanguageCode())) {
						
						//rebuild using the appropriate language
						List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
						for(BreadcrumbItem item : breadCrumb.getBreadCrumbs()) {
							
							if(item.getItemType().name().equals(BreadcrumbItemType.HOME)) {
								BreadcrumbItem homeItem = this.getDefaultBreadcrumbItem(language, locale);
								homeItem.setItemType(BreadcrumbItemType.HOME);
								homeItem.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, locale));
								homeItem.setUrl(Constants.HOME_URL);
								items.add(homeItem);
							} else if(item.getItemType().name().equals(BreadcrumbItemType.PRODUCT)) {
								BreadcrumbItem productItem = productApi.getBreadcrumbItemForLocale(item.getId(), language.toDTO(), locale);
								if (productItem != null) {
									items.add(productItem);
								}
							}else if(item.getItemType().name().equals(BreadcrumbItemType.CATEGORY)) {
								BreadcrumbItem categoryItem = categoryApi.getBreadcrumbItemForLocale(item.getId(), language.toDTO());
								if (categoryItem != null) {
									items.add(categoryItem);
								}
							}else if(item.getItemType().name().equals(BreadcrumbItemType.PAGE)) {
								Content content = contentService.getByLanguage(item.getId(), language);
								if(content!=null) {
									BreadcrumbItem contentItem = new  BreadcrumbItem();
									contentItem.setId(content.getId());
									contentItem.setItemType(BreadcrumbItemType.PAGE);
									contentItem.setLabel(content.getDescription().getName());
									contentItem.setUrl(content.getDescription().getSeUrl());
									items.add(contentItem);
								}
							}
							
						}
						
						breadCrumb = new Breadcrumb();
						breadCrumb.setLanguage(language.getCode());
						breadCrumb.setBreadCrumbs(items);
						
					}
					
				}
			
			request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
			request.setAttribute(Constants.BREADCRUMB, breadCrumb);
			
			} catch (Exception e) {
				LOGGER.error("Error while building breadcrumbs",e);
			}
		   
	   }
	   
	   private BreadcrumbItem getDefaultBreadcrumbItem(Language language, Locale locale) {

			//set home page item
			BreadcrumbItem item = new BreadcrumbItem();
			item.setItemType(BreadcrumbItemType.HOME);
			item.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, locale));
			item.setUrl(Constants.HOME_URL);
			return item;
		   
	   }

	   /**
	    * Sets a MerchantStore with the given storeCode in the session.
	    * @param request
	    * @param storeCode The storeCode of the Merchant.
	    * @return the MerchantStore inserted in the session.
	    * @throws Exception
	    */
	   private MerchantStoreDTO setMerchantStoreInSession(HttpServletRequest request, String storeCode) throws Exception{
		   if(storeCode == null || request == null)
			   return null;
		   MerchantStore store = merchantService.getByCode(storeCode);
			if(store!=null) {
				MerchantStoreDTO storeDTO = store.toDTO();
				request.getSession().setAttribute(Constants.MERCHANT_STORE_DTO, storeDTO);
				request.getSession().setAttribute(Constants.MERCHANT_STORE, store);
				return storeDTO;
			}		
			return null;
	   }

}
