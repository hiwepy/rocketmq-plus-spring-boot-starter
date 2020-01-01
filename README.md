# rocketmq-plus-spring-boot-starter

Spring Boot Starter For Rocketmq

###基于Rocketmq客户端实现的 消息订阅、发布封装。

- 1、消息发布

   a、配置简单，少量配置即可实现消息发布

- 2、消息订阅

   a、配置简单，少量配置即可实现基础的消费订阅

   b、组件实现了基于责任链的消息消费实现；可实现对具备不同 Topic、Tags、Keys 的消息对象进行专责处理；就如 Filter，该组件实现的Handler采用了同样的原理；

	 1. /Topic-DC-Output/TagA-Output/** = inDbPostHandler  该配置表示；Topic = Topic-DC-Output , Tags = TagA-Output , Keys = 任何类型 的消息对象交由 inDbPostHandler  来处理
	 2. /Topic-DC-Output/TagB-Output/** = smsPostHandler  该配置表示；Topic = Topic-DC-Output , Tags = TagB-Output , Keys = 任何类型 的消息对象交由 smsPostHandler 来处理

   通过这种责任链的机制，很好的实现了消息的分类处理；比如数据中心需要处理各个系统推送来的数据，每种处理实现都不相同；这时候就需要用到消息对象的分类处理。

  c、实现了基于 Disruptor 异步并发框架的消息异步消费实现；该实现依赖于 disruptor-biz 组件；此组件一样实现了基于责任链模式的事件分发；该实现主要应用 Disruptor 的 高性能，在大量的消息异步处理时，应该会很有效。

  d、实现了基于Spring 框架的 ApplicationEvent 机制的消息消费实现，该实现只负责将消息对象以事件形式发布处理；具体消息处理逻辑需开发者实现 ApplicationEvent 监听接口


### Maven

``` xml
<dependency>
	<groupId>com.github.hiwepy</groupId>
	<artifactId>rocketmq-plus-spring-boot-starter</artifactId>
	<version>1.0.1.RELEASE</version>
</dependency>
```