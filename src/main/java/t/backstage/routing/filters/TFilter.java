package t.backstage.routing.filters;

import com.alibaba.fastjson.JSONObject;

/***
 * 请求过滤工具
 * @author zhangj
 * @date 2018年9月10日 下午10:12:42
 * @email zhangjin0908@hotmail.com
 */
public interface TFilter {
	/**
	 * 判断当前请求是否允许,true表示允许,false表示拦截
	 * @param j
	 * @return
	 */
	boolean request(String path,JSONObject j);
}

