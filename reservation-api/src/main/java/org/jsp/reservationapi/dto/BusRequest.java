package org.jsp.reservationapi.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BusRequest {
	private String bus_name;
	private String bus_number;
	private LocalDate date_of_dept;
	private String form_location;
	private String to_location;
	private int num_of_seats;
}
