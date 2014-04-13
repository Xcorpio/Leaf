package com.xcorpio.knowit;

import java.io.File;

import com.xcorpio.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
	
	ImageView imgResult;
	TextView tvResult;
	String filePath;
	Bitmap mBitmap;
	Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_result);
		Intent intent=getIntent();
		filePath=intent.getStringExtra("file");
		
		initWidgets();
		displayResult();
	}
	
	/**
	 * ��ʼ���ؼ�
	 */
	private void initWidgets(){
		imgResult=(ImageView) findViewById(R.id.img_result);
		tvResult=(TextView) findViewById(R.id.tv_result);
		//tvResult.setMovementMethod(new ScrollingMovementMethod());
		if(filePath!=null){
			BitmapFactory.Options options=new BitmapFactory.Options();
			File file=new File(filePath);
			if(file.length()>1024*1024){
				options.inSampleSize=5;
			}else{
				options.inSampleSize=2;
			}
			mBitmap=BitmapFactory.decodeFile(filePath,options);
			imgResult.setImageBitmap(mBitmap);
			//tvResult.setText(mBitmap.getWidth()+"x"+mBitmap.getHeight());
		}
	}
	
	/**
	 * ��ʾ���
	 */
	private void displayResult(){
		if(!Utility.isNetworkAvailable(getApplicationContext())){
			Toast.makeText(getApplicationContext(), "�޿�������", Toast.LENGTH_SHORT).show();
			tvResult.setTextColor(Color.RED);
			tvResult.setText("���粻����!");
		}else{
			tvResult.setTextColor(Color.GREEN);
			mHandler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 0:
						tvResult.setText("��ѯ��,���Ե�");
						break;
					case 1:
						tvResult.setText("��ѯ��,���Ե�.");
						break;
					case 2:
						tvResult.setText("��ѯ��,���Ե�..");
						break;
					case 3:
						tvResult.setText("��ѯ��,���Ե�...");
						break;
					case 4:
						String result=(String) msg.obj;
						if(result.equals("�޷����ӵ�������")){
							tvResult.setTextColor(Color.RED);
							Toast.makeText(ResultActivity.this, "�޷����ӵ�������", Toast.LENGTH_SHORT).show();
						}
						tvResult.setText(result);
						break;
					default:
						break;
					}
				}
				
			};
			new FetchResultThread().start();
		}
	}
	
	/**
	 * �������������ݵ��߳�
	 * @author Dong
	 *
	 */
	class FetchResultThread extends Thread{
		
		public void run(){
			ChangeViewThread ct=new ChangeViewThread();
			ct.start();
			Looper.prepare();
			String result=Utility.getResult(getApplicationContext(), filePath);
			ct.setFlag(false);
			try {
				ct.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg=Message.obtain();
			msg.what=4;
			msg.obj=result;
			mHandler.sendMessage(msg);
		}
	}
	/**
	 * �ı�TextView��ʾ
	 * @author Dong
	 *
	 */
	class ChangeViewThread extends Thread{
		
		boolean flag=true;
		int a;
		
		public void run(){
			while(flag){
				mHandler.sendEmptyMessage(a%4);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				a++;
			}
		}
		
		public void setFlag(boolean flag){
			this.flag=flag;
		}
	}
}
