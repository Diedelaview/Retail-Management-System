package com.project.code.Repo;

import com.project.code.Model.Inventory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    Inventory findByProductIdandStoreId(@Param("productId") Long productId, @Param("storeId") Long storeId);

    List<Inventory> findByStore_Id(Long storeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Inventory i WHERE i.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
