package com.project.qrsample;

import android.app.Activity;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DecoderActivity extends Activity implements OnQRCodeReadListener {

    private TextView myTextView;
	private QRCodeReaderView mydecoderview;
	private ImageView line_image;
	String rimei,rotp;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);
        
        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        
        myTextView = (TextView) findViewById(R.id.exampleTextView);
        
        line_image = (ImageView) findViewById(R.id.red_line_image);


        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.5f);
       mAnimation.setDuration(1000);
       mAnimation.setRepeatCount(-1);
		// Use this function to enable/disable decoding
		mydecoderview.setQRDecodingEnabled(true);
       mAnimation.setRepeatMode(Animation.REVERSE);
       mAnimation.setInterpolator(new LinearInterpolator());
       line_image.setAnimation(mAnimation);
        
    }

    
    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		if (text.isEmpty()|| text == null) {
			myTextView.setText("Failed");

		} else {
			myTextView.setText("successfully scanned qr-code");

			TelephonyManager tm=(TelephonyManager)getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
			String imei=tm.getDeviceId();
			rimei=text.substring(4, text.length());
			rotp=text.substring(0,4);
			Log.i(imei+"IMEI"+rotp, "IMEI"+rimei);
			if(imei.equalsIgnoreCase(rimei)){

        new transaction().execute();
				myTextView.setText("Transaction success");
			}
				}
	}
	
	// Called when your device have no camera
	
    
	@Override
	protected void onResume() {
		super.onResume();
		mydecoderview.startCamera();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mydecoderview.stopCamera();
	}



	public class transaction extends AsyncTask<String, String, String> {
		InputStream is = null;
		String result = null;
		String line = null;

		@Override
		protected String doInBackground(String... arg) {


			List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("resotp",rotp ));


			String stat = null;
String URl=Constants.ip+"resultFromApp.php";


			try {

				HttpParams httpParameters = new BasicHttpParams();

				int timeoutConnection = 10000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);

				int timeoutSocket = 10000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(URl);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				Log.e(" PRINT RESULT", result + "");

			} catch (final ClientProtocolException e) {

				e.printStackTrace();

			} catch (final IllegalStateException e) {

				e.printStackTrace();

			} catch (final UnsupportedEncodingException e) {

				e.printStackTrace();

			} catch (final IOException e) {

				e.printStackTrace();

			}

			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);


		}

	}


}
