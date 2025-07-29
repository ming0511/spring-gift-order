package gift.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.exception.option.OptionNotFoundException;
import gift.exception.order.OutOfStockException;
import gift.kakao.service.KakaoMessageService;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.option.service.OptionService;
import gift.order.dto.OrderCreateCommand;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Product;
import gift.wish.repository.WishRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private OptionService optionService;
    @Mock
    private KakaoMessageService kakaoMessageService;
    @Mock
    private WishRepository wishRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder() {
        // given
        Long memberId = 1L;
        int quantity = 2;
        String message = "주문 메시지";

        Product product = new Product("기본 상품");

        Option option = new Option(10, product);

        OrderCreateCommand dto = new OrderCreateCommand(option.getOptionId(), quantity, message);

        when(optionRepository.findById(option.getOptionId())).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenAnswer(
            invocation -> invocation.getArgument(0));
        when(kakaoMessageService.createTextMessage(message)).thenReturn(
            "{\"object_type\":\"text\"}");

        // when
        Order order = orderService.createOrder(memberId, dto);

        // then
        assertNotNull(order);
        assertEquals(quantity, order.getQuantity());
        verify(optionService).subtractOptionQuantity(option.getOptionId(), quantity);
        verify(wishRepository).deleteByMember_MemberIdAndProduct_ProductId(memberId,
            product.getProductId());
        verify(kakaoMessageService).sendTextMessage("{\"object_type\":\"text\"}");
    }

    @Test
    void createOrder_OptionNotFound() {
        // given
        Long optionId = 100L;
        OrderCreateCommand dto = new OrderCreateCommand(optionId, 1, "msg");

        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(OptionNotFoundException.class, () -> {
            orderService.createOrder(1L, dto);
        });
    }

    @Test
    void createOrder_OutOfStock() {
        // given
        Option option = new Option(1);

        OrderCreateCommand dto = new OrderCreateCommand(option.getOptionId(), 2, "msg");

        when(optionRepository.findById(option.getOptionId())).thenReturn(Optional.of(option));

        // when & then
        assertThrows(OutOfStockException.class, () -> {
            orderService.createOrder(1L, dto);
        });
    }
}