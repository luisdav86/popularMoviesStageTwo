package com.example.luisa.popularmovies.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LuisA on 8/28/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseField {
    String name();

    boolean autoincrement() default false;

    boolean primaryKey() default false;

    boolean notNull() default false;

    boolean foreignKey() default false;

    boolean unique() default false;

    boolean onCascadeDelete() default false;
}

