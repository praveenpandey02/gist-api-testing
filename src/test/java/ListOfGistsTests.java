import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ListOfGistsTests {
    public static RequestSpecification requestSpec;

    @BeforeAll
    public static void setUpSpec() {
        TestSetup.globalSetup();
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(TestSetup.BASE_URI)
                .addHeader("Authorization", "token " + TestSetup.auth_token)
                .build();
    }

    @Test
    public void getListForAuthenticatedUser() {
        List<String> gistIDs = given()
                .spec(requestSpec)
                .when()
                .get("/gists")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath().getList("id");

        System.out.println("List of gists: " + gistIDs);
    }

    @Test
    public void getErrorForUnauthenticatedUsers() {
        given().header("Authorization", "token " + "some_invalid_token")
                .baseUri(TestSetup.BASE_URI)
                .when()
                .get("/gists")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @Tag("regression")
    public void checkResponseSchemaJson() {
        given()
                .spec(requestSpec)
                .when()
                .get("/gists")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("gistsResponseSchema.json"));
    }
}
