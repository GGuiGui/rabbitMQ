package kr.co.shortenurlservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange; // Changed from TopicExchange for simplicity as per user's previous implicit preference for direct routing
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqAdminService {

    private final RabbitAdmin rabbitAdmin;

    @Value("${tcs_job.exchange.name:tcs_job.exchange}") // Default value for exchange name
    private String exchangeName;

    public String createQueueAndBinding(String systemTp, Integer maxPriority) {
        String queueName = "tcs_job_queue." + systemTp;
        String routingKey = "job." + systemTp;

        // 큐 생성 (durable, x-max-priority 설정)
        Map<String, Object> args = new HashMap<>();
        if (maxPriority != null && maxPriority > 0) {
            args.put("x-max-priority", maxPriority);
        } else {
            args.put("x-max-priority", 10); // 기본값
        }
        Queue queue = new Queue(queueName, true, false, false, args);
        rabbitAdmin.declareQueue(queue);

        // Exchange 선언 (이미 존재할 것이므로 idempotent)
        // Note: We are using TopicExchange as per previous discussions, but the user's proposal implies direct routing.
        // For dynamic queue creation, a TopicExchange is more flexible.
        // The binding will use the queueName as the routing key for a DirectExchange, or a pattern for TopicExchange.
        // Let's stick to TopicExchange for now, as it was the last agreed-upon type.
        TopicExchange exchange = new TopicExchange(exchangeName);
        rabbitAdmin.declareExchange(exchange);

        // 바인딩 생성
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);

        log.info("동적으로 큐 및 바인딩 생성 완료: 큐 이름='{}', 라우팅 키='{}', Exchange='{}'", queueName, routingKey, exchangeName);
        return "큐 및 바인딩 생성 완료: " + queueName;
    }
}
