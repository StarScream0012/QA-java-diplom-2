package api;

import io.restassured.response.Response;
import model.CreateUserModel;
import model.LoginModel;
import model.UpdateUserModel;

import static io.restassured.RestAssured.given;

public class UserApiHelper {
    public Response createUser(CreateUserModel createUserModel){
        return given()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post(API.REGISTER_URI);
    }
    public Response deleteUser(String accessToken){
       return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete(API.USER_URI);
    }
    public Response login(LoginModel loginModel){
        return given()
                .header("Content-type", "application/json")
                .body(loginModel)
                .when()
                .post(API.LOGIN_URI);
    }
    public Response updateUser(UpdateUserModel updateUserModel){
        return given()
                .header("Content-type", "application/json")
                .body(updateUserModel)
                .when()
                .patch(API.USER_URI);
    }
    public Response updateUser(UpdateUserModel updateUserModel, String accessToken){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(updateUserModel)
                .when()
                .patch(API.USER_URI);
    }
    public Response getUser(String accessToken){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .get(API.USER_URI);
    }

}
