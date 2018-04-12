package com.company.rest.api;

import com.company.rest.api.common.Constants;
import com.company.rest.api.common.RestApiError;
import com.company.rest.api.common.RestApiException;
import com.company.rest.api.common.RestApiUtils;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstanceSearchDescriptor;
import org.bonitasoft.engine.identity.impl.UserImpl;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class SampleRestApi extends BaseRestApiExtension {

    public static final String PARAM_TASKID = "taskId";

    // logger name should start with 'org.bonitasoft' so that log messages will appear in the default bonita log file
    private static final Logger LOGGER = LoggerFactory.getLogger("org.bonitasoft.rest.api.SampleRestApi");

    private Long taskId;

    @Override
    public RestApiResponse doHandle(HttpServletRequest request,
                                    RestApiResponseBuilder responseBuilder,
                                    RestAPIContext restApiContext) {
        try {
            initialiseBonitaApiAccessors(restApiContext);

            // retrieve params
            retrieveParameters(request);

            LOGGER.info("taskId: " + taskId);

            // perform tasks using the bonita java apis. In this example, we retrieve the current user
            // and the list of human tasks assigned to them. Refer to TODO for the associated bonita javadoc

            // retrieve current user information
            long currentUserId = restApiContext.getApiSession().getUserId();
            UserImpl currentUser = new UserImpl(getIdentityApi().getUser(currentUserId));
            LOGGER.info("currentUser: " + currentUser);

            // retrieve tasks assigned to current user
            SearchOptions searchOptions = createAssignedTaskSearchOptions(currentUserId);
            SearchResult<HumanTaskInstance> humanTasksAssignedToUserSearchResult = getProcessApi().searchHumanTaskInstances(searchOptions);
            List<HumanTaskInstance> humanTasksAssignedToCurrentUser = humanTasksAssignedToUserSearchResult.getResult();
            LOGGER.info("humanTasksAssignedToCurrentUser: " + humanTasksAssignedToCurrentUser);

            // construct response
            SampleRestApiResponse sampleRestApiResponse = new SampleRestApiResponse();
            sampleRestApiResponse.setCurrentUser(currentUser);
            sampleRestApiResponse.setHumanTasksAssignedToCurrentUser(humanTasksAssignedToCurrentUser);

            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_OK, RestApiUtils.toJson(sampleRestApiResponse));
        } catch (RestApiException e) {
            String errorMessage = "An error was encountered within SampleRestApi: " + e.getMessage();
            LOGGER.error(errorMessage);
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            RestApiError restApiError = new RestApiError(e.getErrorCode(), errorMessage);
            return RestApiUtils.buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST,
                    RestApiUtils.toJson(restApiError));
        } catch (Exception e) {
            String errorMessage = "An error was encountered within SampleRestApi: " + e.getMessage();
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
