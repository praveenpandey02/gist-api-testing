import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

public class CreateAGistTests {
    String baseURI = "https://api.github.com";
    String auth_token;

    @BeforeEach
    public void setUp() {
        List<String> gistIDs;

        Dotenv dotenv = Dotenv.load();
        auth_token = dotenv.get("TOKEN_FOR_GIST");
        if (auth_token.isEmpty()) {
            throw new RuntimeException("Please set the auth token before running tests");
        }

        gistIDs = given()
                .header("Authorization", "token " + auth_token)
                .baseUri(baseURI)
                .when()
                .get("/gists")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("id");

        System.out.println("Gists to delete --> " + gistIDs);
        for (String gist_id : gistIDs) {
            given()
                    .header("Authorization", "token " + auth_token)
                    .baseUri(baseURI)
                    .when()
                    .delete("/gists/" + gist_id)
                    .then()
                    .statusCode(204);
        }
    }

    @Test
    @Tag("smoke")
    public void createASecretGist() {
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
                .header("Authorization", "token " + auth_token)
                .baseUri(baseURI)
                .body(body)
                .when()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
                .body("description", equalTo("Example of a SECRET gist " + uniqueString));

    }

    @Test
    @Tag("smoke")
    public void createAPublicGistSuccessfully() {
        createAPublicGist();
    }

    @Test
    public void updateAPublicGist(){
        createAPublicGist();

    }

    @Test
    public void deleteAPublicGist(){
        createAPublicGist();

    }

    public void createAPublicGist(){
        JSONObject body = new JSONObject();
        JSONObject file = new JSONObject();
        JSONObject fileContent = new JSONObject();
        String uniqueString = UUID.randomUUID().toString();

        fileContent.put("content", "This is a PUBLIC gist " + uniqueString);
        file.put("README.md", fileContent);
        body.put("files", file);
        body.put("description", "Example of a PUBLIC gist " + uniqueString);
        body.put("public", true);

        given().header("Authorization", "token " + auth_token)
                .baseUri(baseURI)
                .body(body.toString()) //This conversion is important when using JSONObject else the object will not be serialized correctly and it may throw 422
                .when()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
                .body("description", equalTo("Example of a PUBLIC gist " + uniqueString))
                .body("public", equalTo(true))
                .body("files.'README.md'.content", notNullValue());

    }
}
