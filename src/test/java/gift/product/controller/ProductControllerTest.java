package gift.product.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gift.member.Role;
import gift.member.builder.MemberBuilder;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import gift.option.dto.OptionCreateRequestDto;
import gift.product.builder.ProductBuilder;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductPageResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    String userToken;
    String adminToken;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/products";
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

        memberRepository.save(
            MemberBuilder.aMember().withEmail("user@email.com").withPassword("1234")
                .withName("user").withRole(Role.USER).build());

        memberRepository.save(
            MemberBuilder.aMember().withEmail("admin@email.com").withPassword("1234")
                .withName("admin").withRole(Role.ADMIN).build());

        memberRepository.findAll();

        userToken = jwtTokenProvider.generateToken(1L, "user@email.com", Role.USER);
        adminToken = jwtTokenProvider.generateToken(2L, "admin@email.com", Role.ADMIN);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        productRepository.save(
            ProductBuilder.aProduct().withName("one").withPrice(1.0).withImageUrl("https://1.img")
                .withMdConfirmed(false).build());

        productRepository.save(
            ProductBuilder.aProduct().withName("two").withPrice(2.0).withImageUrl("https://2.img")
                .withMdConfirmed(false).build());

        productRepository.save(
            ProductBuilder.aProduct().withName("three").withPrice(3.0).withImageUrl("https://3.img")
                .withMdConfirmed(false).build());

        productRepository.findAll();
    }

    // POST
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 단건상품등록_CREATED_테스트(String token) {
        // given
        Set<OptionCreateRequestDto> options = new HashSet<>(Set.of(
            new OptionCreateRequestDto("test", 1), new OptionCreateRequestDto("test2", 2)));

        var request = new ProductCreateRequestDto("default", 1234.0, "email.com", false, options);

        // when
        var response = exchange(HttpMethod.POST, baseUrl(), token, request,
            new ParameterizedTypeReference<ProductCreateResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        " ",
        "123451234512345",            // 숫자 15자
        "Abcdefghijklmno",            // 영어 15자
        "일이삼사오일이삼사오일이삼사오",  // 한글 15자
        "()[]+-&/_",                  // 허용되는 특수문자
        "카카오"                       // 협의된 '카카오' 포함
    })
    void 단건상품등록_CREATED_상품이름_유효성_검사(String validName) {
        // given
        Set<OptionCreateRequestDto> options = new HashSet<>(Set.of(
            new OptionCreateRequestDto("test", 1), new OptionCreateRequestDto("test2", 2)));

        var request = new ProductCreateRequestDto(validName, 1234.0, "email.com", true, options);

        // when
        var response = exchange(HttpMethod.POST, baseUrl(), userToken, request,
            new ParameterizedTypeReference<ProductCreateResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12345 12345 12345",             // 숫자 17자
        "Abcde fghij klmno",             // 영어 17자
        "일이삼사오 일이삼사오 일이삼사오",   // 한글 17자
        "콜라@맛!",                       // 허용되지 않은 특수문자
        "카카오커피"                       // 협의되지 않은 '카카오' 포함
    })
    void 단건상품등록_BAD_REQUEST_상품이름_유효성_검사(String invalidName) {
        //given
        var request = ProductBuilder.aProduct()
            .withName(invalidName)
            .build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), userToken, request,
                    new ParameterizedTypeReference<ProductCreateResponseDto>() {
                    })
            );
    }

    @Test
    void 단건상품등록_UNAUTHORIZED_인증없음() {
        //given
        var request = ProductBuilder.aProduct().build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST, baseUrl(), null, request,
                    new ParameterizedTypeReference<ProductCreateResponseDto>() {
                    })
            );
    }

    // GET
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 전체상품조회_OK_테스트(String token) {
        // given & when
        var response = exchange(HttpMethod.GET, baseUrl(), token, null,
            new ParameterizedTypeReference<ProductPageResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @Test
    void 단건상품조회_OK_테스트() {
        // given
        Product savedProduct = productRepository.save(
            ProductBuilder.aProduct()
                .withName("one")
                .withPrice(1.0)
                .withImageUrl("https://1.img")
                .withMdConfirmed(false)
                .build());

        Long savedProductId = savedProduct.getProductId();

        // when
        var response = exchange(HttpMethod.GET, baseUrl() + "/" + savedProductId, userToken, null,
            new ParameterizedTypeReference<ProductGetResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @Test
    void 단건상품조회_NOT_FOUND_데이터베이스_상품존재() {
        // given & when
        assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
            .isThrownBy(
                () -> exchange(HttpMethod.GET, baseUrl() + "/321", userToken, null,
                    new ParameterizedTypeReference<ProductGetResponseDto>() {
                    })
            );
    }

    @Test
    void 단건상품조회_UNAUTHORIZED_인증없음() {
        // given & when
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.GET, baseUrl() + "/1", null, null,
                    new ParameterizedTypeReference<ProductGetResponseDto>() {
                    })
            );
    }

    // PUT
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 단건상품수정_NO_CONTENT_테스트(String token) {
        // given
        Product savedProduct = productRepository.save(ProductBuilder.aProduct().build());
        Long productId = savedProduct.getProductId();

        var request = ProductBuilder.aProduct().build();

        // when
        var response = exchange(HttpMethod.PUT, baseUrl() + "/" + productId, token, request,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Product product = productRepository.findById(productId).get();
        assertThat(product.getName()).isEqualTo(request.getName());
        assertThat(product.getPrice()).isEqualTo(request.getPrice());
        assertThat(product.getImageUrl()).isEqualTo(request.getImageUrl());
        assertThat(product.getMdConfirmed()).isEqualTo(request.getMdConfirmed());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        " ",
        "123451234512345",            // 숫자 15자
        "Abcdefghijklmno",            // 영어 15자
        "일이삼사오일이삼사오일이삼사오",  // 한글 15자
        "()[]+-&/_",                  // 허용되는 특수문자
        "카카오"                       // 협의된 '카카오' 포함
    })
    void 단건상품수정_NO_CONTENT_상품이름_유효성_검사(String validName) {
        // given

        Product savedProduct = productRepository.save(ProductBuilder.aProduct().build());
        Long productId = savedProduct.getProductId();

        Boolean mdConfirmed = false;

        if (validName.equals("카카오")) {
            mdConfirmed = true;
        }

        var request = ProductBuilder.aProduct()
            .withName(validName)
            .withMdConfirmed(mdConfirmed)
            .build();

        // when
        var response = exchange(HttpMethod.PUT, baseUrl() + "/" + productId, userToken, request,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12345 12345 12345",             // 숫자 17자
        "Abcde fghij klmno",             // 영어 17자
        "일이삼사오 일이삼사오 일이삼사오",   // 한글 17자
        "콜라@맛!",                       // 허용되지 않은 특수문자
        "카카오커피"                       // 협의되지 않은 '카카오' 포함
    })
    void 단건상품수정_BAD_REQUEST_상품이름_유효성_검사(String invalidName) {
        //given
        Product savedProduct = productRepository.save(ProductBuilder.aProduct().build());
        Long productId = savedProduct.getProductId();

        var request = ProductBuilder.aProduct()
            .withName(invalidName)
            .build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.PUT, baseUrl() + "/" + productId, userToken, request,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    @Test
    void 단건상품수정_UNAUTHORIZED_인증없음() {
        //given
        Product savedProduct = productRepository.save(ProductBuilder.aProduct().build());
        Long productId = savedProduct.getProductId();

        var request = ProductBuilder.aProduct().build();

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.PUT, baseUrl() + "/" + productId, null, request,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    // DELETE
    @ParameterizedTest
    @MethodSource("tokenProvider")
    void 단건상품삭제_NO_CONTENT_테스트(String token) {
        // given
        Product savedProduct = productRepository.save(
            ProductBuilder.aProduct()
                .withName("one")
                .withPrice(1.0)
                .withImageUrl("https://1.img")
                .withMdConfirmed(false)
                .build());

        Long savedProductId = savedProduct.getProductId();

        // when
        var response = exchange(HttpMethod.DELETE, baseUrl() + "/" + savedProductId, token, null,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 단건상품삭제_NOT_FOUND_데이터베이스_상품존재() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.NotFound.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/321", userToken, null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }

    @Test
    void 단건상품삭제_UNAUTHORIZED_인증없음() {
        // given & when & then
        assertThatExceptionOfType(HttpClientErrorException.Unauthorized.class)
            .isThrownBy(
                () -> exchange(HttpMethod.DELETE, baseUrl() + "/1", null, null,
                    new ParameterizedTypeReference<Void>() {
                    })
            );
    }
}