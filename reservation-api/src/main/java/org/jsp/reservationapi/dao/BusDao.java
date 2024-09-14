package org.jsp.reservationapi.dao;

import java.util.List;
import java.util.Optional;

import org.jsp.reservationapi.model.Bus;
import org.jsp.reservationapi.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class BusDao {
	@Autowired
	private BusRepository busRepository;

	public Bus saveBus(Bus bus) {
		return busRepository.save(bus);
	}

	public Optional<Bus> findById(int id) {
		return busRepository.findById(id);
	}

	public Optional<Bus> findBusByBusNumber(String bus_number) {
		return busRepository.findByBus_number(bus_number);
	}
	public List<Bus> findAll() {
		return busRepository.findAll();
	}

	public void delete(int id) {
		busRepository.deleteById(id);
	}
}
