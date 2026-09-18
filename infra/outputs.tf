output "aws_region" {
  description = "AWS region used by the infrastructure."
  value       = var.aws_region
}

output "eks_cluster_name" {
  description = "EKS cluster name."
  value       = aws_eks_cluster.main.name
}

output "database_endpoint" {
  description = "RDS PostgreSQL endpoint."
  value       = aws_db_instance.postgres.endpoint
}

output "database_credentials_secret_arn" {
  description = "Secrets Manager ARN for the RDS-managed master credentials."
  value       = aws_db_instance.postgres.master_user_secret[0].secret_arn
  sensitive   = true
}

output "ecr_repository_url" {
  description = "ECR repository URL for the service image."
  value       = aws_ecr_repository.service.repository_url
}

output "service_workload_role_arn" {
  description = "IAM role ARN intended for the Kubernetes service account."
  value       = aws_iam_role.service_workload.arn
}
