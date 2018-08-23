package t.backstage.error;

/***
 * 
 * @author zhangj
 * @date 2018年8月20日 下午1:36:03
 * @email zhangjin0908@hotmail.com
 */
public class ServiceException  extends RuntimeException{

	private static final long serialVersionUID = 8875064531463702841L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
	
}

