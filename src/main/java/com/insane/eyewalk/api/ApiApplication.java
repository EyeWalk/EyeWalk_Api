package com.insane.eyewalk.api;

import com.insane.eyewalk.api.security.auth.AuthenticationRequest;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.security.auth.RegisterRequest;
import com.insane.eyewalk.api.user.User;
import com.insane.eyewalk.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import static com.insane.eyewalk.api.user.Role.ADMIN;
import static com.insane.eyewalk.api.user.Role.MANAGER;

@SpringBootApplication
public class ApiApplication {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.name("Admin")
					.email("admin@email.com")
					.password("admin")
					.role(ADMIN)
					.build();
			if (userRepository.findByEmail(admin.getEmail()).isPresent()) {
				System.out.println("Admin token " + service.authenticate(new AuthenticationRequest(admin.getEmail(), admin.getPassword())).getAccessToken());
			} else {
				System.out.println("Admin token: " + service.register(admin).getAccessToken());
			}

			var manager = RegisterRequest.builder()
					.name("Manager")
					.email("manager@email.com")
					.password("manager")
					.role(MANAGER)
					.build();
			if (userRepository.findByEmail(manager.getEmail()).isPresent()) {
				System.out.println("Manager token " + service.authenticate(new AuthenticationRequest(manager.getEmail(), manager.getPassword())).getAccessToken());
			} else {
				System.out.println("Manager token: " + service.register(manager).getAccessToken());
			}

		};
	}

}
