package kr.co.shortenurlservice.service.impl;

import kr.co.shortenurlservice.dto.MessageDto;
import kr.co.shortenurlservice.service.ProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * ProducerService 구현체
 *
 * @author : jonghoon
 * @fileName : ProducerServiceImpl
 * @since : 10/15/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private static final String TOPIC_EXCHANGE_NAME = "tcs_job.exchange";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendMessage(String routingKey, MessageDto messageDto, Integer priority) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String objectToJSON = objectMapper.writeValueAsString(messageDto);

            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, routingKey, objectToJSON, message -> {
                if (priority != null) {
                    message.getMessageProperties().setPriority(priority);
                }
                return message;
            });
        } catch (JsonProcessingException jpe) {
            log.error("Failed to serialize message to JSON", jpe);
        }
    }
}