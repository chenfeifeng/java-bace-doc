# dubbo的十层结构
![图片](https://img-blog.csdn.net/20160822145751759?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

Dubbo的架构设计共分为了10层。最上面的Service层是留给实际想要使用Dubbo开发分布式服务的开发者实现业务逻辑的接口层。图中左边淡蓝色背景为服务消费方使用的接口，右边淡绿色背景为服务提供方使用的接口，位于中轴线的为双方都要用到的接口。
结合Dubbo官网，总结如下：
1. Service-服务接口层：该层与实际业务逻辑有关，根据服务消费方和服务提供方的业务设计，实现对应的接口。
2. Config-配置层：对外配置接口，以ServiceConfig和ReferenceConfig为中心，可以直接new配置类，也可以根据spring解析配置生成配置类。
3. Proxy-服务代理层：服务接口通明代理，生成服务的客户端Stub和服务端Skeleton，以ServiceProxy为中心，扩展接口ProxyFactory
4. Rsgistry-服务注册层：封装服务地址的注册和发现，以服务URL为中心，扩展接口为RegistryFactory、Registry、RegistryService，可能没有服务注册中心，此时服务提供方直接暴露服务。
5. Cluster-集群层：封装多个提供者的路由和负载均衡，并桥接注册中心，以Invoker为中心，扩展接口为Cluster、Directory、Router和LoadBalance，将多个服务提供方组合为 一个服务提供方，实现对服务消费通明。只需要与一个服务提供方进行交互。
6. Monitor-监控层：RPC调用时间和次数监控，以Statistics为中心，扩展接口MonitorFactory、Monitor和MonitorService。
7. Protocol-远程调用层：封装RPC调用，以Invocation和Result为中心，扩展接口为Protocol、Invoker和Exporter。Protocol是服务域，它是Invoker暴露和引用的主功能入口，它负责Invoker的生命周期管理。Invoker是实体域，它是Dubbo的核心模型，其他模型都是向它靠拢，或转换成它，它代表一个可执行体，可向它发起Invoker调用，它有可能是一个本地实现，也有可能是一个远程实现，也有可能是一个集群实现。
8. Exchange-信息交换层：封装请求响应模式，同步转异步，以Request和Response为中心，扩展接口为Exchanger和ExchangeChannel,ExchangeClient和ExchangeServer。
9. Transporter-网络传输层：抽象和mina和netty为统一接口，以Message为中心，扩展接口为Channel、Transporter、Client、Server和Codec。
10. Serialize-数据序列化层：可复用的一些工具，扩展接口为Serialization、ObjectInput,ObejctOutput和ThreadPool。


# dubbo 负载均衡的四种方式
1. RandomLoadBalance:随机，按权重设置随机概率。**Dubbo的默认负载均衡策略**
在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。
2. LeastActiveLoadBalance:最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。
3. RoundRobinLoadBalance:轮循，按公约后的权重设置轮循比率。存在慢的提供者累积请求问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。
4. ConsistentHashLoadBalance:一致性Hash，相同参数的请求总是发到同一提供者。
当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。


# dubbo协议注册说明
当dubbo配置多协议的时候,且服务端接口没有特别指明是什么协议,dubbo会将该接口分别注册到2个协议中去, 当被消费端消费的时候,会随机选择一个协议进行消费,  
rmi协议默认用jdk的序列化
dubbo协议默认使用hession序列化


# dubbo为什么重新实现自己的SPI
1. JDK 标准的 SPI 会一次性实例化扩展点所有实现，如果有扩展实现初始化很耗时，但如果没用上也加载，会很浪费资源。
2. 如果扩展点加载失败，连扩展点的名称都拿不到了。
3. 增加了对扩展点 IoC 和 AOP 的支持，一个扩展点可以直接 setter 注入其它扩展点。

# DUbbo-SPI机制
1. com.alibaba.dubbo.common.extension.ExtensionLoader，拓展加载器。这是 Dubbo SPI 的核心。
2.  