package sg.gov.babybonus.enrollment

object TestProperties {
    const val API_KEY = "baby-bonus.auth.api-key=test-api-key"
    const val CALLER_IDENTITY = "baby-bonus.auth.caller-identity=test-client"
    const val UNIQUE_H2_DATABASE =
        "spring.datasource.url=jdbc:h2:mem:\${random.uuid};DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
}
