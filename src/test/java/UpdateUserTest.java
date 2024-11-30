import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.UpdateUserModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest {
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String name = faker.name().firstName();
    String accessToken;
    private static Faker faker = new Faker();

    @Before
    public void setUp() {
        RestAssured.baseURI = API.baseURI;
        email = faker.internet().emailAddress();
        password = faker.internet().password();
        name = faker.name().firstName();
        CreateUserModel createUserModel = new CreateUserModel(email, password, name);
        Response response = given()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post(API.registerURI);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        accessToken = response.path("accessToken");
    }

    @Test
    public void updateUserWithoutToken() {
        UpdateUserModel updateUserModel = new UpdateUserModel(faker.internet().emailAddress(), faker.name().firstName());
        Response response = given()
                .header("Content-type", "application/json")
                .body(updateUserModel)
                .when()
                .patch(API.userURI);
        response.then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @Test
    public void updateUser() {
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        UpdateUserModel updateUserModel = new UpdateUserModel(email, name);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(updateUserModel)
                .patch(API.userURI);
        response.then().assertThat().body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .and()
                .statusCode(200);
        checkUserUpdated(email, name);
    }

    @Step("Проверка, что информация пользователя обновлена")
    public void checkUserUpdated(String email, String name) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .get(API.userURI);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(email))
                .and()
                .body("user.name", equalTo(name))
                .and()
                .statusCode(200);
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
