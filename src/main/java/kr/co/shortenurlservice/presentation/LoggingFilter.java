package kr.co.shortenurlservice.presentation;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest httpServletRequest){
            CachedBodyHttpServletRequest wrappedReqeust = new CachedBodyHttpServletRequest(httpServletRequest);
            String url = wrappedReqeust.getRequestURI();
            String method = wrappedReqeust.getMethod();
            String body =  wrappedReqeust.getReader().lines().collect(Collectors.joining());
            log.info("input Reqeust: URL={}, Method={}, Body={}", url,method,body);

            filterChain.doFilter(wrappedReqeust,response);
        }else{
            filterChain.doFilter(request,response);
        }

    }
}
