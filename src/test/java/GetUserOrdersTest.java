import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.OrderModel;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class GetUserOrdersTest {
    private static Faker faker = new Faker();
    private String accessToken;
    private List <List<String>>orderIngredients = new ArrayList<>();


    @Before
    public void setUp() {
        RestAssured.baseURI = API.baseURI;
        createUser();
        createUserOrder();
        createUserOrder();
    }
    @Test
    public void getUserOrderWithAuth(){
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get(API.ordersURI);
        response.then().assertThat().body("success", equalTo(true))
                .body("orders.status", everyItem(notNullValue()))
                .body("orders.name", everyItem(notNullValue()))
                .body("orders.createdAt", everyItem(notNullValue()))
                .body("orders.updatedAt", everyItem(notNullValue()))
                .body("orders.number", everyItem(notNullValue()))
                .and()
                .statusCode(200);
        List<String> orderIds = response
                .jsonPath()
                .getList("orders._id", String.class);
        List<List<String>> ingredientsLists = response
                .jsonPath()
                .getList("orders.ingredients");
        Assert.assertEquals(2, orderIds.size());
        Assert.assertEquals(orderIngredients, ingredientsLists);
    }
    @Test
    public void getUserOrderWithoutAuth(){
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get(API.ordersURI);
        response.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
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
    @Step("Создание заказа")
    public void createUserOrder() {

        List<String> orderIngredients = getRandomIngredients();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(orderModel)
                .when()
                .post(API.ordersURI);
        response.then().assertThat().body("order.number", notNullValue())
                .and()
                .statusCode(200);
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
        List ingredients= getIngredients();
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        this.orderIngredients.add(orderIngredients);
        return orderIngredients;
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
