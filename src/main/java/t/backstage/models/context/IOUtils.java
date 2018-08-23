package t.backstage.models.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/***
 * 
 * @author zhangj
 * @date 2018年8月20日 下午2:08:01
 * @email zhangjin0908@hotmail.com
 */
public class IOUtils {
	/**
	 * 将Input流转换为字符串
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readInputStream(java.io.InputStream in ) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}
}

