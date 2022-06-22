package client;

import io.restassured.response.ValidatableResponse;
import user.User;

public class UserClient extends RestAssuredClient{

    private final String AUTH = "/auth";
    private final String REGISTER = AUTH + "/register";
    private final String LOGIN = AUTH + "/login";
    private final String USER = AUTH + "/user";
    private final String TOKEN = AUTH + "/token";

    public ValidatableResponse register(User user) {
        return reqSpec
                .body(user)
                .when()
                .post(REGISTER)
                .then().log().all();
    }

    public ValidatableResponse login(User user) {
        return reqSpec
                .body(user)
                .when()
                .post(LOGIN)
                .then().log().all();
    }

    public ValidatableResponse delete(String token) {
        return reqSpec
                .header("Authorization", token)
                .when()
                .delete(USER)
                .then().log().all();
    }
}
