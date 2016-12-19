package com.hrocloud.apigw.service;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**
 * Created by hanzhihua on 2016/12/11.
 */
public class ApiRmqSender {

    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        com.alibaba.rocketmq.client.producer.DefaultMQProducer producer = new DefaultMQProducer("prodgroup");
        producer.setNamesrvAddr("server4:9876");
        producer.start();
        Message msg = new Message("API_NOTIFICATION","apigw",new byte[10]);
        producer.send(msg);
        producer.shutdown();


    }
}
