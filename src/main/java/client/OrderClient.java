package client;

import io.restassured.response.ValidatableResponse;
import order.IngredientsIds;

public class OrderClient extends RestAssuredClient {

    private final String INGREDIENTS = "/ingredients";
    private final String ORDERS =  "/orders";
    private final String NUMBER = ORDERS + "/{number}";

    public ValidatableResponse getIngredients() {
        return reqSpec()
                .when()
                .get(INGREDIENTS)
                .then().log().all();
    }

    public ValidatableResponse createOrder(IngredientsIds order, String token) {
        return reqSpec()
                .header("Authorization", token)
                .body(order)
                .when()
                .post(ORDERS)
                .then().log().all();
    }

    public ValidatableResponse createOrder(String token, String order) {
        return reqSpec()
                .header("Authorization", token)
                .body(order)
                .when()
                .post(ORDERS)
                .then().log().all();
    }

    public ValidatableResponse createOrder(String order) {
        return reqSpec()
                .body(order)
                .when()
                .post(ORDERS)
                .then().log().all();
    }

    public ValidatableResponse createOrder() {
        return reqSpec()
                .when()
                .post(ORDERS)
                .then().log().all();
    }

    public ValidatableResponse getOrder(String token) {
        return reqSpec()
                .header("Authorization", token)
                .when()
                .get(ORDERS)
                .then().log().all();
    }

    public ValidatableResponse getOrder() {
        return reqSpec()
                .when()
                .get(ORDERS)
                .then().log().all();
    }

    public ValidatableResponse getOrdersInfoBy(Integer number) {
        return reqSpec()
                .pathParam("number", number)
                .when()
                .get(NUMBER)
                .then().log().all();
    }
}
