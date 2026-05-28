import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import models.User;
import steps.UserSteps;
import steps.OrderSteps;
import config.ApiConfig;

import java.util.ArrayList;
import java.util.List;

public class StellarBurgersApiTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Создание уникального пользователя — Успех")
    public void testCreateUniqueUserSuccess() {
        User user = userSteps.generateUserData();

        Response response = userSteps.registerUser(user);

        userSteps.checkSuccessResponse(response);
        accessToken = userSteps.getAccessToken(response);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован — Ошибка")
    public void testCreateDuplicateUserFails() {
        User user = userSteps.generateUserData();
        Response firstRegister = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(firstRegister);

        Response secondRegister = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(secondRegister);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email — Ошибка")
    public void testCreateUserMissingEmailFails() {
        User user = userSteps.generateUserData();
        user.setEmail(null);

        Response response = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля password — Ошибка")
    public void testCreateUserMissingPasswordFails() {
        User user = userSteps.generateUserData();
        user.setPassword(null);

        Response response = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля name — Ошибка")
    public void testCreateUserMissingNameFails() {
        User user = userSteps.generateUserData();
        user.setName(null);

        Response response = userSteps.registerUser(user);
        userSteps.checkForbiddenResponse(response);
    }

    @Test
    @DisplayName("Вход под существующим пользователем — Успех")
    public void testLoginExistingUserSuccess() {
        User user = userSteps.generateUserData();
        Response registerResponse = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(registerResponse);

        Response loginResponse = userSteps.loginUser(user.getEmail(), user.getPassword());

        userSteps.checkSuccessResponse(loginResponse);
        accessToken = userSteps.getAccessToken(loginResponse);
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем — Ошибка")
    public void testLoginWithInvalidCredentialsFails() {
        Response response = userSteps.loginUser("non_existent_burger_user_999@mail.com", "wrong_password");
        userSteps.checkUnauthorizedResponse(response);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией — Успех")
    public void testCreateOrderWithAuthorizationSuccess() {
        User user = userSteps.generateUserData();
        Response registerResponse = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(registerResponse);

        List<String> ingredients = orderSteps.getValidIngredientsList();

        Response orderResponse = orderSteps.createOrderWithAuth(ingredients, accessToken);
        userSteps.checkSuccessResponse(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без авторизации — Успех")
    public void testCreateOrderWithoutAuthorization() {
        List<String> ingredients = orderSteps.getValidIngredientsList();

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        userSteps.checkSuccessResponse(response);
    }

    @Test
    @DisplayName("Создание заказа со списком ингредиентов — Успех")
    public void testCreateOrderWithIngredientsSuccess() {
        User user = userSteps.generateUserData();
        Response registerResponse = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(registerResponse);

        List<String> ingredients = orderSteps.getValidIngredientsList();

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        userSteps.checkSuccessResponse(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов — Ошибка")
    public void testCreateOrderNoIngredientsFails() {
        List<String> ingredients = new ArrayList<>(); // Намеренно пустой список

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        orderSteps.checkBadRequestResponse(response);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента — Ошибка")
    public void testCreateOrderInvalidIngredientHashFails() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(ApiConfig.INVALID_INGREDIENT); // Намеренно невалидный хеш

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        orderSteps.checkInvalidHashResponse(response);
    }
}
