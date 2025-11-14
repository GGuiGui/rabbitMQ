package kr.co.shortenurlservice.dto;
import lombok.*;

/**
 * 메시지 정보를 관리합니다.
 *
 * @author : jonghoon
 * @fileName : MessageDto
 * @since : 10/15/23
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageDto {
    private String title;
    private String message;
    private Integer priority;

    @Builder
    public MessageDto(String title, String message, Integer priority) {
        this.title = title;
        this.message = message;
        this.priority = priority;
    }
}