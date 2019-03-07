package com.salesmanager.catalog.presentation.tag;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.presentation.util.CatalogImageFilePathUtils;
import com.salesmanager.catalog.presentation.util.UriUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.salesmanager.common.presentation.constants.Constants;



public class ShopProductImageUrlTag extends RequestContextAwareTag {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShopProductImageUrlTag.class);

	private final static String SMALL = "SMALL";
	private final static String LARGE = "LARGE";

	private String imageName;
	private String sku;
	private String size; //SMALL | LARGE
	
	@Inject
	private UriUtils filePathUtils;
	
	@Autowired
	private CatalogImageFilePathUtils imageUtils;

	@Autowired
	private MerchantStoreInfoService merchantStoreInfoService;
	
	public int doStartTagInternal() throws JspException {
		try {
			
			if (filePathUtils==null || imageUtils==null) {
	            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
	            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
	            factory.autowireBean(this);
	        }

			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();

			String storeCode = (String) request.getSession().getAttribute(Constants.MERCHANT_STORE_CODE);
			MerchantStoreInfo store = this.merchantStoreInfoService.findbyCode(storeCode);

			StringBuilder imagePath = new StringBuilder();
			
			String baseUrl = filePathUtils.getRelativeStoreUri(store, request.getContextPath());
			imagePath.append(baseUrl);
			
			if(StringUtils.isBlank(this.getSize()) || this.getSize().equals(SMALL)) {
				imagePath.append(imageUtils.buildProductImageUtils(store, this.getSku(), this.getImageName())).toString();
			} else {
				imagePath.append(imageUtils.buildLargeProductImageUtils(store, this.getSku(), this.getImageName())).toString();
			}

			//System.out.println("Printing image -M " + imagePath.toString());

			pageContext.getOut().print(imagePath.toString());


			
		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}
		return SKIP_BODY;
	}


	public int doEndTag() {
		return EVAL_PAGE;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageName() {
		return imageName;
	}



	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSku() {
		return sku;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}




	

}
