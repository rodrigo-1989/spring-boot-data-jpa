package com.bolsadeideas.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;
import com.bolsadeideas.springboot.app.models.service.JpaUserDetailsService;

@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
@Configuration
public class SpringSecurityConfig  extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
//	@Autowired
//	private DataSource dataSource;
	
	@Autowired
	private JpaUserDetailsService userDetailService;
	
	
	
	//Permisos para los usuarios segun su rol
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/","/css/**","/js/**","/images/**","/listar").permitAll()
//		.antMatchers("/ver/**").hasAnyRole("USER")
//		.antMatchers("/upload/**").hasAnyRole("USER")
//		.antMatchers("/form/**").hasAnyRole("ADMIN")
//		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and()
		.formLogin()
			.successHandler(successHandler)
			.loginPage("/login")
		.permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403");
	}

	@Autowired
	public void configurerGlobal (AuthenticationManagerBuilder builder) throws Exception {
//		En este metodo se implementa JPA para autenticacion de usuaios desde BD
		
		builder.userDetailsService(userDetailService)
		.passwordEncoder(passwordEncoder);
		
		
		
		
		
// En este caso se implementa autenticacion de usuarios con JDBC
//		builder.jdbcAuthentication()
//		.dataSource(dataSource)
//		.passwordEncoder(passwordEncoder)
//		.usersByUsernameQuery("select username, password, enable from users where username=?")
//		.authoritiesByUsernameQuery("select u.username, a.role from roles a inner join users u on (a.user_id=u.id) where u.username=?");
		
		
		
		
		
//En este caso se puede ver spring security pero con usuarios en memoria
//		PasswordEncoder encoder = passwordEncoder;
//		UserBuilder users =  User.builder().passwordEncoder(encoder::encode);
//		//Usuarios en memoria
//		builder.inMemoryAuthentication()
//		.withUser(users.username("admin").password("12345").roles("ADMIN","USER"))
//		.withUser(users.username("julio").password("12345").roles("USER"));
	}
}
