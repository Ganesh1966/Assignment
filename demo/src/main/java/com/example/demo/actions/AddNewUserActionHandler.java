package com.example.demo.actions;

import com.example.demo.actions.builder.Action;
import com.example.demo.actions.builder.ActionHandler;
import com.example.demo.models.User;
import exceptions.DataValidationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AddNewUserActionHandler extends ActionHandler<String> {

    @Override
    public Action handlingFor() {
        return Action.ADD_NEW_USER;
    }

    @Override
    public String executeAction(Map operateOn) {

        if(operateOn.isEmpty())
            throw new DataValidationException("User cannot be empty");

        User user = mapper.convertValue(operateOn.get("user"), User.class);

        if(persistenceStore.checkemail(user)){
            throw new DataValidationException("email is already used");
        }

        return persistenceStore.saveUser(user);
    }
}
