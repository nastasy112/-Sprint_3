import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import orders.Order;
import orders.OrderCancelCredentials;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrderByNumberTest {

    private OrderClient orderClient;
    private Order order;
    int track;
    int idOrder;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        order = Order.getDefault();

        track = orderClient.post(order).then().log().all().statusCode(201).extract().path("track");
    }

    @After
    public void cancelOrder() {
        OrderCancelCredentials orderCreds = new OrderCancelCredentials(track);
        orderClient.cancel(orderCreds);
    }

    //успешный запрос возвращает объект с заказом;

    @Test
    @DisplayName("Check status code and body of /orders/track?t")
    @Description("Basic test for /orders/track?t endpoint")
    public void getOrdersByNumberAndCheckResponse() {
        Response response = orderClient.getByNumber(track);
        response
                .then().log().all()
                .assertThat()
                .statusCode(200);
        idOrder = response.then()
                .extract()
                .path("order.id");

        Assert.assertNotNull(idOrder);

        System.out.println("id заказа: " + idOrder);
    }

    //запрос без номера заказа возвращает ошибку;
    @Test
    @DisplayName("Check 400 status code and error message of /orders/track?t")
    @Description("Request hasn't track parameter for /orders/track?t endpoint")
    public void getOrdersWithoutTrackByNumber() {
        Response response = orderClient.getByNumber();
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"));
    }

    //запрос с несуществующим заказом возвращает ошибку.

    @Test
    @DisplayName("Check 404 status code and error message of /orders/track?t")
    @Description("Request has invalid track parameter for /orders/track?t endpoint")
    public void getOrdersWitInvalidTrackByNumber() {
        Response response = orderClient.getByNumber(RandomUtils.nextInt());
        response
                .then().log().all()
                .statusCode(404)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Заказ не найден"));
    }
}