package com.example.takephotosilence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class BlackBcak extends Activity implements View.OnClickListener, ScreenObserver.ScreenStateListener{

	private String TAG = "BlackBcak";
	private Button mButton;
	private boolean isCamera = true;
	private boolean isRepeat = false;
	private View mButtom;
	private View mTop;
	private ScreenObserver mScreenObserver = null;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.black_back);
		//mButton = (Button)findViewById(R.id.black_button_id);
		mButtom = findViewById(R.id.black_back_bottom_view);
		mButtom.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick");
				isCamera = !isCamera;
				if (isCamera)
				{
					CameraAty.vibratorSignal(CameraAty.vibrator_change_to_camera_mode);

				}
				else
				{
					CameraAty.vibratorSignal(CameraAty.vibrator_change_to_record_mode);

				}
			}});
		mTop = findViewById(R.id.black_back_top_view);
		mTop.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick");
				isRepeat = !isRepeat;
				if (isRepeat)
				{
					CameraAty.vibratorSignal(CameraAty.vibrator_repeat_take_photo_start);

				}
				else
				{
					CameraAty.vibratorSignal(CameraAty.vibrator_repeat_take_photo_end);

				}
	        	Handler h = GlobalValue.sHanToFxSev;
				Message mag = new Message();
				mag.arg1 = isRepeat ? CameraAty.call_to_take_photo_times_start :CameraAty.call_to_take_photo_times_end;
				h.sendMessage(mag);

			}});
		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.startObserver(this);

//		mButtom.setOnClickListener(new View.OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(BlackBcak.this, AlbumAty.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onClick");
		
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
 
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_UP:
        	//Toast.makeText(this, "444", Toast.LENGTH_SHORT).show();
        	Handler h = GlobalValue.sHanToFxSev;
			Message mag = new Message();
			mag.arg1 = isCamera ? CameraAty.call_to_take_photo :CameraAty.call_to_record_view;
			h.sendMessage(mag);
            return true;
        case KeyEvent.KEYCODE_VOLUME_MUTE:

        	//Toast.makeText(this, "5555", Toast.LENGTH_SHORT).show();
            return true;
        }
        //return super.onKeyDown(keyCode, event);
        return true;
    }
	@Override
	public void onBackPressed() {


		super.onBackPressed();
	}
	@Override
	public void onScreenOn() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScreenOff() {
		// TODO Auto-generated method stub
		this.finish();
	}
	@Override
	public void onUserPresent() {
		// TODO Auto-generated method stub
	}
	@Override
	public void onDestroy() {		
		super.onDestroy();
		mScreenObserver.shutdownObserver();
	}
}
