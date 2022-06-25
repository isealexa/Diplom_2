package order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.BurgerComposition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderNoAuthTest {

    private OrderClient orderClient;

    @Before
    @Step("Set up order client")
    public void setUp(){
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Create correct mini burger order without authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order without authorization and correct data")
    public void createOrderMiniBurgerWithoutAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getMiniBurger();
        ValidatableResponse newOrder = createOrder(burger);
        checkResponseAndGetId(newOrder);
    }

    @Test
    @DisplayName("Create correct big burger order without authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order without authorization and correct data")
    public void createOrderBigBurgerWithoutAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getBigBurger();
        ValidatableResponse newOrder = createOrder(burger);
        checkResponseAndGetId(newOrder);
    }

    @Test
    @DisplayName("Create order return double bun without authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order without authorization and correct data")
    public void createOrderDoubleBunWithoutAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getDoubleBun();
        ValidatableResponse newOrder = createOrder(burger);
        checkResponseAndGetId(newOrder);
    }

    @Test
    @DisplayName("Create order return only sauce without authorization return success ")
    @Description("This tests checks getting success response and creating order when push post-request to create order without authorization and correct data")
    public void createOrderOnlySauceWithoutAuthorizationReturnSuccess(){
        String burger = new BurgerComposition().getSauce();
        ValidatableResponse newOrder = createOrder(burger);
        checkResponseAndGetId(newOrder);
    }

    @Step("Push post-request to create order without authorization and get response")
    public ValidatableResponse createOrder(String burger){
        return orderClient.createOrder(burger);
    }

    @Step("Check response fields and get order id")
    public void checkResponseAndGetId(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        //NewOrder order = response.extract().body().as(NewOrder.class);
        assertTrue("В ответе вернулись некорректные код состояние и статус заказа", response.assertThat().statusCode(200).extract().path("success"));
        //return order.getOrder().get_id();
    }
}
