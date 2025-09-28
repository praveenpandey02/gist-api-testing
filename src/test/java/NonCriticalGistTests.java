import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class NonCriticalGistTests {
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
    @Disabled("for now")
    public void listOfGistsPerPage(){
        given()
                .spec(requestSpec)
                .queryParam("per_page", 1)
                .when()
                .get("/gists")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }
}
