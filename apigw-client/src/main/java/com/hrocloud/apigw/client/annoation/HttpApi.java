package com.hrocloud.apigw.client.annoation;

import com.hrocloud.apigw.client.define.ApiOpenState;
import com.hrocloud.apigw.client.define.SecurityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hanzhihua on 2016/11/21.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {

    String name();

    String desc();

    String detail() default "";

    SecurityType security();

    ApiOpenState state() default ApiOpenState.OPEN_TO_CLIENT;

    String owner() default "";

    String transfer() default "http";
}

