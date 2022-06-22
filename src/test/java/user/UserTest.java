package user;

import client.UserClient;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Locale;

import static org.junit.Assert.*;

public class UserTest {

    private UserClient userClient;
    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void clear(){
        userClient.delete(token);
    }

    @Test
    public void registerNewUserHasToCreateUser(){
        User user = User.getRandomUser();
        int expectedCode = 200;
        String expectedEmail = user.getEmail().toLowerCase(Locale.ROOT);
        String expectedName = user.getName();

        ValidatableResponse response = userClient.register(user);
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
    @Test
    public void registerTheSameUserTwiceHasToReturnError() {
        User user = User.getRandomUser();
        int expectedCode = 403;
        String expectedMessage = "User already exists";

        token = userClient.register(user).extract().body().as(Token.class).getAccessToken();
        ValidatableResponse response = userClient.register(user);
        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualMessage = body.getMessage();

        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
        assertFalse("В ответе вернулось некорректное значение для поля success", status);
        assertFalse("В ответе вернулось пустое значение в поле message", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение для поля message", expectedMessage, actualMessage);
    }
}
