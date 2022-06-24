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

public class RegisterUserTest {

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
    @DisplayName("Register new user has to create user success")
    @Description("This tests checks getting success response when push post-request to register new user")
    public void registerNewUserHasToCreateUser(){
        User user = getUser();
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

        //проверка ответа на запрос о регистрации пользовтаеля
        checkResponse(body, expectedCode, actualCode);
        checkData(status, expectedEmail, actualEmail, expectedName, actualName, body);
        checkUserExist(token); //убедиться, что юзер действительно есть в базе
    }

    @Test
    @DisplayName("Register the same email twice has to return error")
    @Description("This tests checks getting error when push post-request to register user with the same email twice")
    public void registerTheSameEmailTwiceHasToReturnError() {
        User user = getUser();
        User theSameUser = getTheSameEmail(user);
        int expectedCode = 403;
        String expectedMessage = "User already exists";

        ValidatableResponse userData = register(user);
        token = userData.extract().body().as(Token.class).getAccessToken();

        ValidatableResponse response = register(theSameUser);
        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualMessage = body.getMessage();

        checkResponse(body, expectedCode, actualCode);
        checkErrorMessage(status, expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Register deleted user has to create user success")
    @Description("This tests checks getting success response when push post-request to register user which was deleted")
    public void registerDeletedUserHasToCreateUser() {
        User user = getUser();
        int expectedCode = 200;
        String expectedEmail = user.getEmail().toLowerCase(Locale.ROOT);
        String expectedName = user.getName();

        ValidatableResponse userData = register(user);
        token = userData.extract().body().as(Token.class).getAccessToken();
        delete(token);

        ValidatableResponse response = userClient.register(user);
        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualEmail = body.getUser().getEmail();
        String actualName = body.getUser().getName();
        token = body.getAccessToken();

        //проверка ответа на запрос о регистрации пользовтаеля
        checkResponse(body, expectedCode, actualCode);
        checkData(status, expectedEmail, actualEmail, expectedName, actualName, body);
        checkUserExist(token); //убедиться, что юзер действительно есть в базе
    }

    @Step("Get random user data for test")
    public User getUser(){
        return User.getRandomUser();
    }

    @Step("Get user with the same email")
    public User getTheSameEmail(User user){
        return new User(user.getEmail(), "P@ssW0rd!", "TestName");
    }

    @Step("Register user")
    public ValidatableResponse register(User user){
        return userClient.register(user);
    }

    @Step("Delete user")
    public void delete(String token){
        ValidatableResponse deletedResponse = userClient.delete(token);
        assertEquals("При удалении юзера в ответе вернулся другой код состояния", 202, deletedResponse.extract().statusCode());
        assertTrue("При удалении юзера в ответе вернулось некорректное значение для поля success", deletedResponse.extract().body().as(Token.class).isSuccess());
    }

    @Step("Check status code and body is not NULL in response")
    public void checkResponse(Token body, int  expectedCode, int actualCode){
        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
    }

    @Step("Check body details: success status, email, name, token in response")
    public void checkData(boolean status, String  expectedEmail, String actualEmail, String expectedName, String actualName, Token body){
        assertTrue("В ответе вернулось некорректное значение для поля success", status);
        assertEquals("В ответе вернулось некорректное значение для поля email", expectedEmail, actualEmail);
        assertEquals("В ответе вернулось некорректное значение для поля name", expectedName, actualName);
        assertFalse("В ответе вернулось пустое значение в поле accessToken", body.getAccessToken().isBlank());
        assertTrue("В ответе поле accessToken должно было начинаться с Bearer", body.getAccessToken().startsWith("Bearer"));
        assertFalse("В ответе вернулось пустое значение в поле refreshToken", body.getRefreshToken().isBlank());
    }

    @Step("Check user was really registered and exist in system")
    public void checkUserExist(String token){
        boolean userExist = userClient.getUserData(token).assertThat().statusCode(200).extract().path("success");
        assertTrue("Пользователь не был зарегистрирован", userExist);
    }

    @Step("Check status and error message in response")
    public void checkErrorMessage(boolean status, String expectedMessage, String actualMessage){
        assertFalse("В ответе вернулось некорректное значение для поля success", status);
        assertFalse("В ответе вернулось пустое значение в поле message", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение для поля message", expectedMessage, actualMessage);
    }
}
