package com.koyfin.koyfin.dbConnection;

import java.sql.DriverManager;

import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class ConnectionIntitializer {
	
	static Connection koyfinConnection = null;
	
	private ConnectionIntitializer() {}
	
	public static void getInstance()throws Exception{
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
				koyfinConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/koyfin","root","root");
			}catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public static Connection getConnectKoyfin() {
		try {
				if(koyfinConnection == null || koyfinConnection.isClosed()) {
					getInstance();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		return koyfinConnection;
	}

}
