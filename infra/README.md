# Infrastructure

Terraform in this directory sketches the AWS production target for the assessment service.

It defines:

- VPC with public and private subnets across two Availability Zones
- Internet egress through a NAT gateway for private workloads
- EKS cluster with a managed node group in private subnets
- RDS PostgreSQL in private subnets with encrypted storage
- AWS-managed RDS master credentials in Secrets Manager
- ECR repository for the service image
- IAM Roles for Service Accounts (IRSA) role scoped to read the database credentials secret

The configuration is intentionally concise for the assessment. A production implementation would usually add remote state, environment-specific variable files, private EKS endpoint restrictions, AWS Load Balancer Controller, Kubernetes manifests or Helm charts, logging/metrics, backup policy, alerting, and a stronger deletion/recovery posture.

Validate locally with:

```bash
terraform init
terraform validate
```
