package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;


public class DebitCardTest {

    private SelenideElement nameInput = $("[data-test-id='name'] input");
    private SelenideElement nameValidationInput = $("[data-test-id='name'] .input__sub");
    private SelenideElement phoneInput = $("[data-test-id='phone'] input");
    private SelenideElement phoneValidationInput = $("[data-test-id='phone'] .input__sub");
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

    @Test
    public void shouldValidatePhone() {
        nameInput.shouldBe(visible).sendKeys("Тест Тестович");
        phoneInput.sendKeys("79777777777");
        continueButton.click();
        phoneValidationInput
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    public void shouldValidateName() {
        nameInput.shouldBe(visible).sendKeys("Test Testovich");
        continueButton.click();
        nameValidationInput
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }

}
