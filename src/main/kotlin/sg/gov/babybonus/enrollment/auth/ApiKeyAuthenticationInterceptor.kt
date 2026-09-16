package sg.gov.babybonus.enrollment.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import sg.gov.babybonus.enrollment.api.ApiErrorCode
import sg.gov.babybonus.enrollment.api.ApiErrorResponse
import tools.jackson.databind.ObjectMapper

@Component
class ApiKeyAuthenticationInterceptor(
    @Value("\${baby-bonus.auth.api-key}") private val configuredApiKey: String,
    @Value("\${baby-bonus.auth.caller-identity}") private val callerIdentity: String,
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {

    init {
        require(configuredApiKey.isNotBlank()) {
            "baby-bonus.auth.api-key must be configured"
        }
        require(callerIdentity.isNotBlank()) {
            "baby-bonus.auth.caller-identity must be configured"
        }
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (request.getHeader(API_KEY_HEADER) == configuredApiKey) {
            MDC.put(CALLER_ID_MDC_KEY, callerIdentity)
            return true
        }

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(
            response.writer,
            ApiErrorResponse(
                code = ApiErrorCode.UNAUTHORIZED,
                message = "Authentication is required.",
            ),
        )
        return false
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        MDC.remove(CALLER_ID_MDC_KEY)
    }

    companion object {
        const val API_KEY_HEADER = "X-API-Key"
        const val CALLER_ID_MDC_KEY = "callerIdentity"
    }
}
