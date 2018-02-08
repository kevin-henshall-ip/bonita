package com.company.rest.test;

import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.impl.internal.ManualTaskInstanceImpl;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.impl.UserImpl;

import java.util.ArrayList;
import java.util.List;

public class TestUtilities {
    /**
     * Create a new user
     * @param userId The userId
     * @param username The username
     * @param password The password
     * @return The User object
     */
    public static User createUser(long userId, String username, String password) {
        UserImpl user = new UserImpl(userId, username, password);
        return user;
    }

    /**
     * Create a list of human task instances
     * @param humanTaskIds The list of human task IDs
     * @return A list of human task instances
     */
    public static List<HumanTaskInstance> createHumanTaskInstances(List<Long> humanTaskIds) {
        List<HumanTaskInstance> humanTaskInstances = new ArrayList<>();
        for (Long humanTaskId : humanTaskIds) {
            HumanTaskInstance humanTaskInstance = new ManualTaskInstanceImpl(humanTaskId.toString(),
                    humanTaskId, humanTaskId);
            humanTaskInstances.add(humanTaskInstance);
        }
        return humanTaskInstances;
    }
}
