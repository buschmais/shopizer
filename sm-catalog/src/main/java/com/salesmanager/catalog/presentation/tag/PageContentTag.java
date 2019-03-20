package com.salesmanager.catalog.presentation.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

@Component
public class PageContentTag extends RequestContextAwareTag  {



	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PageContentTag.class);

	private String contentCode;




	public String getContentCode() {
		return contentCode;
	}


	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}


	@Override
	protected int doStartTagInternal() throws Exception {
		/*if (contentService == null || contentService==null) {
			LOGGER.debug("Autowiring contentService");
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
            factory.autowireBean(this);
        }

		HttpServletRequest request = (HttpServletRequest) pageContext
		.getRequest();

		Language language = (Language)request.getAttribute(Constants.LANGUAGE);

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		Content content = contentService.getByCode(contentCode, store, language);

		String pageContent = "";
		if(content!=null) {
			ContentDescription description = content.getDescription();
			if(description != null) {
				pageContent = description.getDescription();
			}
		}


		pageContext.getOut().print(pageContent);*/
		pageContext.getOut().print("HALLO");
		return SKIP_BODY;

	}


	public int doEndTag() {
		return EVAL_PAGE;
	}




}
