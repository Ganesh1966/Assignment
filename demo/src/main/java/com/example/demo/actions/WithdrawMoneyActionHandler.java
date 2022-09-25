package com.example.demo.actions;

import com.example.demo.actions.builder.Action;
import com.example.demo.actions.builder.ActionHandler;
import com.example.demo.models.User;
import org.springframework.stereotype.Component;
import exceptions.DataValidationException;

import java.util.Map;

@Component
public class WithdrawMoneyActionHandler extends ActionHandler<Map> {

    @Override
    public Action handlingFor() {
        return Action.WITHDRAW_MONEY;
    }

    @Override
    public Map executeAction(Map operateOn) {

        if(operateOn.isEmpty())
            throw new DataValidationException("User cannot be empty");

        User user = mapper.convertValue(operateOn.get("user"), User.class);

        return Map.of("user",persistenceStore.withdrawMoney(user));
    }
}
