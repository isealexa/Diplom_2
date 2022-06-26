package client;

import io.restassured.response.ValidatableResponse;
import user.User;

public class UserClient extends RestAssuredClient{

    private final String AUTH = "/auth";
    private final String REGISTER = AUTH + "/register";
    private final String LOGIN = AUTH + "/login";
    private final String USER = AUTH + "/user";

    public ValidatableResponse register(User user) {
        return getReqSpec()
                .body(user)
                .when()
                .post(REGISTER)
                .then().log().all();
    }

    public ValidatableResponse login(User user) {
        return getReqSpec()
                .body(user)
                .when()
                .post(LOGIN)
                .then().log().all();
    }

    public ValidatableResponse getUserData(String token) {
        return getReqSpec()
                .header("Authorization", token)
                .when()
                .get(USER)
                .then().log().all();
    }

    public ValidatableResponse changeUser(String data, String token) {
        return getReqSpec()
                .header("Authorization", token)
                .body(data)
                .when()
                .patch(USER)
                .then().log().all();
    }

    public ValidatableResponse changeUserWithoutToken(String data) {
        return getReqSpec()
                .body(data)
                .when()
                .patch(USER)
                .then().log().all();
    }

    public void delete(String token) {
         getReqSpec()
                 .header("Authorization", token)
                 .when()
                 .delete(USER)
                 .then().log().all()
                 .assertThat()
                 .statusCode(202)
                 .extract()
                 .path("success");
    }
}
