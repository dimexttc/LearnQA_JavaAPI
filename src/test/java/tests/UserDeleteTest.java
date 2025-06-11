package tests;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import lib.ApiCoreRequests;

public class UserDeleteTest extends BaseTestcase{
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String urlLogin = "https://playground.learnqa.ru/api/user/login";
    String urlUser = "https://playground.learnqa.ru/api/user/";
    Map<String, String> userData;
    JsonPath responseCreateAuth;
    String userId;
    @BeforeEach
    public void generateUser(){
        this.userData = DataGenerator.getResgistrationData();

        this.responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();

        this.userId = responseCreateAuth.getString("id");
    }

    @Test
    public void deleteUsewWithId2(){
        String email="vinkotov@example.com";
        String password="1234";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        Response deleteUsewWithId2= apiCoreRequests.makeDeleteRequest(urlUser+userId,authData,header,cookie);
        deleteUsewWithId2.prettyPrint();

        Assertions.assertResponseCodeEquals(deleteUsewWithId2, 400);
        Assertions.assertJsonFieldHasValue(deleteUsewWithId2, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

    }

    @Test
    public void positiveDeleteUser() {
        //login
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, userData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //delete
        Response deleteUser= apiCoreRequests.makeDeleteRequest(urlUser+userId,userData,header,cookie);
        Assertions.assertResponseCodeEquals(deleteUser, 200);
        Assertions.assertJsonFieldHasValue(deleteUser, "success", "!");

        //Get
        Response getUser= apiCoreRequests.makeGetRequest(urlUser+userId,header,cookie);
        Assertions.assertResponseTextEquals(getUser,"User not found");
    }

    @Test
    public void deleteUsewWithStrange(){
        //generate second user
        Map<String, String> authData = DataGenerator.getResgistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(urlUser,authData);
        String userId1 = responseCreateAuth.jsonPath().getString("id");

        //login
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, userData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //attemp to delete another user
        Response deleteUsewWithStrange= apiCoreRequests.makeDeleteRequest(urlUser+userId1,userData,header,cookie);

        Assertions.assertResponseCodeEquals(deleteUsewWithStrange, 400);
        Assertions.assertJsonFieldHasValue(deleteUsewWithStrange, "error", "This user can only delete their own account.");

    }

}
