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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderAuthorizationTest {

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
    @DisplayName("Create order standard burger with authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order with authorization and correct data")
    public void createOrderStandardBurgerWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getStandardBurger();
        ValidatableResponse newOrder = createOrder(token, burger);
        checkResponseAndGetId(newOrder);
    }

    @Test
    @DisplayName("Create order super burger with authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order with authorization and correct data")
    public void createOrderSuperBurgerWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getSuperBurger();
        ValidatableResponse newOrder = createOrder(token, burger);
        checkResponseAndGetId(newOrder);
    }

    @Test
    @DisplayName("Create order only bun with authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order with authorization and correct data")
    public void createOrderOnlyBunWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getBun();
        ValidatableResponse newOrder = createOrder(token, burger);
        checkResponseAndGetId(newOrder);
    }

    @Test
    @DisplayName("Create order only main with authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order with authorization and correct data")
    public void createOrderOnlyMainWithAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getMain();
        ValidatableResponse newOrder = createOrder(token, burger);
        checkResponseAndGetId(newOrder);
    }

    @Step("Push post-request to create order with authorization and get response")
    public ValidatableResponse createOrder(String token, String burger){
        return orderClient.createOrder(token, burger);
    }

    @Step("Check response fields and get order id")
    public void checkResponseAndGetId(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        //NewOrder order = response.extract().body().as(NewOrder.class);
        assertTrue("В ответе вернулись некорректные код состояние и статус заказа", response.assertThat().statusCode(200).extract().path("success"));
        //return order.getOrder().get_id();
    }
}
