package com.company.rest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateRestApiTest {
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

    private TemplateRestApi templateRestApi;
    private RestApiResponseBuilder restApiResponseBuilder;

    @Before
    public void setup() throws Exception {
        templateRestApi = new TemplateRestApi();
        restApiResponseBuilder = new RestApiResponseBuilder();

        // restApiContent mock
        when(restApiContextMock.getApiClient()).thenReturn(apiClientMock);

        // apiClient mock
        when(apiClientMock.getProcessAPI()).thenReturn(processApiMock);
        when(apiClientMock.getIdentityAPI()).thenReturn(identityApiMock);
        when(apiClientMock.getProfileAPI()).thenReturn(profileApiMock);
    }

    @Test
    public void taskIdParamNull() {
        RestApiResponse restApiResponse = templateRestApi.doHandle(httpRequestMock, restApiResponseBuilder, restApiContextMock);
        TemplateRestApiResponse restApiError = gson.fromJson(restApiResponse.getResponse().toString(), TemplateRestApiResponse.class);
        assertEquals("httpStatus", 200, restApiResponse.getHttpStatus());
    }
}
