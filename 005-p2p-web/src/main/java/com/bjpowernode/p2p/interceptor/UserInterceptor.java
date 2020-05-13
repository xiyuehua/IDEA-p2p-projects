package com.bjpowernode.p2p.interceptor;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.user.User;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:UserInterceptor
 * Package:com.bjpowernode.p2p.interceptor
 * Description
 *
 * @Date:2020/4/216:49
 * @author:xyh
 */
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (ObjectUtils.allNotNull(sessionUser)) {
            return true;
        } else {
            response.sendRedirect(request.getContextPath()+"/loan/page/login");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
