package user;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class LoginUserTest {

    private UserClient userClient;
    private String token;

    @Before
    @Step("Set up client")
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    @Step("Clean test data")
    public void clean(){
        userClient.delete(token);
    }


    @Test
    @DisplayName("Login existent user has to return success")
    @Description("This tests checks getting success response when push post-request to login existent user")
    public void loginExistentUserHasToReturnSuccessLogin(){
        User user = getUser();
        String email = user.getEmail();
        String password = user.getPassword();
        register(user);

        int expectedCode = 200;
        String expectedEmail = email.toLowerCase(Locale.ROOT);
        String expectedName = user.getName();

        ValidatableResponse response = userClient.login(new User(email, password));
        checkResponse(response, expectedCode, expectedEmail, expectedName);
    }

    @Step("Get random user data for test")
    public User getUser(){
        return User.getRandomUser();
    }

    @Step("Register User")
    public void register(User user){
        userClient.register(user);
    }

    @Step("Check response")
    public void checkResponse(ValidatableResponse response, int expectedCode, String expectedEmail, String expectedName){

        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualEmail = body.getUser().getEmail();
        String actualName = body.getUser().getName();
        token = body.getAccessToken();

        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
        assertTrue("В ответе вернулось некорректное значение для поля success", status);
        assertEquals("В ответе вернулось некорректное значение для поля email", expectedEmail, actualEmail);
        assertEquals("В ответе вернулось некорректное значение для поля name", expectedName, actualName);
        assertFalse("В ответе вернулось пустое значение в поле accessToken", body.getAccessToken().isBlank());
        assertTrue("В ответе поле accessToken должно было начинаться с Bearer", body.getAccessToken().startsWith("Bearer"));
        assertFalse("В ответе вернулось пустое значение в поле refreshToken", body.getRefreshToken().isBlank());
    }
}
