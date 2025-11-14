# rabbitMQ 큐 사용 관련 테스트 예제

# queue 생성 URL 예시
[POST] http://localhost:8080/api/v1/queues/create/02

# 큐에 데이터 넣기 예시[우선순위(priority) 가 높으면 더 먼저 가져와짐]
[POST] http://127.0.0.1:8080/api/v1/producer/send/job.02?priority=10
{
    "title":"Rabbitmq Title_job2",
    "message":"Rabbitmq Title_job2 Message 10"
}

# 큐에 데이터 가져오기 예시
[GET] http://127.0.0.1:8080/api/v1/consumer/receive/tcs_job_queue.02
