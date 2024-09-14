package org.jsp.reservationapi.service;

import java.util.List;
import java.util.Optional;

import org.jsp.reservationapi.dao.AdminDao;
import org.jsp.reservationapi.dao.BusDao;
import org.jsp.reservationapi.dto.BusRequest;
import org.jsp.reservationapi.dto.BusResponse;
import org.jsp.reservationapi.dto.ResponseStructure;
import org.jsp.reservationapi.exception.AdminNotFoundException;
import org.jsp.reservationapi.exception.BusNotFoundException;
import org.jsp.reservationapi.model.Admin;
import org.jsp.reservationapi.model.Bus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BusService {
	@Autowired
	private BusDao busDao;
	@Autowired
	private AdminDao adminDao;

	public ResponseEntity<ResponseStructure<BusResponse>> saveBus(BusRequest busRequest, int id) {
		ResponseStructure<BusResponse> structure = new ResponseStructure<>();
		Optional<Admin> recAdmin = adminDao.findById(id);
		
		if (recAdmin.isPresent()) {
			Admin admin = recAdmin.get();
			
			Bus bus = mapToBus(busRequest);
			bus.setAdmin(admin);// assigning admin to bus
			admin.getBuses().add(bus);// assigning bus to admin
			adminDao.saveAdmin(admin);// updating admin
			
			structure.setMessage("Bus saved");
			structure.setData(mapToBusResponse(busDao.saveBus(bus)));
			structure.setStatusCode(HttpStatus.CREATED.value());
			
			return ResponseEntity.status(HttpStatus.CREATED).body(structure);
		}
		throw new AdminNotFoundException("Cannot Update Admin as Id is Invalid");
	}

	public ResponseEntity<ResponseStructure<BusResponse>> update(BusRequest busRequest, int id) {
		Optional<Bus> recBus = busDao.findById(id);

		ResponseStructure<BusResponse> structure = new ResponseStructure<>();
		
		if (recBus.isPresent()) {
			Bus dbbus = recBus.get();
			dbbus.setBus_number(busRequest.getBus_number());
			dbbus.setBus_name(busRequest.getBus_name());
			dbbus.setDate_of_dept(busRequest.getDate_of_dept());
			dbbus.setForm_location(busRequest.getForm_location());
			dbbus.setTo_location(busRequest.getTo_location());
			dbbus.setNum_of_seats(busRequest.getNum_of_seats());

			structure.setData(mapToBusResponse(busDao.saveBus(dbbus)));
			structure.setMessage("Bus Updated");
			structure.setStatusCode(HttpStatus.ACCEPTED.value());
			
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(structure);
		}
		throw new BusNotFoundException("Cannot Update Bus as Id is Invalid");

	}

	public ResponseEntity<ResponseStructure<BusResponse>> findById(int id) {
		ResponseStructure<BusResponse> structure = new ResponseStructure<>();
		Optional<Bus> dbbus = busDao.findById(id);
		if (dbbus.isPresent()) {
			structure.setData(mapToBusResponse(dbbus.get()));
			structure.setMessage("Bus Found");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new BusNotFoundException("Invalid Bus Id");
	}

	public ResponseEntity<ResponseStructure<BusResponse>> findByBusNumber(String bus_number) {
		ResponseStructure<BusResponse> structure = new ResponseStructure<>();
		Optional<Bus> dbbus = busDao.findBusByBusNumber(bus_number);
		if (dbbus.isPresent()) {
			structure.setData(mapToBusResponse(dbbus.get()));
			structure.setMessage("Bus Found");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new BusNotFoundException("Invalid Bus Id");
	}
	public ResponseEntity<ResponseStructure<List<Bus>>> findAll() {
		ResponseStructure<List<Bus>> structure = new ResponseStructure<>();
		structure.setData(busDao.findAll());
		structure.setMessage("List of All Buses");
		structure.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.status(HttpStatus.OK).body(structure);
	}

	public ResponseEntity<ResponseStructure<String>> delete(int id) {
		ResponseStructure<String> structure = new ResponseStructure<>();
		Optional<Bus> dbbus = busDao.findById(id);
		if (dbbus.isPresent()) {
			busDao.delete(id);
			structure.setData("Bus Found");
			structure.setMessage("Bus Not Found");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new BusNotFoundException("Cannot delete bus as Id is Invalid");
	}

	private Bus mapToBus(BusRequest busRequest) {
		return Bus.builder().bus_name(busRequest.getBus_name()).bus_number(busRequest.getBus_number())
				.date_of_dept(busRequest.getDate_of_dept()).to_location(busRequest.getTo_location())
				.form_location(busRequest.getForm_location()).num_of_seats(busRequest.getNum_of_seats()).build();
	}

	private BusResponse mapToBusResponse(Bus bus) {
		return BusResponse.builder().id(bus.getId()).bus_name(bus.getBus_name()).bus_number(bus.getBus_number())
				.date_of_dept(bus.getDate_of_dept()).form_location(bus.getForm_location())
				.to_location(bus.getTo_location()).num_of_seats(bus.getNum_of_seats()).build();
	}
}
