package user;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RegisterUserNegativeTest {

    private final String field;

    public RegisterUserNegativeTest(String field){
        this.field = field;
    }

    @Parameterized.Parameters(name = "The test {index} checks error to response for register user with empty or null {0}")
    public static Object[][] getNegativeUserData() {
        return new Object[][] {
                {"email"},
                {"password"},
                {"name"},
        };
    }

    private UserClient userClient;
    private int expectedCode;
    private String expectedMessage;

    @Before
    @Step("Set up client and expected")
    public void setUp() {
        userClient = new UserClient();
        expectedCode = 403;
        expectedMessage = "Email, password and name are required fields";
    }

    @Test
    @DisplayName("Register user with empty field has to return error")
    @Description("This param tests checks getting error when push post-request to register user with one empty field")
    public void registerUserWithEmptyFieldHasToReturnError() {
        User user = getEmptyField();
        ValidatableResponse response = tryToRegister(user);
        checkResponse(response);
    }

    @Test
    @DisplayName("Register user with null field has to return error")
    @Description("This param tests checks getting error when push post-request to register user with one null field")
    public void registerUserWithNullFieldHasToReturnError() {
        User user = getNullField();
        ValidatableResponse response = tryToRegister(user);
        checkResponse(response);
    }

    @Step("Getting one empty field")
    public User getEmptyField(){
        return User.getEmptyField(field);
    }

    @Step("Getting one null field")
    public User getNullField(){
        return User.getNullField(field);
    }

    @Step("Try to register and get response")
    public ValidatableResponse tryToRegister(User user){
        return userClient.register(user);
    }

    @Step("Check response: waiting for 403 status code and error message")
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
