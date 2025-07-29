package gift.kakao.service;

public interface KakaoMessageService {

    String createTextMessage(String message);

    void sendTextMessage(String templateJson);
}
