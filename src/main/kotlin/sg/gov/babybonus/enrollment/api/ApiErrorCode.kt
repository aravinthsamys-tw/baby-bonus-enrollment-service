package sg.gov.babybonus.enrollment.api

enum class ApiErrorCode {
    UNAUTHORIZED,
    INVALID_REQUEST,
    ENROLLMENT_NOT_FOUND,
    CHILD_NOT_FOUND,
    CHILD_NOT_SINGAPORE_CITIZEN,
    PARENT_NOT_FOUND,
    CHILD_ALREADY_ENROLLED,
}
