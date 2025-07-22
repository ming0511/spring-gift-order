package gift.wish.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gift.member.Role;
import gift.member.builder.MemberBuilder;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wish.dto.WishCreateRequestDto;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

// TODO: 전체 TEST할 때만, 실패
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class WishControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    String userToken;
    String adminToken;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/wishes";
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

    Stream<String> tokenProvider() {
        return Stream.of(userToken, adminToken);
    }

    @BeforeAll
    void beforeAll() {
        memberRepository.deleteAll();

        Member member1 = memberRepository.save(
            MemberBuilder.aMember().withEmail("user@email.com").withPassword("1234")
                .withName("user").withRole(Role.USER).build());

        Member member2 = memberRepository.save(
            MemberBuilder.aMember().withEmail("admin@email.com").withPassword("1234")
                .withName("admin").withRole(Role.ADMIN).build());

        userToken = jwtTokenProvider.generateToken(member1.getMemberId(), "user@email.com",
            Role.USER);
        adminToken = jwtTokenProvider.generateToken(member2.getMemberId(), "admin@email.com",
            Role.ADMIN);

        // product
        productRepository.deleteAll();

        Product product1 = productRepository.save(
            ProductBuilder.aProduct().withName("one").withPrice(1.0).withImageUrl("https://1.img")
                .withMdConfirmed(false).build());

        Product product2 = productRepository.save(
            ProductBuilder.aProduct().withName("two").withPrice(2.0).withImageUrl("https://2.img")
                .withMdConfirmed(false).build());

        Product product3 = productRepository.save(
            ProductBuilder.aProduct().withName("three").withPrice(3.0).withImageUrl("https://3.img")
                .withMdConfirmed(false).build());

        wishRepository.deleteAll();

        wishRepository.save(new Wish(member1, product1));
        wishRepository.save(new Wish(member1, product2));
        wishRepository.save(new Wish(member2, product1));
    }

    // POST
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 위시상품추가_CREATED_성공(String token) {
        // given

        Product product = productRepository.save(ProductBuilder.aProduct().build());

        var request = new WishCreateRequestDto(product.getProductId());

        // when
        var response = exchange(HttpMethod.POST, baseUrl(), token, request,
            new ParameterizedTypeReference<WishCreateResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actual = response.getBody();
        System.out.println(actual);
    }

    //
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 위시상품추가_BAD_REQUEST_유효성검사실패(String token) {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), token, null,
                    new ParameterizedTypeReference<WishCreateResponseDto>() {
                    })
            );
    }

    @Test
    void 위시상품추가_UNAUTHORIZED_토큰없음() {
        // given
        var request = new WishCreateRequestDto(3L);

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), null, request,
                    new ParameterizedTypeReference<WishCreateResponseDto>() {
                    })
            );
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
        // TODO: 이후 수량 변경에 활용할 수 있어서 따로 예외 처리 안함.
    void 위시상품추가_500_테스트(String token) {
        Product product = productRepository.save(ProductBuilder.aProduct().build());
        var response = exchange(HttpMethod.POST, baseUrl(), token,
            new WishCreateRequestDto(product.getProductId()),
            new ParameterizedTypeReference<WishCreateResponseDto>() {
            });

        // given
        var request = new WishCreateRequestDto(product.getProductId());

        // when & then
        assertThatExceptionOfType(HttpServerErrorException.InternalServerError.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), token, request,
                    new ParameterizedTypeReference<WishCreateResponseDto>() {
                    })
            );
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 위시상품조회_OK_성공(String token) {
        // given & when

        var response = exchange(HttpMethod.GET, baseUrl() + "?page=0&size=10&sort=createdAt,desc",
            token, null, new ParameterizedTypeReference<WishPageResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "?page=0",
        "?size=10",
        "?sort=createdAt,desc",
        "?sort=createdAt",
        "?sort=createdAt,asc",
        "?page=10&size=5&sort=createdAt,asc",
        "?sort=wishId,desc",
        "?sort=wishId",
        "?sort=wishId,asc",
        "?sort=createdAt,asc&sort=wishId,asc",
        "?page=-1",
        "?size=0"
    })
    void 위시상품조회_OK_유효성검사성공(String validUrl) {
        // given & when
        var response = exchange(HttpMethod.GET, baseUrl() + validUrl,
            userToken, null, new ParameterizedTypeReference<WishPageResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @Test
    void 위시상품조회_BAD_REQUEST_유효성검사실패() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.GET, baseUrl() + "?sort=productId",
                    userToken, null, new ParameterizedTypeReference<WishPageResponseDto>() {
                    })
            );
    }

    @Test
    void 위시상품조회_UNAUTHORIZED_토큰없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.GET, baseUrl() + "?page=0&size=10&createdAt,desc", null,
                    null,
                    new ParameterizedTypeReference<WishPageResponseDto>() {
                    })
            );
    }

    // DELETE
    @Test
    void 위시상품삭제_NO_CONTENT_성공() {
        // given & when
        var response = exchange(HttpMethod.DELETE, baseUrl() + "/1", userToken, null,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 위시상품삭제_NOT_FOUND_존재하지않은위시상품() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/321", userToken,
                    null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    @Test
    void 위시상품삭제_UNAUTHORIZED_토큰없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/1", null,
                    null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    @Test
    void 위시상품삭제_FORBIDDEN_삭제권한없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Forbidden.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/3", userToken,
                    null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }
}