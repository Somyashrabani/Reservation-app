package org.jsp.reservationapi.service;

import java.util.Optional;

import org.hibernate.boot.internal.DefaultSessionFactoryBuilderService;
import org.jsp.reservationapi.dao.UserDao;
import org.jsp.reservationapi.dto.EmailConfiguration;
import org.jsp.reservationapi.dto.ResponseStructure;
import org.jsp.reservationapi.dto.UserRequest;
import org.jsp.reservationapi.dto.UserResponse;
import org.jsp.reservationapi.exception.UserNotFoundException;
import org.jsp.reservationapi.model.User;
import org.jsp.reservationapi.util.AccountStstus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
@Autowired
private UserDao userDao;
@Autowired
private LinkGeneratorService linkGeneratorService;
@Autowired
private ReservationApiMailService mailService;
@Autowired
private EmailConfiguration emailConfiguration;

public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, HttpServletRequest request){
	ResponseStructure<UserResponse> structure = new ResponseStructure<>();
	User user = mapToUser(userRequest);
	user.setStatus(AccountStstus.IN_ACTIVE.toString());
	  user = userDao.saveUser(user);
	String activation_link = linkGeneratorService.getUserActivationLink(user, request);
	emailConfiguration.setSubject("Activate Your Account");
	emailConfiguration
			.setText("Dear User Please Activate Your Account by clicking on the following link:" );
	emailConfiguration.setToAddress(user.getEmail());
	structure.setMessage(mailService.sendMail(emailConfiguration));
	structure.setData(mapToUserResponse(user));
	structure.setStatusCode(HttpStatus.CREATED.value());
	return ResponseEntity.status(HttpStatus.CREATED).body(structure);
}
public ResponseEntity<ResponseStructure<UserResponse>> updateUser(UserRequest userRequest, int id) {
	Optional<User> recUser = userDao.findByUserId(id);
	ResponseStructure<UserResponse> structure = new ResponseStructure<>();
	if (recUser.isPresent()) {
		User dbUser = mapToUser(userRequest);
		dbUser.setId(id);
		structure.setData(mapToUserResponse(userDao.saveUser(dbUser)));
		structure.setMessage("User Updated");
		structure.setStatusCode(HttpStatus.ACCEPTED.value());
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(structure);
	}
	throw new UserNotFoundException("Cannot Update User as Id is Invalid");
}
public ResponseEntity<ResponseStructure<UserResponse>> findUserById(int id) {
	
	ResponseStructure<UserResponse> structure = new ResponseStructure<>();

	Optional<User> dbUser = userDao.findByUserId(id);
	
	if( dbUser.isPresent()) {
		
		structure.setData(mapToUserResponse(dbUser.get()));
		structure.setMessage("User Found");
		structure.setStatusCode(HttpStatus.OK.value());
		
		return ResponseEntity.status(HttpStatus.OK).body(structure);
	}
	throw new UserNotFoundException("Invalid User Id");
}
public ResponseEntity<ResponseStructure<UserResponse>> verfiyUserByPhoneAndPassword(long phone,String password){
	ResponseStructure<UserResponse> structure=new ResponseStructure<>();
	Optional<User> dbUser=userDao.verifyUserByPhoneAndPassword(phone, password);
	if(dbUser.isPresent()) {
		structure.setMessage("user Found and verification Successfull");
		structure.setStatusCode(HttpStatus.OK.value());
		structure.setData(mapToUserResponse(dbUser.get()));
		return ResponseEntity.status(HttpStatus.OK).body(structure);
		
	}
	throw new UserNotFoundException("Invalid Phone number and Password");
}
public ResponseEntity<ResponseStructure<UserResponse>> verfiyUserByEmailAndPassword(String email,String password){
	ResponseStructure<UserResponse> structure=new ResponseStructure<>();
	Optional<User> dbUser=userDao.verifyUserByEmailAndPassword(email, password);
	if(dbUser.isPresent()) {
		structure.setMessage("user Found and verification Successfull");
		structure.setStatusCode(HttpStatus.OK.value());
		structure.setData(mapToUserResponse(dbUser.get()));
		return ResponseEntity.status(HttpStatus.OK).body(structure);
		
	}
	throw new UserNotFoundException("Invalid Email id and Password");
}
public ResponseEntity<ResponseStructure<String>> deleteUserById(int id){
	ResponseStructure<String> structure=new ResponseStructure<>();
	Optional<User> recUser = userDao.findByUserId(id);
	if(recUser.isPresent()) {
		userDao.delete(id);
		structure.setData("User Found");
		structure.setMessage("User deleted");
		structure.setStatusCode(HttpStatus.OK.value());
		return ResponseEntity.status(HttpStatus.OK).body(structure);
	}
	throw new UserNotFoundException("Cannot delete User as Id is Invalid");
	}
private User mapToUser(UserRequest userRequest) {
	return User.builder().email(userRequest.getEmail()).name(userRequest.getName()).age(userRequest.getAge()).gender(userRequest.getGender()).phone(userRequest.getPhone()).password(userRequest.getPassword()).build();
	
}
private UserResponse mapToUserResponse(User user) {
	return UserResponse.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).age(user.getAge()).gender(user.getGender()).phone(user.getPhone()).password(user.getPassword()).build()
;}
public String activate(String token) {
	Optional<User> recUser = userDao.findByToken(token);
	if (recUser.isEmpty())
		throw new UserNotFoundException("Invalid Token");
	User dbUser = recUser.get();
	dbUser.setStatus("ACTIVE");
	dbUser.setToken(null);
	userDao.saveUser(dbUser);
	return "Your Account has been activated";
}
}

