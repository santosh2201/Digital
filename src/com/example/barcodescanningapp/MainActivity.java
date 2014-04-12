package com.example.barcodescanningapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
/**
 * 
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	public static String responseString = null;
	// UI instance variables
	private Button scanBtn;
	private TextView formatTxt, contentTxt;
	private ProgressDialog progressDialog;
	String mCurrentPhotoPath;
	private static final int REQUEST_IMAGE_CAPTURE = 12;
	int status;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// instantiate UI items
		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);
		Button cameraButton=(Button) findViewById(R.id.camera_open_button);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		cameraButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dispatchTakePictureIntent();	
				//sendImageToServer();
			}
		});
		
		// listen for clicks
		scanBtn.setOnClickListener(this);
		
	}
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	          
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	        }
	    }
	}
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES+"/CameraSample");
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	public void onClick(View v) {
		// check for scan button
		if (v.getId() == R.id.scan_button) {
			// instantiate ZXing integration cla	String mCurrentPhotoPath;
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			// start scanning
			scanIntegrator.initiateScan();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// retrieve result of scanning - instantiate ZXing object
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		// check we have a valid result
		if (scanningResult != null) {
			// get content from Intent Result
			String scanContent = scanningResult.getContents();
			// get format name of data scanned

			long currentTime = System.currentTimeMillis();
			//sendDataToServer(scanContent, currentTime + "");
			contentTxt.setText("Student: " + scanContent);
		} else {
			// invalid scan data or scan canceled
			/*Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();*/
		}
		if(requestCode==REQUEST_IMAGE_CAPTURE){
			sendImageToServer();
		}
	}

	/**
	 * sends data to server asynchronously
	 * 
	 * */
	void sendImageToServer() {
		Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object  
		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setMessage("encoding image .... ");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		byte[] b = baos.toByteArray(); 
		String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		//WriteDataToFile fileWriter=new WriteDataToFile();
		//fileWriter.writeOnFile("encodedImage.txt", encodedImage);
		
		RequestParams params=new RequestParams();
		params.put("image", encodedImage);
		
		AsyncHttpClient client = new AsyncHttpClient();
		Toast.makeText(MainActivity.this, "sending data ..",
				   Toast.LENGTH_LONG).show();
			client.post("http://10.20.3.70:9000/store",params,new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String response) {
					progressDialog.dismiss();
					Toast.makeText(MainActivity.this, response,
							   Toast.LENGTH_LONG).show();
				}
			});
		
		//Toast.makeText(MainActivity.this, encodedImage, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.student_history:
			
			if (responseString == null) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"No scan data received!", Toast.LENGTH_SHORT);
				toast.show();

			} else {
				try {
					Intent intent = new Intent(MainActivity.this,
							HistoryActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
				}
			}

			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}
	
	
	public String dateConverter(Long x) {

		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd/MM/yyyy hh:mm:ss.SSS");
		String dateString = formatter.format(new Date(x));
		// System.out.println(dateString);
		return dateString;

	}
	

}