package com.common;
import java.io.Serializable;


public class PicFile implements Serializable{
	
	public long fileLength;
	public String fileName;
	public byte[] fileData;
	
	public PicFile(int length){
		fileData=new byte[length];
	}
	
	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
}
