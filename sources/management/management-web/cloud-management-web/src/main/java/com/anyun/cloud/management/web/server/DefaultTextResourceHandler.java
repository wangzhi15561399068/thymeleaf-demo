package com.anyun.cloud.management.web.server;

import com.anyun.cloud.management.web.common.ResourceResolver;
import com.anyun.cloud.management.web.common.thymeleaf.ThymeleafContext;
import com.anyun.common.lang.bean.InjectorsBuilder;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by twitchgg on 11/07/2017.
 */
public class DefaultTextResourceHandler implements ResourceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTextResourceHandler.class);
    private static final String RESOURCE_JS = ".js";
    private static final String RESOURCE_CSS = ".css";
    private ResourceResolver resourceResolver;

    public DefaultTextResourceHandler() {
        this.resourceResolver = InjectorsBuilder.getBuilder()
                .getInstanceByType(ThymeleafContext.class).getResourceResolver();
    }

    @Override
    public void process(ServletRequest request, ServletResponse response) throws IOException {
        LOGGER.debug("Resource resolver [{}]", resourceResolver);
        byte[] resourceBytes = resourceResolver.resolveResourceBytes((HttpServletRequest) request);
        response.setContentType(getResourceContentType(request));
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(resourceBytes);
        outputStream.flush();
    }

    @Override
    public boolean isResource(ServletRequest request) {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        String resource = requestURI;
        if (requestURI.toLowerCase().endsWith(RESOURCE_JS) || requestURI.toLowerCase().endsWith(RESOURCE_CSS)) {
            LOGGER.debug("Resolve resource: {}", resource);
            return true;
        }
        return false;
    }

    private String getResourceContentType(ServletRequest request) {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        if (requestURI.toLowerCase().endsWith(RESOURCE_JS))
            return "text/javascript";
        if (requestURI.toLowerCase().endsWith(RESOURCE_CSS))
            return "text/css";
        return "text/html";
    }

}
