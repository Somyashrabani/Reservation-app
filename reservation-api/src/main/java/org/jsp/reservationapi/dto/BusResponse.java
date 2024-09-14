package org.jsp.reservationapi.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusResponse {
	private int id;
	private String bus_name;
	private String bus_number;
	private LocalDate date_of_dept;
	private String form_location;
	private String to_location;
	private int num_of_seats;
}
