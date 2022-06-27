package user;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangeUserDataNegativeTest {

    private UserClient userClient;
    private Token anotherUserData;
    private String token;
    private String anotherUsersToken;

    private int expectedCode;
    private String expectedMessage;

    @Before
    @Step("Set up client, expected and test data")
    public void setUp(){
        userClient = new UserClient();
        User user = User.getRandomUser();
        User anotherUser =  User.getRandomUser();
        Token userData = userClient.register(user).extract().as(Token.class);
        anotherUserData = userClient.register(anotherUser).extract().as(Token.class);
        token = userData.getAccessToken();
        anotherUsersToken = anotherUserData.getAccessToken();

        expectedCode = 403;
        expectedMessage = "User with such email already exists";
    }

    @After
    @Step("Clean test data")
    public void clean(){
        userClient.delete(token);
        userClient.delete(anotherUsersToken);
    }

    @Test
    @DisplayName("Check return error when update user data with negative email")
    @Description("The test checks patch request to change users data with someone else's email")
    public void changeUserDataWithNegativeEmailReturnError(){
        String anotherUserEmail = getAnotherUserEmail(anotherUserData);
        ValidatableResponse response = getResponseFor(anotherUserEmail);
        checkResponse(response, expectedCode, expectedMessage);
    }

    @Step("Get user data with someone else's email")
    public String getAnotherUserEmail(Token anotherUserData){
        String newEmail = anotherUserData.getUser().getEmail();
        return "{\"email\":\"" + newEmail + "\"}";
    }

    @Step("Push patch request to change users data with another user email")
    public ValidatableResponse getResponseFor(String json){
        return userClient.changeUser(json, token);
    }

    @Step("Check response: waiting for error")
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
