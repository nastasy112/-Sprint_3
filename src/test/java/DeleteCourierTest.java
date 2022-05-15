import clients.CourierClient;
import courier.Courier;
import courier.CourierCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class DeleteCourierTest {

    private Courier courier;
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void createCourierAndLogin(){
        courier = Courier.getRandom();
        courierClient = new CourierClient();
        courierClient.create(courier);
        CourierCredentials creds = CourierCredentials.from(courier);
        courierId = courierClient.login(creds);
    }

    @After
    public void deleteCourier(){
        courierClient.delete(courierId);
    }

    //успешный запрос возвращает ok: true;

    @Test
    @DisplayName("Check status code and body of /courier/:id")
    @Description("Basic test for /courier/:id endpoint")
    public void deleteCourierAndCheckResponse(){
        Response response = courierClient.delete(courierId);
        response.then().log().all()
                .statusCode(200)
                .assertThat().body("ok", notNullValue())
                .assertThat().body("ok", equalTo(true));
    }

    // если отправить запрос с несуществующим id, вернётся ошибка.
    // неуспешный запрос возвращает соответствующую ошибку;

    @Test
    @DisplayName("Check 404 status code and error message of /courier/:id")
    @Description("Courier with this id don't find for /courier/:id endpoint")
    public void deleteCourierWithInvalidIdAndCheckError(){
        courierId = 0;

        Response response = courierClient.delete(courierId);
        response.then().log().all()
                .statusCode(404)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Курьера с таким id нет."));
        System.out.println(response.asString());
    }

    //если отправить запрос без id, вернётся ошибка;
    @Test
    @DisplayName("Check 400 status code and error message of /courier/:id")
    @Description("Request hasn't id parameter  for /courier/:id endpoint")
    public void deleteCourierWithoutIdAndCheckError(){

        Response response = courierClient.deleteWithoutParam();
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для удаления курьера"));
        System.out.println(response.asString());
    }
    // Дефект. Не найден URL. Неверный код и тест ошибки
}
