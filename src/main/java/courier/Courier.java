package courier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Courier {

    private String login;
    private String password;
    private String firstName;

    // фабрика
    public static Courier getRandom(){
        String login = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomAlphanumeric(10);
        String firstName = RandomStringUtils.randomAlphanumeric(10);

        return new Courier(login, password, firstName);

    }
}
