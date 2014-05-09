package cn.mointe.vaccination.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public class FileUtils {

	public static final long B = 1;
	public static final long KB = B * 1024;
	public static final long MB = KB * 1024;
	public static final long GB = MB * 1024;

	/**
	 * 格式化文件大小<b> 带有单位
	 * 
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		StringBuilder sb = new StringBuilder();
		String u = null;
		double tmpSize = 0;
		if (size < KB) {
			sb.append(size).append("B");
			return sb.toString();
		} else if (size < MB) {
			tmpSize = getSize(size, KB);
			u = "KB";
		} else if (size < GB) {
			tmpSize = getSize(size, MB);
			u = "MB";
		} else {
			tmpSize = getSize(size, GB);
			u = "GB";
		}
		return sb.append(twodot(tmpSize)).append(u).toString();
	}

	/**
	 * 保留两位小数
	 * 
	 * @param d
	 * @return
	 */
	public static String twodot(double d) {
		return String.format(Locale.getDefault(), "%.2f", d);
	}

	public static double getSize(long size, long u) {
		return (double) size / (double) u;
	}

	/**
	 * sd卡挂载且可用
	 * 
	 * @return
	 */
	public static boolean isSdCardMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 递归创建文件目录
	 * 
	 * @param filePath
	 * */
	public static void CreateDir(String filePath) {
		if (!isSdCardMounted())
			return;
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				Log.e("hulutan", "error on creat dirs:" + e.getStackTrace());
			}
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readTextFile(File file) throws IOException {
		String text = null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			text = readTextInputStream(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return text;
	}

	/**
	 * 从流中读取文件
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readTextInputStream(InputStream is) throws IOException {
		StringBuffer strbuffer = new StringBuffer();
		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is));
			while ((line = reader.readLine()) != null) {
				strbuffer.append(line).append("\r\n");
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return strbuffer.toString();
	}

	/**
	 * 将文本内容写入文件
	 * 
	 * @param file
	 * @param str
	 * @throws IOException
	 */
	public static void writeTextFile(File file, String str) throws IOException {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(file));
			out.write(str.getBytes());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 获取一个文件夹大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {

		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // 删除文件
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		}
	}

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static File updateDir = null;
	public static File updateFile = null;

	/**
	 * 创建文件
	 * 
	 * @param dir
	 * @param name
	 */
	public static void createFile(String dir, String name) {
		// FIXME
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + dir);
			updateFile = new File(updateDir + "/" + name + ".gif");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static File sApkPath = null;

	/**
	 * 保存到SDCard
	 * 
	 * @param fileName
	 * @param data
	 * @return
	 */
	public static boolean saveToDisk(String fileName, byte[] data) {
		boolean flag = false;
		File file = Environment.getExternalStorageDirectory();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			FileOutputStream outputStream = null;
			try {
				sApkPath = new File(file, fileName);
				outputStream = new FileOutputStream(sApkPath);
				outputStream.write(data, 0, data.length);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		return flag;
	}
}
