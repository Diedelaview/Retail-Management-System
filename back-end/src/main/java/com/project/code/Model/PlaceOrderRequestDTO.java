package com.project.code.Model;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderRequestDTO {

    private Long storeId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private Double totalPrice;

    private List<PurchaseProductDTO> purchaseProduct = new ArrayList<>();

    public PlaceOrderRequestDTO() {}

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public List<PurchaseProductDTO> getPurchaseProduct() { return purchaseProduct; }
    public void setPurchaseProduct(List<PurchaseProductDTO> purchaseProduct) { this.purchaseProduct = purchaseProduct; }
}
