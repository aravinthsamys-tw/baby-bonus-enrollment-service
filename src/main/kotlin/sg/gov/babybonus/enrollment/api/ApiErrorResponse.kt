package sg.gov.babybonus.enrollment.api

data class ApiErrorResponse(
    val code: ApiErrorCode,
    val message: String,
)
