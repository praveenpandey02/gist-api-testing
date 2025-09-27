import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TestSetup {
    public static final String BASE_URI = "https://api.github.com";
    public static String auth_token;

    public static void globalSetup() {
        setToken();
        cleanupAllGists();
    }

    static void setToken() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String ci_token = System.getenv("TOKEN_FOR_GIST");
        if (ci_token == null) {
            auth_token = dotenv.get("TOKEN_FOR_GIST");
            if (auth_token.isEmpty()) {
                throw new RuntimeException("Please set the auth token before running tests");
            }
        } else auth_token = ci_token;

    }

    static void cleanupAllGists() {
        List<String> gistIDs;
        gistIDs = given()
                .header("Authorization", "token " + auth_token)
                .baseUri(BASE_URI)
                .when()
                .get("/gists")
                .then()
                .extract().jsonPath().getList("id");

        System.out.println("Gists to delete --> " + gistIDs);
        for (String gist_id : gistIDs) {
            given()
                    .header("Authorization", "token " + auth_token)
                    .baseUri(BASE_URI)
                    .when()
                    .delete("/gists/" + gist_id);
        }
    }
}