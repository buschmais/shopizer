package com.salesmanager.gateway.configuration;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class SessionFilter extends ZuulFilter {

    private final RouteLocator routeLocator;
    private final UrlPathHelper urlPathHelper;

    @Autowired
    public SessionFilter(RouteLocator routeLocator, UrlPathHelper urlPathHelper) {
        this.routeLocator = routeLocator;
        this.urlPathHelper = urlPathHelper;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        String requestURI = this.urlPathHelper.getPathWithinApplication(requestContext.getRequest());
        Route route = this.routeLocator.getMatchingRoute(requestURI);
        if (route != null && route.getLocation() != null) {
            // add session based on jsessionid url parameter
            HttpServletRequest request = requestContext.getRequest();
            boolean sessionIdCookiePresent = false;
            if (route.getId().equals("catalog") && request.getParameter("jsessionid") != null) {
                Cookie[] cookies = requestContext.getRequest().getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equalsIgnoreCase("jsessionid")) {
                            sessionIdCookiePresent = true;
                        }
                    }
                }
                if (!sessionIdCookiePresent) {
                    requestContext.addZuulRequestHeader("Cookie", "SESSION=" + request.getParameter("jsessionid"));
                }
            }
        }
        return null;
    }
}
