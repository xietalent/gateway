package com.hrocloud.apigw.client.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hanzhihua on 2016/11/21.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiGroup {

    int minCode();

    int maxCode();

    String name();

    Class codeDefine();

    String owner() default "";
}
