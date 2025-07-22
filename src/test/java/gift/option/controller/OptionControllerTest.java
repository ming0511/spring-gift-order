package gift.option.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gift.member.Role;
import gift.member.builder.MemberBuilder;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import gift.option.dto.OptionCreateRequestDto;
import gift.option.dto.OptionCreateResponseDto;
import gift.option.dto.OptionGetResponseDto;
import gift.option.dto.OptionUpdateRequestDto;
import gift.option.entity.Option;
import gift.option.entity.OptionName;
import gift.option.repository.OptionRepository;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
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
class OptionControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient client = RestClient.builder().build();

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/products";
    }

    String userToken;
    String adminToken;

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

    // POST
    @ParameterizedTest
    @ValueSource(strings = {
        "test",
        "( ) [ ] + - & / _"
    })
    void 상품옵션추가_CREATED(String validName) {
        // given
        Product product = ProductBuilder.aProduct().withName("product1").build();

        Product savedProduct = productRepository.save(product);

        OptionCreateRequestDto request = new OptionCreateRequestDto(validName, 10);

        // when
        var response = exchange(HttpMethod.POST,
            baseUrl() + "/" + savedProduct.getProductId() + "/options", userToken, request,
            new ParameterizedTypeReference<OptionCreateResponseDto>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actual = response.getBody();
        System.out.println(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234567890,1234567890,1234567890,1234567890,1234567890",
        "핸드크림@시어버터"
    })
    void 상품옵션추가_BAD_REQUEST_이름(String invalidName) {
        // given
        Product product = ProductBuilder.aProduct().withName("product1").build();

        Product savedProduct = productRepository.save(product);

        OptionCreateRequestDto request = new OptionCreateRequestDto(invalidName, 10);

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST,
                    baseUrl() + "/" + savedProduct.getProductId() + "/options", userToken, request,
                    new ParameterizedTypeReference<OptionCreateResponseDto>() {
                    })
            );
    }

    @Test
    void 상품옵션추가_BAD_REQUEST_수량() {
        // given
        Product product = ProductBuilder.aProduct().withName("product1").build();

        Product savedProduct = productRepository.save(product);

        OptionCreateRequestDto request = new OptionCreateRequestDto("test", 100000000);

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST,
                    baseUrl() + "/" + savedProduct.getProductId() + "/options", userToken, request,
                    new ParameterizedTypeReference<OptionCreateResponseDto>() {
                    })
            );
    }

    @Test
    void 상품옵션추가_BAD_REQUEST_동일옵션() {
        // given
        OptionName one = new OptionName("test");
        OptionName two = new OptionName("test2");

        Set<Option> options = new HashSet<>(Set.of(
            new Option(one, 10),
            new Option(two, 20)
        ));

        Product product = ProductBuilder.aProduct().withName("product1").withOptions(options)
            .build();

        Product savedProduct = productRepository.save(product);

        OptionCreateRequestDto request = new OptionCreateRequestDto("test", 10);

        // when & then
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
            .isThrownBy(
                () -> exchange(HttpMethod.POST,
                    baseUrl() + "/" + savedProduct.getProductId() + "/options", userToken, request,
                    new ParameterizedTypeReference<OptionCreateResponseDto>() {
                    })
            );
    }

    // GET
    @Test
    void 상품옵션조회_OK() {
        // given
        Product product = ProductBuilder.aProduct().withName("product1").build();

        Product savedProduct = productRepository.save(product);

        // when
        var response = exchange(HttpMethod.GET,
            baseUrl() + "/" + savedProduct.getProductId() + "/options", userToken, null,
            new ParameterizedTypeReference<List<OptionGetResponseDto>>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var actual = response.getBody();
        System.out.println(actual);
    }

    // PUT
    @Test
    void 상품옵션수정_NO_CONTENT() {
        // given
        Product product = ProductBuilder.aProduct().withName("product1").build();

        Product savedProduct = productRepository.save(product);

        Option option = savedProduct.getOptions().iterator().next();

        OptionUpdateRequestDto request = new OptionUpdateRequestDto("change", 10);

        // when
        var response = exchange(HttpMethod.PUT,
            baseUrl() + "/" + savedProduct.getProductId() + "/options/" + option.getOptionId(),
            userToken, request,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    // DELETE
    @Test
    void 상품옵션삭제_NO_CONTENT() {
        // given
        Product product = ProductBuilder.aProduct().withName("product1").build();

        Product savedProduct = productRepository.save(product);

        Option option = savedProduct.getOptions().iterator().next();

        // when
        var response = exchange(HttpMethod.DELETE,
            baseUrl() + "/" + savedProduct.getProductId() + "/options/" + option.getOptionId(),
            userToken, null,
            new ParameterizedTypeReference<Void>() {
            });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}