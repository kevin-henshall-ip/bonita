package com.company.rest.api;

import com.company.rest.api.common.Constants;
import com.company.rest.api.common.RestApiError;
import com.company.rest.api.common.RestApiException;
import com.company.rest.api.common.RestApiUtils;
import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstanceSearchDescriptor;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiController;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleRestApi implements RestApiController {

    public static final String PARAM_TASKID = "taskId";

    private static final Logger LOGGER = LoggerFactory.getLogger("org.bonitasoft.rest.api.SampleRestApi");

    private Long taskId;
    private ProcessAPI processApi;
    private IdentityAPI identityApi;

    @Override
    public RestApiResponse doHandle(HttpServletRequest request,
                                    RestApiResponseBuilder responseBuilder,
                                    RestAPIContext context) {
        try {
            APIClient apiClient = context.getApiClient();
            processApi = apiClient.getProcessAPI();
            identityApi = apiClient.getIdentityAPI();

            // retrieve params
            retrieveParameters(request);

            // perform tasks using the bonita java apis. In this example, we retrieve the current user
            // and the list of human tasks assigned to them. Refer to TODO for the associated bonita javadoc

            // retrieve current user information
            long currentUserId = context.getApiSession().getUserId();
            User currentUser = identityApi.getUser(currentUserId);
            LOGGER.info("currentUser: " + currentUser);

            // retrieve tasks assigned to current user
            SearchOptions searchOptions = createAssignedTaskSearchOptions(currentUserId);
            SearchResult<HumanTaskInstance> humanTasksAssignedToUserSearchResult = processApi.searchHumanTaskInstances(searchOptions);
            List<HumanTaskInstance> humanTasksAssignedToCurrentUser = humanTasksAssignedToUserSearchResult.getResult();
            LOGGER.info("humanTasksAssignedToUserSearchResult: " + humanTasksAssignedToCurrentUser);

            // construct response
            Map<String, Object> response = new HashMap<>();
            response.put("currentUser", currentUser);
            response.put("humanTasksAssignedToCurrentUser", humanTasksAssignedToCurrentUser);

            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_OK, RestApiUtils.toJson(response));
        } catch (RestApiException e) {
            LOGGER.error("An error was encountered within SampleRestApi: " + e);
            LOGGER.error(e.getStackTrace().toString());
            RestApiError restApiError = new RestApiError(e.getErrorCode(), e.getMessage());
            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST, RestApiUtils.toJson(restApiError));
        } catch (Exception e) {
            LOGGER.error("An error was encountered within SampleRestApi: " + e);
            LOGGER.error(e.getStackTrace().toString());
            RestApiError restApiError = new RestApiError(Constants.ERROR_UNKNOWN_ERROR, e.getMessage());
            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST, RestApiUtils.toJson(restApiError));
        }
    }

    /**
     * Retrieve parameters from the given request
     * @param request The request
     * @throws RestApiException If an error is encountered
     */
    public void retrieveParameters(HttpServletRequest request) throws RestApiException {
        String taskIdString = RestApiUtils.getParameter(request, PARAM_TASKID, true);
        LOGGER.debug("taskIdString: " + taskIdString);
        validateParameters(taskIdString);
    }

    /**
     * Validates the given parameters
     * @param taskIdString The taskId parameter value retrieved from the request
     * @throws RestApiException If a validation error is encountered
     */
    private void validateParameters(String taskIdString)
            throws RestApiException {
        try {
            taskId = Long.parseLong(taskIdString);
        } catch (NumberFormatException e) {
            throw new RestApiException(Constants.ERROR_INVALID_PARAMETER, "Parameter '" + PARAM_TASKID +
                    "' must be a positive integer");
        }
    }

    /**
     * Returns a search options object for finding active tasks assigned to the given userId
     * @param userId The userId
     * @return A search options object for finding active tasks assigned to the given userId
     */
    private SearchOptions createAssignedTaskSearchOptions(long userId) {
        return new SearchOptionsBuilder(0, Integer.MAX_VALUE)
                .filter(HumanTaskInstanceSearchDescriptor.ASSIGNEE_ID, userId)
                .done();
    }
}
