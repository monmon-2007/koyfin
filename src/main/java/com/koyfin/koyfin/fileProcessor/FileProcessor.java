package com.koyfin.koyfin.fileProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FileProcessor {
	public static double processFile(FileInputStream file, Connection koyfin, double AdjustmentMax) throws Exception {
		
			JSONArray JsonFile = new JSONArray(getFileContent(file));
					
			for(int i=0; i<JsonFile.length(); i++) {
				JSONObject record = JsonFile.getJSONObject(i);
				
				if(record.has("date") && record.has("nav") && record.has("assetId")) {
					String date 	= record.get("date").toString();
					int assetID = Integer.parseInt(record.get("assetId").toString());
					double marketPrice	= Double.parseDouble(record.get("nav").toString());
					Statement statement = koyfin.createStatement();
					if(record.has("adjustmentFactor")) {
						AdjustmentMax = Double.parseDouble(record.get("adjustmentFactor").toString());
						statement.executeUpdate("UPDATE marketData SET maxAdjustedNav = "+ AdjustmentMax +";");
					}
					
					statement.executeUpdate("INSERT INTO marketData (dt, assetId,price,maxAdjustedNav,adjustedNav) VALUES('"
											+ date+"',"+assetID+","+marketPrice+","+AdjustmentMax+","+AdjustmentMax+");");
					koyfin.commit();
					
				}
			}
		
		return AdjustmentMax;
	}
	
	//converts file from a byte array to data that can be processed.
	public static String getFileContent(FileInputStream fis) throws IOException
	{
		try( BufferedReader br = new BufferedReader( new InputStreamReader(fis)))
		{
			StringBuilder sb = new StringBuilder();
			String line;
			while(( line = br.readLine()) != null ){
				sb.append( line );
				sb.append( '\n' );
			}
			return sb.toString();
		}
	}
}
