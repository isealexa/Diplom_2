package user;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
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
    @Step("Set up client, expected and test data")
    public void setUp() {
        user = User.getRandomUser();
        userClient = new UserClient();
        expectedCode = 401;
        expectedMessage = "email or password are incorrect";
    }

    @Test
    @DisplayName("Login with non-existent User")
    @Description("The test checks get error when push post request to login with user with doesn't exist")
    public void loginNonExistentUserHasToReturnError(){
        User userData = new User("userDoesNtExist@testDomain.test", "qwe456");
        ValidatableResponse response = tryToLogin(userData);
        checkResponse(response);
    }

    @Test
    @DisplayName("Login with deleted user")
    @Description("The test checks get error when push post request to login with user with was deleted")
    public void loginDeletedUserHasToReturnError(){
        String email = user.getEmail();
        String password = user.getPassword();
        User userData = new User(email, password);

        ValidatableResponse thisUser = register();
        delete(thisUser);

        ValidatableResponse response = tryToLogin(userData);
        checkResponse(response);
    }

    @Step("Register user")
    public ValidatableResponse register(){
       return userClient.register(user);
    }

    @Step("Delete this user")
    public void delete(ValidatableResponse register){
        String token = register.extract().body().as(Token.class).getAccessToken();
        userClient.delete(token);
    }

    @Step("Try to login with this user")
    public ValidatableResponse tryToLogin(User userData){
        return userClient.login(userData);
    }

    @Step("Check response")
    public void checkResponse(ValidatableResponse response){
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
