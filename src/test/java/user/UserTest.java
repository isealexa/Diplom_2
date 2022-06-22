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
        User user = User.getRandomUser(6);
        int expectedCode = 200;
        String expectedEmail = user.getEmail().toLowerCase(Locale.ROOT);
        String expectedName = user.getName();

        ValidatableResponse response = userClient.register(user);
        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean actualStatus = body.isSuccess();
        String actualEmail = body.getUser().getEmail();
        String actualName = body.getUser().getName();
        token = body.getAccessToken();

        assertEquals("В ответе вернулся другой код состояние", expectedCode, actualCode);
        assertTrue("В ответе вернулось некорректное значение для поля success", actualStatus);
        assertEquals("В ответе вернулось некорректное значение для поля email", expectedEmail, actualEmail);
        assertEquals("В ответе вернулось некорректное значение для поля name", expectedName, actualName);
        assertFalse("В ответе вернулось некорректное значение для поля accessToken", body.getAccessToken().isBlank());
        assertTrue("В ответе поле accessToken должно было начинаться с Bearer", body.getAccessToken().startsWith("Bearer"));
        assertFalse("В ответе вернулось некорректное значение для поля refreshToken", body.getRefreshToken().isBlank());
    }
}
