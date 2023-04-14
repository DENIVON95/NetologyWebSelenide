package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;


public class DebitCardTest {

    private SelenideElement nameInput = $("[data-test-id='name'] input");
    private SelenideElement nameValidationInput = $("[data-test-id='name'].input_invalid .input__sub");
    private SelenideElement phoneInput = $("[data-test-id='phone'] input");
    private SelenideElement phoneValidationInput = $("[data-test-id='phone'].input_invalid .input__sub");
    private SelenideElement agreementCheckbox = $("[data-test-id='agreement'] .checkbox__box");
    private SelenideElement continueButton = $x("//span[text()='Продолжить']");
    private SelenideElement successMessage = $("[data-test-id='order-success']");
    private SelenideElement iconOk = successMessage.$(".icon");


    @BeforeAll
    public static void setup() {
        Configuration.baseUrl = "http://localhost:7777";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless"));
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--start-fullscreen", "--start-incognito");
        Configuration.browser = "chrome";
        Configuration.browserCapabilities = options;
        Configuration.startMaximized = true;
    }

    @BeforeEach
    public void openMainPage() {
        Selenide.open("/");
    }

    @Test
    public void shouldSuccessfullyCompleteApplication() {
        nameInput
                .shouldBe(visible)
                .sendKeys("Тест Тестович");
        phoneInput.sendKeys("+79777777777");
        agreementCheckbox.click();
        continueButton.click();
        successMessage
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive("  Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время."));
        iconOk.shouldBe(visible);
    }

    @Test
    public void shouldValidateAgreementCheckbox() {
        nameInput
                .shouldBe(visible)
                .sendKeys("Тест Тестович");
        phoneInput.sendKeys("+79777777777");
        continueButton.click();
        agreementCheckbox
                .parent()
                .shouldHave(attribute("class", "checkbox checkbox_size_m checkbox_theme_alfa-on-white input_invalid"));
    }

    @ParameterizedTest
    @CsvSource(value = {"79777777777;Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
            " ;Поле обязательно для заполнения"}, delimiter = ';')
    public void shouldValidatePhone(String phone, String validationMessage) {
        nameInput.shouldBe(visible).sendKeys("Тест Тестович");
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        continueButton.click();
        phoneValidationInput
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive(validationMessage));
    }

    @ParameterizedTest
    @CsvSource(value = {"Test Testovich;Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
            " ;Поле обязательно для заполнения"}, delimiter = ';')
    public void shouldValidateName(String name, String validationMessage) {
        nameInput.shouldBe(visible).setValue(name);
        phoneInput.sendKeys("+79777777777");
        agreementCheckbox.click();
        continueButton.click();
        nameValidationInput
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive(validationMessage));
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }

}
