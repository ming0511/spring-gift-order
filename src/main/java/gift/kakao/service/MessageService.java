package gift.kakao.service;

public interface MessageService {

    String createTextMessage(String message);

    void sendTextMessage(String templateJson);
}
