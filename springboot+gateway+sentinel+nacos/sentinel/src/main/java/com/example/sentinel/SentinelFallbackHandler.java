package com.example.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 自定义限流异常处理
 *
 * @author ruoyi
 */
public class SentinelFallbackHandler implements WebExceptionHandler
{
    private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange,Integer code,String msg)
    {
        return webFluxResponseWriter(exchange.getResponse(),msg, code);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex)
    {
        ResCodeEnum resCodeEnum = null;
        if (ex instanceof FlowException) {
            resCodeEnum = ResCodeEnum.SENTINEL_FLOW_ERROR;
        } else if (ex instanceof DegradeException) {
            resCodeEnum = ResCodeEnum.SENTINEL_DEGRADE_ERROR;
        } else if (ex instanceof ParamFlowException) {
            resCodeEnum = ResCodeEnum.SENTINEL_PARAM_FLOW_ERROR;
        } else if (ex instanceof SystemBlockException) {
            resCodeEnum = ResCodeEnum.SENTINEL_SYSTEM_ERROR;
        } else if (ex instanceof AuthorityException) {
            resCodeEnum = ResCodeEnum.SENTINEL_AUTHORITY_ERROR;
        }

        ResCodeEnum finalResCodeEnum = resCodeEnum;
        return handleBlockedRequest(exchange, ex).flatMap(response -> writeResponse(response, exchange,finalResCodeEnum.getCode(), finalResCodeEnum.getMsg()));
    }

    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable)
    {
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }

    /**
     * 设置webflux模型响应
     *
     * @param response ServerHttpResponse
     * @param value 响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value,Integer code)
    {
        return webFluxResponseWriter(response, HttpStatus.OK, value, code);
    }

    /**
     * 设置webflux模型响应
     *
     * @param response ServerHttpResponse
     * @param status http状态码
     * @param code 响应状态码
     * @param value 响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, HttpStatus status, Object value, int code)
    {
        return webFluxResponseWriters(response, MediaType.APPLICATION_JSON_VALUE, status, value, code);
    }

    /**
     * 设置webflux模型响应
     *
     * @param response ServerHttpResponse
     * @param contentType content-type
     * @param status http状态码
     * @param code 响应状态码
     * @param value 响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriters(ServerHttpResponse response, String contentType, HttpStatus status, Object value, int code)
    {
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        R<?> result = R.fail(code, value.toString());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }
}
