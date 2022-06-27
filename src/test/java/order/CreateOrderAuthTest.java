package order;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import user.Token;
import user.User;

import static org.junit.Assert.*;

public class CreateOrderAuthTest {

    private OrderClient orderClient;
    private String token;
    private String expectedUserEmail;
    private String expectedUserName;
    private String expectedOrderId;
    private Integer expectedNumber;
    private String expectedBurgerName;

    @Before
    @Step("Set up clients and test user")
    public void setUp(){
        orderClient = new OrderClient();
        UserClient userClient = new UserClient();
        Token newUser = userClient.register(User.getRandomUser()).extract().body().as(Token.class);
        token = newUser.getAccessToken();
        expectedUserEmail = newUser.getUser().getEmail();
        expectedUserName = newUser.getUser().getName();
        expectedOrderId = null;
        expectedNumber = null;
        expectedBurgerName = null;
    }

    @Test
    @DisplayName("Create order with authorization return success")
    @Description("This tests checks getting success response and creating order when push post-request to create order with authorization and correct data")
    public void createOrderWithAuthorizationReturnSuccess(){
        Ingredients ingredients = getIngredients();
        Ingredient bun = findIngredientBy("bun", ingredients);
        Ingredient main = findIngredientBy("main", ingredients);
        Ingredient sauce = findIngredientBy("sauce", ingredients);
        IngredientsIds burger = setBurger(bun, main, sauce);
        Integer expectedPrice = bun.getPrice() + main.getPrice() + sauce.getPrice();
        Ingredient[] expectedIngredient = {bun, main, sauce};

        ValidatableResponse response = createOrder(burger);
        checkResponseAndSetNumberAndIdFor(response, expectedPrice, expectedIngredient);
        checkOrderExist();
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
    public IngredientsIds setBurger(Ingredient bun, Ingredient main, Ingredient sauce){
        String[] composition = {bun.get_id(), main.get_id(), sauce.get_id()};
        return new IngredientsIds(composition);
    }

    @Step("Push post-request to create order with authorization and get response")
    public ValidatableResponse createOrder(IngredientsIds burger){
        return orderClient.createOrder(burger, token);
    }

    @Step("Check response fields and name, price and set order, number id")
    public void checkResponseAndSetNumberAndIdFor(ValidatableResponse response, Integer expectedPrice, Ingredient[] expectedIngredient){
        assertNotNull("Вернулся невалидный ответ", response);
        assertTrue("В ответе вернулись некорректные код состояние и статус заказа", response.assertThat().statusCode(200).extract().path("success"));

        NewOrder order = response.extract().body().as(Order.class).getOrder();
        expectedOrderId =  order.get_id();
        expectedNumber = order.getNumber();
        expectedBurgerName = order.getName();
        assertFalse("В ответе вернулось пустое значение в поле id", expectedOrderId.isBlank());
        assertNotNull("В ответе вернулось пустое значение в поле number", expectedNumber);
        assertNotEquals("Номер заказа не может быть равен 0",(Integer) 0, expectedNumber);
        assertFalse("В ответе вернулось пустое значение в поле name для названия бургера", expectedBurgerName.isBlank());
        assertEquals("Цена бургера не соотвествует ожидаемому", expectedPrice, order.getPrice());
        assertEquals("В заказе неправльно указан email пользователя", expectedUserEmail, order.getOwner().getEmail());
        assertEquals("В заказе неправильно указано имя пользователя", expectedUserName, order.getOwner().getName());
        assertNotNull("В ответе вернулся пустой список ингридиентов для бургера в заказе ", order.getIngredients());
        assertArrayEquals("Состав бургера не соответвует ожидаемому", expectedIngredient, order.getIngredients());
    }

    @Step("Check order exists in system")
    public void checkOrderExist(){
        Orders order = orderClient.getOrdersInfoBy(expectedNumber).extract().body().as(UsersOrders.class).getOrders()[0];

        assertEquals("Заказ не найден в системе", expectedOrderId, order.get_id());
        assertEquals("Номер заказа не соотвествует ожидаемому", expectedNumber, order.getNumber() );
        assertEquals("Название бургера не соотвествует ожидаемому", expectedBurgerName, order.getName());
        assertFalse("В ответе вернулось пустое значение для поля owner", order.getOwner().isBlank());
    }
}
