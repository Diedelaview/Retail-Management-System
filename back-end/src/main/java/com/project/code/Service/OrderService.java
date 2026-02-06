package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        if (placeOrderRequest == null) {
            throw new RuntimeException("Invalid request");
        }

        // 1) Retrieve or create customer (by email)
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer(
                    placeOrderRequest.getCustomerName(),
                    placeOrderRequest.getCustomerEmail(),
                    placeOrderRequest.getCustomerPhone()
            );
            customer = customerRepository.save(customer);
        }

        // 2) Retrieve store
        Store store = storeRepository.findByid(placeOrderRequest.getStoreId());
        if (store == null) {
            throw new RuntimeException("Store not found");
        }

        // 3) Create order details
        Double total = placeOrderRequest.getTotalPrice();
        if (total == null) total = 0.0;

        OrderDetails order = new OrderDetails(customer, store, total, LocalDateTime.now());
        order = orderDetailsRepository.save(order);

        // 4) Create items + update inventory
        double computedTotal = 0.0;

        if (placeOrderRequest.getPurchaseProduct() != null) {
            for (PurchaseProductDTO itemReq : placeOrderRequest.getPurchaseProduct()) {

                Product product = productRepository.findById(itemReq.getProductId());
                if (product == null) {
                    throw new RuntimeException("Invalid product id: " + itemReq.getProductId());
                }

                Inventory inv = inventoryRepository.findByProductIdandStoreId(product.getId(), store.getId());
                if (inv == null) {
                    throw new RuntimeException("Inventory not found for product " + product.getId() +
                            " in store " + store.getId());
                }

                int qty = (itemReq.getQuantity() == null) ? 0 : itemReq.getQuantity();
                if (qty <= 0) {
                    throw new RuntimeException("Invalid quantity for product " + product.getId());
                }

                if (inv.getStockLevel() == null || inv.getStockLevel() < qty) {
                    throw new RuntimeException("Insufficient stock for product " + product.getId());
                }

                inv.setStockLevel(inv.getStockLevel() - qty);
                inventoryRepository.save(inv);

                OrderItem orderItem = new OrderItem(order, product, qty, product.getPrice());
                orderItemRepository.save(orderItem);

                computedTotal += product.getPrice() * qty;
            }
        }

        // If totalPrice not provided, correct it from computed total
        if (placeOrderRequest.getTotalPrice() == null || placeOrderRequest.getTotalPrice() <= 0.0) {
            order.setTotalPrice(computedTotal);
            orderDetailsRepository.save(order);
        }
    }
}
