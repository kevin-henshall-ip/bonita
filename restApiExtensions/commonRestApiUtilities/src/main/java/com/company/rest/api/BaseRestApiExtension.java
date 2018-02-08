package com.company.rest.api;

import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiController;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseRestApiExtension implements RestApiController {
    private ProcessAPI processApi;
    private IdentityAPI identityApi;
    private ProfileAPI profileApi;

    @Override
    public abstract RestApiResponse doHandle(HttpServletRequest request,
                                    RestApiResponseBuilder responseBuilder,
                                    RestAPIContext restApiContext);

    public void initialiseBonitaApiAccessors(RestAPIContext restApiContext) {
        APIClient apiClient = restApiContext.getApiClient();
        processApi = apiClient.getProcessAPI();
        identityApi = apiClient.getIdentityAPI();
        profileApi = apiClient.getProfileAPI();
    }

    public ProcessAPI getProcessApi() {
        return processApi;
    }

    public IdentityAPI getIdentityApi() {
        return identityApi;
    }

    public ProfileAPI getProfileApi() {
        return profileApi;
    }
}
