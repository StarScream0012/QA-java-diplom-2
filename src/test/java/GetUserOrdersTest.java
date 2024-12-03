import api.API;
import api.IngredientsApiHelper;
import api.OrderApiHelper;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.OrderModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class GetUserOrdersTest extends BaseTest {
    private String accessToken;
    private List <List<String>>orderIngredients = new ArrayList<>();
    private final UserApiHelper userApiHelper = new UserApiHelper();
    private final OrderApiHelper orderApiHelper = new OrderApiHelper();
    private final IngredientsApiHelper ingredientsApiHelper = new IngredientsApiHelper();
    @Before
    @Description("Подготовка данных. Создание пользователя и заказов")
    public void setUp() {
        super.setUp();
        createUser();
        createUserOrder();
        createUserOrder();
    }
    @Test
    @Description("Получение заказов пользователя с авторизацией")
    public void getUserOrderWithAuth(){
        Response response = orderApiHelper.getUserOrder(accessToken);
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
    @Description("Попытка получения заказов пользователя без авторизации")
    public void getUserOrderWithoutAuth(){
        Response response = orderApiHelper.getUserOrder();
        response.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
    @Step("Создание юзера")
    public void createUser(){
        CreateUserModel createUserModel = new CreateUserModel(faker.internet().emailAddress(),
                faker.internet().password(), faker.name().firstName());
        Response response = userApiHelper.createUser(createUserModel);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        accessToken=response.path("accessToken");
    }
    @Step("Создание заказа")
    public void createUserOrder() {

        List<String> orderIngredients = getRandomIngredients();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = orderApiHelper.createOrder(orderModel,accessToken);
        response.then().assertThat().body("order.number", notNullValue())
                .and()
                .statusCode(200);
    }
    @Step("Получение случайного списка ингредиентов")
    public List<String> getRandomIngredients() {
        List<String> orderIngredients = new ArrayList<String>();
        Random random = new Random();
        List ingredients= ingredientsApiHelper.getAllIngredients();
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        this.orderIngredients.add(orderIngredients);
        return orderIngredients;
    }
    @After
    @Description("Удаление пользователя")
    public void cleanData(){
        Response response = userApiHelper.deleteUser(accessToken);
        response.then().assertThat().statusCode(202);
    }
}
