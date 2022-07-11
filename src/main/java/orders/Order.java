package orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private List color;

    public static Order getDefault() {
        return new Order(
                "Илья",
                "Зверев",
                "Москва, ул. Ленина, д.15",
                "Тропарево",
                "88002347549",
                5,
                "2022-05-06",
                "Комментарий",
                List.of("BLACK")
        );

    }
}
