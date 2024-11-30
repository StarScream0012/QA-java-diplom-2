import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParameterizedTest {
    String email;
    String password;
    String name;
    String accessToken;
    private static Faker faker = new Faker();
    public CreateUserParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Before
    public void setUp() {
        RestAssured.baseURI =API.baseURI;
    }
    @Test
    @Description("Проверка создания пользователя с одним отсутствующим параметром")
    public void oneParameterEmptyTest() {
        CreateUserModel model = new CreateUserModel(email, password, name);
        Response response = given()
                .header("Content-type", "application/json")
                .body(model)
                .when()
                .post(API.registerURI);
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {faker.internet().emailAddress(),faker.internet().password(), ""},
                {faker.internet().emailAddress(), "", faker.name().firstName()},
                {"", faker.internet().password(), faker.name().firstName()}
        };
    }

}
