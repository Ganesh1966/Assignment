package com.example.demo.db;

import com.example.demo.db.jooqs.Tables;
import com.example.demo.db.jooqs.tables.records.UserRecord;
import com.example.demo.models.User;
import com.example.demo.models.View;
import exceptions.DataValidationException;
import io.github.cdimascio.dotenv.Dotenv;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;

@Component
public class Jooq implements PersistenceStore{

    private DSLContext jooqContext;

    @PostConstruct
    private void createJooqContext() {
        try {

            Dotenv dotenv = Dotenv
                    .configure()
                    .ignoreIfMissing()
                    .load();

            this.jooqContext = DSL.using(
                    dotenv.get("POSTGRES_URL"),
                    dotenv.get("POSTGRES_USERNAME"),
                    dotenv.get("POSTGRES_PASSWORD")
            );
        } catch (Exception e) {

            throw e;
        }
    }

    private DSLContext liveContext(){
        this.jooqContext.connection((connection -> {
            if (connection.isClosed()){
                createJooqContext();
            }
        }));

        return this.jooqContext;
    }

    @Override
    public String saveUser(User user) {

        UserRecord userRecord = liveContext().newRecord(Tables.USER);
        userRecord.setEmail(user.getEmail());
        userRecord.setName(user.getName());
        userRecord.setPassword(String.format("p:%s", generateHashedPassword(user.getPassword())));
        userRecord.setAccountStatus(user.getAccounttype());
        userRecord.setCurrentAmount(user.getAddMoney());
        userRecord.setAge(user.getAge());
        userRecord.store();
        return "user record stored successfully";
    }

    @Override
    public View login(User credentials) {
        String hashedPassword = generateHashedPassword(credentials.getPassword());
        return liveContext().selectFrom(Tables.USER)
                .where(Tables.USER.EMAIL.eq(credentials.getEmail()))
                .and(Tables.USER.PASSWORD.eq(String.format("p:%s", hashedPassword)))
                .stream()
                .map(userRecord -> {
                    View user = new View();
                    user.setId(userRecord.getId());
                    user.setEmail(userRecord.getEmail());
                    user.setName(userRecord.getName());
                    user.setAge(userRecord.getAge());
                    user.setAccounttype(userRecord.getAccountStatus());
                    user.setCurrentBalance(userRecord.getCurrentAmount());
                    return user;
                })
                .findFirst()
                .orElseThrow(() -> new DataValidationException("Password is not correct"));
    }

    @Override
    public boolean checkemail(User user) {
        return liveContext().fetchExists(
                liveContext().selectFrom(Tables.USER).where(Tables.USER.EMAIL.eq(user.getEmail())));

    }

    @Override
    public View withdrawMoney(User user) {
        Record1<String> result5 = liveContext()
                .select(Tables.USER.CURRENT_AMOUNT)
                .from(Tables.USER)
                .where(Tables.USER.EMAIL.eq(user.getEmail()))
                .fetchOne();

        if(Integer.parseInt(result5.get(Tables.USER.CURRENT_AMOUNT))<Integer.parseInt(user.getWithdrawMoney())){
            throw new exceptions.DataValidationException("Current balance is low");
        }


        liveContext().update(Tables.USER)
                .set(Tables.USER.CURRENT_AMOUNT, String.valueOf(Integer.parseInt(result5.get(Tables.USER.CURRENT_AMOUNT))-Integer.parseInt(user.getWithdrawMoney()) ))
                .where(Tables.USER.EMAIL.eq(user.getEmail()))
                .execute();

        return liveContext().selectFrom(Tables.USER)
                .where(Tables.USER.EMAIL.eq(user.getEmail()))
                .stream()
                .map(userRecord -> {
                    View users = new View();
                    users.setId(userRecord.getId());
                    users.setEmail(userRecord.getEmail());
                    users.setName(userRecord.getName());
                    users.setAge(userRecord.getAge());
                    users.setAccounttype(userRecord.getAccountStatus());
                    users.setCurrentBalance(userRecord.getCurrentAmount());
                    return users;
                })
                .findFirst().get();
    }

    @Override
    public View addMoney(User user) {
        Record1<String> result5 = liveContext()
                .select(Tables.USER.CURRENT_AMOUNT)
                .from(Tables.USER)
                .where(Tables.USER.EMAIL.eq(user.getEmail()))
                .fetchOne();

        liveContext().update(Tables.USER)
                .set(Tables.USER.CURRENT_AMOUNT, String.valueOf(Integer.parseInt(result5.get(Tables.USER.CURRENT_AMOUNT))+Integer.parseInt(user.getAddMoney()) ))
                .where(Tables.USER.EMAIL.eq(user.getEmail()))
                .execute();

        return liveContext().selectFrom(Tables.USER)
                .where(Tables.USER.EMAIL.eq(user.getEmail()))
                .stream()
                .map(userRecord -> {
                    View users = new View();
                    users.setId(userRecord.getId());
                    users.setEmail(userRecord.getEmail());
                    users.setName(userRecord.getName());
                    users.setAge(userRecord.getAge());
                    users.setAccounttype(userRecord.getAccountStatus());
                    users.setCurrentBalance(userRecord.getCurrentAmount());
                    return users;
                })
                .findFirst().get();
    }

    private String generateHashedPassword(String originalPassword) {
        return Hashing.sha256()
                .hashString(originalPassword + "wsedrftgyhjvhgc", StandardCharsets.UTF_8)
                .toString();
    }
}
