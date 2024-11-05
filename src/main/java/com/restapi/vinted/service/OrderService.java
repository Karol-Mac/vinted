package com.restapi.vinted.service;

import com.restapi.vinted.entity.Clothe;

public interface OrderService {

    void createOrder(Clothe clothe, String email);
}
