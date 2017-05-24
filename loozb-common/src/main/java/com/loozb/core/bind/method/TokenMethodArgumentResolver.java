package com.loozb.core.bind.method;

import com.loozb.core.bind.annotation.Token;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Author： 龙召碧
 * @Date: Created in 2017-5-24 21:13
 */
public class TokenMethodArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Token.class);
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Token tokenAnnotation = parameter.getParameterAnnotation(Token.class);
        return webRequest.getAttribute(tokenAnnotation.value(), NativeWebRequest.SCOPE_REQUEST);
    }
}
