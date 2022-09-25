package com.example.demo.actions.builder;

import com.example.demo.db.PersistenceStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class ActionHandler<OUTPUT> {

    @Autowired
    protected PersistenceStore persistenceStore;

    @Autowired
    protected ObjectMapper mapper;

    public abstract Action handlingFor();
    public abstract OUTPUT executeAction(Map operateOn);

}
