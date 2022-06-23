package user;

import client.UserClient;
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
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void clean(){
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

        //проверка ответа на запрос о регистрации пользовтаеля
        checkResponse(body, expectedCode, actualCode, status);
        checkData(expectedEmail, actualEmail, expectedName, actualName, body);
        checkUserExist(token); //убедиться, что юзер действительно есть в базе
    }

    @Test
    public void registerTheSameUserTwiceHasToReturnError() {
        User user = User.getRandomUser();
        User theSameUser = new User(user.getEmail(), "P@ssW0rd!", "TestName");
        int expectedCode = 403;
        String expectedMessage = "User already exists";

        token = userClient.register(user).extract().body().as(Token.class).getAccessToken();
        ValidatableResponse response = userClient.register(theSameUser);
        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualMessage = body.getMessage();

        checkResponse(body, expectedCode, actualCode, status);
        checkErrorMessage(expectedMessage, actualMessage);
    }

    @Test
    public void registerDeletedUserHasToCreateUser() {
        User user = User.getRandomUser();
        int expectedCode = 200;
        String expectedEmail = user.getEmail().toLowerCase(Locale.ROOT);
        String expectedName = user.getName();

        token = userClient.register(user).extract().body().as(Token.class).getAccessToken();
        ValidatableResponse deletedResponse = userClient.delete(token);
        assertEquals("При удалении юзера в ответе вернулся другой код состояния", 202, deletedResponse.extract().statusCode());
        assertTrue("При удалении юзера в ответе вернулось некорректное значение для поля success", deletedResponse.extract().body().as(Token.class).isSuccess());

        ValidatableResponse response = userClient.register(user);
        int actualCode = response.extract().statusCode();
        Token body = response.extract().body().as(Token.class);
        boolean status = body.isSuccess();
        String actualEmail = body.getUser().getEmail();
        String actualName = body.getUser().getName();
        token = body.getAccessToken();

        //проверка ответа на запрос о регистрации пользовтаеля
        checkResponse(body, expectedCode, actualCode, status);
        checkData(expectedEmail, actualEmail, expectedName, actualName, body);
        checkUserExist(token); //убедиться, что юзер действительно есть в базе
    }

    public void checkResponse(Token body, int  expectedCode, int actualCode, boolean status){
        assertNotNull("В ответе вернулось пустое Body", body);
        assertEquals("В ответе вернулся другой код состояния", expectedCode, actualCode);
        assertTrue("В ответе вернулось некорректное значение для поля success", status);
    }

    public void checkData(String  expectedEmail, String actualEmail, String expectedName, String actualName, Token body){
        assertEquals("В ответе вернулось некорректное значение для поля email", expectedEmail, actualEmail);
        assertEquals("В ответе вернулось некорректное значение для поля name", expectedName, actualName);
        assertFalse("В ответе вернулось пустое значение в поле accessToken", body.getAccessToken().isBlank());
        assertTrue("В ответе поле accessToken должно было начинаться с Bearer", body.getAccessToken().startsWith("Bearer"));
        assertFalse("В ответе вернулось пустое значение в поле refreshToken", body.getRefreshToken().isBlank());
    }

    public void checkUserExist(String token){
        boolean userExist = userClient.getUserData(token).assertThat().statusCode(200).extract().path("success");
        assertTrue("Пользователь не был зарегистрирован", userExist);
    }

    public void checkErrorMessage(String expectedMessage, String actualMessage){
        assertFalse("В ответе вернулось пустое значение в поле message", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение для поля message", expectedMessage, actualMessage);
    }
}
