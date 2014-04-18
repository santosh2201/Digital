package com.example.barcodescanningapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class DecodedActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decoded);
		
		try{
			ImageView iv=(ImageView) findViewById(R.id.imageView);
			final String pathName=Environment.getExternalStorageDirectory()
					+ File.separator + "Octave" + File.separator + "output.bmp";
			final Bitmap bmp = BitmapFactory.decodeFile(pathName);
			iv.setImageBitmap(bmp);
			
		Intent intent=getIntent();
		String message=intent.getStringExtra("message");
		TextView tv=(TextView) findViewById(R.id.message);
		Button share=(Button) findViewById(R.id.share);
		
		Boolean encode=intent.getBooleanExtra("encode", true);
		if(!encode){
		tv.setText("decoded message :\n \n "+message);
		}else{
			tv.setText("share image ");
		}
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bitmap icon = bmp;
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/bmp");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
				try {
				    f.createNewFile();
				    FileOutputStream fo = new FileOutputStream(f);
				    fo.write(bytes.toByteArray());
				} catch (IOException e) {                       
				        e.printStackTrace();
				}
				share.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathName));
				startActivity(Intent.createChooser(share, "Share Image"));
				
			}
		});
		}catch(Exception e){
			Toast.makeText(DecodedActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}
		
		
	}

	



	
}
