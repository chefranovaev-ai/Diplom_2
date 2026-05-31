package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;
import config.ApiConfig;
import static org.hamcrest.CoreMatchers.is;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(List<String> ingredients, String accessToken) {
        Order orderBody = new Order(ingredients);

        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(orderBody)
                .log().all()
                .when()
                .post(ApiConfig.ORDERS_PATH);
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(List<String> ingredients) {
        Order orderBody = new Order(ingredients);

        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(orderBody)
                .log().all()
                .when()
                .post(ApiConfig.ORDERS_PATH);
    }

    @Step("Подготовка списка с одним валидным ингредиентом")
    public List<String> getValidIngredientsList() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(ApiConfig.VALID_INGREDIENT);
        return ingredients;
    }

    @Step("Проверка ответа при создании заказа без ингредиентов (код 400, success: false, сообщение: {expectedMessage})")
    public void checkBadRequestResponse(Response response, String expectedMessage) {
        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is(expectedMessage));
    }

    @Step("Проверка ответа при неверном хеше ингредиента (код 500)")
    public void checkInvalidHashResponse(Response response) {
        response.then()
                .statusCode(500);

    }
}

