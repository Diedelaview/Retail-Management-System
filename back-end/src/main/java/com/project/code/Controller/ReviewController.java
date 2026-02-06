package com.project.code.Controller;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    public ReviewController(ReviewRepository reviewRepository, CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
        Map<String, Object> res = new HashMap<>();

        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
        List<Map<String, Object>> cleaned = new ArrayList<>();

        for (Review r : reviews) {
            Map<String, Object> row = new HashMap<>();
            row.put("comment", r.getComment());
            row.put("rating", r.getRating());

            Customer c = customerRepository.findById(r.getCustomerId());
            row.put("customerName", (c != null && c.getName() != null) ? c.getName() : "Unknown");

            cleaned.add(row);
        }

        res.put("reviews", cleaned);
        return res;
    }
}
