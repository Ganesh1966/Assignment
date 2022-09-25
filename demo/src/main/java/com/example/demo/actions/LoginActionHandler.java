package com.example.demo.actions;

import com.example.demo.actions.builder.Action;
import com.example.demo.actions.builder.ActionHandler;
import com.example.demo.models.User;
import org.springframework.stereotype.Component;

import exceptions.DataValidationException;
import java.util.Map;

@Component
public class LoginActionHandler extends ActionHandler<Map> {

    @Override
    public Action handlingFor() {
        return Action.LOGIN;
    }

    @Override
    public Map executeAction(Map operateOn) {
        if (operateOn.isEmpty())
            throw new DataValidationException("Login credentials missing for login  ");

        User credentials = mapper.convertValue(operateOn.get("credentials"), User.class);

        return Map.of("loggedInDetails", persistenceStore.login(credentials));
    }
}
