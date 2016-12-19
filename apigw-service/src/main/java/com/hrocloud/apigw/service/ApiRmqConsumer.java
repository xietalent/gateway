package com.hrocloud.apigw.service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.hrocloud.apigw.meta.BaseCachedManager;
import net.sf.ehcache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Service
public class ApiRmqConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ApiRmqConsumer.class);

    private final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();

    @Value("${rocketmq.nameserver.address}")
    private String address;

    @PostConstruct
    public void start() throws UnknownHostException,MQClientException {

        logger.info("api rocketmq address:[{}]",address);

        InetAddress host = InetAddress.getLocalHost();
        String hostName= host.getHostName().toString().replaceAll("\\.","-");

        consumer.setConsumerGroup("apigw-consumer-"+hostName);
        consumer.setNamesrvAddr(address);
        consumer.setInstanceName("inst");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("API_NOTIFICATION", "apigw");

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                logger.info("ApiRmqConsumer Receive New Messages: " + msgs);
                if (msgs.get(0) != null) {
                    logger.info("cacheManager is being to clear");
                    try {
                        BaseCachedManager.cacheManager.clearAll();
                    }catch (CacheException cacheException) {
                        logger.error("ApiRmqConsumer cacheManager clearAll failed", cacheException);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    logger.info("cacheManager has cleared");
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }

    @PreDestroy
    public  void  shutdown() {
        consumer.shutdown();
    }
}
