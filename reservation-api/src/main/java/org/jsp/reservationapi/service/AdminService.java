package org.jsp.reservationapi.service;
import java.util.Optional;

import org.jsp.reservationapi.dao.AdminDao;
import org.jsp.reservationapi.dto.AdminRequest;
import org.jsp.reservationapi.dto.AdminResponse;
import org.jsp.reservationapi.dto.EmailConfiguration;
import org.jsp.reservationapi.dto.ResponseStructure;
import org.jsp.reservationapi.exception.AdminNotFoundException;
import org.jsp.reservationapi.model.Admin;
import org.jsp.reservationapi.util.AccountStstus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AdminService {
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private ReservationApiMailService mailService;
	@Autowired
	private LinkGeneratorService linkGeneratorService;
	@Autowired
	private EmailConfiguration emailConfiguration;

	public ResponseEntity<ResponseStructure<AdminResponse>> saveAdmin(AdminRequest adminRequest, HttpServletRequest request) {
		ResponseStructure<AdminResponse> structure = new ResponseStructure<>();
		Admin admin=mapToAdmin(adminRequest);
		admin.setStatus(AccountStstus.IN_ACTIVE.toString());
		admin = adminDao.saveAdmin(admin);
		String activation_link = linkGeneratorService.getActivationLink(admin, request);
		emailConfiguration.setSubject("Activate Your Account");
		emailConfiguration.setText("Dear Admin please Activate your account by clicking on the following link:"+ activation_link);
		emailConfiguration.setToAddress(admin.getEmail());
		structure.setMessage(mailService.sendMail(emailConfiguration));
		structure.setData(mapToAdminResponse(adminDao.saveAdmin(mapToAdmin(adminRequest))));
		structure.setStatusCode(HttpStatus.CREATED.value());
		return ResponseEntity.status(HttpStatus.CREATED).body(structure);
	}

	public ResponseEntity<ResponseStructure<AdminResponse>> update(AdminRequest adminRequest, int id) {
		Optional<Admin> recAdmin = adminDao.findById(id);
		ResponseStructure<AdminResponse> structure = new ResponseStructure<>();
		if (recAdmin.isPresent()) {
			Admin dbAdmin=mapToAdmin(adminRequest);
			dbAdmin.setId(id);
			structure.setData(mapToAdminResponse(adminDao.saveAdmin(dbAdmin)));
			structure.setMessage("Admin Updated");
			structure.setStatusCode(HttpStatus.ACCEPTED.value());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(structure);
		}
		throw new AdminNotFoundException("Cannot Update Admin as Id is Invalid");
	}

	public ResponseEntity<ResponseStructure<AdminResponse>> findById(int id) {
		ResponseStructure<AdminResponse> structure = new ResponseStructure<>();
		Optional<Admin> dbAdmin = adminDao.findById(id);
		if (dbAdmin.isPresent()) {
			structure.setData(mapToAdminResponse(dbAdmin.get()));
			structure.setMessage("Admin Found");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new AdminNotFoundException("Invalid Admin Id");
	}

	public ResponseEntity<ResponseStructure<AdminResponse>> verify(long phone, String password) {
		ResponseStructure<AdminResponse> structure = new ResponseStructure<>();
		Optional<Admin> dbAdmin = adminDao.verify(phone, password);
		if (dbAdmin.isPresent()) {
			structure.setData(mapToAdminResponse(dbAdmin.get()));
			structure.setMessage("Verification Succesfull");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new AdminNotFoundException("Invalid Phone Number or Password");
	}

	public ResponseEntity<ResponseStructure<AdminResponse>> verify(String email, String password) {
		ResponseStructure<AdminResponse> structure = new ResponseStructure<>();
		Optional<Admin> dbAdmin = adminDao.verify(email, password);
		if (dbAdmin.isPresent()) {
			Admin admin= dbAdmin.get();
			if(admin.getStatus().equals(AccountStstus.IN_ACTIVE.toString()))
				throw new IllegalStateException("Please Activate Your Account");
			structure.setData(mapToAdminResponse(dbAdmin.get()));
			structure.setMessage("Verification Succesfull");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new AdminNotFoundException("Invalid Email Id or Password");
	}

	public String forgotPassword(String email, HttpServletRequest request) {
		Optional<Admin> recAdmin =adminDao.findByEmail(email);
		if (recAdmin.isEmpty())
			throw new AdminNotFoundException("Invalid Email Id");
		Admin admin =recAdmin.get();
		String resetPasswordLink= linkGeneratorService.getRestPasswordLink(admin, request);
		emailConfiguration.setToAddress(email);
		emailConfiguration.setText("please click on the following link to reset your password:"+ resetPasswordLink);
		emailConfiguration.setSubject("Reset your Password");
		mailService.sendMail(emailConfiguration);
		return "reset password link has been sent entered mail id";
	}
	public AdminResponse verifyLink(String token) {
		Optional<Admin> recAdmin= adminDao.findByToken(token);
		if(recAdmin.isEmpty())
			throw new AdminNotFoundException("Link has been expired or it is Invalid");
		Admin dbAdmin =recAdmin.get();
		dbAdmin.setToken(null);
		adminDao.saveAdmin(dbAdmin);
		return mapToAdminResponse(dbAdmin);
	}
	public ResponseEntity<ResponseStructure<String>> delete(int id) {
		ResponseStructure<String> structure = new ResponseStructure<>();
		Optional<Admin> dbAdmin = adminDao.findById(id);
		if (dbAdmin.isPresent()) {
			adminDao.delete(id);
			structure.setData("Admin Found");
			structure.setMessage("Admin deleted");
			structure.setStatusCode(HttpStatus.OK.value());
			return ResponseEntity.status(HttpStatus.OK).body(structure);
		}
		throw new AdminNotFoundException("Cannot delete Admin as Id is Invalid");
	}
	private Admin mapToAdmin(AdminRequest adminRequest) {
		return Admin.builder().email(adminRequest.getEmail()).name(adminRequest.getName()).phone(adminRequest.getPhone()).gst_numnber(adminRequest.getGst_number()).travel_name(adminRequest.getTravel_name()).password(adminRequest.getPassword()).build();
	}
	private AdminResponse mapToAdminResponse(Admin admin) {
		return AdminResponse.builder().name(admin.getName()).email(admin.getEmail()).id(admin.getId()).gst_number(admin.getGst_numnber()).phone(admin.getPhone()).travels_name(admin.getTravel_name()).password(admin.getPassword()).build();
	}
	public String activate(String token) {
		Optional<Admin> recAdmin = adminDao.findByToken(token);
		if (recAdmin.isEmpty())
			throw new AdminNotFoundException("Invalid Token");
		Admin dbAdmin = recAdmin.get();
		dbAdmin.setStatus("ACTIVE");
		dbAdmin.setToken(null);
		adminDao.saveAdmin(dbAdmin);
		return "Your Account has been activated";
	}
}
