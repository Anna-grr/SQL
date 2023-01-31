package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class LoginTest {

    @AfterAll
    static void teardrop() {
        cleanDatabase();
    }

    @Test
    void happyPath() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getValidVerificationCode();
        verificationPage.validVerify(String.valueOf(verificationCode));
    }

    @Test
    void shouldGetErrorNotificationIfUnregisteredUser() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        loginPage.invalidLogin(DataHelper.getRandomUser());
        loginPage.findErrorNotification("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    void shouldGetErrorNotificationIfInvalidCode() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getRandomVerificationCode();
        verificationPage.verify(String.valueOf(verificationCode));
        verificationPage.findErrorNotificationIfInvalidCode("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    void shouldBlockUserAfterThreeTimesOfInvalidPassword() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        loginPage.invalidPassword();
        loginPage.findErrorNotification("Ошибка! Неверно указан логин или пароль");
        loginPage.invalidPassword();
        loginPage.findErrorNotification("Ошибка! Неверно указан логин или пароль");
        loginPage.invalidPassword();
        loginPage.findErrorNotification("Ошибка! Неверно указан логин или пароль");
        assertEquals("blocked", SQLHelper.getUserStatus(authInfo.getLogin()));
    }
}
