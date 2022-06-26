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

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(String email){
        this.email = email;
    }

    public static User getRandomUser(){
        Faker faker = new Faker();
        return new User(faker.name().username() +  RandomStringUtils.randomAlphanumeric(6) + "mail@testDomain.test",
                RandomStringUtils.randomAlphanumeric(6),
                faker.name().fullName()
        );
    }

    public static User getEmptyField(String field){
        Faker faker = new Faker();
        String email = faker.name().firstName() + "mail@testDomain.test";
        String password = RandomStringUtils.randomAlphanumeric(6);
        String name = faker.name().firstName();

        switch (field){
            case "email":
                email = "";
                break;
            case "password":
                password = "";
                break;
            default:
                name = "";
        }
        return new User(email, password, name);
    }

    public static User getNullField(String field){
        Faker faker = new Faker();
        String email = faker.name().firstName() + "mail@testDomain.test";
        String password = RandomStringUtils.randomAlphanumeric(6);
        String name = faker.name().firstName();

        switch (field){
            case "email":
                email = null;
                break;
            case "password":
                password = null;
                break;
            default:
                name = null;
        }
        return new User(email, password, name);
    }
}
