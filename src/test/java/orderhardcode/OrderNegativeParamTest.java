package orderhardcode;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.NewOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.User;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderNegativeParamTest {

    private final String order;
    private final String description;

    public OrderNegativeParamTest(String order, String description){
        this.order = order;
        this.description = description;
    }

    @Parameterized.Parameters(name = "The test {index} checks receive error when attempt to create order: {1}")
    public static Object[][] getOrderParam(){
        return new Object[][] {
                {"", "with empty body request"},
                {"{}", "with empty json body request"},
                {"{\"ingredients\":[]}", "with empty incorrect list"},
        };
    }

    private OrderClient orderClient;
    private int expectedCode;
    private String expectedMessage;

    @Before
    @Step("Set up order client and expected result")
    public void setUp(){
        orderClient = new OrderClient();
        expectedCode = 400;
        expectedMessage = "Ingredient ids must be provided";
    }

    @Test
    @DisplayName("Receive error when attempt to create incorrect order without authorization")
    @Description("This tests checks getting error 400 and message 'Ingredient ids must be provided' when push post-request to create order without authorization and incorrect data")
    public void createIncorrectOrderWithoutAuthorizationReturnError(){
        ValidatableResponse newOrder = create(order);
        checkResponse(newOrder);
    }

    @Test
    @DisplayName("Receive error when attempt to create incorrect order with authorization")
    @Description("This tests checks getting error 400 and message 'Ingredient ids must be provided' when push post-request to create order with authorization and incorrect data")
    public void createIncorrectOrderWithAuthorizationReturnError(){
        UserClient userClient = new UserClient();
        ValidatableResponse newUser = userClient.register(User.getRandomUser());
        String token = newUser.extract().path("accessToken");

        ValidatableResponse newOrder = create(order, token);
        checkResponse(newOrder);

        userClient.delete(token);
    }

    @Step("Push post-request to create order without authorization and get response")
    public ValidatableResponse create(String json){
        return orderClient.createOrder(json);
    }

    @Step("Push post-request to create order with authorization and get response")
    public ValidatableResponse create(String json, String token){
        return orderClient.createOrder(token, json);
    }

    @Step("Check response: error code and message")
    public void checkResponse(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        int actualCode = response.extract().statusCode();
        String actualMessage = response.extract().body().as(NewOrder.class).getMessage();
        assertEquals("код состояния в ответе не соотвестует ожидаемому", expectedCode, actualCode);
        assertFalse("Неверный статус в ответе на запрос", response.extract().path("success"));
        assertFalse("В ответе вернулось пустое сообщение об ошибке", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение в сообщении об ошибке", expectedMessage, actualMessage);
    }
}