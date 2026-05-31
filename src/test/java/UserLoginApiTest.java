import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import models.User;
import steps.UserSteps;

public class UserLoginApiTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();
    private User user;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        user = userSteps.generateUserData();
        Response registerResponse = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(registerResponse);
    }

    @Test
    @DisplayName("Вход под существующим пользователем — Успех")
    @Description("Проверяет успешную авторизацию в системе под ранее созданными учетными данными. Ожидается возвращение токена авторизации и код 200.")
    public void testLoginExistingUserSuccess() {
        Response loginResponse = userSteps.loginUser(user.getEmail(), user.getPassword());

        userSteps.checkSuccessResponse(loginResponse);
        accessToken = userSteps.getAccessToken(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным логином — Ошибка")
    @Description("Проверяет попытку авторизации с некорректным логином. Ожидается код 401 Unauthorized и сообщение об ошибке.")
    public void testLoginWithInvalidLoginFails() {
        Response response = userSteps.loginUser("non_existent_burger_user_999@mail.com", user.getPassword());
        userSteps.checkUnauthorizedResponse(response, "email or password are incorrect");
    }
    @Test
    @DisplayName("Вход с неверным паролем — Ошибка")
    @Description("Проверяет попытку авторизации существующего пользователя с некорректным паролем. Ожидается код 401 Unauthorized и сообщение об ошибке.")
    public void testLoginWithInvalidPasswordFails() {

        Response response = userSteps.loginUser(user.getEmail(), "completely_wrong_password_123");
        userSteps.checkUnauthorizedResponse(response, "email or password are incorrect");
    }
}

