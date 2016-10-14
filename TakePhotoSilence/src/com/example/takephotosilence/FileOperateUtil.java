package com.example.takephotosilence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/** 
 * @ClassName: FileOperateUtil 
 * @Description:  �ļ�����������
 * @author ����
 * @date 2016��5��18��9:20:00 
 *  
 */
public class FileOperateUtil {
	public final static String TAG="FileOperateUtil";

	public final static int ROOT=0;//��Ŀ¼
	public final static int TYPE_IMAGE=1;//ͼƬ
	public final static int TYPE_THUMBNAIL=2;//����ͼ
	public final static int TYPE_VIDEO=3;//��Ƶ
	public final static int TYPE_RECYCLE_BIN = 4;//����վ 

	/**
	 *��ȡ�ļ���·�� �ɸ�������϶���
	 * @param type �ļ������
	 * @param rootPath ��Ŀ¼�ļ������� Ϊҵ����ˮ��
	 * @return
	 * 
	 */
	public static String getFolderPath(Context context,int type,String rootPath) {
		//��ҵ���ļ���Ŀ¼
		StringBuilder pathBuilder=new StringBuilder();
		//���Ӧ�ô洢·��
		//pathBuilder.append(context.getExternalFilesDir(null).getAbsolutePath());
		pathBuilder.append(getSDPath());
		pathBuilder.append(File.separator);
		//����ļ���Ŀ¼
		pathBuilder.append(context.getString(R.string.Files));
		pathBuilder.append(File.separator);
		//��ӵ�Ȼ�ļ�����·��
//		pathBuilder.append(rootPath);
//		pathBuilder.append(File.separator);
		switch (type) {
		case TYPE_IMAGE:
			pathBuilder.append(context.getString(R.string.Image));
			break;
		case TYPE_VIDEO:
			pathBuilder.append(context.getString(R.string.Video));
			break;
		case TYPE_THUMBNAIL:
			pathBuilder.append(context.getString(R.string.Thumbnail));
			break;
		case TYPE_RECYCLE_BIN:
			pathBuilder.append(context.getString(R.string.recycle_bin));
			break;	
		default:
			break;
		}
		return pathBuilder.toString();
	}

	/**
	 * ��ȡĿ���ļ�����ָ����׺�����ļ�����,�����޸���������
	 * @param file Ŀ���ļ���·��
	 * @param format ָ����׺��
	 * @param content ����������,���Բ�����Ƶ����ͼ
	 * @return
	 */
	public static List<File> listFiles(String file,final String format,String content){
		return listFiles(new File(file), format,content);
	}
	/**
	 * ��ȡĿ���ļ�����ָ����׺�����ļ�����,�����޸���������
	 * @param file Ŀ���ļ���·��
	 * @param format ָ����׺��
	 * @return
	 */
	public static List<File> listFiles(String file,final String format){
		return listFiles(new File(file), format,null);
	}
	/**
	 * ��ȡĿ���ļ�����ָ����׺�����ļ�����,�����޸���������
	 * @param file Ŀ���ļ� ��
	 * @param extension ָ����׺��
	 * @param content ����������,���Բ�����Ƶ����ͼ
	 * @return
	 */
	public static List<File> listFiles(File file,final String extension,final String content){
		File[] files=null;
		if(file==null||!file.exists()||!file.isDirectory())
			return null;
		//�ҳ�Ŀ¼��ָ�����͵��ļ�
		files=file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				if(content==null||content.equals(""))
					return arg1.endsWith(extension);
				else {
                    return  arg1.contains(content)&&arg1.endsWith(extension);           		
				}
			}
		});
		if(files!=null){
			List<File> list=new ArrayList<File>(Arrays.asList(files));
			sortList(list, false);
			return list;
		}
		return null;
	}

	/**  
	 *  �����޸�ʱ��Ϊ�ļ��б�����
	 *  @param list ������ļ��б�
	 *  @param asc  �Ƿ��������� trueΪ���� falseΪ���� 
	 */
	public static void sortList(List<File> list,final boolean asc){
		//���޸���������
		Collections.sort(list, new Comparator<File>() {
			public int compare(File file, File newFile) {
				if (file.lastModified() > newFile.lastModified()) {
					if(asc){
						return 1;
					}else {
						return -1;
					}
				} else if (file.lastModified() == newFile.lastModified()) {
					return 0;
				} else {
					if(asc){
						return -1;
					}else {
						return 1;
					}
				}

			}
		});
	}

	/**
	 * �ѵ�ǰʱ��Ϊ������ָ����׺���ļ�
	 * @param extension ��׺�� ��".jpg"
	 * @return
	 */
	public static String createFileNmae(String extension){
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
		// ת��Ϊ�ַ���
		String formatDate = format.format(new Date());
		//�鿴�Ƿ��"."
		if(!extension.startsWith("."))
			extension="."+extension;
		return formatDate+extension;
	}

	/**  
	 *  ɾ������ͼ ͬʱɾ��Դͼ��Դ��Ƶ
	 *  @param thumbPath ����ͼ·��
	 *  @return   
	 */
	public static boolean deleteThumbFile(String thumbPath,Context context) {
		boolean flag = false;

		//Log.e(TAG, "deleteThumbFile��ʼɾ��"+thumbPath);
		File file = new File(thumbPath);
		if (file.exists()) { // �ļ�������ֱ�ӷ���
			//Log.e(TAG, thumbPath+"�Ҳ�������ͼ");
			flag = file.delete();
			//return flag;
		}

		
		//Դ�ļ�·��
		String sourcePath=thumbPath.replace(context.getString(R.string.Thumbnail),
				context.getString(R.string.Image));
		file = new File(sourcePath);
		if (!file.exists()) { // �ļ�������ֱ�ӷ���
			//Log.e(TAG, "ͼƬģʽ��"+sourcePath+"�Ҳ������л�����Ƶģʽ");
			String sourcePathVideo=thumbPath.replace(context.getString(R.string.Thumbnail),
					context.getString(R.string.Video));
			String sourcePathVideo1=sourcePathVideo.replace(".jpg",".3gp");
			sourcePath = sourcePathVideo1;
			file = new File(sourcePathVideo1);
			if (!file.exists())
			{
				//Log.e(TAG, sourcePathVideo1+"������Ƶ��ͼƬ���Ҳ���");
				return flag;
			}
		}
		//Log.e(TAG, "��ʼɾ��"+sourcePath);
		flag = file.delete();
		return flag;
	}
	/**  
	 *  ɾ��Դͼ��Դ��Ƶ ͬʱɾ������ͼ
	 *  @param sourcePath ����ͼ·��
	 *  @return   
	 */
	public static boolean deleteSourceFile(String sourcePath,Context context) {
		boolean flag = false;
		//ɾ����Ƶʱ����ɾ������ͼ����Ϊ��Ƶֻ������ͼ�������
		//Log.e(TAG, "deleteSourceFile��ʼɾ��"+sourcePath);
		File file = new File(sourcePath);
		if (file.exists()) { // �ļ�������ֱ�ӷ���,�������ͼ�Ƿ���ڴ�����ɾ��
			flag = file.delete();
			//return flag;
		}

		
		//����ͼ�ļ�·��
		String thumbPath=sourcePath.replace(context.getString(R.string.Image),
				context.getString(R.string.Thumbnail));
		file = new File(thumbPath);
		if (!file.exists()) { // �ļ�������ֱ�ӷ���
			thumbPath=sourcePath.replace(context.getString(R.string.Thumbnail),
					context.getString(R.string.Video));
			String thumbPath1=thumbPath.replace(".jpg",".3gp");
			file = new File(thumbPath1);
			thumbPath = thumbPath1;
			if (!file.exists())
			{
				//Log.e(TAG, thumbPath1+"������Ƶ��ͼƬ���Ҳ���");
				return flag;
			}
				
		}
		flag = file.delete();
		//Log.e(TAG, "��ʼɾ��"+thumbPath);
		return flag;
	}
	/**
	 * ��ȡSD path
	 * 
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) 
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
		else
			sdDir = Environment.getRootDirectory();
		return sdDir.toString();
	}
}