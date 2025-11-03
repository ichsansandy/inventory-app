package com.obs.inventory.repository;

import com.obs.inventory.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order,String> {

    @Query(value = "SELECT NEXT VALUE FOR order_seq", nativeQuery = true)
    Long getNextOrderNo();
}
