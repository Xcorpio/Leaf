package com.xcorpio.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import com.common.PicFile;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Utility {
	
	/**
	 * �ж������Ƿ����
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager connManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] infos=connManager.getAllNetworkInfo();
		for(int i=0;i<infos.length;++i){
			if(infos[i].isConnected()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �ӷ��������ؽ��
	 * @return
	 */
	public static String getResult(Context context,String filePath){
		Socket s = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String result = null;
		String address=getPreference(context, "address");
		int port=Integer.parseInt(getPreference(context, "port"));
		try {
			s=new Socket(address, port);
			out=new ObjectOutputStream(s.getOutputStream());
			out.writeObject(getPicFile(filePath));
			in=new ObjectInputStream(s.getInputStream());
			result=(String) in.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "�޷����ӵ�������";
		}finally{
			try {
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
				if(s!=null&&s.isConnected()){
					s.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * ��·������ļ�ģ��
	 * @param filePath
	 * @return
	 */
	public static PicFile getPicFile(String filePath){
		File file=new File(filePath);
		PicFile picFile=new PicFile((int) file.length());
		picFile.fileName=file.getName();
		picFile.fileLength=file.length();
		try {
			FileInputStream in=new FileInputStream(file);
			try {
				int ret=in.read(picFile.fileData);
				//System.out.println("return="+ret);
				//ret=in.read(picFile.fileData);
				//System.out.println("return="+ret);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(in!=null){
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(picFile.fileName+":"+picFile.fileLength+" size:"+picFile.fileData.length);
		return picFile;
	}
	
	/**
	 * �������
	 * @param key
	 * @return
	 */
	public static String getPreference(Context context,String key){
		SharedPreferences sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getString(key, "null");
	}
	/**
	 * ��������
	 * @param key
	 * @param value
	 */
	public static boolean setPreference(Context context,String key,String value){
		SharedPreferences sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.edit().putString(key, value).commit();
	}
	
}
