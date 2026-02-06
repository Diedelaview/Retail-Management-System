package com.project.code.Controller;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final ServiceClass serviceClass;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository,
                             ServiceClass serviceClass,
                             InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.serviceClass = serviceClass;
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> res = new HashMap<>();
        try {
            if (!serviceClass.validateProduct(product)) {
                res.put("message", "Product already exists");
                return res;
            }
            productRepository.save(product);
            res.put("message", "Product added successfully");
            return res;
        } catch (DataIntegrityViolationException e) {
            res.put("Error", "Data integrity violation (possible duplicate SKU)");
            return res;
        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return res;
        }
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        res.put("products", productRepository.findById(id));
        return res;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> res = new HashMap<>();
        try {
            productRepository.save(product);
            res.put("message", "Product updated successfully");
            return res;
        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return res;
        }
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
        Map<String, Object> res = new HashMap<>();

        if ("null".equalsIgnoreCase(name) && "null".equalsIgnoreCase(category)) {
            res.put("products", productRepository.findAll());
            return res;
        }
        if ("null".equalsIgnoreCase(name)) {
            res.put("products", productRepository.findByCategory(category));
            return res;
        }
        if ("null".equalsIgnoreCase(category)) {
            res.put("products", productRepository.findProductBySubName(name));
            return res;
        }

        res.put("products", productRepository.findProductBySubNameAndCategory(name, category));
        return res;
    }

    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> res = new HashMap<>();
        res.put("products", productRepository.findAll());
        return res;
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category,
                                                              @PathVariable("storeid") Long storeId) {
        Map<String, Object> res = new HashMap<>();
        res.put("product", productRepository.findProductByCategory(category, storeId));
        return res;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
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

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("products", productRepository.findProductBySubName(name));
        return res;
    }
}
