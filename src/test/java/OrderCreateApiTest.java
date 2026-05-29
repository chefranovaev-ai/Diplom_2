import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import models.User;
import steps.UserSteps;
import steps.OrderSteps;
import config.ApiConfig;
import java.util.ArrayList;
import java.util.List;

public class OrderCreateApiTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

    @Before
    @Override
    public void setUp() {
        super.setUp();

        // Регистрируем пользователя заранее для тестов заказа с авторизацией
        User user = userSteps.generateUserData();
        Response registerResponse = userSteps.registerUser(user);
        accessToken = userSteps.getAccessToken(registerResponse);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией — Успех")
    @Description("Проверяет создание заказа авторизованным пользователем. В заголовок Authorization передается валидный токен. Ожидается код 200.")
    public void testCreateOrderWithAuthorizationSuccess() {
        List<String> ingredients = orderSteps.getValidIngredientsList();

        Response orderResponse = orderSteps.createOrderWithAuth(ingredients, accessToken);
        userSteps.checkSuccessResponse(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без авторизации — Успех")
    @Description("Проверяет создание заказа неавторизованным пользователем. Заголовок Authorization не передается. Разрешается создание такого заказа (код 200).")
    public void testCreateOrderWithoutAuthorization() {
        List<String> ingredients = orderSteps.getValidIngredientsList();

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        userSteps.checkSuccessResponse(response);
    }

    @Test
    @DisplayName("Создание заказа со списком ингредиентов — Успех")
    @Description("Проверяет передачу корректного массива хешей ингредиентов при создании заказа. Ожидается статус-код ответа 200.")
    public void testCreateOrderWithIngredientsSuccess() {
        List<String> ingredients = orderSteps.getValidIngredientsList();

        // Тест проверяет именно работу с ингредиентами под учетной записью
        Response response = orderSteps.createOrderWithAuth(ingredients, accessToken);
        userSteps.checkSuccessResponse(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов — Ошибка")
    @Description("Негативный сценарий: передача пустого списка ингредиентов. Ожидается ошибка 400 Bad Request и сообщение 'Ingredient ids must be provided'.")
    public void testCreateOrderNoIngredientsFails() {
        List<String> ingredients = new ArrayList<>();

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        orderSteps.checkBadRequestResponse(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента — Ошибка")
    @Description("Негативный сценарий: передача несуществующего (невалидного) хеша ингредиента. Ожидается сбой бэкенда с внутренним статус-кодом 500.")
    public void testCreateOrderInvalidIngredientHashFails() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(ApiConfig.INVALID_INGREDIENT);

        Response response = orderSteps.createOrderWithoutAuth(ingredients);
        orderSteps.checkInvalidHashResponse(response);
    }
}
