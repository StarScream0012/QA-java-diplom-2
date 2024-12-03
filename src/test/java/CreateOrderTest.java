import api.IngredientsApiHelper;
import api.OrderApiHelper;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CreateUserModel;
import model.OrderModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest extends BaseTest{

    private String accessToken;
    private List ingredients;
    private final OrderApiHelper orderApiHelper = new OrderApiHelper();
    private final UserApiHelper userApiHelper = new UserApiHelper();
    private final IngredientsApiHelper ingredientsApiHelper = new IngredientsApiHelper();
    @Before
    @Description("Подготовка к тесту. Создание юзера и получение общего списка ингредиентов")
    public void setUp() {
        super.setUp();
        createUser();
        ingredients= ingredientsApiHelper.getAllIngredients();
    }
    @Test
    @Description("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthAndIngredients() {
        List<String> orderIngredients = getRandomIngredients();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = orderApiHelper.createOrder(orderModel, accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .assertThat().body("order.number", notNullValue())
                .and()
                .assertThat().body("name", notNullValue())
                .and()
                .statusCode(200);
    }
    @Test
    @Description("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        List<String> orderIngredients = getRandomIngredients();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = orderApiHelper.createOrder(orderModel);
        response.then().assertThat().body("success", equalTo(true))
                .assertThat().body("order.number", notNullValue())
                .and()
                .assertThat().body("name", notNullValue())
                .and()
                .statusCode(200);
    }
    @Test
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {

        List<String> orderIngredients = new ArrayList<>();
        OrderModel orderModel=new OrderModel(orderIngredients);
        Response response = orderApiHelper.createOrder(orderModel,accessToken);
        response.then().assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }
    @After
    @Description("Удаление юзера по завершении теста")
    public void cleanData(){
        Response response = userApiHelper.deleteUser(accessToken);
        response.then().assertThat().statusCode(202);
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
    @Step("Получение случайного списка ингредиентов")
    public List<String> getRandomIngredients() {
        List<String> orderIngredients = new ArrayList<String>();
        Random random = new Random();
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        orderIngredients.add((String) ingredients.get(random.nextInt(ingredients.size())));
        return orderIngredients;
    }

}
