package kr.co.shortenurlservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.shortenurlservice.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final RabbitTemplate rabbitTemplate;
    private final ProducerService producerService;
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 ObjectMapper 주입

    public Object receiveMessage(String routingKey) {
        // 라우팅 키에서 SYSTEM_TP 부분을 추출하여 큐 이름 동적 생성
        String systemTp = routingKey.substring(routingKey.lastIndexOf('.') + 1);
        String queueName = "tcs_job_queue." + systemTp;

        Object receivedObject = rabbitTemplate.receiveAndConvert(queueName);

        if (receivedObject == null) {
            log.info("{} 큐가 비어있습니다.", queueName);
            return queueName + " 큐가 비어있습니다.";
        }

        try {
            // 수신한 JSON 문자열을 MessageDto 객체로 파싱
            String messageJson = (String) receivedObject;
            MessageDto messageDto = objectMapper.readValue(messageJson, MessageDto.class);

            try {
                // ----- 실제 업무 처리 시뮬레이션 -----
                log.info("{}에서 메시지를 수신했습니다: {}", queueName, messageDto.toString());

                if (messageDto.getMessage() != null && messageDto.getMessage().contains("fail")) {
                    throw new RuntimeException("메시지 처리 중 의도적인 실패 발생!");
                }

                log.info("메시지 처리 성공: {}", messageDto.toString());
                return messageDto;
                // ------------------------------------

            } catch (Exception e) {
                // 업무 처리 실패 시
                log.error("메시지 처리 실패, 큐의 맨 뒤로 다시 보냅니다. 원인: {}", e.getMessage());
                producerService.sendMessage(routingKey, messageDto, messageDto.getPriority()); // 동일한 라우팅키와 우선순위로 다시 발행
                return "작업 처리 실패: " + messageDto.toString() + " (큐의 맨 뒤로 다시 입력됨)";
            }
        } catch (JsonProcessingException e) {
            // JSON 파싱 자체를 실패한 경우
            log.error("수신한 메시지를 파싱하는 데 실패했습니다: {}", receivedObject, e);
            // 여기에 실패한 메시지를 별도의 "dead-letter" 큐로 보내는 등의 추가 로직을 구현할 수 있습니다.
            return "메시지 파싱 실패.";
        }
    }
}