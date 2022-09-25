package com.example.demo.db;


import com.example.demo.models.User;
import com.example.demo.models.View;

import java.util.Map;

public interface PersistenceStore {
    String saveUser(User user);

    View login(User credentials);

    boolean checkemail(User user);

    View withdrawMoney(User user);

    View addMoney(User user);
}
