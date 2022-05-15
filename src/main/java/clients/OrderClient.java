package clients;

import io.restassured.response.Response;
import orders.Order;
import orders.OrderCancelCredentials;
import orders.OrderCredentials;

import static io.restassured.RestAssured.given;


public class OrderClient extends RestAssuredClient{

    private final String ORDER_URL = "/orders";
    private final String ACCEPT_ID_ORDER_URL = ORDER_URL + "/accept/{id}";
    private final String ACCEPT_ORDER_URL = ORDER_URL + "/accept/";
    private final String GET_ORDER_URL = ORDER_URL + "/track";
    private final String CANCEL_ORDER_URL = ORDER_URL + "/cancel";



    public Response post (Order order){
        return reqSpec
                .body(order)
                .when()
                .post(ORDER_URL);
    }

    public Response put(OrderCredentials orderCredentials){
        return given().log().all()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .pathParam("id", orderCredentials.getId())
                .queryParam("courierId", orderCredentials.getCourierId())
                .body(orderCredentials)
                .when()
                .put(ACCEPT_ID_ORDER_URL);
    }

    public Response putWithoutId(OrderCredentials orderCredentials){
        return reqSpec
                .queryParam("courierId", orderCredentials.getCourierId())
                .body(orderCredentials)
                .when()
                .put(ACCEPT_ORDER_URL);
    }

    public Response get (){
        return reqSpec
                .when()
                .get(ORDER_URL);
    }

    public Response getByNumber(int track){
        return reqSpecWithoutHeaders
                .queryParam("t", track)
                .when()
                .get(GET_ORDER_URL);
    }

    public Response getByNumber(){
        return reqSpecWithoutHeaders
                .when()
                .get(GET_ORDER_URL);
    }

    public Response cancel(OrderCancelCredentials orderCreds){
         return reqSpec
                .body(orderCreds)
                .when().log().all()
                .put(CANCEL_ORDER_URL);
    }
}
