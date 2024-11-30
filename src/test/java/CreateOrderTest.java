import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.OrderModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {
    private static Faker faker = new Faker();
    private String accessToken;
    private List ingredients;
    @Before
    public void setUp() {
        RestAssured.baseURI = API.baseURI;
        createUser();
        ingredients= getIngredients();
    }
    @Test
    public void createOrderWithAuthAndIngredients() {

        List<String> orderIngredients = getRandomIngredients();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(orderModel)
                .when()
                .post(API.ordersURI);
        response.then().assertThat().body("success", equalTo(true))
                .assertThat().body("order.number", notNullValue())
                .and()
                .assertThat().body("name", notNullValue())
                .and()
                .statusCode(200);
    }
    @Test
    public void createOrderWithoutAuth() {
        List<String> orderIngredients = getRandomIngredients();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .body(orderModel)
                .when()
                .post(API.ordersURI);
        response.then().assertThat().body("success", equalTo(true))
                .assertThat().body("order.number", notNullValue())
                .and()
                .assertThat().body("name", notNullValue())
                .and()
                .statusCode(200);
    }
    @Test
    public void createOrderWithoutIngredients() {

        List<String> orderIngredients = new ArrayList<>();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(orderModel)
                .when()
                .post(API.ordersURI);
        response.then().assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
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
    @Step("Создание юзера")
    public void createUser(){
        CreateUserModel createUserModel = new CreateUserModel(faker.internet().emailAddress(),
                faker.internet().password(), faker.name().firstName());
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

    @Step("Получение общего списка ингредиентов")
    public static List<String>  getIngredients(){

        Response response = given()
                .when()
                .get(API.getIngredientsURI)
                .then()
                .statusCode(200)
                .extract()
                .response();
       return response.jsonPath().getList("data._id");

    }
    @Step("Получение случайного списка ингредиентов")
    public List<String> getRandomIngredients() {
        List<String> orderIngredients = new ArrayList<String>();
        Random random = new Random();
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        return orderIngredients;
    }

}
