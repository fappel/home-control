package com.codeaffine.home.control;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Schedule {

  long initialDelay() default 0l;

  long period();

  TimeUnit timeUnit() default SECONDS;
}
