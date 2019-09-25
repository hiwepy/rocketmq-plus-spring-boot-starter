package org.apache.rocketmq.spring.boot;

import java.util.Date;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

//生产者
public class SimpleProducer {

	public static void main(String[] args) throws MQClientException, InterruptedException {
		/*
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */
		DefaultMQProducer producer = new DefaultMQProducer("rmq-group");
		//nameserver服务,多个以;分开
		producer.setNamesrvAddr("127.0.0.1:9876");
		producer.setInstanceName("rmq-instance");
		/** 
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br> 
         * 注意：切记不可以在每次发送消息时，都调用start方法 
         */  
        producer.start();
        
        /** 
         * 下面这段代码表明一个Producer对象可以发送多个topic，多个tag的消息。 
         * 注意：send方法是同步调用，只要不抛异常就标识成功。但是发送成功也可会有多种状态，<br> 
         * 例如消息写入Master成功，但是Slave不成功，这种情况消息属于成功，但是对于个别应用如果对消息可靠性要求极高，<br> 
         * 需要对这种情况做处理。另外，消息可能会存在发送失败的情况，失败重试由应用来处理。 
         */  
		try {
			for (int i = 0; i < 3; i++) {
				
				//通过topic订阅消息，tag过滤消息  
				Message msg = new Message("TopicA", // topic
						"TagA",// tag 消息标签，只支持设置一个Tag（服务端消息过滤使用）  
                        "OrderID001",// key 消息关键词，多个Key用KEY_SEPARATOR隔开（查询消息使用） 
						(new Date() + "Hello RocketMQ ,QuickStart" + i).getBytes()// body
				);
				SendResult sendResult = producer.send(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/** 
         * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己 
         * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法 
         */  
        // producer.shutdown();  
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
            public void run() {  
                producer.shutdown();  
            }  
        }));  
        System.exit(0);  
	}
}
