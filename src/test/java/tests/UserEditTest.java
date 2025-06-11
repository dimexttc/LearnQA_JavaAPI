package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lib.ApiCoreRequests;
import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestcase {
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
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getResgistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");


        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData,"firstName",newName);
    }

    @Test
    public void testEditUserWithoutAuth(){

        //Edit
        String newName="Alibaba";
        Map<String,String>editData=new HashMap<>();
        editData.put("firstName",newName);

        Response responseEditUserWithoutAuth = apiCoreRequests
                .makePutRequestWithoutTokenAndCookie("https://playground.learnqa.ru/api/user/"+userId,editData);


        Assertions.assertResponseCodeEquals(responseEditUserWithoutAuth,400);
        Assertions.assertJsonFieldHasValue(responseEditUserWithoutAuth,"error","Auth token not supplied");

    }

    @Test
    public void testEditUserWithStrangeUser(){
        //login
        Response responseGetAuth=apiCoreRequests
                .makePostRequest(urlLogin,userData);
        String header =this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie=this.getCookie(responseGetAuth,"auth_sid");

        //edit
        String newName="Alibaba";
        Map<String,String>editData=new HashMap<>();
        editData.put("firstName",newName);
        Response editUserWithStrangeUser= apiCoreRequests.makePutRequestWithTokenAndCookie(urlUser+"321",editData,header,cookie);editUserWithStrangeUser.prettyPrint();

        Assertions.assertResponseCodeEquals(editUserWithStrangeUser,400);
        Assertions.assertJsonFieldHasValue(editUserWithStrangeUser,"error","This user can only edit their own data.");

    }

    @Test
    public void testEditUserWrongEmail() {
        //login
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, userData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //edit
        String wrongEmail = "qwertymail.ru";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", wrongEmail);
        Response editUserWrongEmail = apiCoreRequests.makePutRequestWithTokenAndCookie(urlUser + userId, editData, header, cookie);

        Assertions.assertResponseCodeEquals(editUserWrongEmail, 400);
        Assertions.assertJsonFieldHasValue(editUserWrongEmail, "error", "Invalid email format");

    }

    @Test
    public void testEditUserShortName() {
        //login
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, userData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //edit
        String newName = "u";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response editUserShortName = apiCoreRequests.makePutRequestWithTokenAndCookie(urlUser + userId, editData, header, cookie);

        Assertions.assertResponseCodeEquals(editUserShortName, 400);
        Assertions.assertJsonFieldHasValue(editUserShortName, "error", "The value for field `firstName` is too short");

    }
    }