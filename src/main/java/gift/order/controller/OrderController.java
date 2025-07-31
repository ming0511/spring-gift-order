package gift.order.controller;

import gift.order.dto.OrderCreateCommand;
import gift.order.dto.OrderCreateRequestDto;
import gift.order.dto.OrderCreateResponseDto;
import gift.order.entity.Order;
import gift.order.service.OrderService;
import gift.wish.annotation.LoginMember;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderCreateResponseDto> createOrder(
        @LoginMember Long memberId,
        @Valid @RequestBody OrderCreateRequestDto requestDto
    ) {
        OrderCreateCommand dto = new OrderCreateCommand(requestDto.optionId(),
            requestDto.quantity(),
            requestDto.message());

        Order order = orderService.createOrder(memberId, dto);

        OrderCreateResponseDto responseDto = new OrderCreateResponseDto(order.getId(),
            order.getOptionId(), order.getQuantity(), order.getOrderDateTime(), order.getMessage());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
