variable "aws_region" {
  description = "AWS region for the assessment infrastructure."
  type        = string
  default     = "ap-southeast-1"
}

variable "project_name" {
  description = "Name prefix used for AWS resources."
  type        = string
  default     = "baby-bonus-enrollment-service"
}

variable "vpc_cidr" {
  description = "CIDR block for the service VPC."
  type        = string
  default     = "10.40.0.0/16"
}

variable "database_name" {
  description = "Initial application database name."
  type        = string
  default     = "babybonus"
}

variable "database_username" {
  description = "Database master username. The password is managed by RDS in AWS Secrets Manager."
  type        = string
  default     = "babybonus_admin"
}

variable "eks_version" {
  description = "Amazon EKS Kubernetes version."
  type        = string
  default     = "1.36"
}

variable "kubernetes_namespace" {
  description = "Kubernetes namespace expected to run the service workload."
  type        = string
  default     = "baby-bonus"
}

variable "kubernetes_service_account" {
  description = "Kubernetes service account expected to run the service workload."
  type        = string
  default     = "baby-bonus-enrollment-service"
}
