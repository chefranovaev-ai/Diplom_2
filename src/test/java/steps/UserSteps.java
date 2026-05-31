package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;
import config.ApiConfig;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class UserSteps {

    @Step("Генерация случайных данных для нового пользователя")
    public User generateUserData() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return User.builder()
                .email("user_" + uniqueId + "@yandex.ru")
                .password("pass_" + uniqueId)
                .name("Name_" + uniqueId)
                .build();
    }

    @Step("Регистрация пользователя в системе")
    public Response registerUser(User user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(ApiConfig.USER_REGISTER_PATH);
    }

    @Step("Логин пользователя в системе под email: {email}")
    public Response loginUser(String email, String password) {

        User loginCredentials = User.builder()
                .email(email)
                .password(password)
                .build();

        return given()
                .header("Content-Type", "application/json")
                .body(loginCredentials)
                .when()
                .post(ApiConfig.USER_LOGIN_PATH);
    }

    @Step("Проверка успешного ответа (код 200, success: true)")
    public void checkSuccessResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Step("Проверка ответа с ошибкой доступа (код 403, success: false)")
    public void checkForbiddenResponse(Response response, String expectedMessage) {
        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is(expectedMessage));
    }


    @Step("Извлечение accessToken из ответа")
    public String getAccessToken(Response response) {
        return response
                .then()
                .extract()
                .path("accessToken");
    }

    @Step("Проверка ответа при неверных реквизитах доступа (код 401, success: false)")
    public void checkUnauthorizedResponse(Response response, String expectedMessage) {
        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is(expectedMessage));
    }

    @Step("Удаление пользователя из системы")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(config.ApiConfig.USER_DATA_PATH);
    }

}

