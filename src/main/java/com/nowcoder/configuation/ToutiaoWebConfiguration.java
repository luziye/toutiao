package com.nowcoder.configuation;

import com.nowcoder.intercepter.LoginRequiredIntercepter;
import com.nowcoder.intercepter.PassportIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by luziye on 2018/1/12.
 */
@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private PassportIntercepter passportIntercepter;
    @Autowired
    private LoginRequiredIntercepter loginRequiredIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportIntercepter);
        registry.addInterceptor(loginRequiredIntercepter).addPathPatterns("/setting");
        super.addInterceptors(registry);
    }
}
