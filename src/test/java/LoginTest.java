import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.LoginModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginTest {
    String email;
    String name;
    String password;
    String accessToken;
    private static Faker faker = new Faker();
    @Before
    public void setUp() {
        RestAssured.baseURI = API.baseURI;
        email=faker.internet().emailAddress();
        name = faker.name().firstName();
        password=faker.internet().password();
        CreateUserModel createUserModel = new CreateUserModel(email,password,name);
        Response response = given()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post(API.registerURI);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        accessToken=response.path("accessToken");
    }
    @Test
    public void loginTest(){
        LoginModel loginModel=new LoginModel(email,password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(loginModel)
                .when()
                .post(API.loginURI);
        response.then().assertThat().body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .and()
                .statusCode(200);
    }
    @Test
    public void loginFailedTest(){
        LoginModel loginModel=new LoginModel(faker.internet().emailAddress(),faker.internet().password());
        Response response = given()
                .header("Content-type", "application/json")
                .body(loginModel)
                .when()
                .post(API.loginURI);
        response.then().assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }
    @After
    public void cleanData(){
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete(API.userURI);
        response.then().assertThat().statusCode(202);
    }
}
