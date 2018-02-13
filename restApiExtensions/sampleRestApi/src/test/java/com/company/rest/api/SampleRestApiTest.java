package com.company.rest.api;

import com.company.rest.api.common.Constants;
import com.company.rest.api.common.RestApiError;
import com.company.rest.test.TestUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.impl.SearchResultImpl;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SampleRestApiTest {

    // declare mocks
    @Mock
    HttpServletRequest httpRequestMock;
    @Mock
    RestAPIContext restApiContextMock;
    @Mock
    APIClient apiClientMock;
    @Mock
    ProcessAPI processApiMock;
    @Mock
    IdentityAPI identityApiMock;
    @Mock
    ProfileAPI profileApiMock;
    @Mock
    APISession apiSessionMock;

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    String taskIdString = "4";
    long taskId = 4;
    long currentUserId = 3;
    String currentUserUsername = "currentUserUsername";
    String currentUserPassword = "currentUserPassword";
    User currentUser = TestUtilities.createUser(currentUserId, currentUserUsername, currentUserPassword);
    List<Long> humanTaskIds = new ArrayList<>();
    List<HumanTaskInstance> humanTaskInstances;
    SearchResult<HumanTaskInstance> humanTaskInstanceSearchResult;

    SampleRestApi sampleRestApi;
    RestApiResponseBuilder restApiResponseBuilder;

    @Before
    public void setup() throws Exception {
        sampleRestApi = new SampleRestApi();
        restApiResponseBuilder = new RestApiResponseBuilder();

        // init human task instances
        humanTaskInstances = TestUtilities.createHumanTaskInstances(humanTaskIds);
        humanTaskIds.add(new Long(1));
        humanTaskIds.add(new Long(2));
        humanTaskIds.add(new Long(3));

        humanTaskInstanceSearchResult = new SearchResultImpl(humanTaskInstances.size(), humanTaskInstances);

        // restApiContent mock
        when(restApiContextMock.getApiClient()).thenReturn(apiClientMock);
        when(restApiContextMock.getApiSession()).thenReturn(apiSessionMock);

        // apiClient mock
        when(apiClientMock.getProcessAPI()).thenReturn(processApiMock);
        when(apiClientMock.getIdentityAPI()).thenReturn(identityApiMock);
        when(apiClientMock.getProfileAPI()).thenReturn(profileApiMock);

        // apiSession mock
        when(apiSessionMock.getUserId()).thenReturn(currentUserId);

        // identityApi mock
        when(identityApiMock.getUser(currentUserId)).thenReturn(currentUser);

        // processApi mock
        when(processApiMock.searchHumanTaskInstances(any(SearchOptions.class))).thenReturn(humanTaskInstanceSearchResult);
    }

    @Test
    public void taskIdParamNull() {
        when(httpRequestMock.getParameter(SampleRestApi.PARAM_TASKID)).thenReturn(null);

        RestApiResponse restApiResponse = sampleRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        RestApiError restApiError = gson.fromJson(restApiResponse.getResponse().toString(), RestApiError.class);
        assertEquals("httpStatus", 400, restApiResponse.getHttpStatus());
        assertEquals("errorCode", Constants.ERROR_REQUIRED_PARAMETER_MISSING, restApiError.getErrorCode());
        assertEquals("errorMessage", "Parameter '" + SampleRestApi.PARAM_TASKID + "' cannot be empty", restApiError.getErrorMessage());
    }

    @Test
    public void taskIdParamEmpty() {
        when(httpRequestMock.getParameter(SampleRestApi.PARAM_TASKID)).thenReturn("");

        RestApiResponse restApiResponse = sampleRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        RestApiError restApiError = gson.fromJson(restApiResponse.getResponse().toString(), RestApiError.class);
        assertEquals("httpStatus", 400, restApiResponse.getHttpStatus());
        assertEquals("errorCode", Constants.ERROR_REQUIRED_PARAMETER_MISSING, restApiError.getErrorCode());
        assertEquals("errorMessage", "Parameter '" + SampleRestApi.PARAM_TASKID + "' cannot be empty", restApiError.getErrorMessage());
    }

    @Test
    public void taskIdParamBlank() {
        when(httpRequestMock.getParameter(SampleRestApi.PARAM_TASKID)).thenReturn("         ");

        RestApiResponse restApiResponse = sampleRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        RestApiError restApiError = gson.fromJson(restApiResponse.getResponse().toString(), RestApiError.class);
        assertEquals("httpStatus", 400, restApiResponse.getHttpStatus());
        assertEquals("errorCode", Constants.ERROR_REQUIRED_PARAMETER_MISSING, restApiError.getErrorCode());
        assertEquals("errorMessage", "Parameter '" + SampleRestApi.PARAM_TASKID + "' cannot be empty", restApiError.getErrorMessage());
    }

    @Test
    public void taskIdParamInvalid() {
        when(httpRequestMock.getParameter(SampleRestApi.PARAM_TASKID)).thenReturn(" eee        ");

        RestApiResponse restApiResponse = sampleRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        RestApiError restApiError = gson.fromJson(restApiResponse.getResponse().toString(), RestApiError.class);
        assertEquals("httpStatus", 400, restApiResponse.getHttpStatus());
        assertEquals("errorCode", Constants.ERROR_INVALID_PARAMETER, restApiError.getErrorCode());
        assertEquals("errorMessage", "Parameter '" + SampleRestApi.PARAM_TASKID + "' must be a positive integer", restApiError.getErrorMessage());
    }

    @Test
    public void getApiSessionThrowsException() {
        when(httpRequestMock.getParameter(SampleRestApi.PARAM_TASKID)).thenReturn(taskIdString);
        when(restApiContextMock.getApiSession()).thenThrow(new RuntimeException("blah"));

        RestApiResponse restApiResponse = sampleRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        RestApiError restApiError = gson.fromJson(restApiResponse.getResponse().toString(), RestApiError.class);
        assertEquals("httpStatus", 400, restApiResponse.getHttpStatus());
        assertEquals("errorCode", Constants.ERROR_UNKNOWN_ERROR, restApiError.getErrorCode());
        assertEquals("errorMessage", "blah", restApiError.getErrorMessage());
    }

    @Test
    public void success() {
        when(httpRequestMock.getParameter(SampleRestApi.PARAM_TASKID)).thenReturn(taskIdString);

        RestApiResponse restApiResponse = sampleRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        SampleRestApiResponse sampleRestApiResponse = gson.fromJson(restApiResponse.getResponse().toString(), SampleRestApiResponse.class);
        assertEquals("httpStatus", 200, restApiResponse.getHttpStatus());
        assertEquals(currentUserId, sampleRestApiResponse.getCurrentUser().getId());
        assertEquals(humanTaskInstances.size(), sampleRestApiResponse.getHumanTasksAssignedToCurrentUser().size());
    }
}
