import api.API;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.Before;

public class BaseTest {
    public static Faker faker = new Faker();
    @Before
    @Description("Прописан корневой URI запросов")
    public void setUp() {
        RestAssured.baseURI = API.BASE_URI;
    }
}
