package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.http.Header;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie" )
    public Response makeGetRequest(String url,String token,String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only" )
    public Response makeGetRequestWithCookie(String url,String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only" )
    public Response makeGetRequestWithToken(String url,String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request" )
    public  Response makePostRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-request" )
    public  Response makePutRequestWithoutTokenAndCookie(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request" )
    public  Response makePutRequestWithTokenAndCookie(String url, Map<String, String> authData,String token,String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .body(authData)
                .put(url)
                .andReturn();
    }

    @Step("Make a DELETE-request" )
    public  Response makeDeleteRequest(String url, Map<String, String> authData,String token,String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .body(authData)
                .delete(url)
                .andReturn();
    }

    @Step("Make a DELETE-request" )
    public  Response makeDeleteRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .delete(url)
                .andReturn();
    }

}
