package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;


import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	
	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFile;
	
	@Secured("ROLE_USER")
	@GetMapping(value = "/foto/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		
		Resource recurso = null;
		try {
			recurso = uploadFile.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename\""+recurso.getFilename()+"\"").body(recurso);
	}
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable (value="id")Long id,Map<String,Object> model) {
		Cliente cliente =clienteService.findOne(id);
		if(cliente.getId() == null) {
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo","Detalle cliente "+ cliente.getNombre());
		return "ver";
	}

	@GetMapping(value = {"/listar","/"})
	public String listar(Model model,Authentication authentication,HttpServletRequest request) {
		
		if(authentication != null) {
			System.out.println("Hola usuario ".concat(authentication.getName()));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null) {
			System.out.println("Hola usuario ".concat(auth.getName())+" con auth");
		}
		if (hasRole("ROLE_USER")) {
			System.out.println("Hola ".concat(auth.getName()).concat(" Tienes acceso"));
		}else {
			System.out.println("Hola ".concat(auth.getName()).concat(" NO Tienes acceso tu role es =>").concat(auth.getAuthorities().toString()));
		}
		
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,"ROLE_");
		
		if(securityContext.isUserInRole("ADMIN")) {
			System.out.println("Hola desde SecurityContextHolderAwareRequestWrapper  ".concat(auth.getName()).concat(" Tienes acceso"));
		}
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			System.out.println("Hola con request  ".concat(auth.getName()).concat(" Tienes acceso"));
		}
		
		model.addAttribute("titulo","Listado de clientes");
		model.addAttribute("clientes",clienteService.findAll());
		return "listar";
	}
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/form")
	public String crear(Map<String,Object> model) {
		
		Cliente cliente = new Cliente();
		model.put("cliente",cliente);
		model.put("titulo","Fromulario de cliente");
		return "form";
	}
	
	@PostMapping(value="/form" )
	public String guardar(@Valid Cliente cliente, BindingResult result,Model model,@RequestParam ("file") MultipartFile foto,SessionStatus status) {
		if(result.hasErrors()) {
			model.addAttribute("titulo","Formulario del cliente");
			return "form";
		}
		if(!foto.isEmpty()) {
			
			if (cliente.getId() != null  
					&& cliente.getId() >0 
					&& cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				uploadFile.delete(cliente.getFoto());
				
			}
			
			String uniqueFileName = null;
			try {
				uniqueFileName = uploadFile.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cliente.setFoto(uniqueFileName);
			
			
		}
		clienteService.save(cliente);
		status.setComplete();
		return "redirect:listar";
	}
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/form/{id}")
	public String editar(@PathVariable(value="id") Long id,Map<String,Object> model) {
		Cliente cliente = new Cliente();
		if (id>0) {
			cliente =clienteService.findOne(id) ;
		}else {
			return "redirect:/listar";
		}
		model.put("cliente",cliente);
		model.put("titulo","Editar cliente");
		return "form";
	}
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,Map<String,Object> model) {

		if (id>0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			
			uploadFile.delete(cliente.getFoto());
		}
		return "redirect:/listar";
		
	}
	
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		
		if(context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();
		
		if(auth == null) {
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		System.out.println("hasRole () => "+auth.getAuthorities());
		return authorities.contains(new SimpleGrantedAuthority(role));
//		for (GrantedAuthority authority:authorities) {
//			if(role.equals(authority.getAuthority())) {
//				return true;
//			}
//		}
//		return false;
	}
}













