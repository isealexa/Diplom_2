package user;

import client.UserClient;
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

    @Parameterized.Parameters(name = "The test {index} checks error to response for register user with empty {0}")
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
    public void setUp() {
        userClient = new UserClient();
        expectedCode = 403;
        expectedMessage = "Email, password and name are required fields";
    }

    @Test
    public void registerUserWithEmptyFieldHasToReturnError() {
        User user = User.getEmptyField(field);

        ValidatableResponse response = userClient.register(user);
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
    public void registerUserWithNullFieldHasToReturnError() {
        User user = User.getNullField(field);

        ValidatableResponse response = userClient.register(user);
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
