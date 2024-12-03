package api;

import model.OrderModel;

import java.util.List;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderApiHelper {
    public Response createOrder(OrderModel orderModel, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(orderModel)
                .when()
                .post(API.ORDERS_URI);
    }
    public Response createOrder(OrderModel orderModel) {
        return given()
                .header("Content-type", "application/json")
                .body(orderModel)
                .when()
                .post(API.ORDERS_URI);
    }
    public Response getUserOrder(String accessToken){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get(API.ORDERS_URI);
    }
    public Response getUserOrder(){
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(API.ORDERS_URI);
    }
}


