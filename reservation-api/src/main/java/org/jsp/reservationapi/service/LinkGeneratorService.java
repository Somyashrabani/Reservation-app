package org.jsp.reservationapi.service;

import org.jsp.reservationapi.dao.AdminDao;
import org.jsp.reservationapi.dao.UserDao;
import org.jsp.reservationapi.model.Admin;
import org.jsp.reservationapi.model.User;
import org.jsp.reservationapi.util.AccountStstus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.jsp.reservationapi.util.ApplicationConstants.ADMIN_VERIFY_LINK;
import static org.jsp.reservationapi.util.ApplicationConstants.USER_VERIFY_LINK;
import static org.jsp.reservationapi.util.ApplicationConstants.ADMIN_RESET_PASSWORD_LINK;
import static org.jsp.reservationapi.util.ApplicationConstants.USER_RESET_PASSWORD_LINK;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;
@Service
public class LinkGeneratorService {
@Autowired
private AdminDao adminDao;
@Autowired
private UserDao userDao;
public String getActivationLink(Admin admin, HttpServletRequest request) {
	admin.setToken(RandomString.make(45));
	admin.setStatus(AccountStstus.ACTIVE.toString());
	adminDao.saveAdmin(admin);
	String siteUrl = request.getRequestURL().toString();
	return siteUrl.replace(request.getServletPath(), ADMIN_VERIFY_LINK + admin.getToken());
}

public String getUserActivationLink(User user, HttpServletRequest request) {
	String siteUrl = request.getRequestURL().toString();
	return siteUrl.replace(request.getServletPath(), USER_VERIFY_LINK + user);
}
public String getRestPasswordLink(Admin admin,HttpServletRequest request) {
	admin.setToken(RandomString.make(45));
	adminDao.saveAdmin(admin);
	String siteUrl=request.getRequestURL().toString();
	return siteUrl.replace(request.getServletPath(), ADMIN_RESET_PASSWORD_LINK + admin.getToken());
	
}
public String getRestPasswordLink(User user,HttpServletRequest request) {
user.setToken(RandomString.make(45));
	userDao.saveUser(user);
	String siteUrl=request.getRequestURL().toString();
	return siteUrl.replace(request.getServletPath(), USER_RESET_PASSWORD_LINK + user.getToken());
}
}
