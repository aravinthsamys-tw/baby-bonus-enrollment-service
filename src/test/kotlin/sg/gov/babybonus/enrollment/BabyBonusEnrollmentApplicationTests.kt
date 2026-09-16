package sg.gov.babybonus.enrollment

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["baby-bonus.auth.api-key=test-api-key"])
class BabyBonusEnrollmentApplicationTests {

    @Test
    fun contextLoads() {
    }
}
