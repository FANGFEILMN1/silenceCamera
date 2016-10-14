package com.example.takephotosilence;

import java.io.File;
import java.util.List;
import java.util.Timer;

import com.example.takephotosilence.CameraContainer.TakePictureListener;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
/** 
 * @ClassName: CameraAty 
 * @Description:  �Զ����������
 * @author LinJ
 * @date 2014-12-31 ����9:44:25 
 *  
 */
public class CameraAty extends Service implements View.OnClickListener,TakePictureListener, ScreenObserver.ScreenStateListener{
	public final static String TAG="CameraAty";
	private String mSaveRoot;
	private CameraContainer mContainer;
	private RelativeLayout mFloatLayout;
	private WindowManager.LayoutParams wmParams;
	private WindowManager mWindowManager;
	private Handler test;
	private Vibrator vibrator1;  
	private static Vibrator vibrator;
	private boolean isTakePhotoTimes = false;
	private boolean isLeftMode = false;
	private ScreenObserver mScreenObserver = null;
	//��ϢID
	public static final int vibrator_camera_start = 0x101;
	public static final int vibrator_camera_end = 0x102;
	public static final int vibrator_record_start = 0x103;
	public static final int vibrator_record_end = 0x104;
	public static final int vibrator_change_to_record_mode = 0x105;
	public static final int vibrator_change_to_camera_mode = 0x106;
	public static final int vibrator_repeat_take_photo_start = 0x107;
	public static final int vibrator_repeat_take_photo_end = 0x108;
	public static final int vibrator_release_camera = 0x109;
	
	public static final int call_to_take_photo = 0x201;
	public static final int call_to_record_view = 0x202;
	public static final int call_to_take_photo_times_start = 0x203;
	public static final int call_to_take_photo_times_end = 0x204;
	public static final int change_edge_mode = 0x205;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
		test = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.arg1)
				{
					case call_to_take_photo:
					{
						callCamera();
						break;
					}
					case call_to_record_view:
					{
						callRecord();
						break;
					}
					case call_to_take_photo_times_start:
					{
						if(true == GlobalValue.isRecording){
							mContainer.stopRecord(CameraAty.this);
							GlobalValue.isRecording=false;	
							vibratorSignal(vibrator_record_end);
						}
						isTakePhotoTimes = true;
						//callCamera();
						break;
					}
					case call_to_take_photo_times_end:
					{
						isTakePhotoTimes = false;
						break;
					}
					case change_edge_mode:
					{
						isLeftMode = !isLeftMode;
						if (true == isLeftMode)
							wmParams.gravity = Gravity.LEFT | Gravity.CENTER_HORIZONTAL;
						else
							wmParams.gravity = Gravity.RIGHT | Gravity.CENTER_HORIZONTAL;
						
						if (null != mFloatLayout)
							mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						break;
					}
					default:
						break;
				}	
		    }
		};
		GlobalValue.sHanToFxSev = test;
		createFloatView();
		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.startObserver(this);
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// ��ȡWindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		// ����window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// ����ͼƬ��ʽ��Ч��Ϊ����͸��
		wmParams.format = PixelFormat.RGB_888;
		// ���ø������ڲ��ɾ۽���ʵ�ֲ���������������������ɼ����ڵĲ�����
		wmParams.flags =
		// LayoutParams.FLAG_NOT_TOUCH_MODAL |
		LayoutParams.FLAG_NOT_FOCUSABLE
		// LayoutParams.FLAG_NOT_TOUCHABLE
		;

		// ������������ʾ��ͣ��λ��Ϊ����ö�
		if (true == isLeftMode)
			wmParams.gravity = Gravity.LEFT | Gravity.CENTER_HORIZONTAL;
		else
			wmParams.gravity = Gravity.RIGHT | Gravity.CENTER_HORIZONTAL;

		// ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
		wmParams.x = 0;
		wmParams.y = 0;

		// �����������ڳ�������
		//wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		//wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.width =80;
		wmParams.height = 80;
		
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// ��ȡ����������ͼ���ڲ���
		try {
			mFloatLayout = (RelativeLayout) inflater.inflate(
					R.layout.cam_for_server, null);
			// ���mFloatLayout
			mWindowManager.addView(mFloatLayout, wmParams);

			// �������ڰ�ť
			// mFloatView = (Button) mFloatLayout.findViewById(R.id.float_id);
			mContainer = (CameraContainer) mFloatLayout
					.findViewById(R.id.container_server);
			GlobalValue.cameraCon = mContainer;
		}
		catch(java.lang.RuntimeException e)
		{
			Toast.makeText(this, "get camera fail", Toast.LENGTH_SHORT).show();
			this.stopSelf();
			return;
		}

		mContainer.setVisibility(View.INVISIBLE);
		mSaveRoot="test";
		mContainer.setRootPath(mSaveRoot);
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// ���ü����������ڵĴ����ƶ�
		mFloatLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i(TAG, "onTouch");
				switch (event.getActionMasked())
				{
					case MotionEvent.ACTION_UP:
						if ((GlobalValue.sWinH / 2 - event.getRawY()) > 150)
						{
							callCamera();
						}
						else if ((event.getRawY() - GlobalValue.sWinH / 2) > 150)
						{									
							callRecord();
						}
						else
						{
							Log.i(TAG, "call black back");
							Intent intent = new Intent(CameraAty.this, BlackBcak.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
						wmParams.x = 0;
						wmParams.y = 0;
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						break;
					case MotionEvent.ACTION_MOVE:
						if (true == isLeftMode)
							wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
						else
							wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2 - GlobalValue.sWinW;
						wmParams.y = (int) event.getRawY() - mFloatLayout.getMeasuredHeight() / 2 - 25 - GlobalValue.sWinH / 2;
						Log.e(TAG, "event.getRawX() "+event.getRawX()+ 
								"mFloatLayout.getMeasuredWidth() / 2 "+(mFloatLayout.getMeasuredWidth() / 2)+
								"event.getRawY() "+event.getRawY()+ 
								"mFloatLayout.getMeasuredHeight() / 2 "+(mFloatLayout.getMeasuredHeight() / 2)+
								"wmParams.x = "+wmParams.x+"wmParams.y"+wmParams.y);
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						break;
					default:
						break;
				}

				return false;
			}
		});

		mFloatLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick");
			}
		});
		vibrator1 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vibrator = vibrator1;
		//mFloatView.setBackgroundColor(Color.GRAY);
	}

	/**
	 * ��������ͼ
	 */
	private void initThumbnail() {

	}

	@Override
	public void onClick(View view) {
	}


	private void stopRecord() {
		mContainer.stopRecord(this);
		GlobalValue.isRecording=false;
	}
	
	@Override
	public void onTakePictureEnd(Bitmap thumBitmap) {
		//mCameraShutterButton.setClickable(true);
		//mContainer.freeisBusy();
		vibratorSignal(vibrator_camera_end);
		if (true == isTakePhotoTimes)
		{
			callCamera();
		}
		stopCameraMonitor();
	}

	@Override
	public void onAnimtionEnd(Bitmap bm,boolean isVideo) 
	{
	}

	@Override
	public void onDestroy() {		
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		try{		
			if (mContainer!=null)
			mContainer.releaseCamera();

			mScreenObserver.shutdownObserver();
		}
		catch(java.lang.IllegalArgumentException e){
			
		}
		catch(java.lang.RuntimeException e){
			
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void vibratorSignal(int id)
	{
		switch (id)
		{
			case vibrator_camera_start:
			{
				long [] pattern = {100,100};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_camera_end:
			{
				long [] pattern = {100,300};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_record_start:
			{
				long [] pattern = {100,100,100,100};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_record_end:
			{
				long [] pattern = {100,100,100,300};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_change_to_record_mode:
			{
				long [] pattern = {100,300,100,300};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_change_to_camera_mode:
			{
				long [] pattern = {100,300,100,100};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_repeat_take_photo_start:
			{
				long [] pattern = {100,100,100,100,100,100};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_repeat_take_photo_end:
			{
				long [] pattern = {100,100,100,100,100,300};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			case vibrator_release_camera:
			{
				long [] pattern = {100,2000};   // ֹͣ ���� ֹͣ ����   
		        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   
				break;
			}
			
			
			default:
				break;
		}
	}
	private void callCamera()
	{
		startCameraMonitor();

		
		try{
			mContainer.forceToFocus();
			mContainer.takePicture(this);
			vibratorSignal(vibrator_camera_start);
			Log.e(TAG, "takePicture");
		}
		catch(java.lang.RuntimeException e){
			Log.e(TAG, "takePicture fail");
		}
	}
	private void callRecord()
	{
		if (true == isTakePhotoTimes)
		{
			Log.i(TAG, "true == isTakePhotoTimes");
			return;
		}
		try{
			if(!GlobalValue.isRecording){
				
				startCameraMonitor();
				
				GlobalValue.isRecording=mContainer.startRecord();
				
				stopCameraMonitor();
				
				if (true == GlobalValue.isRecording)
					vibratorSignal(vibrator_record_start);
			}else {
				startCameraMonitor();
				
				mContainer.stopRecord(this);
				
				stopCameraMonitor();
				
				GlobalValue.isRecording=false;	
				vibratorSignal(vibrator_record_end);
			}
		}
		catch(java.lang.RuntimeException e){
			
		}
	}
	private void startCameraMonitor()
	{
		GlobalValue.isCameraing = true;
		GlobalValue.timerCount = 12;
	}
	private void stopCameraMonitor()
	{
		GlobalValue.isCameraing = false;
		GlobalValue.timerCount = GlobalValue.timerCountMax;
	}

	@Override
	public void onScreenOn() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onScreenOn");
	}

	@Override
	public void onScreenOff() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onScreenOff");
		if (mContainer!=null)
			mContainer.releaseCamera();
		//mScreenObserver.shutdownObserver();
		//this.stopSelf();
		Vibrator vibrator;
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		long [] pattern = {100,100};   // ֹͣ ���� ֹͣ ����   
        vibrator.vibrate(pattern,-1);           //�ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1   

	}

	@Override
	public void onUserPresent() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onUserPresent");
		mScreenObserver.shutdownObserver();
		this.stopSelf();
	}
	
}