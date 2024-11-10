output "ec2_public_ip" {
  description = "Public IP of EC2 instance"
  value       = aws_instance.stock_dashboard.public_ip
}

output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = aws_db_instance.stock_market_db.endpoint
}

output "api_gateway_url" {
  description = "API Gateway invocation URL"
  value       = "${aws_api_gateway_rest_api.stock_market.execution_arn}/prod"
}