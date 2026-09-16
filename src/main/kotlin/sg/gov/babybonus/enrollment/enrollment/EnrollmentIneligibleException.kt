package sg.gov.babybonus.enrollment.enrollment

import sg.gov.babybonus.enrollment.eligibility.IneligibilityReason

class EnrollmentIneligibleException(
    val reason: IneligibilityReason,
) : RuntimeException("Enrollment is not eligible")
