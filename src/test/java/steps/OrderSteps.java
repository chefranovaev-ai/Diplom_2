package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;
import config.ApiConfig;

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

    @Step("Проверка ответа при создании заказа без ингредиентов (код 400, success: false)")
    public void checkBadRequestResponse(Response response) {
        response.then()
                .statusCode(400)
                .body("success", org.hamcrest.Matchers.is(false));
    }

    @Step("Проверка ответа при неверном хеше ингредиента (код 400 или 500)")
    public void checkInvalidHashResponse(Response response) {
        response.then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(500),
                        org.hamcrest.Matchers.is(400)
                ));
    }
}

