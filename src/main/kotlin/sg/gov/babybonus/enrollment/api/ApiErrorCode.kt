package sg.gov.babybonus.enrollment.api

enum class ApiErrorCode {
    INVALID_REQUEST,
    CHILD_NOT_FOUND,
    CHILD_NOT_SINGAPORE_CITIZEN,
    PARENT_NOT_FOUND,
    CHILD_ALREADY_ENROLLED,
}
