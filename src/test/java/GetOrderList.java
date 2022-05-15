import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrderList {
    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    //Проверь, что в тело ответа возвращается список заказов.

    @Test
    @DisplayName("Check status code and body of /orders")
    @Description("Basic test for /orders endpoint")
    public void getOrdersList(){
        Response response = orderClient.get();
        response.then()
                .statusCode(200)
                .assertThat().body("orders", notNullValue());
        System.out.println(response.asString());
    }
}
