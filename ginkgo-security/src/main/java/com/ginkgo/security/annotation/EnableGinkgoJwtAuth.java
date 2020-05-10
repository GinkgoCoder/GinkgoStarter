package com.ginkgo.security.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JwtAuthImporterSelector.class)
public @interface EnableGinkgoJwtAuth {
}
