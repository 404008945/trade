package com.xishan.store.trade.server.mq.listener;

public interface TopicListener {
    void execute(String jsonBody);
}
