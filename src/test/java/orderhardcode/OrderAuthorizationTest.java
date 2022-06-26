package orderhardcode;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.BurgerComposition;
import order.NewOrder;
import order.OrderNumber;
import order.OrdersByNumber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.User;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderAuthorizationTest {

    private final String burger;
    private final String expectedName;
    private final Integer expectedPrice;

    public OrderAuthorizationTest(String burger, String expectedName, Integer expectedPrice){
        this.burger = burger;
        this.expectedName = expectedName;
        this.expectedPrice = expectedPrice;
    }

    @Parameterized.Parameters(name = "The test {index} checks create order: {1} and with authorization")
    public static Object[][] getOrderParamWithAuth(){
        return new Object[][] {
                {new BurgerComposition().getStandardBurger(), new BurgerComposition().getName("standard"), new BurgerComposition().getPrice("standard")},
                {new BurgerComposition().getSuperBurger(), new BurgerComposition().getName("super"), new BurgerComposition().getPrice("super")},
                {new BurgerComposition().getBun(), new BurgerComposition().getName("bun"), new BurgerComposition().getPrice("bun")},
                {new BurgerComposition().getMain(), new BurgerComposition().getName("main"), new BurgerComposition().getPrice("main")},
        };
    }

    private OrderClient orderClient;
    private UserClient userClient;
    private String token;
    private String orderId;
    private Integer number;

    @Before
    @Step("Set up clients and test user")
    public void setUp(){
        orderClient = new OrderClient();
        userClient = new UserClient();
        ValidatableResponse newUser = userClient.register(User.getRandomUser());
        token = newUser.extract().path("accessToken");
        orderId = null;
        number = null;
    }

    @After
    @Step("Clean test data")
    public void clean(){
        userClient.delete(token);
    }

    @Test
    @DisplayName("Create order burger with authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order with authorization and correct data")
    public void createOrderBurgerWithAuthorizationReturnSuccess(){
        ValidatableResponse newOrder = createOrder();
        checkResponseAndSetNumberAndIdFor(newOrder);
        checkExistOrder();
    }

    @Step("Push post-request to create order with authorization and get response")
    public ValidatableResponse createOrder(){
        return orderClient.createOrder(token, burger);
    }


    @Step("Check response fields and name, price and set order, number id")
    public void checkResponseAndSetNumberAndIdFor(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        assertTrue("В ответе вернулись некорректные код состояние и статус заказа", response.assertThat().statusCode(200).extract().path("success"));

        NewOrder order = response.extract().body().as(NewOrder.class);
        orderId =  order.getOrder().get_id();
        number = order.getOrder().getNumber();
        assertNotNull("В ответе вернулось пустое значение в поле id", orderId);
        assertNotNull("В ответе вернулось пустое значение в поле number", number);
        assertEquals("Название бургера не соотвествует ожидаемому", expectedName, order.getName());
        assertEquals("Цена бургера не соотвествует ожидаемому", expectedPrice, order.getOrder().getPrice());
    }

    @Step("Check order exists in system")
    public void checkExistOrder(){
        OrderNumber order = orderClient.getOrdersInfoBy(number).extract().body().as(OrdersByNumber.class).getOrders()[0];

        assertEquals("Заказ не найден в системе", orderId, order.get_id());
        assertEquals("Номер заказа не соотвествует ожидаемому", number, order.getNumber() );
        assertEquals("Название бургера не соотвествует ожидаемому", expectedName, order.getName());
    }
}
