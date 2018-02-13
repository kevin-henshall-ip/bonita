package com.company.rest.api;

import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.impl.UserImpl;

import java.util.List;

public class SampleRestApiResponse {
    private UserImpl currentUser;
    private List<HumanTaskInstance> humanTasksAssignedToCurrentUser;

    public void setCurrentUser(UserImpl currentUser) {
        this.currentUser = currentUser;
    }

    public void setHumanTasksAssignedToCurrentUser(List<HumanTaskInstance> humanTasksAssignedToCurrentUser) {
        this.humanTasksAssignedToCurrentUser = humanTasksAssignedToCurrentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<HumanTaskInstance> getHumanTasksAssignedToCurrentUser() {
        return humanTasksAssignedToCurrentUser;
    }
}
