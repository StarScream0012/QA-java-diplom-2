package api;

import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientsApiHelper {
    public List<String> getAllIngredients(){

        Response response= given()
                .when()
                .get(API.GET_INGREDIENTS_URI)
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.jsonPath().getList("data._id");
    }
}
