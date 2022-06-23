package user;

import client.UserClient;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ChangeUserDataTest {

    private final String newData;

    public ChangeUserDataTest(String newData){
        this.newData = newData;
    }

    @Parameterized.Parameters(name = "The test {index} checks update user data when body has these fields: {0}")
    public static Object[][] getParamNewUserdata(){
        return new Object[][] {
                {"newAll"},
                {"email"},
                {"password"},
                {"name"},
                {"allTheSame"},
                {"{}"},
        };
    }

    private UserClient userClient;
    private User user;
    private String token;
    private String email;
    private String password;
    private String name;
    private String expectedEmail;
    private String expectedPassword;
    private String expectedName;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        email = user.getEmail().toLowerCase(Locale.ROOT);
        password = user.getPassword();
        name = user.getName();
    }

    @After
    public void clean(){
        userClient.delete(token);
    }

    @Test
    public void changeUserDataReturnSuccessTrue(){
        String json = getData(newData, email, password, name);
        token = userClient.register(user).extract().path("accessToken");

        ValidatableResponse response = userClient.changeUser(json, token);

        checkResponse(response, expectedEmail, expectedName);
        checkSuccessUpdateUserData(expectedEmail, expectedPassword, expectedName); //логинимся с новыми данными, чтобы убедиться, что данные действительно обновились
    }

    public String getData(String data, String email, String password, String name){
        String json = null;
        Faker faker = new Faker();
        String newEmail = faker.name().username().toLowerCase(Locale.ROOT)+ "@test.test";
        String newPassword = RandomStringUtils.randomAlphanumeric(6);
        String newName = String.valueOf(faker.name());

        switch (data){

            case "newAll":
                json = "{\"email\":\"" + newEmail + "\", \"password\": \"" + newPassword + "\", \"name\": \"" + newName + "\"}";
                expectedEmail = newEmail;
                expectedPassword = newPassword;
                expectedName = newName;
                break;
            case "email":
                json = "{\"email\": \"" + newEmail + "\" }";
                expectedEmail = newEmail;
                expectedPassword = password;
                expectedName = name;
                break;
            case "password":
                json = "{\"password\": \"" + newPassword + "\"}";
                expectedEmail = email;
                expectedPassword = newPassword;
                expectedName = name;
                break;
            case "name":
                json = "{\"name\": \"" + newName + "\"}";
                expectedEmail = email;
                expectedPassword = password;
                expectedName = newName;
                break;
            case "allTheSame":
                json = "{\"email\":\"" + email +"\", \"password\": \"" + password + "\", \"name\": \"" + name + "\"}";
                expectedEmail = email;
                expectedPassword = password;
                expectedName = name;
                break;
            default:
                json = "{}";
                expectedEmail = email;
                expectedPassword = password;
                expectedName = name;
        }
        return json;
    }

    public void checkResponse(ValidatableResponse response, String email, String name){
        Token body = response.extract().body().as(Token.class);
        String actualEmail = body.getUser().getEmail();
        String actualName = body.getUser().getName();

        //проверяем ответ на запрос о смене данных пользователя
        assertNotNull("Вернулся невалидный ответ", response);
        assertNotNull("В ответе вернудось пустое тело ответа", body);
        assertTrue("Код состояния и статус ответа не соответсвуют ожидаемому", response.assertThat().statusCode(200).extract().path("success"));
        assertEquals("В ответе вернулось некорректное значение для поля email" , email, actualEmail);
        assertEquals("В ответе вернулось некорректное значение для поля name", name, actualName);
    }

    public void checkSuccessUpdateUserData(String email, String password, String name){
        ValidatableResponse response = userClient.login(new User(email, password));
        Token body = response.extract().body().as(Token.class);
        String actualEmail = body.getUser().getEmail();
        String actualName = body.getUser().getName();
        token = body.getAccessToken();

        assertNotNull("В ответе вернулось пустое Body", body);
        assertTrue("Код состояния и статус ответа не соответсвуют ожидаемому", response.assertThat().statusCode(200).extract().path("success"));
        assertEquals("В ответе вернулось некорректное значение для поля email", email, actualEmail);
        assertEquals("В ответе вернулось некорректное значение для поля name", name, actualName);
        assertFalse("В ответе вернулось пустое значение в поле accessToken", body.getAccessToken().isBlank());
        assertTrue("В ответе поле accessToken должно было начинаться с Bearer", body.getAccessToken().startsWith("Bearer"));
        assertFalse("В ответе вернулось пустое значение в поле refreshToken", body.getRefreshToken().isBlank());
    }
}
