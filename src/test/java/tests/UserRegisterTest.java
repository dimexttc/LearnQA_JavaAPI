package tests;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import lib.ApiCoreRequests;

public class UserRegisterTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testCreateUserWithExistingEmail() {

        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData=DataGenerator.getResgistrationData(userData);


        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

    @Test
    public void testCreateUserSuccesfully() {

        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getResgistrationData();


        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }

    @Test
    public void testCreatUserWrongEmail() {
        String email = "vinkotovexample.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getResgistrationData(userData);


        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }
        @ParameterizedTest()
        @CsvSource({
                "kdm@inbox.ru,pass123,dimexttc,Dima,,lastName",
                "kdm@inbox.ru,pass123,dimexttc,,Kulikov,firstName",
                 "kdm@inbox.ru,pass123,,Dima,Kulikov,username",
                 "kdm@inbox.ru,,dimexttc,Dima,Kulikov,password",
                ",pass123,dimexttc,Dima,Kulikov,email"})
        public void testCreateUserWithoutOneParam(String email,String password,String username,String firstName,String lastName,
                                                  String misingField){
        Map<String,String> userData=new HashMap<>();
            userData.put("email",email);
            userData.put("password",password);
            userData.put("username",username);
            userData.put("firstName",firstName);
            userData.put("lastName",lastName);

            Response responseCreateAuth = apiCoreRequests
                    .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: "+misingField);

        }

        @Test
    public void testCreateUserWithShortName(){
        String userName="D";
        Map<String,String> userData=new HashMap<>();
        userData.put("firstName",userName);
        userData=DataGenerator.getResgistrationData(userData);

            Response responseCreateAuth = apiCoreRequests
                    .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'firstName' field is too short");

        }

        @Test
    public void testCreateUserWithLongName(){
        String userName="qwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwertyqwerty";
        Map<String,String>userData=new HashMap<>();
        userData.put("firstName",userName);
        userData=DataGenerator.getResgistrationData(userData);

            Response responseCreateAuth = apiCoreRequests
                    .makePostRequest("https://playground.learnqa.ru/api/user/", userData);
            Assertions.assertResponseCodeEquals(responseCreateAuth,400);
            Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'firstName' field is too long");
        }
    }
