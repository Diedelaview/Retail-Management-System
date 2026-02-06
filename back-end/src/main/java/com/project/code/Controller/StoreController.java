package com.project.code.Controller;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final OrderService orderService;

    public StoreController(StoreRepository storeRepository, OrderService orderService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
    }

    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        Map<String, String> res = new HashMap<>();
        Store saved = storeRepository.save(store);
        res.put("message", "Store successfully created with ID: " + saved.getId());
        return res;
    }

    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.findByid(storeId) != null;
    }

    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequestDTO) {
        Map<String, String> res = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequestDTO);
            res.put("message", "Order placed successfully");
            return res;
        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return res;
        }
    }
}
