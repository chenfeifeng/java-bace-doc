# dubbo
## dubbo原理
主要应该讲述，dubbo的远程服务暴露和服务调用说明



## dubbo的spi机制


## 看过源码，那说下服务暴露的流程？
服务的暴露起始于 Spring IOC 容器刷新完毕之后，会根据配置参数组装成 URL， 然后根据 URL 的参数来进行本地或者远程调用。
会通过 proxyFactory.getInvoker，利用 javassist 来进行动态代理，封装真的实现类，然后再通过 URL 参数选择对应的协议来进行 protocol.export，默认是 Dubbo 协议。
在第一次暴露的时候会调用 createServer 来创建 Server，默认是 NettyServer。
然后将 export 得到的 exporter 存入一个 Map 中，供之后的远程调用查找，然后会向注册中心注册提供者的信息。




