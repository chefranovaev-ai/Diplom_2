import io.restassured.RestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.After;
import org.junit.Before;
import config.ApiConfig;
import steps.UserSteps;

public class BaseApiTest {

    protected String accessToken;
    private final UserSteps userSteps = new UserSteps();


    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        accessToken = null;
    }

    @After
    public void tearDown() {

        if (accessToken != null) {
            userSteps.deleteUser(accessToken)
                    .then()
                    .statusCode(202);
        }
    }
}
