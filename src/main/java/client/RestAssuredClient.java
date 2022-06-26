package client;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class RestAssuredClient {

    protected String URL = "https://stellarburgers.nomoreparties.site/api";

    protected final RequestSpecification reqSpec = given()
            .log().all()
            .header("Content-Type", "application/json")
            .baseUri(URL);

    protected RequestSpecification getReqSpec() {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .baseUri(URL);
    }
}
