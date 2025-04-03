package com.fawry.store_api.repository;

import com.fawry.store_api.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    Optional<List<InventoryReservation>> findByOrderId(Long orderId);
}
