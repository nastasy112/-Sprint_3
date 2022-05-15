import courier.Courier;
import clients.CourierClient;
import courier.CourierCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;

@Data
public class CreateCourierTest {
    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void deleteCourier(){
        if((courier.getLogin() != null)&(courier.getPassword() != null)){
            CourierCredentials creds = CourierCredentials.from(courier);
            courierId = courierClient.login(creds);
            courierClient.delete(courierId);
        }
    }

    // курьера можно создать
    // запрос возвращает правильный код ответа;
    // успешный запрос возвращает ok: true;

    @Test
    @DisplayName("Check status code and body of /courier")
    @Description("Basic test for /courier endpoint")
    public void createNewCourierAndCheckResponse(){
        courier = Courier.getRandom();
        boolean created = courierClient.create(courier);

        Assert.assertTrue(created);
    }

    // нельзя создать двух одинаковых курьеров

    @Test
    @DisplayName("Check 409 status code and error message of /courier")
    @Description("Courier is already exist for /courier endpoint")
    public void courierAlreadyExistsAndCheckError(){
        Courier courier_prescript = Courier.getRandom();
        courierClient.create(courier_prescript);

        courier = courier_prescript;
        Response response = courierClient.post(courier);
        response.then().log().all()
                .statusCode(409)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Этот логин уже используется"));
        System.out.println(response.asString());
    }
    // Дефект. Неверный текст ошибки


    // чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    @DisplayName("Check status code and body of /courier")
    @Description("Courier with all required fields for /courier endpoint")
    public void createNewCourierWithAllRequiredFieldsAndCheckResponse(){
        courier = new Courier(RandomStringUtils.randomAlphabetic(8), "1234", null);
        boolean created = courierClient.create(courier);

        Assert.assertTrue(created);
    }

    //если одного из полей нет, запрос возвращает ошибку
    @Test
    @DisplayName("Check 400 status code and error message of /courier")
    @Description("Courier can't created without password for /courier endpoint")
    public void createNewCourierWithoutRequiredFieldPasswordAndCheckError(){
        courier = new Courier(UUID.randomUUID().toString(), null, null);
        Response response = courierClient.post(courier);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Check 400 status code and error message of /courier")
    @Description("Courier can't created without login for /courier endpoint")
    public void createNewCourierWithoutRequiredFieldLoginAndCheckError(){
        courier = new Courier(null, "1234", null);
        Response response = courierClient.post(courier);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // если создать пользователя с логином, который уже есть, возвращается ошибка.
    @Test
    @DisplayName("Check 409 status code and error message of /courier")
    @Description("Login is already exist for /courier endpoint")
    public void courierAlreadyExistsThisLoginAndCheckError(){
        Courier courier_prescript = Courier.getRandom();
        courierClient.create(courier_prescript);

        courier = new Courier(courier_prescript.getLogin(), courier_prescript.getPassword(), "Alex");
        Response response = courierClient.post(courier);
        response.then().log().all()
                .statusCode(409)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Этот логин уже используется"));
        System.out.println(response.asString());
    }
    // Дефект. Неверный текст ошибки



}
