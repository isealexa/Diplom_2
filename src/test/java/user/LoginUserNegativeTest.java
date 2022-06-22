package user;

import client.UserClient;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

public class LoginUserNegativeTest {

    private UserClient userClient;
    private int expectedCode;
    private String expectedMessage;

    @Before
    public void setUp() {
        userClient = new UserClient();
        expectedCode = 401;
        expectedMessage = "email or password are incorrect";
    }

    @Test
    public void loginIsNotExistingUserHasToReturnError(){
        User userData = new User("userDoesNtExist@testDomain.test", "qwe456");

        ValidatableResponse response = userClient.login(userData);
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

    @Test
    public void  loginExistingUserWithOnlyEmailHasToReturnError(){
        User user = User.getRandomUser();
        String email = user.getEmail();
        User userData = new User(email);

        ValidatableResponse register = userClient.register(user);
        String token = register.extract().body().as(Token.class).getAccessToken();
        ValidatableResponse response = userClient.login(userData);

        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualMessage = body.getMessage();

        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
        assertFalse("В ответе вернулось некорректное значение для поля success", status);
        assertFalse("В ответе вернулось пустое значение в поле message", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение для поля message", expectedMessage, actualMessage);
        userClient.delete(token);
    }

    @Test
    public void  loginExistingUserWithNameAndPasswordHasToReturnError(){
        User user = User.getRandomUser();
        String password = user.getPassword();
        String name = user.getName();
        User userData = new User(null, password, name);

        ValidatableResponse register = userClient.register(user);
        String token = register.extract().body().as(Token.class).getAccessToken();
        ValidatableResponse response = userClient.login(userData);

        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualMessage = body.getMessage();

        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
        assertFalse("В ответе вернулось некорректное значение для поля success", status);
        assertFalse("В ответе вернулось пустое значение в поле message", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение для поля message", expectedMessage, actualMessage);
        userClient.delete(token);
    }

    @Test
    public void  loginExistingUserWithIncorrectPasswordHasToReturnError(){
        User user = User.getRandomUser();
        String email = user.getEmail();
        User userData = new User(email, "incorrect");

        ValidatableResponse register = userClient.register(user);
        String token = register.extract().body().as(Token.class).getAccessToken();
        ValidatableResponse response = userClient.login(userData);

        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualMessage = body.getMessage();

        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
        assertFalse("В ответе вернулось некорректное значение для поля success", status);
        assertFalse("В ответе вернулось пустое значение в поле message", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение для поля message", expectedMessage, actualMessage);
        userClient.delete(token);
    }

    @Test
    public void  loginDeletedUserHasToReturnError(){
        User user = User.getRandomUser();
        String email = user.getEmail();
        String password = user.getPassword();
        User userData = new User(email, password);

        ValidatableResponse register = userClient.register(user);
        String token = register.extract().body().as(Token.class).getAccessToken();
        userClient.delete(token);

        ValidatableResponse response = userClient.login(userData);

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
