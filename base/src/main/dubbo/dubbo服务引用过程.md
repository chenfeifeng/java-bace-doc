# 服务引用
1. spring根据dubbo.xml的<dubbo:reference>配置,生成ReferenceBean.
2. 生成ReferenceBean的过程中，调用ReferenceConfig获取对应的实例
   + 首先通过ReferenceConfig类的private void init()方法会先检查初始化所有的配置信息后，
   + 调用private T createProxy(Map<String, String> map)创建代理，消费者最终得到的是服务的代理
   + 在createProxy接着调用Protocol接口实现的<T> Invoker<T> refer(Class<T> type, URL url)方法生成Invoker实例(如上图中的红色部分)，这是服务消费的关键。
   + 接下来把Invoker通过ProxyFactory代理工厂转换为客户端需要的接口(如：HelloWorld)，创建服务代理并返回。
   + 在调用代理类接口的时候，会通过netty去访问提供者的接口（中间涉及序列化）


# dubbo接口调用
1. dubbo协议会启动netty服务（其他协议有不同服务，比如rmi以https调用）
2. 消费者和提供者之间都是直连通过ip进行调用访问
3. 连接zk，zk只是相当于一个节点注册中心，所有信息将会缓存到本地
   + 在消费者调用接口的时候，dubbo会在本地提供者缓存中，通过负载均衡获取一个提供者的ip，进行调用访问。


# 服务引用
1. 收集配置的参数,例：
```
methods=hello,
timestamp=1443695417847,
dubbo=2.5.3
application=consumer-of-helloService
side=consumer
pid=7748
interface=com.demo.dubbo.service.HelloService
```

2. 从注册中心引用服务，创建出Invoker对象，如果是单个注册中心，代码如下：
```
Protocol refprotocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

invoker = refprotocol.refer(interfaceClass, url);
```
```
registry://127.0.0.1:2181/com.alibaba.dubbo.registry.RegistryService?
application=consumer-of-helloService&
dubbo=2.5.3&
pid=8292&
registry=zookeeper&
timestamp=1443707173909&

refer=
	application=consumer-of-helloService&
	dubbo=2.5.3&
	interface=com.demo.dubbo.service.HelloService&
	methods=hello&
	pid=8292&
	side=consumer&
	timestamp=1443707173884&
```
前面的信息是注册中心的配置信息，如使用zookeeper来作为注册中心

后面refer的内容是要引用的服务信息，如引用HelloService服务

使用协议Protocol根据上述的url和服务接口来引用服务，创建出一个Invoker对象

3. 使用ProxyFactory创建出一个接口的代理对象，该代理对象的方法的执行都交给上述Invoker来执行，代码如下：
```
ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
proxyFactory.getProxy(invoker);
```



# 远程调用

1.  consumer 端在进行服务引用的时候。consumer 首先会根据配置 Protocol(协议) 创建 Invoke 调用对象，它代表一个可执行体，可向它发起 invoke 调用，它有可能是一个本地的实现，也可能是一个远程的实现，也可能一个集群实现。然后再使用 ProxyFactory 接口创建代理对象，进行远程调用。
2.  创建的代理对象为 InvokeInvocationHandler，然后它组合了一个 MockClusterInvoker 对象。 dubbo 里面的服务治理就是通过它来完成的。这个里面就涉及到服务治理也就是集群容错。
    + 首先通过 MockClusterInvoke 将多个 Invoker 伪装成一个Invoker，这样其它人只要关注Protocol 层 Invoker 即可，加上 Cluster 或者去掉 Cluster 对其它层都不会造成影响，因为只有一个提供者时，是不需要Cluster的。
    + 接着 Directory 服务的主要作用是通过注册中心推送变更。当 Provider 暴露新的接口服务，或者失效掉接口服务的时候就会动态的更新 Invoke。
    + 然后 Router 服务配置可以过滤 Directory 服务里面注册的接口服务，可以通过路由规则从多个 Invoke 里面选择出子集。
    + 最后 LoadBalance 负责从过滤后的 Invoke 列表中通过负载均衡算法选择一个具体的 Invoke 来用于本次服务调用。



# consumer调用返回情况
通过集群容错最终选择一个合适的 Invoke 通过 netty 直联调用 provider 的服务。众所周知， netty 是基于 Java Nio 的 Reactor 模型的异步网络通信框架，所以 dubbo 在 consumer 端把异步变成了同步。

1. 同步（异步变同步(默认)）
   + ResponseFuture模式异步转同步，等待响应返回
2. 异步
   + 异步且无返回值
     + Oneway返回空RpcResult
   + 异步且有返回值
     + 直接返回空RpcResult, ResponseFuture回调