import courier.Courier;
import clients.CourierClient;
import courier.CourierCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {
    private Courier courier;
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void createCourier(){
        courier = Courier.getRandom();
        courierClient = new CourierClient();
        courierClient.create(courier);
    }

    @After
    public void deleteCourier(){
        courierClient.delete(courierId);
    }

    // курьер может авторизоваться
    // для авторизации нужно передать все обязательные поля;
    // успешный запрос возвращает id

    @Test
    @DisplayName("Check status code and body of /courier/login")
    @Description("Basic test for courier/login endpoint")
    public void loginCourierAndCheckResponse(){
        CourierCredentials creds = CourierCredentials.from(courier);
        courierId = courierClient.login(creds);


        Assert.assertNotEquals(0, courierId);
        System.out.println("Courier logged successful. Test passed");
    }

    //система вернёт ошибку, если неправильно указать логин или пароль;
    //если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
    @Test
    @DisplayName("Check 404 status code and error message of /courier/login")
    @Description("Login with invalid login for /courier/login endpoint")
    public void loginCourierWithInvalidLoginAndCheckError(){
        CourierCredentials creds = CourierCredentials.from(courier);
        creds.setLogin(RandomStringUtils.randomAlphanumeric(8));
        Response response = courierClient.postLogin(creds);
        response.then().log().all()
                .statusCode(404)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Учетная запись не найдена"));
        System.out.println("Courier didn't login. Test passed" + response.asString());
    }

    //система вернёт ошибку, если неправильно указать логин или пароль;

    @Test
    @DisplayName("Check 404 status code and error message of /courier/login")
    @Description("Login with invalid password for /courier/login endpoint")
    public void loginCourierWithInvalidPasswordAndCheckError(){
        CourierCredentials creds = CourierCredentials.from(courier);
        creds.setPassword("error");
        Response response = courierClient.postLogin(creds);
        response.then().log().all()
                .statusCode(404)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Учетная запись не найдена"));
        System.out.println("Courier didn't login. Test passed" + response.asString());
    }

    //если какого-то поля нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Check 400 status code and error message of /courier/login")
    @Description("Login with invalid login for /courier/login endpoint")
    public void loginCourierWithoutLoginAndCheckError(){
        CourierCredentials creds = CourierCredentials.from(courier);
        creds.setLogin(null);
        Response response = courierClient.postLogin(creds);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для входа"));
        System.out.println("Courier didn't login. Test passed" + response.asString());
    }

    //если какого-то поля нет, запрос возвращает ошибку;
    @Test
    public void loginCourierWithoutPasswordAndCheckError(){
        CourierCredentials creds = CourierCredentials.from(courier);
        creds.setPassword(null);
        Response response = courierClient.postLogin(creds);
        response.then().log().all()
                .statusCode(400)
                .assertThat().body("message", notNullValue())
                .assertThat().body("message", equalTo("Недостаточно данных для входа"));
        System.out.println("Courier didn't login. Test passed" + response.asString());
    }
    // Дефект. Неверный код ошибки (ошибка сервера 504)

}
