package com.obs.inventory.repository;

import com.obs.inventory.entity.Orders;
import com.obs.inventory.model.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders,String> {

    @Query(value = "SELECT NEXT VALUE FOR order_seq", nativeQuery = true)
    Long getNextOrderNo();

    @Query(
            """
                SELECT new com.obs.inventory.model.order.OrderResponse(
                   ord.orderNo,
                   item.id,
                   ord.quantity,
                   ord.price
                )
                FROM Orders ord
                LEFT JOIN ord.item item
            """
    )
    Page<OrderResponse> findAllReturnResponse(Pageable pageable);

    @Query(
            """
                SELECT new com.obs.inventory.model.order.OrderResponse(
                   ord.orderNo,
                   item.id,
                   ord.quantity,
                   ord.price
                )
                FROM Orders ord
                LEFT JOIN ord.item item
                WHERE ord.orderNo = :id
            """
    )
    Optional<OrderResponse> findByOrderNoReturnResponse(@Param("id") String orderNo);

    @Query("""
        SELECT COALESCE(SUM(o.quantity), 0)
        FROM Orders o
        WHERE o.item.id = :itemId
    """)
    Long getTotalOrderedQty(@Param("itemId") Long itemId);

}
