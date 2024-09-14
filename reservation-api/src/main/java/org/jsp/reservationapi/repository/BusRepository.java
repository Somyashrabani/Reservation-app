package org.jsp.reservationapi.repository;

import java.util.Optional;

import org.jsp.reservationapi.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BusRepository extends JpaRepository<Bus, Integer>{
	
	@Query("select b from Bus b where b.bus_number = ?1")
	Optional<Bus> findByBus_number(String bus_number);
}
