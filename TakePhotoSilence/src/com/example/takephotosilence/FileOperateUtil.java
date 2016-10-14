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
 * @Description:  文件操作工具类
 * @author 方菲
 * @date 2016年5月18日9:20:00 
 *  
 */
public class FileOperateUtil {
	public final static String TAG="FileOperateUtil";

	public final static int ROOT=0;//根目录
	public final static int TYPE_IMAGE=1;//图片
	public final static int TYPE_THUMBNAIL=2;//缩略图
	public final static int TYPE_VIDEO=3;//视频
	public final static int TYPE_RECYCLE_BIN = 4;//回收站 

	/**
	 *获取文件夹路径 由各部分组合而成
	 * @param type 文件夹类别
	 * @param rootPath 根目录文件夹名字 为业务流水号
	 * @return
	 * 
	 */
	public static String getFolderPath(Context context,int type,String rootPath) {
		//本业务文件主目录
		StringBuilder pathBuilder=new StringBuilder();
		//添加应用存储路径
		//pathBuilder.append(context.getExternalFilesDir(null).getAbsolutePath());
		pathBuilder.append(getSDPath());
		pathBuilder.append(File.separator);
		//添加文件总目录
		pathBuilder.append(context.getString(R.string.Files));
		pathBuilder.append(File.separator);
		//添加当然文件类别的路径
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
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件夹路径
	 * @param format 指定后缀名
	 * @param content 包含的内容,用以查找视频缩略图
	 * @return
	 */
	public static List<File> listFiles(String file,final String format,String content){
		return listFiles(new File(file), format,content);
	}
	/**
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件夹路径
	 * @param format 指定后缀名
	 * @return
	 */
	public static List<File> listFiles(String file,final String format){
		return listFiles(new File(file), format,null);
	}
	/**
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件 夹
	 * @param extension 指定后缀名
	 * @param content 包含的内容,用以查找视频缩略图
	 * @return
	 */
	public static List<File> listFiles(File file,final String extension,final String content){
		File[] files=null;
		if(file==null||!file.exists()||!file.isDirectory())
			return null;
		//找出目录下指定类型的文件
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
	 *  根据修改时间为文件列表排序
	 *  @param list 排序的文件列表
	 *  @param asc  是否升序排序 true为升序 false为降序 
	 */
	public static void sortList(List<File> list,final boolean asc){
		//按修改日期排序
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
	 * 已当前时间为名产生指定后缀的文件
	 * @param extension 后缀名 如".jpg"
	 * @return
	 */
	public static String createFileNmae(String extension){
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
		// 转换为字符串
		String formatDate = format.format(new Date());
		//查看是否带"."
		if(!extension.startsWith("."))
			extension="."+extension;
		return formatDate+extension;
	}

	/**  
	 *  删除缩略图 同时删除源图或源视频
	 *  @param thumbPath 缩略图路径
	 *  @return   
	 */
	public static boolean deleteThumbFile(String thumbPath,Context context) {
		boolean flag = false;

		//Log.e(TAG, "deleteThumbFile开始删除"+thumbPath);
		File file = new File(thumbPath);
		if (file.exists()) { // 文件不存在直接返回
			//Log.e(TAG, thumbPath+"找不到缩略图");
			flag = file.delete();
			//return flag;
		}

		
		//源文件路径
		String sourcePath=thumbPath.replace(context.getString(R.string.Thumbnail),
				context.getString(R.string.Image));
		file = new File(sourcePath);
		if (!file.exists()) { // 文件不存在直接返回
			//Log.e(TAG, "图片模式下"+sourcePath+"找不到，切换到视频模式");
			String sourcePathVideo=thumbPath.replace(context.getString(R.string.Thumbnail),
					context.getString(R.string.Video));
			String sourcePathVideo1=sourcePathVideo.replace(".jpg",".3gp");
			sourcePath = sourcePathVideo1;
			file = new File(sourcePathVideo1);
			if (!file.exists())
			{
				//Log.e(TAG, sourcePathVideo1+"按照视频和图片均找不到");
				return flag;
			}
		}
		//Log.e(TAG, "开始删除"+sourcePath);
		flag = file.delete();
		return flag;
	}
	/**  
	 *  删除源图或源视频 同时删除缩略图
	 *  @param sourcePath 缩略图路径
	 *  @return   
	 */
	public static boolean deleteSourceFile(String sourcePath,Context context) {
		boolean flag = false;
		//删除视频时是先删除缩略图，因为视频只有缩略图才能浏览
		//Log.e(TAG, "deleteSourceFile开始删除"+sourcePath);
		File file = new File(sourcePath);
		if (file.exists()) { // 文件不存在直接返回,检查缩略图是否存在存在则删除
			flag = file.delete();
			//return flag;
		}

		
		//缩略图文件路径
		String thumbPath=sourcePath.replace(context.getString(R.string.Image),
				context.getString(R.string.Thumbnail));
		file = new File(thumbPath);
		if (!file.exists()) { // 文件不存在直接返回
			thumbPath=sourcePath.replace(context.getString(R.string.Thumbnail),
					context.getString(R.string.Video));
			String thumbPath1=thumbPath.replace(".jpg",".3gp");
			file = new File(thumbPath1);
			thumbPath = thumbPath1;
			if (!file.exists())
			{
				//Log.e(TAG, thumbPath1+"按照视频和图片均找不到");
				return flag;
			}
				
		}
		flag = file.delete();
		//Log.e(TAG, "开始删除"+thumbPath);
		return flag;
	}
	/**
	 * 获取SD path
	 * 
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) 
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		else
			sdDir = Environment.getRootDirectory();
		return sdDir.toString();
	}
}