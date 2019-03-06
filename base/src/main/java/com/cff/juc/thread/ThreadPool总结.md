# ThreadPoolExecutor
## ThreadPoolExecutor重要参数
1. corePoolSize:核心线程数
2. maximumPoolSize:最大线程数
3. keepAliveTime：当线程池中线程数量超过corePoolSize时，允许等待多长时间从workQueue中拿任务.(线程存活时间。当线程数大于core数，那么超过该时间的线程将会被终结。)
4. unit：keepAliveTime对应的时间单位，为TimeUnit类
5. workQueue：工作队列，可控大小
6. threadFactory：线程池采用，该线程工厂创建线程池中的线程。
7. handler：为RejectedExecutionHandler，当线程线中线程超过maximumPoolSize时采用的，拒绝执行处理器。
---
8. executor.getPoolSize() 获取线程池中的线程数量
9. executor.getActiveCount() 获取正在处理任务的数量
10. executor.getLargestPoolSize() 获取线程池最大线程的数量



## ThreadPoolExecutor 基本工作原理
1. 初始化线程池，如果线程数没有达到coreSize，那么就会新建一个线程，并绑定该任务，直到数量到达coreSize前都不会重用之前的线程
2. 线程数量大于coreSize，提交的任务都会放到一个等待队列（Workqueue）中进行等待，线程池中的线程会使用 take()阻塞的从等待队列拿任务
3. 当队列有界，并且线程不能及时取走队列中的任务，等待队列有可能会满，那么线程池将会创建临时线程来执行任务
4. 临时线程 通过Worker -> runWorker -> getTask -> poll(keepAliveTime，timeUnit)来执行任务，如果到了keepAliveTime还取不到，
那么会被回收掉，临时线程的数量不能大于（maxPoolSize - coreSize）,临时线程最终会被processWorkerExit方法进行清除。
5. 当线程数到达maxSize后，将会执行拒绝策略RejectedExecutionHandler，包括抛异常，静默拒绝，抛弃最old任务，使用原远程执行等策略



## 线程失效过程
### 哪些线程会失效
1. 当allowCoreThreadTimeOut为false时候，临时线程会失效
2. 当allowCoreThreadTimeOut为true时候，核心线程也会失效

### 什么时候失效
1. 当runWokder异常或者添加woker失败的时候，会执行processWorkerExit方法
2. 该方法会将线程Worker对象进行移除。
3. 如果是runWokder方法正常执行完毕，会再次校验，最小线程数是多少。如果任务队列不为空，至少会存在一个线程
   + 线程池中有线程在处理任务，则直接返回，不做处理
   + 线程池中没有线程处理任务队列中的任务，创建一个线程处理任务队列中的任务



## 线程复用情况
1. 任务进入线程池，首先会校验是否允许创建线程。假如此时工作线程数量是小于核心线程数的，那么此时将创建新的线程。
2. 线程将会存在于Worker对象中，一个Worker会对应一个线程，当这个线程启动的时候，首先会去处理立即赋予给他的任务firstTask,当改任务处理完毕之后，会进行循环地从workQueue中拉取任务执行。
3. 此时就行程了一种现象：当workQueue中有未执行的任务时，不会去创建临时线程，而是优先以core线程去触发任务。


## 线程池中对通过execute/submit，新增的task处理方式：
1. 当线程池内有效线程数 <= corePoolSize,此时会新建一个Work，即新建线程。
2. 当线程池内有效线程数 > corePoolSize,workqueue未满，则添加新的任务到workQueue中，根据FIFO（先进先出）执行。
3. 当线程池内有效线程数量 > corePoolSize,workqueue已满，则会进行临时建线程执行。




## 部分函数区别说明
### shutdown和shutdownnow
1. shutdown() 会等待执行线程处理完之后，进入SHUTDOWN状态。会处理以前提交的任务。
2. shutdownnow() 会试图停止所有正在执行的活动任务，暂停处理正在等待的任务，并返回等待执行的任务列表

### execute和submit
1. execute():相当于一个普通的线程操作，无返回值，无法确认正状态
2. submit():相当于对execute的包装，包装成为一个future方法，管理状态和返回值
3. submit其实是对execute的包装，最终调用的还是execute方法
```
    RunnableFuture<Void> ftask = newTaskFor(task, null);
    //这就是execute中的execute，是不是很像线程中callable/future包装start。。。
    execute(ftask); 
    return ftask;
```


# Executors相关小结






# 参考资料

[JUC 之ThreadPoolExecutor实现原理分析](https://mp.weixin.qq.com/s?__biz=Mzg5ODAwMTEyNA==&mid=2247483674&idx=1&sn=e491934e0c702f11765fffc569043e33&scene=21#wechat_redirect)

[怎么才算掌握了JDK中的线程池](https://mp.weixin.qq.com/s/vzKMNagVpweurpGxEJdStQ)