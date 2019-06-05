# Netty远程服务暴露


## ServiceConfig#doExportUrls-加载注册中心
1. 首先通过方法loadRegistries(true)来加载注册中心。
2. 在方法checkRegistry()方法中判断如果 xml 里面没有配置注解中心，从 dubbo 的 properties 文件中获取(默认是dubbo.properties)。
3. 然后会返回List<URL> 作为配置信息的统一格式，所有扩展点都通过传递 URL 携带配置信息。

## RegistryProtocol#export 
根据这个类名我们就可以推测出这个类具有的功能，具有 Registry(注册)与 Protocol (协议–服务暴露)在这个方法里面就包括上面提到的三个逻辑：
1. dubbo 远程暴露 – Netty 暴露服务，通过配置的协议根据 SPI 获取到对应的 Protocol对象，这里是 DubboProtocol，对象。
2. dubbo 远程暴露 – Zookeeper 连接 服务注册，通过RegistryFactory根据 SPI 获取对应的 Registry 对象(ZookeeperRegistry)，然后注册到注册中心上面去，供 consumer调用
3. dubbo 远程暴露 – Zookeeper 注册 & 订阅，它会把创建2个节点：
   + 一个是/dubbo/服务全类名/provider/...节点提供给服务消费方查看节点信息；
   + 二是/dubbo/服务全类名/configurators/...节点提供给服务方 watch(监控) dubbo-admin 对于服务的修改。比如：服务权重。

# DubboProtocol#export
1. 根据传入的Invoker中的 URL 通过serviceKey(url)获取到 serviceKey，它的格式为：com.alibaba.dubbo.demo.DemoService:20880. 
2. 以传的Invoker、第 1 步生成的 key 和 Map<String, Exporter<?>> exporterMap 生成 DubboExporter，并以第 1 步生成的 key 为索引，把生成的 DubboExporter添加到Map<String, Exporter<?>> exporterMap中 
3. 根据 URL 判断是不是服务端，如果是服务端并且从Map<String, ExchangeServer> serverMap获取到的 ExchangeServer 为空，就通过DubboProtocol#createServer 创建服务，达到服务暴露的目的。返回DubboExporter对象


# DubboProtocol#createServer
1. dubbo 远程服务(Provider)暴露最终其实就是创建一个 Netty Serve 服务，
2. 然后在 dubbo 在服务引用的时候创建一个 Netty Client 服务
3. 其实 dubbo 远程通信的原理其实就是基于 Socket 的远程通信
![图片](https://img-blog.csdn.net/20180306232416889?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMjQxMDczMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)


# Zk连接
## RegistryProtocol#getRegistry
1. 把 URL 里面的 protocol 设置成 <dubbo:registry address="zookeeper://127.0.0.1:2181"里面设置的 zookeeper。 
2. 通过 RegistryFactory 的 SPI 接口 RegistryFactory$Adaptive 根据 URL 里面的 protocol 获取 zookeeper 的注册工厂调用 getRegistry获取 zookeeper 注册中心 – ZookeeperRegistry

## AbstractRegistryFactory.getRegistry
1. URL 添加 参数interface 为 com.alibaba.dubbo.registry.RegistryService，并去掉 export 参数
2. ZookeeperRegistryFactory#createRegistry创建 ZookeeperRegistry 实例 
3. AbstractRegistry#init()中调用loadProperties()，在以下目录中保存注册信息(以window为例)
4. FailbackRegistry#init()中故障回复注册类中创建线程池 ScheduledExecutorService 检测并连接注册中心，如果失败就就调用 retry()进行重连，高可用
5. zookeeperTransporter#connect()由于 ZookeeperTransporter 是一个 @SPI 接口并且 @Adaptive，所以会生成一个 ZookeeperTransporter$Adaptive，并且是由RegistryFactory这个 SPI 接口创建的时候通过 SPI 依赖注入创建 ZookeeperRegistryFactory 对象的时候依赖注入的。


# ZK注册和订阅
## ZookeeperRegistry#register
这个就是把服务信息注册到 Zookeeper 上面去。
1. AbstractRegistry#register添加注册Set<URL> registered(已注册的URL)，用于失败重试。 
2. ZookeeperRegistry#doRegister通过上篇 blog 讲解的获取到的 ZookeeperClient 把服务信息注册到 Zookeeper 上。此时的 URL 为：
```
dubbo://192.168.75.1:20880/com.alibaba.dubbo.demo.DemoService?
anyhost=true&application=demo-provider&dubbo=2.0.0&generic=false&
interface=com.alibaba.dubbo.demo.DemoService&methods=sayHello&pid=2076&
side=provider&timestamp=1522237459900
```
dubbo 会基于这个 URL 生成一个新的URL，生成规则为：
```
"/dubbo/" + url里面的interface的值 + "/providers/" + URL.encode(url)
```
生成后的值 :
```
/dubbo/com.alibaba.dubbo.demo.DemoService/providers/
dubbo%3A%2F%2F192.168.75.1%3A20880%2Fcom.alibaba.dubbo.demo.DemoService%3Fanyhost%3Dtrue%26application%3Ddemo-provider%26dubbo%3D2.0.0%26generic%3Dfalse%26interface%3Dcom.alibaba.dubbo.demo.DemoService%26methods%3DsayHello%26pid%3D2076%26side%3Dprovider%26timestamp%3D1522237459900
```
3. 然后在 Zookeeper 上面把 /dubbo/com.alibaba.dubbo.demo.DemoService/providers/ 创建成持久化节点，而后面部分的 URL 就会创建成临时节点。
4. 把服务提供者信息创建成临时节点的好处就是如果当前服务挂掉，这个节点就会自动删除。这样失效服务就可以自动剔除。
5. 
```
Zookeeper 持久化节点 和临时节点有什么区别？ 
持久化节点：一旦被创建，触发主动删除掉，否则就一直存储在ZK里面。 
临时节点：与客户端会话绑定，一旦客户端会话失效，这个客户端端所创建的所有临时节点都会被删除。
```

## ZookeeperRegistry#subscribe
通过订阅 Zookeeper 上面的的节点信息变更， 可以通过 dubbo-admin来修改服务路由规则、权重等。
1. 创建一个 NotifyListener 实例 OverrideListener， 当收到服务变更通知时触发。 
2. 在 Zookeeper 注册中心创建持久化节点/dubbo/com.alibaba.dubbo.demo.DemoService/configurators，用于接收 dubbo-admin这个客户端上对于集群的服务治理。
3. 启动加入订阅/dubbo/com.alibaba.dubbo.demo.DemoService/configurators，如果该节点信息发生改变，就会交给FailbackRegistry.notify处理。


## FailbackRegistry#notify
1. 把服务端的注册 url 信息更新到本地缓存(AbstractRegistry#saveProperties)。
2. 调用传入的OverrideListener#notify，如果修改了 URL 信息，调用RegistryProtocol.this.doChangeLocalExport(originInvoker, newUrl)重新暴露当前服务。

[!图片](https://img-blog.csdn.net/20180328204716237?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTI0MTA3MzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)





---
1. 当dubbo进行服务暴露的时候。
2. 首先是通过 Protocol 的 SPI 接口 与 获取到 RegistryProtocol 实例。
3. 然后通过 dubbo 的 aop把这个对象先代理到 ProtocolListenerWrapper， 
4. 然后再将代理后的对象代理到 ProtocolFilterWrapper中。
 