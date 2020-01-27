import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Перейти на страницу http://www.sberbank.ru/ru/person
 * Нажать на – Страхование
 * Выбрать – Путешествие и покупки
 * Проверить наличие на странице заголовка – Страхование путешественников Нажать на – Оформить Онлайн
 * На вкладке – Выбор полиса выбрать сумму страховой защиты – Минимальная Нажать Оформить
 * На вкладке Оформить заполнить поля:
 * Фамилию и Имя, Дату рождения застрахованных
 * Данные страхователя: Фамилия, Имя, Отчество, Дата рождения, Пол Паспортные данные
 * Контактные данные не заполняем
 * <p>
 * Проверить, что все поля заполнены правильно Нажать продолжить
 * Проверить, что появилось сообщение - Заполнены не все обязательные поля
 */

public class SberbankAutotest {

    public static WebDriver webDriver = null;

    @BeforeClass
    public static void testUp() {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver");
        webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        webDriver.manage().window().maximize();
    }

    @AfterClass
    public static void cleanUp() {
        webDriver.close();
        webDriver.quit();
    }

    @Test
    public void sberbank() throws InterruptedException {
        webDriver.get("http://www.sberbank.ru/ru/person");
        WebDriverWait wait = new WebDriverWait(webDriver, 30);

        //Ждем, когда появится выплывающее предупреждение, закрываем его (так как оно будет мешать потом)
        WebElement element = webDriver.findElement(By.xpath("//a[@title='Закрыть предупреждение']"));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Нажимаем на выпадающий список "Страхование"
        webDriver.findElement(By.xpath("//li[contains(@class,'lg-menu')]//span[text()='Страхование']")).click();

        //Выбираем пункт "Страхование путешественников"
        webDriver.findElement(By
                .xpath("//div[@class='kit-row']//a[contains(@class,'lg-menu') and text()='Страхование путешественников']"))
                .click();

        //Проверяем заголовок (на самом деле их два - один для узких экранов, другой для широких)
        List<WebElement> elements = webDriver.findElements(By.xpath("//div[contains(@class,'kit-col_xs_12')]/h2"));
        boolean visible = elements.get(0).isDisplayed() || elements.get(1).isDisplayed();
        Assert.assertTrue(visible);

        //Нажимаем кнопку "Оформить онлайн" (везде ставим elementToBeClickable(), так как кнопки медленно грузятся)
        element = webDriver.findElement(By.xpath("//div[contains(@class,'button')]//b[text()='Оформить онлайн']"));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Выбираем сумму страховой защиты - "Минимальная"
        element = webDriver.findElement(By.xpath("//h3[text()='Минимальная']/.."));
        wait.until(ExpectedConditions.visibilityOf(element));

        //Нажимаем кнопку "Оформить"
        element = webDriver.findElement(By.xpath("//button[@type='button' and text()='Оформить']"));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Вбиваем данные застрахованных
        WebElement surname = webDriver.findElement(By.xpath("//input[@placeholder='Surname']"));
        WebElement name = webDriver.findElement(By.xpath("//input[@placeholder='Name']"));
        WebElement birthDate = webDriver.findElement(By.xpath("//input[@placeholder='дд.мм.гггг']"));

        surname.sendKeys("Surname");
        name.sendKeys("Surname");
        birthDate.sendKeys("27051994");
        birthDate.sendKeys(Keys.TAB);   //После поля ввода даты необходимо нажимать таб, иначе не перейдет к следующему полю

        //Вбиваем данные страхователя
        webDriver.findElement(By.xpath("//label[contains(@class,'btn') and text()='гражданин РФ']")).click();
        WebElement personLastName = webDriver.findElement(By.xpath("//input[@id='person_lastName']"));
        WebElement personFirstName = webDriver.findElement(By.xpath("//input[@id='person_firstName']"));
        WebElement personMiddleName = webDriver.findElement(By.xpath("//input[@id='person_middleName']"));
        WebElement personBirthDate = webDriver.findElement(By.xpath("//input[@id='person_birthDate']"));

        personLastName.sendKeys("Петров");
        personFirstName.sendKeys("Петр");
        personMiddleName.sendKeys("Петрович");
        personBirthDate.sendKeys("27051994");
        personBirthDate.sendKeys(Keys.TAB);

        //Вбиваем паспортные данные
        webDriver.findElement(By.xpath("//label[contains(@class,'btn') and text()='Мужской']")).click();
        WebElement passportSeries = webDriver.findElement(By.xpath("//input[@id='passportSeries']"));
        WebElement passportNumber = webDriver.findElement(By.xpath("//input[@id='passportNumber']"));
        WebElement documentDate = webDriver.findElement(By.xpath("//input[@id='documentDate']"));
        WebElement documentIssue = webDriver.findElement(By.xpath("//input[@id='documentIssue']"));

        passportSeries.sendKeys("4617");
        passportNumber.sendKeys("345345");
        documentDate.sendKeys("27052014");
        documentDate.sendKeys(Keys.TAB);
        documentIssue.sendKeys("йцукейцуке");

        //Нажимаем кнопку "Продолжить"
        element = webDriver.findElement(By.xpath("//button[@type='submit' and contains(text(),'Продолжить')]"));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Проверяем появились ли надписи "Поле не заполнено."
        elements = webDriver.findElements(By.xpath("//span[contains(@class,'invalid-validate') and text()='Поле не заполнено.']"));
        visible = elements.get(0).isDisplayed() && elements.get(1).isDisplayed() && elements.get(2).isDisplayed();
        Assert.assertTrue(visible);

    }
}
