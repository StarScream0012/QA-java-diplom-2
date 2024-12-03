import api.API;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.UpdateUserModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest extends BaseTest{
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String name = faker.name().firstName();
    String accessToken;
    private final UserApiHelper userApiHelper = new UserApiHelper();
    private static Faker faker = new Faker();

    @Before
    @Description("Подготовка данных")
    public void setUp() {
        super.setUp();
        email = faker.internet().emailAddress();
        password = faker.internet().password();
        name = faker.name().firstName();
        CreateUserModel createUserModel = new CreateUserModel(email, password, name);
        Response response = userApiHelper.createUser(createUserModel);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        accessToken = response.path("accessToken");
    }

    @Test
    @Description("Попытка обновить пользователя без авторизации")
    public void updateUserWithoutToken() {
        UpdateUserModel updateUserModel = new UpdateUserModel(faker.internet().emailAddress(), faker.name().firstName());
        Response response = userApiHelper.updateUser(updateUserModel);
        response.then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @Test
    @Description("Обновление информации о пользователе")
    public void updateUser() {
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        UpdateUserModel updateUserModel = new UpdateUserModel(email, name);
        Response response = userApiHelper.updateUser(updateUserModel, accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .and()
                .statusCode(200);
        checkUserUpdated(email, name);
    }

    @Step("Проверка, что информация пользователя обновлена")
    public void checkUserUpdated(String email, String name) {
        Response response = userApiHelper.getUser(accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(email))
                .and()
                .body("user.name", equalTo(name))
                .and()
                .statusCode(200);
    }
    @After
    @Description("Удаление пользователя")
    public void cleanData(){
        Response response = userApiHelper.deleteUser(accessToken);
        response.then().assertThat().statusCode(202);
    }
}
