package com.xcorpio.knowit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xcorpio.util.Utility;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	ImageButton btnFromPhoto;
	ImageButton btnFromFile;
	ImageButton btnSet;
	ImageButton btnExit;
	String fileFolder;
	String filePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		fileFolder=Environment.getExternalStorageDirectory().getAbsolutePath()+"/knowit";
		File file=new File(fileFolder);
		if(!file.exists()){
			file.mkdirs();
			Log.i("result", "filefoleder:"+fileFolder);
		}
		initWidgets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 初始化控件
	 */
	private void initWidgets(){
		btnFromPhoto=(ImageButton) findViewById(R.id.from_photo);
		btnFromFile=(ImageButton) findViewById(R.id.from_file);
		btnSet=(ImageButton) findViewById(R.id.set);
		btnExit=(ImageButton) findViewById(R.id.exit);
		btnFromPhoto.setOnClickListener(this);
		btnFromFile.setOnClickListener(this);
		btnSet.setOnClickListener(this);
		btnExit.setOnClickListener(this);
	}
	/**
	 * 处理按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){	//点击的是哪个按钮
		case R.id.from_photo:
			Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			filePath=fileFolder+"/"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";
			Log.i("result", "filename:"+filePath);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://"+filePath));
			startActivityForResult(intent, 1000);
			break;
		case R.id.from_file:
			intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 1001);
			break;
		case R.id.set:
			showSetDialog();
			break;
		case R.id.exit:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setMessage("确定要退出？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					MainActivity.this.finish();
				}
			});
			builder.setNegativeButton("取消", null);
			AlertDialog dialog=builder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			break;
		case R.id.help:
			break;
		case R.id.about:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1000://从照相获得
			//Log.i("result", "照像-> requestCode:"+requestCode+"  resultCode:"+resultCode);
			if( resultCode==RESULT_OK){
				//Bitmap bitmap=(Bitmap) data.getExtras().get("data");
				Intent intent=new Intent(this, ResultActivity.class);
				intent.putExtra("file", filePath);
				startActivity(intent);
			}
			break;
		case 1001://从文件获得
			//Log.i("result", "文件-> requestCode:"+requestCode+"  resultCode:"+resultCode+"  data:"+data.getData());
			if(resultCode == RESULT_OK && data!=null){
				Uri selectedFile=data.getData();
				String[] filePathColumn={MediaStore.Images.Media.DATA};
				Cursor cursor=getContentResolver().query(selectedFile, filePathColumn, null, null, null);
				cursor.moveToFirst();
				filePath=cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
				//Log.i("result", "filePath:"+filePath);
				Intent intent=new Intent(this, ResultActivity.class);
				intent.putExtra("file", filePath);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 显示设置对话框
	 */
	private void showSetDialog(){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		LayoutInflater inflater=getLayoutInflater();
		View view=inflater.inflate(R.layout.layout_set, null);
		builder.setView(view);
		final EditText et_address=(EditText) view.findViewById(R.id.server_address);
		final EditText et_port=(EditText) view.findViewById(R.id.server_port);
		String str_address=Utility.getPreference(getApplicationContext(),"address");
		String str_port=Utility.getPreference(getApplicationContext(),"port");
		if(str_address.equals("null")){
			et_address.setText("");
		}else{
			et_address.setText(str_address);
		}
		if(str_port.equals("null")){
			et_port.setText("");
		}else{
			et_port.setText(str_port);
		}
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(Integer.parseInt(et_port.getText().toString())<65536&&Utility.setPreference(getApplicationContext(),"address", et_address.getText().toString())&&Utility.setPreference(getApplicationContext(),"port", et_port.getText().toString())){
					Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(MainActivity.this, "保存失败,数据不合法!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("返回", null);
		AlertDialog dialog=builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
}
