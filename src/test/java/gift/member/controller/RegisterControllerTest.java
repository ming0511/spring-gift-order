package gift.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gift.member.builder.MemberBuilder;
import gift.member.dto.TokenResponseDto;
import gift.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private MemberRepository memberRepository;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/members";
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method,
        String url,
        Object body,
        ParameterizedTypeReference<T> type) {

        if (body == null) {
            return client.method(method)
                .uri(url)
                .retrieve()
                .toEntity(type);
        }

        return client.method(method)
            .uri(url)
            .body(body)
            .retrieve()
            .toEntity(type);
    }

    @BeforeEach
    void setUp() {

        memberRepository.deleteAll();

        memberRepository.save(
            MemberBuilder.aMember().withEmail("one@email.com").withPassword("1234").build());
        memberRepository.save(
            MemberBuilder.aMember().withEmail("two@email.com").withPassword("1234").build());
        memberRepository.save(
            MemberBuilder.aMember().withEmail("three@email.com").withPassword("1234").build());

        memberRepository.findAll();
    }

    @Test
    void 회원가입_CREATED_테스트() {
        // given
        var request = MemberBuilder.aMember().build();

        // when
        var response = exchange(HttpMethod.POST, baseUrl() + "/register", request,
            new ParameterizedTypeReference<TokenResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void 회원가입_BAD_REQUEST_이미존재하는이메일() {
        // given
        var request = MemberBuilder.aMember()
            .withEmail("one@email.com")
            .build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl() + "/register", request,
                    new ParameterizedTypeReference<TokenResponseDto>() {
                    })
            );
    }

}