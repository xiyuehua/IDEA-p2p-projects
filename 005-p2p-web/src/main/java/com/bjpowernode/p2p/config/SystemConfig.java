package com.bjpowernode.p2p.config;

import com.bjpowernode.p2p.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName:SystemConfig
 * Package:com.bjpowernode.p2p.config
 * Description
 *
 * @Date:2020/4/216:52
 * @author:xyh
 */
@Configuration
public class SystemConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludePath = {
                "/loan/loan",
                "/loan/loanInfo",
                "/loan/page/register",
                "/loan/page/login",
                "/loan/getMessageCode"
        };
        String[] addPath = {

                "/loan/**",
                "/page/realName",
                "/user/realName"
        };

        registry.addInterceptor(new UserInterceptor()).excludePathPatterns(excludePath).addPathPatterns(addPath);
    }
}
