# Provider Configuration
provider "aws" {
  region = var.aws_region
}

# Use existing default VPC
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# EC2 Instance
data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023*-x86_64"]
  }
}

resource "aws_instance" "stock_dashboard" {
  ami           = data.aws_ami.amazon_linux_2023.id
  instance_type = var.instance_type

  vpc_security_group_ids = [aws_security_group.ec2_sg.id]

  tags = {
    Name = "Stock-Dashboard-EC2"
  }
}

# Security Group for EC2
resource "aws_security_group" "ec2_sg" {
  name        = "stock-dashboard-ec2-sg"
  description = "Security group for Stock Dashboard EC2"
  vpc_id      = data.aws_vpc.default.id

  # Existing security group rules from your configuration
  ingress {
    from_port        = 8080
    to_port          = 8080
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "stock-dashboard-ec2-sg"
  }
}

# RDS Instance
resource "aws_db_instance" "stock_market_db" {
  identifier        = "stock-market-db"
  engine            = "mysql"
  engine_version    = "8.0.35"
  instance_class    = var.db_instance_type
  allocated_storage = 20
  storage_type      = "gp2"

  db_name  = "stock_market_dashboard"
  username = var.db_username
  password = var.db_password

  publicly_accessible    = true
  skip_final_snapshot    = true
  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  tags = {
    Name = "${var.project_name}-rds"
  }

  copy_tags_to_snapshot = true
  storage_encrypted     = true
  max_allocated_storage = 1000

  db_subnet_group_name = "default-vpc-08d4a19dfca416b49"

  # Parameter and option groups
  parameter_group_name = "default.mysql8.0"
  option_group_name    = "default:mysql-8-0"

  # Backup configuration
  backup_retention_period = 1
  backup_window          = "06:55-07:25"
  maintenance_window     = "mon:12:58-mon:13:28"
}

# Security Group for RDS
resource "aws_security_group" "rds_sg" {
  name        = "stock-market-rds-sg"
  description = "Security group for Stock Market RDS instance"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2_sg.id]
    cidr_blocks     = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-rds-sg"
  }
}

# API Gateway
resource "aws_api_gateway_rest_api" "stock_market" {
  name = "stock-market-api"

  endpoint_configuration {
    types = ["REGIONAL"]
  }
}

resource "aws_api_gateway_resource" "cors" {
  rest_api_id = aws_api_gateway_rest_api.stock_market.id
  parent_id   = aws_api_gateway_rest_api.stock_market.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "cors" {
  rest_api_id   = aws_api_gateway_rest_api.stock_market.id
  resource_id   = aws_api_gateway_resource.cors.id
  http_method   = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "cors" {
  rest_api_id = aws_api_gateway_rest_api.stock_market.id
  resource_id = aws_api_gateway_resource.cors.id
  http_method = aws_api_gateway_method.cors.http_method
  type        = "MOCK"
  request_templates = {
    "application/json" = jsonencode({
      statusCode = 200
    })
  }
}

resource "aws_api_gateway_method_response" "cors" {
  rest_api_id = aws_api_gateway_rest_api.stock_market.id
  resource_id = aws_api_gateway_resource.cors.id
  http_method = aws_api_gateway_method.cors.http_method
  status_code = "200"

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }
}

resource "aws_api_gateway_integration_response" "cors" {
  rest_api_id = aws_api_gateway_rest_api.stock_market.id
  resource_id = aws_api_gateway_resource.cors.id
  http_method = aws_api_gateway_method.cors.http_method
  status_code = aws_api_gateway_method_response.cors.status_code

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,OPTIONS,POST,PUT'"
    "method.response.header.Access-Control-Allow-Origin"  = "'https://aesliva.github.io'"
  }
}