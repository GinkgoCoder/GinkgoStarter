package com.ginkgo.data.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DataImportSelector.class)
public @interface EnableGinkgoData {
}
