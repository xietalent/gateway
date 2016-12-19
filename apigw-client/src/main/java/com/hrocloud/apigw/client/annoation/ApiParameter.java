package com.hrocloud.apigw.client.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hanzhihua on 2016/11/21.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParameter {

    boolean required();

    boolean rsaEncrypted() default false;

    boolean aesEncrypted() default false;

    String name();

    String defaultValue() default "";

    String verifyRegex() default "";

    String verifyMsg() default "";

    String desc();
}

