import api.API;
import api.IngredientsApiHelper;
import api.OrderApiHelper;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.LoginModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginTest extends BaseTest  {
    String email;
    String name;
    String password;
    String accessToken;
    private final UserApiHelper userApiHelper = new UserApiHelper();
    @Before
    @Description("Подготовка данных")
    public void setUp() {
        super.setUp();
        email=faker.internet().emailAddress();
        name = faker.name().firstName();
        password=faker.internet().password();
        CreateUserModel createUserModel = new CreateUserModel(email,password,name);
        Response response = userApiHelper.createUser(createUserModel);
        accessToken=response.path("accessToken");
    }
    @Test
    @Description("Успешная авторизация")
    public void loginTest(){
        LoginModel loginModel=new LoginModel(email,password);
        Response response = userApiHelper.login(loginModel);
        response.then().assertThat().body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .and()
                .statusCode(200);
    }
    @Test
    @Description("Проверка авторизации с неправильным логином или паролем")
    public void loginFailedTest(){
        LoginModel loginModel=new LoginModel(faker.internet().emailAddress(),faker.internet().password());
        Response response = userApiHelper.login(loginModel);
        response.then().assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }
    @After
    @Description("Удаление пользователя")
    public void cleanData(){
        Response response = userApiHelper.deleteUser(accessToken);
        response.then().assertThat().statusCode(202);
    }
}
