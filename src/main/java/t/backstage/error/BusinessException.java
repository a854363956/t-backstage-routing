package t.backstage.error;

/***
 * 业务异常,应该使用此异常来进行抛出错误信息
 * @author zhangj
 * @date 2018年8月20日 下午4:15:29
 * @email zhangjin0908@hotmail.com
 */
public class BusinessException  extends RuntimeException{
	private static final long serialVersionUID = -3928735668741049379L;
	public BusinessException(int code) {
		super("TX-"+code+":"+t.backstage.models.context.ContextUtils.getLanguage(code));
	}
	public BusinessException(int code,Object...rep) {
		super(String.format(("TX-"+code+":"+t.backstage.models.context.ContextUtils.getLanguage(code)),rep));
	}
}

