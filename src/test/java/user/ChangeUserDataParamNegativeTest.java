package user;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ChangeUserDataParamNegativeTest {

    private final int expectedCode;
    private final String expectedMessage;
    private final String negativeToken;
    private final String description;

    public ChangeUserDataParamNegativeTest(int expectedCode, String expectedMessage, String negativeToken, String description){
        this.expectedCode = expectedCode;
        this.expectedMessage = expectedMessage;
        this.negativeToken = negativeToken;
        this.description = description;
    }

    @Parameterized.Parameters(name = "The test {index} checks patch request to change users data with {3}")
    public static Object[][] getNegativeToken(){
        return new Object[][]{
                {403, "jwt expired", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYyYjRiNGUxOTNlYmMyMDAxYjQ5NGU2NiIsImlhdCI6MTY1NjAwOTk1MywiZXhwIjoxNjU2MDExMTUzfQ.Gqj9MR37y7R3_ozX9RdVNY8q1SSXagZPEtj_Zl2y99E", "expired token"},
                {403, "jwt malformed", "incorrect token", "incorrect token"},
                {401, "You should be authorised",  "", "empty token"},
                {401, "You should be authorised",  null, "null token"},
        };
    }

    private UserClient userClient;
    ValidatableResponse register;
    String token;

    @Before
    @Step("Set up client and test data")
    public void setUp(){
        userClient = new UserClient();
        User user = User.getRandomUser();
        register = userClient.register(user);
        token = register.extract().path("accessToken");
    }

    @After
    @Step("Clean test data")
    public void clean(){
        userClient.delete(token);
    }

    @Test
    @DisplayName("Check return error when update user data with negative token")
    @Description("The test checks patch request to change users data with negative token")
    public void changeUserDataWithNegativeTokenReturnError(){
        ValidatableResponse response = getResponseFor(negativeToken);
        checkResponse(response, expectedCode, expectedMessage);
        clean();
    }

    @Step("Push patch request to change users data with negative token")
    public ValidatableResponse getResponseFor(String newToken){
        User newData = User.getRandomUser();
        String newEmail = newData.getEmail();
        String newPassword = newData.getPassword();
        String newName = newData.getName();
        String json = "{\"email\":\"" + newEmail + "\", \"password\": \"" + newPassword + "\", \"name\": \"" + newName + "\"}";

        ValidatableResponse response;

        if (newToken == null) {
            response = userClient.changeUserWithoutToken(json);
        } else response = userClient.changeUser(json, newToken);

        return response;
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
