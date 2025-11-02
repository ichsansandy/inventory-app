package com.obs.inventory.repository;

import com.obs.inventory.entity.Item;
import com.obs.inventory.model.item.ItemResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item,Long> {
    Optional<Item> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);

    @Query("""
    SELECT new com.obs.inventory.model.item.ItemResponse(
        i.id,
        i.name,
        i.price,
        COALESCE((
            SELECT SUM(
                CASE WHEN inv.type = InventoryActionType.T THEN inv.quantity ELSE -inv.quantity END
            )
            FROM Inventory inv
            WHERE inv.item.id = i.id
        ), 0)
    )
    FROM Item i
""")
    Page<ItemResponse> findAllWithStock(Pageable pageable);

    @Query("""
        SELECT new com.obs.inventory.model.item.ItemResponse(
            i.id,
            i.name,
            i.price,
            COALESCE((
                SELECT SUM(
                    CASE WHEN inv.type = InventoryActionType.T THEN inv.quantity ELSE -inv.quantity END
                )
                FROM Inventory inv
                WHERE inv.item.id = i.id
            ), 0)
        )
        FROM Item i
        WHERE i.id = :id
    """)
    Optional<ItemResponse> findByIdWithStock(@Param("id") Long id);
}
