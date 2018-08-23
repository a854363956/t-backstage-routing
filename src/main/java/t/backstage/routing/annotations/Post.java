package t.backstage.routing.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
/***
 * 用来标识此方法可以提供给外部访问
 * @author zhangj
 * @date 2018年8月20日 上午9:45:36
 * @email zhangjin0908@hotmail.com
 */
public @interface Post {
}

