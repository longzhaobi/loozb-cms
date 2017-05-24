package com.loozb.core.bind.annotation;

import com.loozb.core.Constants;

import java.lang.annotation.*;

/**
 * @Author： 龙召碧
 * @Date: Created in 2017-5-24 21:11
 */
@Target({ElementType.PARAMETER}) //用在方法参数
@Retention(RetentionPolicy.RUNTIME)//运行时
@Documented
public @interface CurrentUser {

    /**
     * 当前用户在request中的名字
     *
     * @return
     */
    String value() default Constants.CURRENT_USER;

}
