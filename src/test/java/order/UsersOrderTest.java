package order;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.BurgerComposition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;

import static org.junit.Assert.*;

public class UsersOrderTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private String token;

    @Before
    @Step("Set up clients and test user")
    public void setUp(){
        orderClient = new OrderClient();
        userClient = new UserClient();
        ValidatableResponse newUser = userClient.register(User.getRandomUser());
        token = newUser.extract().path("accessToken");
    }

    @After
    @Step("Clean test data")
    public void clean(){
        userClient.delete(token);
    }

    @Test
    @DisplayName("Get user's order with authorization return success")
    @Description("This tests checks getting success response and show user's order when push get-request with authorization to see user's order")
    public void getUsersOrderWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getSuperBurger();
        ValidatableResponse order = createOrder(burger);
        String orderId = getId(order);
        Integer number = getNumber(order);
        ValidatableResponse response = getUserOrder();
        check(response, orderId, number);
    }

    @Step("Push post-request to create order with authorization")
    public ValidatableResponse createOrder(String burger){
        return orderClient.createOrder(token, burger);
    }

    @Step("Push get-request with authorization to see user's orders")
    public ValidatableResponse getUserOrder(){
        return orderClient.getOrder(token);
    }

    @Step("Get order id from response")
    public String getId(ValidatableResponse response){
        return response.extract().body().as(NewOrder.class).getOrder().get_id();
    }

    @Step("Get order number from response")
    public Integer getNumber(ValidatableResponse response){
        return response.extract().body().as(NewOrder.class).getOrder().getNumber();
    }

    @Step("Check response: status code, orders count, order id and price")
    public void check(ValidatableResponse response, String orderId, Integer number){
        assertNotNull("Вернулся невалидный ответ", response);
        assertEquals("В ответе вернулись некорректный код состояния", 200, response.extract().statusCode());
        assertTrue(response.extract().body().as(Orders.class).isSuccess());

        Order[] orders = response.extract().body().as(Orders.class).getOrders();
        assertNotNull("В ответе вернулся пустой ответ ", orders);
        assertEquals("Колиство заказазов в ответе не соотвествует ожидаемому", 1, orders.length);
        assertFalse("В ответе вернулся пустое значение в поле _id заказа", orders[0].get_id().isBlank());
        assertEquals("Поле id заказаза в ответе не соотвествует ожидаемому", orderId, orders[0].get_id());
        assertEquals("Поле price цена заказа в ответе не соотвествует ожидаемому", number,  orders[0].getNumber());
    }
}
