package cn.mointe.vaccination.tools;

import android.os.Environment;
/**
 * 
 * @author
 * 
 */
public class BabyImageTools {
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}
