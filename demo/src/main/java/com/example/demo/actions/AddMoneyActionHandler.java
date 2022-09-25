package com.example.demo.actions;

import com.example.demo.actions.builder.Action;
import com.example.demo.actions.builder.ActionHandler;
import com.example.demo.models.User;
import exceptions.DataValidationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AddMoneyActionHandler extends ActionHandler<Map> {
    @Override
    public Action handlingFor() {
        return Action.ADD_MONEY;
    }

    @Override
    public Map executeAction(Map operateOn) {

        if(operateOn.isEmpty())
            throw new DataValidationException("User cannot be empty");

        User user = mapper.convertValue(operateOn.get("user"), User.class);

        return Map.of("user",persistenceStore.addMoney(user));
    }
}
