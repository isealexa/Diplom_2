package user;

import client.UserClient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangeUserDataNegativeTest {

    private UserClient userClient;
    private User user;
    private ValidatableResponse register;
    private String token;

    @Before
    @Step("Set test data")
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        register = userClient.register(user);
        token = register.extract().path("accessToken");
    }

    @After
    @Step("Clean test data")
    public void clean(){
        userClient.delete(token);
    }

    @Test
    public void changeUserDataWithoutExpiredTokenReturnError(){
        int expectedCode =  403;
        String expectedMessage = "jwt expired";
        String expiredToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYyYjRiNGUxOTNlYmMyMDAxYjQ5NGU2NiIsImlhdCI6MTY1NjAwOTk1MywiZXhwIjoxNjU2MDExMTUzfQ.Gqj9MR37y7R3_ozX9RdVNY8q1SSXagZPEtj_Zl2y99E";
        User updateUser = User.getRandomUser();
        ValidatableResponse response = getResponseFor(updateUser, expiredToken);
        checkResponse(response, expectedCode, expectedMessage);
    }

    @Test
    public void changeUserDataWithoutIncorrectTokenReturnError(){
        int expectedCode =  403;
        String expectedMessage = "jwt malformed";
        String incorrectToken = "incorrect token";
        User updateUser = User.getRandomUser();
        ValidatableResponse response = getResponseFor(updateUser, incorrectToken);
        checkResponse(response, expectedCode, expectedMessage);
    }

    @Test
    public void changeUserDataWithoutAuthorizationReturnError(){
        int expectedCode = 401;
        String expectedMessage = "You should be authorised";
        String emptyToken = "";
        User updateUser = User.getRandomUser();
        ValidatableResponse response = getResponseFor(updateUser, emptyToken);
        checkResponse(response, expectedCode, expectedMessage);
    }

    @Step("Push patch request to change users data with incorrect token")
    public ValidatableResponse getResponseFor(User newData, String token){
        String newEmail = newData.getEmail();
        String newPassword = newData.getPassword();
        String newName = newData.getName();
        String update = "{\"email\":\"" + newEmail + "\", \"password\": \"" + newPassword + "\", \"name\": \"" + newName + "\"}";
        return userClient.changeUser(update, token);
    }

    @Step("Checking response: waiting for error")
    public void checkResponse(ValidatableResponse response, int expectedCode, String expectedMessage){
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
