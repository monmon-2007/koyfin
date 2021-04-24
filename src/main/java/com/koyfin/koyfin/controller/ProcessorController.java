package com.koyfin.koyfin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.koyfin.koyfin.KoyfinApplication;
import com.koyfin.koyfin.dbConnection.ConnectionIntitializer;
import com.koyfin.koyfin.fileProcessor.FileProcessor;


@Component
public class ProcessorController {
	
	private static String currentFileName = null;
	
	//cache value to keep track of the maximum adjustment value
	private static double maxAd_CACHE = 0;
	
	/*
	 * Path to input data.
	 */
	private String dirPath = "data/inputs/feed files/";
	private String archiveDir = "data/inputs/archive/";
	private String errorDir = "data/inputs/error/";
	
	private Connection koyfinConnection = null;
	File directory1 = new File(dirPath);
	
	Logger logger = LoggerFactory.getLogger(ProcessorController.class);
	
	@Autowired ConnectionIntitializer connectionIntitializer;
	@Autowired FileProcessor fileProcessor;
	public void process() {
		try {
			if(maxAd_CACHE == 0) {
				maxAd_CACHE = setMaxAdj();
			}
				if(!isDirectoryEmpty(directory1)) {
					Path path = Paths.get(dirPath);
					DirectoryStream<Path> stream = Files.newDirectoryStream(path);
					connectionIntitializer.getInstance();
					Connection koyfinConnection = connectionIntitializer.getConnectKoyfin();
					koyfinConnection.setAutoCommit(false);
					
				    for (Path file: stream) {
				    	currentFileName = file.getFileName().toString();
				        FileInputStream in = new FileInputStream(dirPath + currentFileName);
				        
				        if(koyfinConnection.isClosed()) {
				        	koyfinConnection = connectionIntitializer.getConnectKoyfin();
				        	koyfinConnection.setAutoCommit(false);
				        }
				        
				        maxAd_CACHE = fileProcessor.processFile(in,koyfinConnection,maxAd_CACHE); 
				        moveFile(dirPath,archiveDir, currentFileName);
				        logger.info("File processed Successfully -- File Name: "+currentFileName);
				        
				        in.close();
				    }
				    koyfinConnection.close();
				}
 
 
		}catch(Exception e) {
			moveFile(dirPath,errorDir, currentFileName);
			logger.error(e.getMessage() +" -- File Name: "+currentFileName);
		}
	}
	
	//Checks if the directory is empty
	public boolean isDirectoryEmpty(File directory) {  
	    String[] files = directory.list();
	    return files.length == 0;  
	}
	
	//moves file after processing
	public void moveFile(String filePath, String location, String fileName) {
		File file = new File(filePath + fileName);
		file.renameTo(new File(location + fileName));
	}
	
	//Sets Max adustment vale in case of application restart
	public double setMaxAdj() throws Exception {
			connectionIntitializer.getInstance();
			Connection koyfinConnection = connectionIntitializer.getConnectKoyfin();
			Statement statement = koyfinConnection.createStatement();
			ResultSet set = statement.executeQuery("Select maxAdjustedNav from marketData where dt in (Select max(dt) from marketData);");
			
			while(set.next()) {
				return set.getDouble("maxAdjustedNav");
			}
		return 1;
	}
}
