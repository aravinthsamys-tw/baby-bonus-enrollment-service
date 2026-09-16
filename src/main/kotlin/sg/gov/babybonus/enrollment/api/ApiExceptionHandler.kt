package sg.gov.babybonus.enrollment.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sg.gov.babybonus.enrollment.eligibility.IneligibilityReason
import sg.gov.babybonus.enrollment.enrollment.EnrollmentIneligibleException

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiErrorResponse(
                    code = ApiErrorCode.INVALID_REQUEST,
                    message = "Request contains invalid enrollment details.",
                ),
            )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableRequest(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiErrorResponse(
                    code = ApiErrorCode.INVALID_REQUEST,
                    message = "Request body is invalid.",
                ),
            )

    @ExceptionHandler(EnrollmentIneligibleException::class)
    fun handleEnrollmentIneligible(error: EnrollmentIneligibleException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(httpStatusFor(error.reason))
            .body(apiErrorFor(error.reason))

    private fun httpStatusFor(reason: IneligibilityReason): HttpStatus =
        when (reason) {
            IneligibilityReason.CHILD_ALREADY_ENROLLED -> HttpStatus.CONFLICT
            else -> HttpStatus.UNPROCESSABLE_CONTENT
        }

    private fun apiErrorFor(reason: IneligibilityReason): ApiErrorResponse =
        when (reason) {
            IneligibilityReason.CHILD_NOT_FOUND ->
                ApiErrorResponse(ApiErrorCode.CHILD_NOT_FOUND, "Child is not eligible for enrollment.")

            IneligibilityReason.CHILD_NOT_SINGAPORE_CITIZEN ->
                ApiErrorResponse(ApiErrorCode.CHILD_NOT_SINGAPORE_CITIZEN, "Child is not eligible for enrollment.")

            IneligibilityReason.PARENT_NOT_FOUND ->
                ApiErrorResponse(ApiErrorCode.PARENT_NOT_FOUND, "Parent or guardian is not eligible for enrollment.")

            IneligibilityReason.CHILD_ALREADY_ENROLLED ->
                ApiErrorResponse(ApiErrorCode.CHILD_ALREADY_ENROLLED, "Child has already been enrolled.")
        }
}
