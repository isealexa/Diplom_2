package order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateOrderNoAuthTest {

    private OrderClient orderClient;

    @Before
    @Step("Set up order client")
    public void setUp(){
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Create order without authorization return success")
    @Description("This tests checks getting success response when push post-request to create order without authorization and correct data")
    public void createOrderWithAuthorizationReturnSuccess(){
        Ingredients ingredients = getIngredients();
        Ingredient main = findIngredientBy("main", ingredients);
        IngredientsIds burger = setBurger(main);

        ValidatableResponse response = createOrder(burger);
        check(response);
    }

    @Step("Get all existent ingredients")
    public Ingredients getIngredients(){
        return orderClient.getIngredients().extract().body().as(Ingredients.class);
    }

    @Step("Find ingredient by type {type}")
    public Ingredient findIngredientBy(String type, Ingredients ingredients){
        for(Ingredient ingredient: ingredients.getData()){
            if (ingredient.getType().equals(type)) {
                return  ingredient;
            }
        }
        return null;
    }

    @Step("Set burgers ingredients")
    public IngredientsIds setBurger(Ingredient main){
        String[] composition = {main.get_id()};
        return new IngredientsIds(composition);
    }

    @Step("Push post-request to create order with authorization and get response")
    public ValidatableResponse createOrder(IngredientsIds burger){
        return orderClient.createOrder(burger);
    }

    @Step("Check response fields and name, price and set order, number id")
    public void check(ValidatableResponse response){
        assertNotNull("Вернулся невалидный ответ", response);
        assertTrue("В ответе вернулись некорректные код состояние и статус заказа", response.assertThat().statusCode(200).extract().path("success"));

        NewOrder order = response.extract().body().as(Order.class).getOrder();
        assertNotNull("В ответе вернулось пустое значение в поле number", order.getNumber());
        assertNotEquals("Номер заказа не может быть равен 0",(Integer) 0, order.getNumber());
        assertFalse("В ответе вернулось пустое значение в поле name для названия бургера", response.extract().body().as(Order.class).getName().isBlank());
    }
}
