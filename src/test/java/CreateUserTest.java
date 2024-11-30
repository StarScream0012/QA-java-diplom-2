import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTest {
    CreateUserModel createUserModel;
    String accessToken;
    private static Faker faker = new Faker();
    @Before
    public void setUp() {
        RestAssured.baseURI = API.baseURI;
    }
    @Test
    public void createUniqueUserTest(){
        createUserModel = new CreateUserModel(faker.internet().emailAddress(),
                faker.internet().password(), faker.name().firstName());
        Response response = given()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post(API.registerURI);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(createUserModel.getEmail()))
                .and().assertThat().body("user.name", equalTo(createUserModel.getName()))
                .and().assertThat().body("accessToken", notNullValue())
                .and().statusCode(200);
        accessToken=response.path("accessToken");
    }
    @Test
    public void createExistingUserTest(){
        createUserModel = new CreateUserModel(faker.internet().emailAddress(),
                faker.internet().password(), faker.name().firstName());
        Response response = given()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post(API.registerURI);
        response.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.email", equalTo(createUserModel.getEmail()))
                .and().assertThat().body("user.name", equalTo(createUserModel.getName()))
                .and().assertThat().body("accessToken", notNullValue())
                .and().statusCode(200);
        accessToken=response.path("accessToken");
        Response duplicateResponse = given()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post(API.registerURI);
        duplicateResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
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
