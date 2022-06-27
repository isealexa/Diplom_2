package client;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class RestAssuredClient {

    protected String URL = "https://stellarburgers.nomoreparties.site/api";

    protected RequestSpecification reqSpec() {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .baseUri(URL);
    }
}
