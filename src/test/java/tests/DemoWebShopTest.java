package tests;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.Cookie;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Story("Shopping Cart")
public class DemoWebShopTest {
    String WEBSHOP_URL = "http://demowebshop.tricentis.com/";

    @Test
    @DisplayName("Verify products quantity in the shopping cart")
    void checkAddingProductToShoppingCard(){
        String expectedProductsAmount = "4";
        step("Get cookie", () -> {
            String authorizationCookie =
                    given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&product_attribute_72_3_20=58&addtocart_72.EnteredQuantity=4")
                    .when()
                    .post(WEBSHOP_URL+"addproducttocart/details/72/1")
                    .then()
                    .statusCode(200)
                    .body("success", is(true))
                    .body("updatetopcartsectionhtml", is("(" + expectedProductsAmount + ")"))
                    .extract()
                    .cookie("Nop.customer");
            step("Open webshop ulr to create session", () -> {
                open(WEBSHOP_URL);
            });
            step("Add cookie to the browser", ()-> {
                getWebDriver().manage().addCookie(
                        new Cookie("Nop.customer", authorizationCookie));
            });
        });

        step("Open online shop:" + WEBSHOP_URL, () ->
                open("http://demowebshop.tricentis.com/"));

        step("Check products quantity", () -> {
            $(".cart-qty").shouldHave(text(expectedProductsAmount));
        });


    }
}
