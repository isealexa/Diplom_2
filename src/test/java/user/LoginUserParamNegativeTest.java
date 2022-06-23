package user;

import client.UserClient;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class LoginUserParamNegativeTest {

    private final String data;

    public LoginUserParamNegativeTest(String data){
        this.data = data;
    }

    @Parameterized.Parameters(name = "The test {index} checks user login with incorrect field: {0}")
    public static Object[][] getLoginUserParam(){
        return new Object[][] {
                {"justEmail"},
                {"withoutEmail"},
                {"incorrectPassword"},
        };
    }

    private UserClient userClient;
    private User user;
    private int expectedCode;
    private String expectedMessage;
    private String token;

    @Before
    public void setUp() {
        user = User.getRandomUser();
        userClient = new UserClient();
        expectedCode = 401;
        expectedMessage = "email or password are incorrect";
    }

    @After
    public void clean(){
        userClient.delete(token);
    }

    @Test
    public void  loginExistingUserWithIncorrectFieldHasToReturnError(){
        User userData = getUserData(data);

        ValidatableResponse register = userClient.register(user);
        token = register.extract().body().as(Token.class).getAccessToken();
        ValidatableResponse response = userClient.login(userData);

        checkResponse(response);
    }

    public User getUserData(String data){
        User userData = null;

        switch (data) {
            case "justEmail":
                userData = new User(user.getEmail());
                break;
            case "withoutEmail":
                userData = new User(null, user.getPassword(), user.getName());
                break;
            case "incorrectPassword":
                userData = new User(user.getEmail(), "incorrect");
        }
        return userData;
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
