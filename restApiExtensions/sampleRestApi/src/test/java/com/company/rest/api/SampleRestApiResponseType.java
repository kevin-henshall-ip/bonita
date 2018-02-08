package com.company.rest.api;

import org.bonitasoft.engine.bpm.flownode.impl.internal.HumanTaskInstanceImpl;
import org.bonitasoft.engine.identity.impl.UserImpl;

import java.util.List;

public class SampleRestApiResponseType {
    private UserImpl currentUser;
    private List<HumanTaskInstanceImpl> humanTasksAssignedToCurrentUser;

    public UserImpl getCurrentUser() {
        return currentUser;
    }

    public List<HumanTaskInstanceImpl> getHumanTasksAssignedToCurrentUser() {
        return humanTasksAssignedToCurrentUser;
    }
}
