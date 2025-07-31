package gift.order.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import gift.kakao.service.MessageService;
import gift.kakao.service.OAuthService;
import gift.member.Role;
import gift.member.builder.MemberBuilder;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import gift.option.entity.Option;
import gift.option.entity.OptionName;
import gift.option.repository.OptionRepository;
import gift.order.dto.OrderCreateRequestDto;
import gift.order.dto.OrderCreateResponseDto;
import gift.order.repository.OrderRepository;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

// TODO: 전체 테스트 실행 시, 오류
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private MessageService kakaoMessageService;


    private String userToken;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/orders";
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method,
        String url,
        String token,
        Object body,
        ParameterizedTypeReference<T> type) {

        var request = client.method(method)
            .uri(url);

        if (token != null) {
            request = request.headers(headers -> headers.setBearerAuth(token));
        }

        if (body != null) {
            request = request.body(body);
        }

        return request.retrieve()
            .toEntity(type);
    }

    @BeforeAll
    void setup() {
        doNothing().when(kakaoMessageService).sendTextMessage(any());

        memberRepository.deleteAll();
        orderRepository.deleteAll();
        optionRepository.deleteAll();
        productRepository.deleteAll();

        Member member = memberRepository.save(
            MemberBuilder.aMember().withEmail("user@email.com").withPassword("1234")
                .withName("user").withRole(Role.USER).build());

        userToken = jwtTokenProvider.generateToken(member.getMemberId(), member.getEmail(),
            member.getRole());

        Product product = productRepository.save(ProductBuilder.aProduct().build());

        OptionName optionName = new OptionName("test");

        Option testOption = new Option(optionName, 10, product);

        Option option = optionRepository.save(testOption);
    }

    @Test
    void 주문생성_CREATED_성공() {
        // given
        Option option = optionRepository.findAll().get(0);
        var requestDto = new OrderCreateRequestDto(option.getOptionId(), 2, "테스트 주문");

        // when
        var response = exchange(HttpMethod.POST, baseUrl(), userToken, requestDto,
            new ParameterizedTypeReference<OrderCreateResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.optionId()).isEqualTo(option.getOptionId());
        assertThat(body.quantity()).isEqualTo(2);
        assertThat(body.message()).isEqualTo("테스트 주문");
        assertThat(body.id()).isNotNull();
    }

    @Test
    void 주문생성_BAD_REQUEST_유효성검사실패() {
        // given
        var requestDto = new OrderCreateRequestDto(null, 1, "message");

        // when & then
        assertThatThrownBy(() -> exchange(HttpMethod.POST, baseUrl(), userToken, requestDto,
            new ParameterizedTypeReference<OrderCreateResponseDto>() {
            }))
            .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    void 주문생성_UNAUTHORIZED_토큰없음() {
        var requestDto = new OrderCreateRequestDto(1L, 1, "message");

        assertThatThrownBy(() -> exchange(HttpMethod.POST, baseUrl(), null, requestDto,
            new ParameterizedTypeReference<OrderCreateResponseDto>() {
            }))
            .isInstanceOf(HttpClientErrorException.Unauthorized.class);
    }
}


