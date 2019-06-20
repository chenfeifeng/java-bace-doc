# NIO线程模型
下图是非常经典的NIO说明图，  
![NIO线程模型](http://dl2.iteye.com/upload/attachment/0130/5437/c519d8fd-d14e-397c-8b20-044a40b21eca.png)

1. mainReactor线程负责监听server socket，accept新连接，并将建立的socket分派给
2. subReactor：subReactor可以是一个线程，也可以是线程池（一般可以设置为CPU核数），负责多路分离已连接的socket，读写网络数据，这里的读写网络数据可类比顾客填表这一耗时动作，对具体的业务处理功能，
3. 其扔给worker线程池完成。
4. 可以看到典型NIO有三类线程，分别是mainReactor线程、subReactor线程、work线程。不同的线程干专业的事情，最终每个线程都没空着，系统的吞吐量自然就上去了  
5. 上面这幅图描述了netty的线程模型，其中mainReacotor,subReactor,Thread Pool是三个线程池。
6. mainReactor负责处理客户端的连接请求，并将accept的连接注册到subReactor的其中一个线程上；
7. subReactor负责处理客户端通道上的数据读写；
8. Thread Pool是具体的业务逻辑线程池，处理具体业务。





# String,StringBuffer与StringBuilder的区别
## String
1. String 字符串常量
2. String类是不可变类，任何对String的改变都 会引发新的String对象的生成；

## StringBuffer
1. StringBuffer 字符串变量（线程安全）；
2. StringBuffer则是可变类，任何对它所指代的字符串的改变都不会产生新的对象。
3. 使用StringBuffer类时，每次都会对StringBuffer对象本身进行操作，而不是生成新的对象并改变对象引用，所以多数情况下推荐使用StringBuffer，特别是字符串对象经常要改变的情况；


## StringBuilder
1. StringBuilder 字符串变量（非线程安全）
2. StringBuilder是5.0新增的，此类提供一个与 StringBuffer 兼容的 API，但不保证同步。该类被设计用作 StringBuffer 的一个简易替换，用在字符串缓冲区被单个线程使用的时候（这种情况很普遍）。如果可能，建议优先采用该类，因为在大多数实现中，它比 StringBuffer 要快。两者的方法基本相同

## 使用策略
1. 基本原则：如果要操作少量的数据，用String ；单线程操作大量数据，用StringBuilder ；多线程操作大量数据，用StringBuffer。
2. 
 