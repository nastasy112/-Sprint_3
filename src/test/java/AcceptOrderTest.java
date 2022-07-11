import clients.CourierClient;
import clients.OrderClient;
import courier.Courier;
import courier.CourierCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import orders.Order;
import orders.OrderCancelCredentials;
import orders.OrderCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class AcceptOrderTest {

    private OrderClient orderClient;
    private Courier courier;
    private CourierClient courierClient;
    private int courierId;
    private Order order;
    int idOrder;
    int track;

    @Before
    public void setUp(){
        // создание курьера
        courier = Courier.getRandom();
        courierClient = new CourierClient();
        courierClient.create(courier);

        // логин, чтоб получить courierId
        CourierCredentials creds = CourierCredentials.from(courier);
        courierId = courierClient.login(creds);

        // создать заказ и получить id заказа по его track номеру
        orderClient = new OrderClient();
        order = Order.getDefault();

        track = orderClient.post(order).then().log().all().statusCode(201).extract().path("track");
        Response response = orderClient.getByNumber(track);
        response
                .then().log().all()
                .assertThat()
                .statusCode(200);
        idOrder = response.then()
                .extract()
                .path("order.id");
    }

    @After
    public void deleteCourierAndOrder(){
        OrderCancelCredentials orderCreds = new OrderCancelCredentials(track);
        orderClient.cancel(orderCreds);
        courierClient.delete(courierId);
    }

    //успешный запрос возвращает ok: true;

    @Test
    @DisplayName("Check status code and body of /accept/{id}")
    @Description("Basic test for /accept/{id} endpoint")
    public void AcceptNewOrderAndCheckResponse(){
        OrderCredentials orderCredentials = new OrderCredentials(idOrder,courierId);
        Response response = orderClient.put(orderCredentials);
        response.then().log().all()
                .statusCode(200)
                .assertThat().body("ok", notNullValue())
                .assertThat().body("ok", equalTo(true));
    }

    //если не передать id курьера, запрос вернёт ошибку;

    @Test
    @DisplayName("Check 400 status code and body of /accept/{id}")
    @Description("Request hasn't courierId for /accept/{id} endpoint")
    public void AcceptNewOrderWithoutCourierIdAndCheckError(){
        OrderCredentials orderCredentials = new OrderCredentials(idOrder,null);
        Response response = orderClient.put(orderCredentials);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"));
    }


    //если не передать номер заказа, запрос вернёт ошибку;

    @Test
    @DisplayName("Check 400 status code and body of /accept/{id}")
    @Description("Request hasn't id for /accept/{id} endpoint")
    public void AcceptNewOrderWithoutIdIdAndCheckError(){
        OrderCredentials orderCredentials = new OrderCredentials(idOrder,courierId);
        Response response = orderClient.putWithoutId(orderCredentials);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"));
    }
    // Дефект. Неверный код ответа

    //если передать неверный id курьера, запрос вернёт ошибку;

    @Test
    @DisplayName("Check 404 status code and body of /accept/{id}")
    @Description("Request has invalid CourierId for /accept/{id} endpoint")
    public void AcceptNewOrderWithInvalCourierIdIdAndCheckError(){
        OrderCredentials orderCredentials = new OrderCredentials(idOrder,0);
        Response response = orderClient.put(orderCredentials);
        response.then().log().all()
                .statusCode(404)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Курьера с таким id не существует"));
    }

    //если передать неверный номер заказа, запрос вернёт ошибку.
    @Test
    @DisplayName("Check 404 status code and body of /accept/{id}")
    @Description("Request has invalid id for /accept/{id} endpoint")
    public void AcceptNewOrderWithInvalidIdAndCheckError(){
        OrderCredentials orderCredentials = new OrderCredentials(0,courierId);
        Response response = orderClient.put(orderCredentials);
        response.then().log().all()
                .statusCode(404)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Заказа с таким id не существует"));
    }
}
