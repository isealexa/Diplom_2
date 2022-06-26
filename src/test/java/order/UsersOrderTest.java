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
    @DisplayName("Get user's order with authorization return success and check id and number")
    @Description("This tests checks getting success response and show user's order when push get-request with authorization to see user's order")
    public void getUsersOrderWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getSuperBurger();
        ValidatableResponse order = createOrder(burger);
        String orderId = getId(order);
        Integer number = getNumber(order);
        ValidatableResponse response = getUserOrder();
        check(response, orderId, number);
    }

    @Test
    @DisplayName("Get user's order with authorization return success when there isn't any order")
    @Description("This tests checks getting success response and show user's order when push get-request with authorization to see user's order")
    public void getUsersNullOrderWithAuthorizationReturnSuccess(){
        ValidatableResponse response = getUserOrder();
        check(response);
    }

    @Test
    @DisplayName("Get two user's orders with authorization return success")
    @Description("This tests checks getting success response and show two user's orders when push get-request with authorization to see user's order")
    public void getTwoUsersOrdersWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getStandardBurger();
        String secondBurger = new BurgerComposition().getBigBurger();
        createOrder(burger);
        createOrder(secondBurger);
        ValidatableResponse response = getUserOrder();
        check(response, 2);
    }

    @Test
    @DisplayName("Get 50 user's orders with authorization return success")
    @Description("This tests checks getting success response and show 50 user's orders when ware created 50 push get-request with authorization to see 50 user's orders")
    public void getFiftyUsersOrdersWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getMiniBurger();
        int createdCount = 50;
        int expectedCount = 50;
        createOrder(burger, createdCount);
        ValidatableResponse response = getUserOrder();
        check(response, expectedCount);
    }

    @Test
    @DisplayName("Get 50 user's orders with authorization return success")
    @Description("This tests checks getting success response and show 50 user's orders when ware created more than 50 push get-request with authorization to see user's order")
    public void getFiftyUsersOrdersWithAuthorizationWhenWereCreatedMoreFiftyReturnSuccess(){
        String burger = new BurgerComposition().getBun();
        int createdCount = 51;
        int expectedCount = 50;
        createOrder(burger, createdCount);
        ValidatableResponse response = getUserOrder();
        check(response, expectedCount);

    }

    @Step("Push post-request to create order with authorization")
    public ValidatableResponse createOrder(String burger){
        return orderClient.createOrder(token, burger);
    }

    @Step("Push post-request to create {count} orders with authorization")
    public void createOrder(String burger, int count){
        for(int i = 0; i < count; i++) {
        orderClient.createOrder(token, burger);
        }
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

    @Step("Check response: status code, orders count")
    public void check(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        assertTrue("В ответе вернулись некорректные код состояния ответа и статус заказа", response.assertThat().statusCode(200).extract().path("success"));

        Order[] orders = response.extract().body().as(Orders.class).getOrders();
        assertNotNull("В ответе вернулся пустой ответ ", orders);
        assertEquals("Колиство заказазов в ответе не соотвествует ожидаемому", 0, orders.length);
    }

    @Step("Check response: status code, orders count, order id and number")
    public void check(ValidatableResponse response, String orderId, Integer number){
        assertNotNull("Вернулся невалидный ответ", response);
        assertTrue("В ответе вернулись некорректные код состояния ответа и статус заказа", response.assertThat().statusCode(200).extract().path("success"));

        Order[] orders = response.extract().body().as(Orders.class).getOrders();
        assertNotNull("В ответе вернулся пустой ответ ", orders);
        assertEquals("Колиство заказазов в ответе не соотвествует ожидаемому", 1, orders.length);
        assertFalse("В ответе вернулся пустое значение в поле _id заказа", orders[0].get_id().isBlank());
        assertEquals("Поле id заказаза в ответе не соотвествует ожидаемому", orderId, orders[0].get_id());
        assertEquals("Поле price цена заказа в ответе не соотвествует ожидаемому", number,  orders[0].getNumber());
    }


    @Step("Check response: status code, orders count")
    public void check(ValidatableResponse response, int count){
        assertNotNull("Вернулся невалидный ответ", response);
        assertTrue("В ответе вернулись некорректные код состояния ответа и статус заказа", response.assertThat().statusCode(200).extract().path("success"));

        Order[] orders = response.extract().body().as(Orders.class).getOrders();
        assertNotNull("В ответе вернулся пустой ответ ", orders);
        assertEquals("Колиство заказазов в ответе не соотвествует ожидаемому", count, orders.length);
        assertFalse("В ответе вернулся пустое значение в поле _id заказа", orders[0].get_id().isBlank());
        assertFalse("В ответе вернулся пустое значение в поле _id заказа", orders[1].get_id().isBlank());
    }
}
