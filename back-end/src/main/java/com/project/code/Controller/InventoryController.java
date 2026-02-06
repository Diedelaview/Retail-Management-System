package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    public InventoryController(ProductRepository productRepository,
                               InventoryRepository inventoryRepository,
                               ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {
        Map<String, String> res = new HashMap<>();
        try {
            Product p = request.getProduct();
            Inventory invReq = request.getInventory();

            if (p == null || p.getId() == null || !serviceClass.ValidateProductId(p.getId())) {
                res.put("Error", "Invalid product id");
                return res;
            }

            // update product
            productRepository.save(p);

            // update inventory if exists
            Inventory existing = null;
            if (invReq != null && invReq.getStore() != null && invReq.getStore().getId() != null) {
                existing = inventoryRepository.findByProductIdandStoreId(p.getId(), invReq.getStore().getId());
            }

            if (existing != null) {
                existing.setStockLevel(invReq.getStockLevel());
                inventoryRepository.save(existing);
                res.put("message", "Successfully updated product");
            } else {
                res.put("message", "No data available");
            }
            return res;

        } catch (DataIntegrityViolationException e) {
            res.put("Error", "Data integrity violation");
            return res;
        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return res;
        }
    }

    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> res = new HashMap<>();
        try {
            boolean ok = serviceClass.validateInventory(inventory);
            if (!ok) {
                res.put("message", "Data already present");
                return res;
            }
            inventoryRepository.save(inventory);
            res.put("message", "Data saved successfully");
            return res;
        } catch (DataIntegrityViolationException e) {
            res.put("Error", "Data integrity violation");
            return res;
        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return res;
        }
    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable("storeid") Long storeId) {
        Map<String, Object> res = new HashMap<>();
        res.put("products", productRepository.findProductsByStoreId(storeId));
        return res;
    }

    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category,
                                              @PathVariable String name,
                                              @PathVariable("storeid") Long storeId) {
        Map<String, Object> res = new HashMap<>();

        if ("null".equalsIgnoreCase(category)) {
            res.put("product", productRepository.findByNameLike(storeId, name));
        } else if ("null".equalsIgnoreCase(name)) {
            res.put("product", productRepository.findByCategoryAndStoreId(storeId, category));
        } else {
            res.put("product", productRepository.findByNameAndCategory(storeId, name, category));
        }
        return res;
    }

    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> res = new HashMap<>();
        res.put("product", productRepository.findByNameLike(storeId, name));
        return res;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable("id") Long id) {
        Map<String, String> res = new HashMap<>();
        try {
            if (!serviceClass.ValidateProductId(id)) {
                res.put("message", "product not present in database");
                return res;
            }
            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            res.put("message", "Product deleted");
            return res;
        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return res;
        }
    }

    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Integer quantity,
                                    @PathVariable Long storeId,
                                    @PathVariable Long productId) {
        Inventory inv = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inv == null || inv.getStockLevel() == null) return false;
        return inv.getStockLevel() >= quantity;
    }
}
