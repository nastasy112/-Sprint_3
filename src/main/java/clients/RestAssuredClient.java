package clients;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestAssuredClient {

    protected final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    protected final RequestSpecification reqSpec = given().log().all()
            .baseUri(BASE_URL)
            .contentType("application/json");

    protected final RequestSpecification reqSpecWithoutHeaders = given().log().all()
            .baseUri(BASE_URL);

}
