import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class ListOfGistsTests {
    String baseURI = "https://api.github.com";
    String auth_token;

    @BeforeEach
    public void setUp() {
        Dotenv dotenv = Dotenv.load();
        auth_token = dotenv.get("TOKEN_FOR_GIST");
        if (auth_token.isEmpty()) {
            throw new RuntimeException("Please set the auth token before running tests");
        }
    }

    @Test
    public void getTotalNumberOfGists() {
        given()
                .header("Authorization", "token " + auth_token)
                .baseUri(baseURI)
                .when().get("/gists")
                .then().assertThat().body("size()", equalTo(4));
    }

    @Test
    public void getListForAuthenticatedUser() {
        List<String> gistIDs = given()
                .header("Authorization", "token " + auth_token)
                .baseUri(baseURI)
                .when()
                .get("/gists")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath().getList("id");

        System.out.println("List of gists: " + gistIDs);
    }

    @Test
    @Tag("smoke")
    public void getErrorForUnauthenticatedUsers() {
        given().header("Authorization", "token " + "some_invalid_token")
                .baseUri(baseURI)
                .when()
                .get("/gists")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    public void checkResponseSchemaJson() {
        given()
                .header("Authorization", "token " + auth_token)
                .baseUri(baseURI)
                .when()
                .get("/gists")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("gistsResponseSchema.json"));
    }
}
