package com.example.kaBank.config;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session  = request.getSession(false);


        String uri = request.getRequestURI();
        // 세션이 존재하지 않거나, 사용자 정보가 세션에 없으면 다시 로그인 화면으로
        if(session == null || request.getSession().getAttribute("userInfo") == null)
        {
        	response.sendRedirect("/pub/login");
        	return false;

        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {


    }
}