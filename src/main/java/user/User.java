package user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String email;
    private String password;
    private String name;

    public static User getRandomUser(int countSymbol){
        Faker faker = new Faker();
        return  new User(RandomStringUtils.randomAlphanumeric(countSymbol) + "@testDomain.test",
                RandomStringUtils.randomAlphanumeric(countSymbol),
                faker.name().firstName()
        );
    }
}
