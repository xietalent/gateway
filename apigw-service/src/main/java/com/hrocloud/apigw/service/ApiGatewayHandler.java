package com.hrocloud.apigw.service;

import com.hrocloud.apigw.client.utils.Constants;
import com.hrocloud.apigw.client.define.ConstField;
import com.hrocloud.apigw.client.define.ApiReturnCode;
import com.hrocloud.apigw.client.define.ReturnCodeContainer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
public class ApiGatewayHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayHandler.class);

    @Value("${api.url}")
    private String uri;

    @Resource
    private ApiGatewayService apiGatewayService;

    @PostConstruct
    public void init(){
        ReturnCodeContainer.findCode(ApiReturnCode.SUCCESS.getCode());
    }

    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        baseRequest.setHandled(true);

        logger.debug("request path: " + target);

        if (!target.startsWith(uri)) {
            baseRequest.setHandled(false);
            return;
        }

        request.setCharacterEncoding(ConstField.UTF8.name());

        apiGatewayService.processRequest(request, response);
    }

}
