package org.yascode.springsecsection1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.yascode.springsecsection1.model.Authority;
import org.yascode.springsecsection1.model.Role;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.AuthorityRepository;
import org.yascode.springsecsection1.repository.RoleRepository;
import org.yascode.springsecsection1.repository.UserRepository;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Springsecsection1Application {

	static void main(String[] args) {
		SpringApplication.run(Springsecsection1Application.class, args);
	}

	@Bean
	public int init(
			AuthorityRepository authorityRepository,
			RoleRepository roleRepository,
			UserRepository userRepository
	) {

		Authority readAuthority = Authority.builder()
			.name("READ")
			.description("Read")
			.build();

		Authority writeAuthority = Authority.builder()
				.name("WRITE")
				.description("Write")
				.build();

		Authority updateAuthority = Authority.builder()
				.name("UPDATE")
				.description("Update")
				.build();

		Authority deleteAuthority = Authority.builder()
				.name("DELETE")
				.description("Delete")
				.build();

		authorityRepository.saveAll(
				List.of(readAuthority, writeAuthority, updateAuthority, deleteAuthority)
		);

		Role adminRole = Role.builder()
				.name("ROLE_ADMIN")
				.description("Admin role")
				.authorities(Set.of(readAuthority, writeAuthority, updateAuthority, deleteAuthority))
				.build();

		Role userRole = Role.builder()
				.name("ROLE_USER")
				.description("User role")
				.authorities(Set.of(readAuthority, writeAuthority))
				.build();

		roleRepository.saveAll(List.of(adminRole, userRole));

		User adminUser = User.builder()
				.username("admin")
				.password("{bcrypt}$2a$12$gSh82YccrxrQPMRtHXnRZ.mQKZ9ov4/Oqxn6FvBsuR603TshWhxO6")
				.roles(Set.of(adminRole))
				.accountNonExpired(true)
				.accountNonLocked(true)
				.credentialsNonExpired(true)
				.enabled(true)
				.build();

		User userUser = User.builder()
				.username("user")
				.password("{noop}^:Q@WYB!*QWPZ|xD&6,Z/s)SX6-Z&.@,P")
				.roles(Set.of(userRole))
				.accountNonExpired(true)
				.accountNonLocked(true)
				.credentialsNonExpired(true)
				.enabled(true)
				.build();

		User userAdminUser = User.builder()
				.username("userAdmin")
				.password("{bcrypt}$2a$12$UtLl2fLHpftN/46.yfjcEu21WX5IzQJDX/q2RKCqbgf.g85rvGAoW")//$3tg6Pdgtb$a%8C@#!BMKL3FcBEhN)PM
				.roles(Set.of(userRole, adminRole))
				.accountNonExpired(true)
				.accountNonLocked(true)
				.credentialsNonExpired(true)
				.enabled(true)
				.build();

		User userEnc = User.builder()
				.username("userEnc")
				.password("{noop}jxy7DS3oMAZOEAcmwfO90edzOwxybsWs39FPm2qk1/23dGxNpm4=")
				.roles(Set.of(userRole))
				.accountNonExpired(true)
				.accountNonLocked(true)
				.credentialsNonExpired(true)
				.enabled(true)
				.build();

		userRepository.saveAll(List.of(adminUser, userUser, userAdminUser, userEnc));

		return 0;
	}
}
