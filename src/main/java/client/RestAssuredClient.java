package client;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class RestAssuredClient {

    protected String URL = "https://stellarburgers.nomoreparties.site/api";

    protected final RequestSpecification reqSpec = given()
            .header("Content-Type", "application/json")
            .baseUri(URL);
}
