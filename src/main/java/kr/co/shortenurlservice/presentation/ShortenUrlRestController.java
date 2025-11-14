package kr.co.shortenurlservice.presentation;

import kr.co.shortenurlservice.dto.MessageDto;
import kr.co.shortenurlservice.service.ConsumerService;
import kr.co.shortenurlservice.service.ProducerService;
import kr.co.shortenurlservice.service.RabbitMqAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortenUrlRestController {

    private final ProducerService producerService;
    private final ConsumerService consumerService;
    private final RabbitMqAdminService rabbitMqAdminService; // RabbitMqAdminService 주입

    @PostMapping("/api/v1/producer/send/{routingKey}")
    public ResponseEntity<?> sendMessage(@PathVariable String routingKey, @RequestBody MessageDto messageDto,
                                         @RequestParam(required = false) Integer priority) {
        producerService.sendMessage(routingKey, messageDto, priority);
        return ResponseEntity.ok(
                Map.of(
                        "resultCode", 200,
                        "resultMsg", "Message sent to exchange with routing key: " + routingKey + " and priority: " + (priority != null ? priority : "default")
                )
        );
    }

    @GetMapping("/api/v1/consumer/receive/{routingKey}")
    public ResponseEntity<?> receiveMessage(@PathVariable String routingKey) {
        Object message = consumerService.receiveMessage(routingKey);
        return ResponseEntity.ok(
                Map.of(
                        "resultCode", 200,
                        "resultMsg", message
                )
        );
    }

    @PostMapping("/api/v1/queues/create/{systemTp}")
    public ResponseEntity<?> createDynamicQueue(@PathVariable String systemTp,
                                                @RequestParam(required = false) Integer maxPriority) {
        String result = rabbitMqAdminService.createQueueAndBinding(systemTp, maxPriority);
        return ResponseEntity.ok(
                Map.of(
                        "resultCode", 200,
                        "resultMsg", result
                )
        );
    }
}
