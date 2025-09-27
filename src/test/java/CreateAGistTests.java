import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateAGistTests {
    @BeforeEach
    public void setUp(){
        TestSetup.globalSetup();
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
                .header("Authorization", "token " + TestSetup.auth_token)
                .baseUri(TestSetup.BASE_URI)
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

        given().header("Authorization", "token " + TestSetup.auth_token)
                .baseUri(TestSetup.BASE_URI)
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
