package com.project.code.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ServiceClass(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public boolean validateInventory(Inventory inventory) {
        if (inventory == null || inventory.getProduct() == null || inventory.getStore() == null) return false;
        if (inventory.getProduct().getId() == null || inventory.getStore().getId() == null) return false;

        Inventory existing = inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
        return existing == null;
    }

    public boolean validateProduct(Product product) {
        if (product == null || product.getName() == null) return false;
        Product existing = productRepository.findByName(product.getName());
        return existing == null;
    }

    public boolean ValidateProductId(long id) {
        Product p = productRepository.findById(id);
        return p != null;
    }

    public Inventory getInventoryId(Inventory inventory) {
        if (inventory == null || inventory.getProduct() == null || inventory.getStore() == null) return null;
        if (inventory.getProduct().getId() == null || inventory.getStore().getId() == null) return null;

        return inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
    }
}
