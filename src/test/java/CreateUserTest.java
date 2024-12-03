import api.API;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.CreateUserModel;
import org.junit.After;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTest extends BaseTest{
    CreateUserModel createUserModel;
    String accessToken;
    private final UserApiHelper userApiHelper = new UserApiHelper();
    @Test
    @Description("Создание уникального пользователя")
    public void createUniqueUserTest(){
        createUserModel = new CreateUserModel(faker.internet().emailAddress(),
                faker.internet().password(), faker.name().firstName());
        Response response = userApiHelper.createUser(createUserModel);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(createUserModel.getEmail()))
                .and().assertThat().body("user.name", equalTo(createUserModel.getName()))
                .and().assertThat().body("accessToken", notNullValue())
                .and().statusCode(200);
        accessToken=response.path("accessToken");
    }
    @Test
    @Description("Попытка создания существующего пользователя")
    public void createExistingUserTest(){
        createUserModel = new CreateUserModel(faker.internet().emailAddress(),
                faker.internet().password(), faker.name().firstName());
        Response response =userApiHelper.createUser(createUserModel);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(createUserModel.getEmail()))
                .and().assertThat().body("user.name", equalTo(createUserModel.getName()))
                .and().assertThat().body("accessToken", notNullValue())
                .and().statusCode(200);
        accessToken=response.path("accessToken");
        Response duplicateResponse = userApiHelper.createUser(createUserModel);
        duplicateResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
    }
    @After
    @Description("Удаление пользователя")
    public void cleanData(){
        Response response = userApiHelper.deleteUser(accessToken);
        response.then().assertThat().statusCode(202);
    }
}
