package com.bolsadeideas.springboot.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IUsuarioDao;
import com.bolsadeideas.springboot.app.models.entity.Role;
import com.bolsadeideas.springboot.app.models.entity.Usuario;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{
	
	private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
	
	@Autowired
	private IUsuarioDao usuarioDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioDao.findByUsername(username);
		if(usuario == null) {
			logger.error("Error Login: no existe el usuario '"+ username + "'");
			throw new UsernameNotFoundException("Usuario No existe");
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Role role:usuario.getRoles()) {
			logger.info("Role: ".concat(role.getRole()));
			authorities.add(new SimpleGrantedAuthority(role.getRole()));
		}
		
		if(authorities.isEmpty()) {
			logger.error("Error Login:El usuario '"+ username + "' No tiene roles asignados");
			throw new UsernameNotFoundException("Usuario No existe");
		}
		return new User(username, usuario.getPassword(), usuario.isEnable(), true, true, true, authorities);
	}

}
