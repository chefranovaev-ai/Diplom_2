import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import models.User;
import steps.UserSteps;

public class UserRegisterApiTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();

    @Test
    @DisplayName("Создание уникального пользователя — Успех")
    @Description("Проверяет успешное создание нового пользователя в системе Stellar Burgers с валидными, уникальными данными. Ожидается статус-код 200 и флаг success: true.")
    public void testCreateUniqueUserSuccess() {
        User user = userSteps.generateUserData();
        Response response = userSteps.registerUser(user);

        userSteps.checkSuccessResponse(response);
        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован — Ошибка")
    @Description("Проверяет невозможность повторной регистрации пользователя с уже существующим email. Ожидается отказ в доступе (код 403) и сообщение 'User already exists'.")
    public void testCreateDuplicateUserFails() {
        User user = userSteps.generateUserData();
        Response firstRegister = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(firstRegister);

        Response secondRegister = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(secondRegister, "User already exists");
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email — Ошибка")
    @Description("Негативный сценарий: создание пользователя с пропущенным полем email. Ожидается код 403 и текст ошибки 'Email, password and name are required fields'.")
    public void testCreateUserMissingEmailFails() {
        User user = userSteps.generateUserData();
        user.setEmail(null);

        Response response = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля password — Ошибка")
    @Description("Негативный сценарий: создание пользователя с пропущенным полем password. Ожидается код 403 и текст ошибки 'Email, password and name are required fields'.")
    public void testCreateUserMissingPasswordFails() {
        User user = userSteps.generateUserData();
        user.setPassword(null);

        Response response = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля name — Ошибка")
    @Description("Негативный сценарий: создание пользователя с пропущенным полем name. Ожидается код 403 и текст ошибки 'Email, password and name are required fields'.")
    public void testCreateUserMissingNameFails() {
        User user = userSteps.generateUserData();
        user.setName(null);

        Response response = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(response, "Email, password and name are required fields");
    }
}
