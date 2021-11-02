package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
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

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping(value = "upload/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		Path pathFoto = Paths.get("upload").resolve(filename).toAbsolutePath();
		log.info("PathFoto  => " + pathFoto);
		Resource recurso = null;
		try {
			recurso = new UrlResource(pathFoto.toUri());
			if(!recurso.exists() && !recurso.isReadable()) {
				throw new RuntimeException("Error: no se puede cargar la imagen " + pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename\""+recurso.getFilename()+"\"").body(recurso);
	}
	
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

	@GetMapping(value = "/listar")
	public String listar(Model model) {
		model.addAttribute("titulo","Listado de clientes");
		model.addAttribute("clientes",clienteService.findAll());
		return "listar";
	}
	
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
			String uniqueFileName = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
			Path rootPath = Paths.get("upload").resolve(uniqueFileName);
			
			log.info("Unique path => "+uniqueFileName);
			
			Path rootAbsoludPath = rootPath.toAbsolutePath();
			
			log.info("absolute Path"+rootAbsoludPath);
			
			
			try {
				Files.copy(foto.getInputStream(),rootAbsoludPath);
				
				cliente.setFoto(uniqueFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		clienteService.save(cliente);
		status.setComplete();
		return "redirect:listar";
	}
	
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
	
	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,Map<String,Object> model) {

		if (id>0) {
			clienteService.delete(id);
		}
		return "redirect:/listar";
		
	}
}













