package org.jsp.reservationapi.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(nullable = false)
	private String bus_name;
	@Column(nullable = false, unique = true)
	private String bus_number;
	@Column(nullable = false)
	private LocalDate date_of_dept;
	@Column(nullable = false)
	private String form_location;
	@Column(nullable = false)
	private String to_location;
	@Column(nullable = false)
	private int num_of_seats;
	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private Admin admin;

}
