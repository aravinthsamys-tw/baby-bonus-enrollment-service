package sg.gov.babybonus.enrollment.auth

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val apiKeyAuthenticationInterceptor: ApiKeyAuthenticationInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(apiKeyAuthenticationInterceptor)
            .addPathPatterns("/api/**")
    }
}
