import api.API;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.CreateUserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParameterizedTest extends BaseTest{
    String email;
    String password;
    String name;
    private final UserApiHelper userApiHelper = new UserApiHelper();
    public CreateUserParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Test
    @Description("Проверка создания пользователя с одним отсутствующим параметром")
    public void oneParameterEmptyTest() {
        CreateUserModel model = new CreateUserModel(email, password, name);
        Response response = userApiHelper.createUser(model);
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
