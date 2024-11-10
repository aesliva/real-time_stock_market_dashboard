variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-west-1"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "RDS root password"
  type        = string
  sensitive   = true
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t2.micro"
}

variable "db_instance_type" {
  description = "RDS instance type"
  type        = string
  default     = "db.t3.micro"
}

variable "project_name" {
  description = "Project name for resource tagging"
  type        = string
  default     = "stock-dashboard"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "prod"
}