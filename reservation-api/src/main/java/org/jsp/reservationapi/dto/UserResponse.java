package org.jsp.reservationapi.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
private int id;
private String name;
private long phone;
private String gender;
private String email;
private int age;
private String password;
}
