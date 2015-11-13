package com.yowoo.newbuyhouse.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

import com.google.gson.Gson;

public class FileUtils {
	
	private static Gson gson;
	
	
	public static String readInternalFile(Context context, String filename){
		
		try {
			
			FileInputStream fis = context.openFileInput(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			br.close();

			return sb.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void writeInternalFile(Context context, String filename, Object object){
		if (gson == null){
			gson = new Gson();
		}
		
		writeInternalFile(context, filename, gson.toJson(object));
	}
	
	public static void writeInternalFile(Context context, String filename, String content){
		FileOutputStream outputStream;

		try {
		  outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(content.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	public static String readAssetsFile(Context context, String filename){
		try {
			InputStream is = context.getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			br.close();

			return sb.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
