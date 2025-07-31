package gift.order.service;

import gift.kakao.service.MessageService;
import gift.option.entity.Option;
import gift.option.service.OptionService;
import gift.order.dto.OrderCreateCommand;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Product;
import gift.wish.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    // Service
    private final OptionService optionService;
    private final MessageService kakaoMessageService;

    // Repository
    private final WishRepository wishRepository;
    private final OrderRepository orderRepository;

    public OrderService(OptionService optionService, MessageService kakaoMessageService,
        WishRepository wishRepository, OrderRepository orderRepository) {
        this.optionService = optionService;
        this.kakaoMessageService = kakaoMessageService;
        this.wishRepository = wishRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Long memberId, OrderCreateCommand dto) {
        Option option = optionService.getOption(dto.optionId());

        Order order = new Order(option, dto.quantity(), dto.message());

        optionService.subtractOptionQuantity(option.getOptionId(), dto.quantity());

        Product product = option.getProduct();
        wishRepository.deleteByMember_MemberIdAndProduct_ProductId(memberId,
            product.getProductId());

        Order savedOrder = orderRepository.save(order);

        String templateJson = kakaoMessageService.createTextMessage(dto.message());
        kakaoMessageService.sendTextMessage(templateJson);

        return savedOrder;
    }

}
