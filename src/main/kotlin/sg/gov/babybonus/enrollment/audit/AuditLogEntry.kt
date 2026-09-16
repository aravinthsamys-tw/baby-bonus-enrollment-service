package sg.gov.babybonus.enrollment.audit

data class AuditLogEntry(
    val timestamp: String,
    val operation: AuditOperation,
    val callerIdentity: String,
    val subject: String,
    val outcome: String,
)
