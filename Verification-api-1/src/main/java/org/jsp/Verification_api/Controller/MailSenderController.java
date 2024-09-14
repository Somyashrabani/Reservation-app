package org.jsp.Verification_api.Controller;


	import org.springframework.aop.AfterReturningAdvice;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.mail.javamail.JavaMailSender;
	import org.springframework.mail.javamail.MimeMessageHelper;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;

	import jakarta.mail.MessagingException;
	import jakarta.mail.internet.MimeMessage;
	import jakarta.servlet.http.HttpServletRequest;


	@RestController
	public class MailSenderController {
		@Autowired
		private JavaMailSender javaMailSender;
		@Value("${activation.token}")
		private String token;

		@PostMapping("/send-mail")
		public String sendMail(HttpServletRequest request, @RequestParam String mailId) {
			String url = request.getRequestURL().toString();
			String path=request.getServletPath();
			String activation_link=url.replace(path, "/api/activate");
			activation_link = activation_link + "?token" + token;
			System.out.println(activation_link);
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			try {
				helper.setTo(mailId);
				helper.setSubject("Account Verification");
				helper.setText("Activation your account by clicking on the following");
				javaMailSender.send(message);
				return "mail has been sent";
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			javaMailSender.send(message);
			return "Mail has been activated";
		}
		@PostMapping("/activate")
			public String activate(@RequestParam String token) {
			if (token.equals(this.token)){
				return "your account has been activated";
			}
			return "Can not activate account bevause verification link is Invalid";
		}

	}


