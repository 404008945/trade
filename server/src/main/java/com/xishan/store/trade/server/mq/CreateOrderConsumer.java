package com.xishan.store.trade.server.mq;

import com.xishan.store.trade.server.mq.listener.CreateOrderListener;
import com.xishan.store.trade.server.mq.listener.TopicListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CreateOrderConsumer {

    @Value("${rocketmq.group:defaultGroup}")
    private String group;
    @Value("${rocketmq.nameServerAddr:47.93.9.181:9876}")
    private String nameServer;
    @Value("${rocketmq.topic:createOrderTopic}")
    private String topic;

    @Autowired
    private CreateOrderListener createOrderListener;


    @PostConstruct
    public void initConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        // Specify name server addresses.
        consumer.setNamesrvAddr(nameServer);
        // Subscribe one more more topics to consume.
        consumer.subscribe(topic, "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setConsumeThreadMax(10);
        consumer.setConsumeThreadMin(5);
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                try {
                    byte[] body = messageExt.getBody();
                    String tags = messageExt.getTags();
                    String topic = messageExt.getTopic();
                    String keys = messageExt.getKeys();
                    log.info("body:"+new String(body, RemotingHelper.DEFAULT_CHARSET)+" tags:"+tags+" topic:"+topic+" keys:"+keys);
                    createOrderListener.execute(new String(body,"UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //Launch the consumer instance.
        consumer.start();
    }
}
