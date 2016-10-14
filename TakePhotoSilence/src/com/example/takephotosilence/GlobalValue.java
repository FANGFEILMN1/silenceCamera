package com.example.takephotosilence;



import android.os.Handler;

public class GlobalValue {
	//全局变量
	public static boolean isCamera = true;
	public static boolean isBlack = true;
	public static boolean isRecording = false;
	public static int sWinH = 0;
	public static int sWinW = 0;
	public static Handler sHanToFxSev;
	public static final int timerCountMax = 0xfffffff;
	public static int timerCount = timerCountMax;
	public static CameraContainer cameraCon;
	public static boolean isCameraing = false;
	//public static final int watch_dog_signal_max = 10;
	//public static int watch_dog_signal = timerCountMax;
}

