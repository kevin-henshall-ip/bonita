package com.company.rest.api;

import com.company.rest.api.common.Constants;
import com.company.rest.api.common.RestApiError;
import com.company.rest.api.common.RestApiException;
import com.company.rest.api.common.RestApiUtils;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class TemplateRestApi extends BaseRestApiExtension {
    // logger name should start with 'org.bonitasoft' so that log messages will appear in the default bonita log file
    private static final Logger LOGGER = LoggerFactory.getLogger("org.bonitasoft.rest.api.TemplateRestApi");

    @Override
    public RestApiResponse doHandle(HttpServletRequest request,
                                    RestApiResponseBuilder responseBuilder,
                                    RestAPIContext restApiContext) {
        try {
            initialiseBonitaApiAccessors(restApiContext);

            // retrieve params
            retrieveParameters(request);

            // perform tasks using the bonita java apis
            // Refer to
            // https://documentation.bonitasoft.com/javadoc/api/7.6/index.html for the associated bonita javadoc

            // construct response
            TemplateRestApiResponse templateRestApiResponse = new TemplateRestApiResponse();

            // return response
            return RestApiUtils.buildResponse(responseBuilder,
                    HttpServletResponse.SC_OK, RestApiUtils.toJson(templateRestApiResponse));
        } catch (RestApiException e) {
            String errorMessage = "An error was encountered within TemplateRestApi: " + e.getMessage();
            LOGGER.error(errorMessage);
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            RestApiError restApiError = new RestApiError(e.getErrorCode(), errorMessage);
            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST,
                    RestApiUtils.toJson(restApiError));
        } catch (Exception e) {
            String errorMessage = "An error was encountered within TemplateRestApi: " + e.getMessage();
            LOGGER.error(errorMessage);
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            RestApiError restApiError = new RestApiError(Constants.ERROR_UNKNOWN_ERROR, errorMessage);
            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST,
                    RestApiUtils.toJson(restApiError));
        }
    }

    /**
     * Retrieve parameters from the given request
     * @param request The request
     * @throws RestApiException If an error is encountered
     */
    public void retrieveParameters(HttpServletRequest request) throws RestApiException {
        // retrieve parameters from the provided request object
    }
}
