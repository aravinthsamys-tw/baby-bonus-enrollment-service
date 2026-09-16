package sg.gov.babybonus.enrollment.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {

    init {
        require(configuredApiKey.isNotBlank()) {
            "baby-bonus.auth.api-key must be configured"
        }
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (request.getHeader(API_KEY_HEADER) == configuredApiKey) {
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

    companion object {
        const val API_KEY_HEADER = "X-API-Key"
    }
}
