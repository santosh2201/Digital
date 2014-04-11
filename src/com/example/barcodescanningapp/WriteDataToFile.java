package com.example.barcodescanningapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

public class WriteDataToFile {
	/**
	 * function checks if file exists if true writes(updates) data to file if false
	 * creates a file and write(updates) data
	 * @author kanaud
	 * @param fileName  name of file
	 * @param fileData  data
	 * @return return true is successful false otherwise
	 * 
	 * */
	public boolean writeOnFile(String fileName, String fileData) {
			return writeData(fileData, fileName, false);
		
	}
	/**
	 * function checks if file exists if true writes(appends) data to file if false
	 * creates a file and write (append) data on new line
	 * @author kanaud
	 * @param fileName  name of file
	 * @param fileData  data
	 * @return return true is successful false otherwise
	 * 
	 * */

	public boolean writeOrAppandDataOnFile(String fileName, String fileData) {
			return writeData(fileData+"\n", fileName, true);
	}
	/**
	 * write data to the file 
	 * @author kanaud
	 * @param fileName  name of file
	 * @param fileData  data
	 * @param appender  true if data is appended to file false otherwise
	 * @return return true is successful false otherwise
	 * 
	 * */
	private boolean writeData(String fileData,String fileName,boolean appender){
		try{
			File file= checkOrCreateFile(fileName);
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), appender);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(fileData);
			bw.close();
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	/**
	 * method checks if directory "knowbus" exist if false creates it and then checks if file exists 
	 * if false creates it in case of any exception returns null
	 * @author kanaud
	 * @param fileName  name of file
	 * @return File is file exist null otherwise 
	 * 
	 * */
	private File checkOrCreateFile(String fileName) {
		addDirectory();	
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Octave" + File.separator + fileName);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} catch (Exception e) {
			return null;
		}
	}

	public void addDirectory() {
		File direct = new File(Environment.getExternalStorageDirectory()
				+ "/Octave");
		if (!direct.exists()) {
			if (direct.mkdir())
				; // directory is created;
		}
	}
}
