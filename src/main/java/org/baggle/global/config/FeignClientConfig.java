package org.baggle.global.config;

import org.baggle.BaggleApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackageClasses = BaggleApplication.class)
@Configuration
public class FeignClientConfig {
}
