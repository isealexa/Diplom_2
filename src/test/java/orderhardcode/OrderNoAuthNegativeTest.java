package orderhardcode;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.BurgerComposition;
import order.NewOrder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class OrderNoAuthNegativeTest {

    private OrderClient orderClient;

    @Before
    @Step("Set up order clients and test data: get all ingredients and user token")
    public void setUp(){
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Receive error when attempt to create order with incorrect token")
    @Description("This tests checks getting error 403 and message 'jwt malformed' when push post-request to create order with incorrect authorization token")
    public void createOrderWithIncorrectTokenReturnError(){
        String json = new BurgerComposition().getStandardBurger();
        String token = "incorrect token";
        int expectedCode = 403;
        String expectedMessage = "jwt malformed";
        ValidatableResponse newOrder = createOrder(token, json);
        checkResponse(newOrder, expectedCode, expectedMessage);
    }

    @Test
    @DisplayName("Receive error when attempt to create order with expired token")
    @Description("This tests checks getting error 403 and message 'jwt expired' when push post-request to create order with expired authorization token")
    public void createOrderWithExpiredTokenReturnError(){
        String json = new BurgerComposition().getStandardBurger();
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYyYjRiNGUxOTNlYmMyMDAxYjQ5NGU2NiIsImlhdCI6MTY1NjAwOTk1MywiZXhwIjoxNjU2MDExMTUzfQ.Gqj9MR37y7R3_ozX9RdVNY8q1SSXagZPEtj_Zl2y99E";
        int expectedCode = 403;
        String expectedMessage = "jwt expired";
        ValidatableResponse newOrder = createOrder(token, json);
        checkResponse(newOrder, expectedCode, expectedMessage);
    }

    @Test
    @DisplayName("Receive error when attempt to create order without body")
    @Description("This tests checks getting error 400 and message 'Ingredient ids must be provided' when push post-request to create order without body")
    public void createOrderWithoutBodyReturnError(){
        int expectedCode = 400;
        String expectedMessage = "Ingredient ids must be provided";
        ValidatableResponse newOrder = createOrder();
        checkResponse(newOrder, expectedCode, expectedMessage);
    }

    @Test
    @DisplayName("Receive error when attempt to create order with incorrect id ingredient")
    @Description("This tests checks getting error 500 when push post-request to create order with incorrect id ingredient")
    public void createOrderWithIncorrectIngredientReturnError(){
        String ingredient = new BurgerComposition().getIncorrect();
        int expectedCode = 500;
        ValidatableResponse newOrder = createOrder(ingredient);
        checkResponse(newOrder, expectedCode);
    }

    @Step("Push post-request to create order and get response")
    public ValidatableResponse createOrder(String token, String json){
        return orderClient.createOrder(token, json);
    }

    @Step("Push post-request to create order without authorization and get response")
    public ValidatableResponse createOrder(){
        return orderClient.createOrder();
    }

    @Step("Push post-request to create order with incorrect id ingredient")
    public ValidatableResponse createOrder(String json){
        return orderClient.createOrder(json);
    }

    @Step("Check response and error code and error message")
    public void checkResponse(ValidatableResponse response, int expectedCode, String expectedMessage){
        assertNotNull("Вернулся невалидный ответ", response);
        int actualCode = response.extract().statusCode();
        String actualMessage = response.extract().body().as(NewOrder.class).getMessage();
        assertEquals("код состояния в ответе не соотвестует ожидаемому", expectedCode, actualCode);
        assertFalse("Неверный статус в ответе на запрос", response.extract().path("success"));
        assertFalse("В ответе вернулось пустое сообщение об ошибке", actualMessage.isBlank());
        assertEquals("В ответе вернулось некорректное значение в сообщении об ошибке", expectedMessage, actualMessage);
    }

    @Step("Check response and error code and error message")
    public void checkResponse(ValidatableResponse response, int expectedCode){
        assertNotNull("Вернулся невалидный ответ", response);
        int actualCode = response.extract().statusCode();
        assertEquals("код состояния в ответе не соотвестует ожидаемому", expectedCode, actualCode);
    }
}
