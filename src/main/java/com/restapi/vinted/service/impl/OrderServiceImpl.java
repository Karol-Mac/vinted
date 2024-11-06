package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Order;
import com.restapi.vinted.repository.OrderRepository;
import com.restapi.vinted.service.OrderService;
import com.restapi.vinted.utils.ClotheUtils;
import com.restapi.vinted.utils.OrderStatus;
import com.restapi.vinted.utils.UserUtils;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClotheUtils clotheUtils;
    private final UserUtils userUtils;

    public OrderServiceImpl(OrderRepository orderRepository, ClotheUtils clotheUtils, UserUtils userUtils) {
        this.orderRepository = orderRepository;
        this.clotheUtils = clotheUtils;
        this.userUtils = userUtils;
    }

    @Override
    public void createOrder(long clotheId, String email) {

        var clothe = clotheUtils.getClotheFromDB(clotheId);
        var buyer = userUtils.getUser(email);

        var order =  Order.builder()
                        .clothe(clothe)
                        .buyer(buyer)
                        .orderStatus(OrderStatus.NEW)
                        .seller(clothe.getUser())
                        .totalAmount(clothe.getPrice())
                        .build();

        orderRepository.save(order);
    }
}
