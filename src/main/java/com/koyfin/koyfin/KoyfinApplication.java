package com.koyfin.koyfin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.koyfin.koyfin.controller.ProcessorController;
import com.koyfin.koyfin.dbConnection.ConnectionIntitializer;

@SpringBootApplication
public class KoyfinApplication implements CommandLineRunner{
	@Autowired ConnectionIntitializer connectionIntitializer;
	@Autowired ProcessorController controller;
	
	public static void main(String[] args) {
		SpringApplication.run(KoyfinApplication.class, args);
	}
	
	
    @Override
    public void run(String...args) throws Exception {
    	while(true) {
    		controller.process();	
    	}
    	
    }

}
