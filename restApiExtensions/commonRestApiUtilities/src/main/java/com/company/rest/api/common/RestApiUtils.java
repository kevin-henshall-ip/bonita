package com.company.rest.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bonitasoft.web.extension.ResourceProvider;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

public class RestApiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("org.bonitasoft.rest.api.RestApiUtils");
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    /**
     * Build an HTTP response.
     * @param responseBuilder the Rest API response builder
     * @param httpStatus      the status of the response
     * @param body            the response body
     * @return a RestAPIResponse
     */
    public static RestApiResponse buildResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Serializable body) {
        return new RestApiResponseBuilder()
                .withResponseStatus(httpStatus)
                .withResponse(body)
                .build();
    }

    /**
     * Retrieves a single parameter from the request
     * @param request       The HttpServletRequest
     * @param parameterName The parameter name to retrieve
     * @param required      Indicates whether an exception will be thrown if the parameter is required and it is not present, null or empty
     * @return The parameter value or null
     * @throws RestApiException If the parameter is required and it is not present, null or empty
     */
    public static String getParameter(HttpServletRequest request, String parameterName, boolean required)
            throws RestApiException {
        String parameterValue = request.getParameter(parameterName);
        if (parameterValue != null && parameterValue.trim().length() == 0) {
            parameterValue = null;
        }
        if (required && parameterValue == null) {
            throw new RestApiException(Constants.ERROR_REQUIRED_PARAMETER_MISSING, "Parameter '" + parameterName +
                    "' cannot be empty");
        }
        return parameterValue;
    }

    /**
     * Parses the json request body
     * @param request The HttpServletRequest
     * @return The parsed json request body
     * @throws RestApiException If an error is encountered
     */
    public Map parseJsonBody(HttpServletRequest request)
            throws RestApiException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map map = mapper.readValue(request.getInputStream(), Map.class);
            return map;
        } catch (IOException e) {
            LOGGER.error("Failed to parse json request body");
            throw new RestApiException(Constants.ERROR_INVALID_INPUT_PAYLOAD,
                    "Failed to parse json request body: " + e.toString());
        }
    }

    /**
     * Load a property file into a java.util.Properties
     * @param filename The name of the property file to load
     * @param resourceProvider The ResourceProvider as obtained from the RestAPIContext
     * @return The java properties object
     * @throws RestApiException If the property file fails to load
     */
    public static Properties loadProperties(String filename, ResourceProvider resourceProvider)
            throws RestApiException {
        Properties properties = new Properties();
        InputStream fileInputStream = null;
        try {
            fileInputStream = resourceProvider.getResourceAsStream(filename);
            // load properties file
            properties.load(fileInputStream);
            return properties;
        } catch (IOException e) {
            LOGGER.error("Failed to load property file '" + filename + "'");
            e.printStackTrace();
            throw new RestApiException(Constants.ERROR_FAILED_TO_LOAD_PROPERTY_FILE, "Failed to load property file '" +
                    filename + "'");
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close property file '" + filename + "'");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns a json string representation of the provided object
     * @param object The object
     * @return A json string representation of the provided object
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
