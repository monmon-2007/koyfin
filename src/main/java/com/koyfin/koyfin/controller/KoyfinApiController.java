package com.koyfin.koyfin.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.koyfin.koyfin.dbConnection.ConnectionIntitializer;
import com.koyfin.koyfin.fileProcessor.ResultSetConverter;

@RestController
public class KoyfinApiController {
	@Autowired ConnectionIntitializer connectionIntitializer;
	@Autowired ResultSetConverter resultConverter;
	final static String DATE_FORMAT = "yyyy-MM-dd";
	@GetMapping("/quote/{kid}/{startDate}/{endDate}")
	public ResponseEntity<?> newEmployee(@PathVariable String kid,@PathVariable String startDate,@PathVariable String endDate) {
		try {
			
			if(kid == null || kid.isEmpty() || startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
				return new ResponseEntity<>("Bad Data",HttpStatus.BAD_REQUEST);
			}else if(!isDateValid(startDate) || !isDateValid(endDate)) {
				return new ResponseEntity<>("Bad Dates format",HttpStatus.BAD_REQUEST);
			}
			
			connectionIntitializer.getInstance();
			Connection koyfinConnection = connectionIntitializer.getConnectKoyfin();
			Statement statement = koyfinConnection.createStatement();

			String s = "Select dt,price,AdjustedPrice from marketData,koyfindata where assetId in "
					+ "(Select acmeAssetID from koyfindata where KID = '"+kid+"' and marketData.dt>= '"+startDate+"' and marketData.dt<='"+endDate+"');";

			ResultSet set = statement.executeQuery(s);
			
			String result = resultConverter.convert(set).toString();
			koyfinConnection.close();
			
			return new ResponseEntity<>(result,HttpStatus.OK);
			
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}

	  }
	
	public static boolean isDateValid(String date) 
	{
	        try {
	            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	            df.setLenient(false);
	            df.parse(date);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	}
}
