package user;

import client.UserClient;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginUserNegativeTest {

    private UserClient userClient;
    private User user;
    private int expectedCode;
    private String expectedMessage;

    @Before
    public void setUp() {
        user = User.getRandomUser();
        userClient = new UserClient();
        expectedCode = 401;
        expectedMessage = "email or password are incorrect";
    }

    @Test
    public void loginIsNotExistingUserHasToReturnError(){
        User userData = new User("userDoesNtExist@testDomain.test", "qwe456");
        ValidatableResponse response = userClient.login(userData);
        checkResponse(response);
    }

    @Test
    public void  loginDeletedUserHasToReturnError(){
        String email = user.getEmail();
        String password = user.getPassword();
        User userData = new User(email, password);

        ValidatableResponse register = userClient.register(user);
        String token = register.extract().body().as(Token.class).getAccessToken();
        userClient.delete(token);

        ValidatableResponse response = userClient.login(userData);
        checkResponse(response);
    }

    public void checkResponse( ValidatableResponse response){
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
