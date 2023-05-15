package com.insane.eyewalk.api;

import com.insane.eyewalk.api.model.domain.Plan;
import com.insane.eyewalk.api.model.domain.User;
import com.insane.eyewalk.api.model.input.PlanInput;
import com.insane.eyewalk.api.repositories.PlanRepository;
import com.insane.eyewalk.api.repositories.UserRepository;
import com.insane.eyewalk.api.security.auth.AuthenticationRequest;
import com.insane.eyewalk.api.security.auth.AuthenticationResponse;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.security.auth.RegisterRequest;
import com.insane.eyewalk.api.security.enums.Role;
import com.insane.eyewalk.api.security.token.TokenRepository;
import com.insane.eyewalk.api.service.PlanService;
import com.insane.eyewalk.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiApplicationTests {

	@Autowired
	private UserService userService;
	@Autowired
	private PlanService planService;
	@Autowired
	private PlanRepository planRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationService authenticationService;
	private final Principal principal = () -> "test@email.com";
	private final String password = "test";
	private final Principal fakeUser = () -> "lorem@ipsulum.com";

	@Test
	void testCreateAdmin() {
		mockUser();
		assertTrue(userService.adminExists());
	}
	@Test
	void testAuthentication() throws InterruptedException {
		Thread.sleep(1000);
		mockUser();
		assertNotNull(authenticate().getAccessToken());
	}
	@Test
	void testGetUser() {
		mockUser();
		User user = userService.getUser(principal.getName());
		assertNotNull(user.getName());
	}
	@Test
	void testUserNotFoundException() {
		assertThrows(UsernameNotFoundException.class, () -> userService.getUser(fakeUser.getName()));
	}
	@Test
	void testUserEmailExists() {
		mockUser();
		assertTrue(userService.userEmailExists(principal.getName()));
		assertFalse(userService.userEmailExists(fakeUser.getName()));
	}
	@Test
	void testListUsers() {
		mockUser();
		assertTrue(userService.getAll(principal).size() > 0);
	}
	@Test
	void testPlanCRUD() {
		mockUser();
		PlanInput planInput = PlanInput.builder()
				.name("test")
				.description("")
				.price(BigDecimal.ZERO)
				.build();
		Plan plan = planService.createPlan(planInput, principal);
		assertNotNull(plan);
		assertEquals(plan.getName(), planInput.getName());
		planInput.setName("planUpdated");
		plan = planService.updatePlan(plan.getId(),planInput, principal);
		assertEquals(plan.getName(), planInput.getName());
		assertNotNull(planService.getPlan(plan.getId()));
		assertTrue(planService.getPlanList().size() > 0);
		planRepository.delete(plan);
		Plan deletePlan = plan;
		assertThrows(NoSuchElementException.class, () -> {planService.getPlan(deletePlan.getId());});
	}
	@Test
	void testDeleteUser() throws InterruptedException {
		Thread.sleep(1100);
		mockUser();
		User user = userService.getUser(principal.getName());
		tokenRepository.removeAllByUser(user);
		userRepository.deleteById(user.getId());
		assertThrows(UsernameNotFoundException.class, () -> userService.getUser(principal.getName()));
	}

	private void mockUser() {
		if (!userService.userEmailExists(principal.getName())) {
			authenticationService.register(mockRegisterRequest());
		}
	}

	private RegisterRequest mockRegisterRequest() {
		return RegisterRequest.builder()
				.name("Test")
				.email(principal.getName())
				.password(password)
				.role(Role.ADMIN)
				.build();
	}

	private AuthenticationRequest mockAuthRequest() {
		return AuthenticationRequest
				.builder()
				.email(principal.getName())
				.password(password)
				.build();
	}

	private AuthenticationResponse authenticate() {
		return authenticationService.authenticate(mockAuthRequest());
	}

}
