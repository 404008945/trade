package com.xishan.store.trade.server.mq;

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
public class MqConsumer {

    @Value("${rocketmq.group:defaultGroup}")
    private String group;
    @Value("${rocketmq.nameServerAddr:47.93.9.181:9876}")
    private String nameServer;
    @Value("${rocketmq.topic:updateName}")
    private String topic;

    @Autowired
    private Map<String, TopicListener> topicListenerMap;


    @PostConstruct
    public void initConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("mq-group");
        // Specify name server addresses.
        consumer.setNamesrvAddr(nameServer);
        // Subscribe one more more topics to consume.
        consumer.subscribe(topic, "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
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
                    topicListenerMap.get(tags).execute(new String(body,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //处理出现异常，获取重试次数.达到某个次数的时候可以记录日志，做补偿处理
                    int reconsumeTimes = messageExt.getReconsumeTimes();
                    if(reconsumeTimes == 3){
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }

                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //Launch the consumer instance.
        consumer.start();
    }
}
