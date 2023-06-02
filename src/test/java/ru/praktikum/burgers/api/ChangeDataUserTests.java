package ru.praktikum.burgers.api;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.burgers.api.client.UserClient;
import ru.praktikum.burgers.api.model.User;
import ru.praktikum.burgers.api.model.UserCredentials;
import ru.praktikum.burgers.api.util.UserGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeDataUserTests {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getUser();
        userClient.createUser(user);
    }

    @After
    public void cleanUp() {
        try {
            userClient.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Изменение данных авторизированного пользователя")
    public void changeDataUserWithAuth() {
        UserCredentials userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        accessToken = loginResponse.extract().path("accessToken");
        ValidatableResponse updateResponse = userClient.updateUserWithAuth(UserGenerator.getUser(), accessToken);
        updateResponse.assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение данных неавторизированного пользователя")
    public void changeDataUserWithoutAuth() {
        ValidatableResponse updateResponse = userClient.updateUserWithoutAuth(UserGenerator.getUser());
        updateResponse.assertThat()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
