package com.obs.inventory.repository;

import com.obs.inventory.entity.Inventory;
import com.obs.inventory.model.inventory.InventoryResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query(
            """
                SELECT new com.obs.inventory.model.inventory.InventoryResponse(
                   inv.id,
                   item.id,
                   inv.quantity,
                   inv.type
                )
                FROM Inventory inv
                LEFT JOIN inv.item item
            """
    )
    Page<InventoryResponse> findAllReturnResponse(Pageable pageable);

    @Query(
            """
                SELECT new com.obs.inventory.model.inventory.InventoryResponse(
                   inv.id,
                   item.id,
                   inv.quantity,
                   inv.type
                )
                FROM Inventory inv
                LEFT JOIN inv.item item
                WHERE inv.id = :id
            """
    )
   Optional<InventoryResponse> findByIdReturnResponse(Long id);

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN i.type = InventoryActionType.T THEN i.quantity ELSE -i.quantity END), 0)
        FROM Inventory i
        LEFT JOIN i.item item
        WHERE item.id = :itemId
    """)
    Long getCurrentStock(Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdForUpdate(@Param("id") Long id);

}
