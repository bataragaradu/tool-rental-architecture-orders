package com.rbinnovative.orders.service;

import com.rbinnovative.orders.repository.OrdersRepository;
import com.rbinnovative.orders.exception.OrderException;
import com.rbinnovative.orders.model.request.OrderRequest;
import com.rbinnovative.orders.model.dao.Orders;
import com.rbinnovative.orders.model.dto.OrdersDTO;
import com.rbinnovative.orders.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdersProcessorImpl implements TransactionProcessor {

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public OrdersDTO processOneQuery(Integer id) throws OrderException {
        Optional<Orders> orderOptional = ordersRepository.findById(id);
        Optional<OrdersDTO> ordersDTO = orderOptional.map((ordersElem) -> mapToOrdersDTOHandler(ordersElem, null));
        if (ordersDTO.isPresent()) {
            return ordersDTO.get();
        } else {
            throw new OrderException("The id doesn't exist, needs to be created " + id);
        }
    }

    @Override
    public List<OrdersDTO> processAllQuery() {
        List<Orders> orders = Optional.ofNullable(ordersRepository.findAll()).orElseGet(ArrayList::new);
        return orders.stream().map((ordersElem) -> mapToOrdersDTOHandler(ordersElem, null)).collect(Collectors.toList());
    }

    @Override
    public OrdersDTO createOrder(OrderRequest orderRequest) {
        Orders createOrders = processOrderCreation(orderRequest);
        createOrders = ordersRepository.save(createOrders);
        return mapToOrdersDTOHandler(createOrders, null);
    }

    @Override
    public void removeOrder(Integer id) throws OrderException {
        Optional<Orders> ordersOptional = ordersRepository.findById(id);

        if (ordersOptional.isPresent()) {
            ordersRepository.delete(ordersOptional.get());
        } else {
            throw new OrderException("Order with requested id doesn't exist");
        }
    }


    private OrdersDTO mapToOrdersDTOHandler(Orders orders, List<String> fields) {
        OrdersDTO orderDTO = new OrdersDTO();
        if (fields != null) {
            Utils.copyProperties(orders, orderDTO, fields);
        } else {
            BeanUtils.copyProperties(orders, orderDTO);
        }
        return orderDTO;
    }

    private Orders processOrderCreation(OrderRequest orderRequest) {
        Orders createOrder = new Orders();
        BeanUtils.copyProperties(orderRequest, createOrder);
        //Particular request entities
        return createOrder;
    }


}

//    @Override
//    public OrderDTO updateParameter(Integer id, OrderRequest invoiceRequest) throws OrderException, BillerException {
//        Optional<Order> requestUpdateId = Optional.empty();
//        if (id != null) {
//            requestUpdateId = invoiceRepository.findById(id);
//        }
//        if (requestUpdateId.isPresent()) {
//            Order updateOrder = requestUpdateId.get();
//            Utils.copyProperties(invoiceRequest, updateOrder, invoiceRequestFields);
//            updateOrder.setUpdatedAt(LocalDateTime.now());
//            updateOrder.setBillerId(extractBillerId(invoiceRequest.getBillerId()));
//            updateOrder = invoiceRepository.save(updateOrder);
//            return mapToOrderDTOHandler(updateOrder, null);
//        } else {
//            throw new OrderException("The id " + id + " doesn't exist, needs to be created ");
//        }
//    }
//

