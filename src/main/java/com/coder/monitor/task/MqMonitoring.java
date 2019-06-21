package com.coder.monitor.task;


import com.coder.monitor.service.WeChatService;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * ActiveMQ：消息中间件
 * 是Apache出品，最流行的，能力强劲的开源消息总线
 * 是一个完全支持JMS1.1和J2EE 1.4规范的 JMS Provider实现
 */
@Component
@EnableScheduling
public class MqMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(MqMonitoring.class);

    private static final int SEND_NUMBER = 2;

    @Autowired
    private WeChatService weChatService;

    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
    public void checkActiveMQ() {
        logger.debug("check activeMQ begin.");
        StringBuilder message = new StringBuilder();
        Config.initDataSourceList(Config.MQ);
        if (Config.activeMQInfoList != null && Config.activeMQInfoList.size() > 0) {
            for (String url : Config.activeMQInfoList) {
                message.append(sender(url));
                message.append(receiver(url));
            }
        }

        if (!TextUtils.isBlank(message.toString())) {
            logger.debug("MQ开发发送异常消息");
            weChatService.sendMessage(message.toString());
        }
        logger.debug("check activeMq end.");

    }

    private String sender(String url) {
        String message = "";
        javax.jms.Connection connection = null; //JMS 客户端到JMS Provider 的连接
        Session session = null; //一个发送或接收消息的线程
        Destination destination;//消息的目的地;消息发送给谁.
        MessageProducer producer = null;//消息发送者
        try {
            connection = createConnection(url);
            session = createSession(connection);
            destination = createQueue(session);
            producer = createProducer(session, destination);

            sendMessage(session, producer);
            session.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            message += e.getMessage() + ",";
        } finally {
            try {
                if (null != session)
                    session.close();
                if (null != producer)
                    producer.close();
                if (null != connection)
                    connection.close();
            } catch (Throwable e) {
                logger.error(e.getMessage());
                message += e.getMessage() + ",";
            }
        }
        return message;
    }

    private String receiver(String url) {
        String errorMessage = "";
        Connection connection = null; //JMS 客户端到JMS Provider 的连接
        Session session = null; //一个发送或接收消息的线程
        Destination destination;//消息的目的地;消息发送给谁.
        MessageConsumer consumer = null;// 消费者，消息接收者
        try {
            connection = createConnection(url);
            session = createSession(connection);
            destination = createQueue(session);
            consumer = createConsumer(session, destination);
            while (true) {
                //设置接收者接收消息的时间，为了便于测试，这里设定为1s
                TextMessage message = (TextMessage) consumer.receive(1000);
                if (null != message) {
                    System.out.println("收到消息：" + message.getText());
                } else {
                    break;
                }
            }
            session.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            errorMessage += e.getMessage() + ",";
        } finally {
            try {
                if (null != session)
                    session.close();
                if (null != consumer)
                    consumer.close();
                if (null != connection)
                    connection.close();
            } catch (Throwable e) {
                logger.error(e.getMessage());
                errorMessage += e.getMessage() + ",";
            }
        }
        return errorMessage;
    }


    private static Connection createConnection(String mqUrl) throws JMSException {
        if (!TextUtils.isBlank(mqUrl))
            mqUrl = "tcp://127.0.0.1:61616";
        mqUrl += "?jms.optimizeAcknowledge=true" +
                "&jms.optimizeAcknowledgeTimeOut=30000" +
                "&jms.redeliveryPolicy.maximumRedeliveries=6";
        // ConnectionFactory ：连接工厂，JMS 用它创建连接
        ConnectionFactory connectionFactory;
        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
        connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                mqUrl);

        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection;
        // 构造从工厂得到连接对象
        connection = connectionFactory.createConnection();
        // 启动
        connection.start();
        return connection;
    }

    private static Session createSession(Connection connection) throws JMSException {
        // Session：一个发送或接收消息的线程
        Session session;
        // 获取操作连接
        session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        return session;
    }

    /**
     * LGZ 可以理解为设置消息队列名称
     *
     * @param session Session
     * @return destination Destination
     * @throws JMSException
     */
    private static Destination createQueue(Session session) throws JMSException {
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
        // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
        destination = session.createQueue("FirstQueue");
        return destination;
    }

    private static MessageProducer createProducer(Session session, Destination destination) throws JMSException {
        // MessageProducer：消息发送者
        MessageProducer producer;
        // 得到消息生成者【发送者】
        producer = session.createProducer(destination);
        // 设置不持久化
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return producer;
    }

    private static MessageConsumer createConsumer(Session session, Destination destination) throws JMSException {
        MessageConsumer consumer;
        consumer = session.createConsumer(destination);
        return consumer;
    }

    private void sendMessage(Session session, MessageProducer producer) throws Exception {
        for (int i = 1; i <= SEND_NUMBER; i++) {
            TextMessage message = session.createTextMessage("ActiveMq 发送消息测试" + i);
            // 发送消息到目的地方
            System.out.println("发送消息：" + "ActiveMq 发送的消息" + i);
            producer.send(message);
        }
    }

}
