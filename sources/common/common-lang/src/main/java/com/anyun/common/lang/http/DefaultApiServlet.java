package com.anyun.common.lang.http;

import com.anyun.common.lang.StringUtils;
import com.anyun.common.lang.bean.InjectorsBuilder;
import com.anyun.common.lang.http.entity.BaseResponseEntity;
import com.anyun.common.lang.http.entity.ResponseRootNode;
import com.anyun.common.lang.http.format.FormatBuilder;
import com.anyun.common.lang.http.format.ResponseFormatBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @auth TwitchGG <twitchgg@yahoo.com>
 * @since 1.0.0 on 23/05/2017
 */
@Singleton
public class DefaultApiServlet extends HttpServlet {
    public static final int ERROR_404 = 404;
    public static final int ERROR_500 = 500;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultApiServlet.class);

    @Inject
    public DefaultApiServlet() {
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseRootNode rootNode = new ResponseRootNode();
        long current = System.currentTimeMillis();
        response.setCharacterEncoding("utf-8");
        String pathInfo = request.getPathInfo();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String remote = request.getRemoteAddr();
        String responseContentType = getResponseContentType(request);
        LOGGER.debug("Remote: {}", remote);
        LOGGER.debug("Path info: {}", pathInfo);
        LOGGER.debug("Method: {}", method);
        LOGGER.debug("Query string: {}", queryString);
        Map<String, List<String>> parameters = ParamaterUtil.getUriQueryParameters(queryString);
        if (StringUtils.isNotEmpty(queryString)) {
            LOGGER.debug("========= Query Parameters ==========");
            for (Map.Entry<String, List<String>> parameter : parameters.entrySet()) {
                LOGGER.debug("Name: {}     Value: {}", parameter.getKey(), parameter.getValue());
            }
        }
        AbstractApiCallbackBindModule apiCallbackBindModule =
                InjectorsBuilder.getBuilder().getInstanceByType(AbstractApiCallbackBindModule.class);
        ApiCallback callback = apiCallbackBindModule.getCallbackByName(pathInfo, ApiCallback.HttpMethod.valueOf(method));
        if (callback == null) {
            LOGGER.debug("Not found api callback by path info [{}]", pathInfo);
            String errorMessage = "Not found api callback by path info [" + pathInfo + "]";
            response.sendError(ERROR_404, errorMessage);
            return;
        }
        try {
            BaseResponseEntity entity = callback.callback(request);
            rootNode.setResult(entity);
            rootNode.setAction(pathInfo);
            PrintWriter writer = response.getWriter();
            response.setContentType(responseContentType);
            FormatBuilder formatBuilder = new ResponseFormatBuilder().withType(responseContentType).build();
            LOGGER.debug("Matched format builder [{}]", formatBuilder);
            if (formatBuilder == null)
                throw new Exception("Unsupported format type [" + responseContentType + "]");
            rootNode.setSystemDate(new Date(System.currentTimeMillis()));
            rootNode.setExecMillisecond(System.currentTimeMillis() - current);
            String responseString = formatBuilder.asString(rootNode);
            LOGGER.debug("Response string [\n{}\n]", responseString);
            writer.write(responseString);
            writer.flush();
        } catch (Exception e) {
            LOGGER.debug("Api callback [{}] error: {}", pathInfo, e.getMessage(), e);
            String errorMessage = "Api callback [" + pathInfo + "] error: " + e.getMessage();
            response.sendError(ERROR_500, errorMessage);
            return;
        }
    }

    private String getResponseContentType(HttpServletRequest request) throws IOException {
        String queryString = request.getQueryString();
        Map<String, List<String>> parameters = ParamaterUtil.getUriQueryParameters(queryString);
        List<String> format = parameters.get("format");
        if (format == null || format.isEmpty())
            return "json";
        String formatName = format.get(0);
        LOGGER.debug("response accept content type [{}]", formatName);
        return format.get(0);
    }
}