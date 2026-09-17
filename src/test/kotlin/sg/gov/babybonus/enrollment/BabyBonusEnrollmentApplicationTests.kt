package sg.gov.babybonus.enrollment

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = [TestProperties.API_KEY, TestProperties.UNIQUE_H2_DATABASE])
class BabyBonusEnrollmentApplicationTests {

    @Test
    fun contextLoads() {
    }
}
