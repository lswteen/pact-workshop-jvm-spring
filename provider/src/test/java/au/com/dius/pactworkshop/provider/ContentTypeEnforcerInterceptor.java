package au.com.dius.pactworkshop.provider;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentTypeEnforcerInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        response.setHeader("Authorization", "Bearer 1957-08-21T20:33");
    }
}
