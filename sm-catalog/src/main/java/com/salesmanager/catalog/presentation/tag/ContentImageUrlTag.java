package com.salesmanager.catalog.presentation.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspException;


public class ContentImageUrlTag extends RequestContextAwareTag {


	/**
	 *
	 */
	private static final long serialVersionUID = 6319855234657139862L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentImageUrlTag.class);

	private String imageName;
	private String imageType;

	public int doStartTagInternal() throws JspException {
		/*try {


			if (filePathUtils==null || imageUtils==null) {
	            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
	            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
	            factory.autowireBean(this);
	        }

			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();

			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			if(this.getMerchantStore()!=null) {
				merchantStore = this.getMerchantStore();
			}

			String img = imageUtils.buildStaticImageUtils(merchantStore,this.getImageType(),this.getImageName());

			pageContext.getOut().print(img);



		} catch (Exception ex) {
			LOGGER.error("Error while getting content url", ex);
		}*/
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

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getImageType() {
		return imageType;
	}





}
