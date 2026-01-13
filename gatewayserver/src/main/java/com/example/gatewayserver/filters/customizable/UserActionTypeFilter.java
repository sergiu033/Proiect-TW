package com.example.gatewayserver.filters.customizable;

import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Order(4)
@Component
public class UserActionTypeFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserActionTypeFilter.class);

    private static final String ACTION_HEADER = "X-Request-Action-Type";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpMethod method = exchange.getRequest().getMethod();
        String path = exchange.getRequest().getURI().getPath();


        String actionType = classifyAction(method, path);

        logger.info("Request Path: {} classified as Action: {}", path, actionType);


        exchange.getRequest()
                .mutate()
                .header(ACTION_HEADER, actionType)
                .build();

        String finalActionType = actionType;

        return chain.filter(exchange).doOnSuccess(f ->{
            exchange.getResponse()
                    .getHeaders()
                    .add(ACTION_HEADER, finalActionType);
        });
    }


    private String classifyAction(HttpMethod method, String path) {
        String action = "UNKNOWN_ACTION";


        if (!path.startsWith("/api/users")) {
            return "NON_USER_SERVICE_ACTION";
        }


        if (method == HttpMethod.GET) {
            action = "READ_ONLY";
        }

        else if (method == HttpMethod.POST && path.equals("/api/users")) {
            action = "MODIFICATION_CREATE";
        }
        else if (method == HttpMethod.PUT || method == HttpMethod.DELETE) {
            action = "MODIFICATION_UPDATE_DELETE";
        }

        else if (method == HttpMethod.POST && (path.endsWith("/login") || path.endsWith("/change-password") || path.endsWith("/logout"))) {
            action = "AUTHENTICATION";
        }

        return action;
    }
}

