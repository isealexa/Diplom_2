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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderNoAuthTest {

    private final String burger;
    private final String expectedName;

    public OrderNoAuthTest(String burger, String expectedName){
        this.burger = burger;
        this.expectedName = expectedName;
    }

    @Parameterized.Parameters(name = "The test {index} checks create order: {1} and without authorization")
    public static Object[][] getOrderParamNoAuth(){
        return new Object[][] {
                {new BurgerComposition().getMain(), new BurgerComposition().getName("main") },
                {new BurgerComposition().getBigBurger(), new BurgerComposition().getName("big") },
                {new BurgerComposition().getDoubleBun(), new BurgerComposition().getName("double bun") },
                {new BurgerComposition().getSauce(), new BurgerComposition().getName("sauce") },
        };
    }

    private OrderClient orderClient;

    @Before
    @Step("Set up order client")
    public void setUp(){
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Create correct order without authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order without authorization and correct data")
    public void createOrderBurgerWithoutAuthorizationReturnSuccess(){
        ValidatableResponse newOrder = createOrder();
        checkResponse(newOrder);
    }

    @Step("Push post-request to create order without authorization and get response")
    public ValidatableResponse createOrder(){
        return orderClient.createOrder(burger);
    }

    @Step("Check response fields")
    public void checkResponse(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        NewOrder order = response.extract().body().as(NewOrder.class);
        assertTrue("В ответе вернулись некорректные код состояние и статус заказа", response.assertThat().statusCode(200).extract().path("success"));
        assertNotNull("В ответе вернулось пустое значение в поле number", order.getOrder().getNumber());
        assertEquals("Название бургера не соотвествует ожидаемому", expectedName, order.getName());
    }
}
