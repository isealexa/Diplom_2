package order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UsersOrderNegativeTest {

    private OrderClient orderClient;

    @Before
    @Step("Set up order client")
    public void setUp(){
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Get user's order without authorization return error")
    @Description("This tests checks getting error 401 and message 'You should be authorised' and show error message when push get-request without authorization")
    public void getUsersOrderWithoutAuthorizationReturnError(){
        int expectedCode = 401;
        String expectedMessage = "You should be authorised";
        ValidatableResponse response = getUserOrder();
        check(response, expectedCode, expectedMessage);
    }

    @Test
    @DisplayName("Get user's order with empty token return error")
    @Description("This tests checks getting error 401 and message 'You should be authorised' and show error message when push get-request with empty authorization token")
    public void getUsersOrderWithEmptyTokenReturnError(){
        String token = "";
        int expectedCode = 401;
        String expectedMessage = "You should be authorised";
        ValidatableResponse response = getUserOrder(token);
        check(response, expectedCode, expectedMessage);
    }


    @Test
    @DisplayName("Get user's order with incorrect token return error")
    @Description("This tests checks getting error 403 and message 'jwt malformed' and show error message when push get-request with incorrect authorization token")
    public void getUsersOrderWithIncorrectTokenReturnError(){
        String token = "incorrect token";
        int expectedCode = 403;
        String expectedMessage = "jwt malformed";
        ValidatableResponse response = getUserOrder(token);
        check(response, expectedCode, expectedMessage);
    }

    @Test
    @DisplayName("Get user's order with expired token return error")
    @Description("This tests checks getting error 403 and message 'jwt expired' and show error message when push get-request with expired authorization token")
    public void getUsersOrderWithExpiredTokenReturnError(){
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYyYjRiNGUxOTNlYmMyMDAxYjQ5NGU2NiIsImlhdCI6MTY1NjAwOTk1MywiZXhwIjoxNjU2MDExMTUzfQ.Gqj9MR37y7R3_ozX9RdVNY8q1SSXagZPEtj_Zl2y99E";
        int expectedCode = 403;
        String expectedMessage = "jwt expired";
        ValidatableResponse response = getUserOrder(token);
        check(response, expectedCode, expectedMessage);
    }


    @Step("Push get-request without authorization to get response")
    public ValidatableResponse getUserOrder(){
        return orderClient.getOrder();
    }

    @Step("Push get-request with incorrect or expired authorization token to get response")
    public ValidatableResponse getUserOrder(String token){
        return orderClient.getOrder(token);
    }

    @Step("Check response: status code and message ")
    public void check(ValidatableResponse response, int expectedCode, String expectedMessage){
        assertNotNull("Вернулся невалидный ответ", response);
        assertEquals("В ответе вернулись некорректный код состояния", expectedCode, response.extract().statusCode());
        assertFalse(response.extract().body().as(Orders.class).isSuccess());

        String actualMessage = response.extract().body().as(Orders.class).getMessage();
        assertFalse("В ответе вернулся пустое значение в message", actualMessage.isBlank());
        assertEquals("Поле id заказаза в ответе не соотвествует ожидаемому", expectedMessage, expectedMessage);
    }
}
