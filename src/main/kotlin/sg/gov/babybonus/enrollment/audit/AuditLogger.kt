package sg.gov.babybonus.enrollment.audit

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sg.gov.babybonus.enrollment.auth.ApiKeyAuthenticationInterceptor
import sg.gov.babybonus.enrollment.sensitive.NricMasker
import tools.jackson.databind.ObjectMapper
import java.time.Instant

@Component
class AuditLogger(
    private val objectMapper: ObjectMapper,
) {

    private val logger = LoggerFactory.getLogger(AuditLogger::class.java)

    fun log(operation: AuditOperation, childNric: String, outcome: String) {
        val entry = AuditLogEntry(
            timestamp = Instant.now().toString(),
            operation = operation,
            callerIdentity = MDC.get(ApiKeyAuthenticationInterceptor.CALLER_ID_MDC_KEY) ?: UNKNOWN_CALLER,
            subject = NricMasker.mask(childNric),
            outcome = outcome,
        )

        logger.info(objectMapper.writeValueAsString(entry))
    }

    private companion object {
        const val UNKNOWN_CALLER = "unknown"
    }
}
