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
        super.setUp(); // Вызываем базовый URL и фильтры из BaseApiTest

        // Переносим создание пользователя сюда, так как он нужен для тестов логина
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
        // Обновляем токен, если сервер сгенерировал новый при логине
        accessToken = userSteps.getAccessToken(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем — Ошибка")
    @Description("Проверяет попытку авторизации с некорректным логином или паролем. Ожидается код 401 Unauthorized и сообщение об ошибке.")
    public void testLoginWithInvalidCredentialsFails() {
        Response response = userSteps.loginUser("non_existent_burger_user_999@mail.com", "wrong_password");
        userSteps.checkUnauthorizedResponse(response, "email or password are incorrect");
    }
}

