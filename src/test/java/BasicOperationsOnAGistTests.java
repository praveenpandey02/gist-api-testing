import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BasicOperationsOnAGistTests {
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
    @Tag("smoke")
    public void createASecretGistSuccessfully() {
        String uniqueString = UUID.randomUUID().toString();

        String body = String.format("""
                {
                    "description": "Example of a SECRET gist %s",
                    "public": false,
                    "files": {
                        "README.md": {
                          "content": "This is a SECRET gist %s"
                        }
                      }
                }
                """, uniqueString, uniqueString);
        given()
                //.header("Authorization", "token " + TestSetup.auth_token) //Note: If Request Specification was not used, the auth token and base uri would have been repeated
                //.baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
                .body("description", equalTo("Example of a SECRET gist " + uniqueString));

    }

    //@RepeatedTest(35)
    @Test
    @Tag("smoke")
    public void createAPublicGistSuccessfully() {
        createAGist();
    }

    @Test
    @Tag("regression")
    public void updateGistDescription() {
        String gist_id = createAGist();
        String changedDescription = "{\"description\": \"Changed description\"}";

        given()
//                .header("Authorization", "token " + TestSetup.auth_token)
//                .baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .body(changedDescription)
                .when()
                .patch("/gists/" + gist_id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("description", equalTo("Changed description"));

    }

    @Test
    @Tag("regression")
    public void deleteGistFile() {
        String gist_id = createAGist();
        String bodyWithDeletedFile = """
                {
                    "files": {
                        "README.md": null
                    }
                }
                """;

        given()
//                .header("Authorization", "token " + TestSetup.auth_token)
//                .baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .body(bodyWithDeletedFile)
                .when()
                .patch("/gists/" + gist_id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("files", not(hasKey("README.md")));
    }

    @Test
    @Tag("regression")
    public void renameGistFile() {
        String gist_id = createAGist();
        String bodyWithRenamedFile = """
                {
                    "files": {
                        "README.md": {"filename":"sample.txt"}
                    }
                }
                
                """;

        given()
//                .header("Authorization", "token " + TestSetup.auth_token)
//                .baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .body(bodyWithRenamedFile)
                .when()
                .patch("/gists/" + gist_id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("files", not(hasKey("README.md")))
                .body("files", hasKey(("sample.txt")));
    }

    @Test
    @Tag("smoke")
    public void deleteAGist() {
        String gist_id = createAGist();
        given()
//                .header("Authorization", "token " + TestSetup.auth_token)
//                .baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .when()
                .delete("/gists/" + gist_id)
                .then()
                .assertThat()
                .statusCode(204);

        // Here we can get the deleted gist_id and check again that the gist does not exist
        given()
//                .header("Authorization", "token " + TestSetup.auth_token)
//                .baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .when()
                .get("/gists/" + gist_id)
                .then()
                .assertThat()
                .statusCode(404);
    }

    private String createAGist() {
        JSONObject body = new JSONObject();
        JSONObject file = new JSONObject();
        JSONObject fileContent = new JSONObject();
        String uniqueString = UUID.randomUUID().toString();

        fileContent.put("content", "This is a PUBLIC gist " + uniqueString);
        file.put("README.md", fileContent);
        body.put("files", file);
        body.put("description", "Example of a PUBLIC gist " + uniqueString);
        body.put("public", true);

        return given()
//                .header("Authorization", "token " + TestSetup.auth_token)
//                .baseUri(TestSetup.BASE_URI)
                .spec(requestSpec)
                .body(body.toString()) //This conversion is important when using JSONObject else the object will not be serialized correctly and it may throw 422
                .when()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
                .body("description", equalTo("Example of a PUBLIC gist " + uniqueString))
                .body("public", equalTo(true))
                .body("files.'README.md'.content", notNullValue())
                .extract().path("id");
    }

    private int getTotalGistCount(){
        return given()
                .spec(requestSpec)
                .when()
                .get("/gists")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("$").size();
    }
}
