package kr.co.shortenurlservice.service;

import kr.co.shortenurlservice.dto.MessageDto;

/**
 * ProducerService Interface
 *
 * @author : jonghoon
 * @fileName : ProducerService
 * @since : 10/21/23
 */
public interface ProducerService {

    // 메시지를 큐로 전송 합니다.
    void sendMessage(String routingKey, MessageDto messageDto, Integer priority);
}