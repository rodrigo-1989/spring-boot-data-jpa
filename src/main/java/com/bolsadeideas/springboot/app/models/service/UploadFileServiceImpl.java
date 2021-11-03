package com.bolsadeideas.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final static String UPLOAD_FOLDER = "upload";

	@Override
	public Resource load(String filename) throws MalformedURLException {
		Path pathFoto = getPath(filename);
		log.info("PathFoto  => " + pathFoto);
		Resource recurso = null;

		recurso = new UrlResource(pathFoto.toUri());
		if (!recurso.exists() || !recurso.isReadable())
			throw new RuntimeException("Error: no se puede cargar la imagen " + pathFoto.toString());

		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPath(uniqueFileName);
		log.info("Unique path => " + uniqueFileName);

		Files.copy(file.getInputStream(), rootPath);

		return uniqueFileName;
	}

	@Override
	public boolean delete(String filename) {
		Path rootPath = getPath(filename);
		
		File archivo = rootPath.toFile();
		
		if(archivo.exists() && archivo.canRead()) 
			if(archivo.delete()) 
				log.info("Foto Eliminada con exito");
		return false;
	}

	
	
	public Path getPath(String filename) {
		return Paths.get(UPLOAD_FOLDER).resolve(filename).toAbsolutePath();
	}
}
