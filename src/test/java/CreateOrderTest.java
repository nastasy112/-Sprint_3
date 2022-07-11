import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import orders.Order;
import orders.OrderCancelCredentials;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private OrderClient orderClient;
    private Order order;
    private final List<String> color;
    private final int expectedCode;
    int track;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @After
    public void cancelOrder() {
        OrderCancelCredentials orderCreds = new OrderCancelCredentials(track);
        orderClient.cancel(orderCreds);
    }

    public CreateOrderTest(List<String> color, int expectedCode) {
        this.color = color;
        this.expectedCode = expectedCode;
    }

    @Parameterized.Parameters
    public static Object[][] createOrderWithColor() {
        return new Object[][]{
                {List.of("BLACK"), 201},
                {List.of("GREY"), 201},
                {List.of("BLACK", "GREY"), 201},
                {null, 201},
        };
    }

    //можно указать один из цветов — BLACK или GREY;
    //можно указать оба цвета;
    //можно совсем не указывать цвет;
    //тело ответа содержит track.

    @Test
    @DisplayName("Check status code and body of /orders")
    @Description("Basic test for /orders endpoint")
    public void createNewOrderAndCheckResponse(){
        order = Order.getDefault();
        order.setColor(color);

        Response response = orderClient.post(order);
        response.then()
                .statusCode(201)
                .assertThat().body("track", notNullValue());
        track = response.then().extract().path("track");
        System.out.println("Response for color "+ color + " : " + response.asString());

        int actualCode = response.statusCode();

        Assert.assertEquals(expectedCode, actualCode);
    }
}
