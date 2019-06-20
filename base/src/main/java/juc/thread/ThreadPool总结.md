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
1. 当runWokder异常或者添加woker失败的时候，会执行processWorkerExit方法,该方法会将线程Worker对象进行移除。
2. 如果是runWokder方法正常执行完毕，会再次校验，最小线程数是多少。如果任务队列不为空，至少会存在一个线程
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


## ThreadPoolExecutor状态
### 状态说明
1. RUNING:线程池处在RUNNING状态时，能够接收新任务，以及对已添加的任务进行处理
2. SHUTDOWN:线程池处在SHUTDOWN状态时，不接收新任务，但能处理已添加的任务
3. STOP:线程池处在STOP状态时，不接收新任务，不处理已添加的任务，并且会中断正在处理的任务
4. TIDYING:当所有的任务已终止，ctl记录的"任务数量"为0，线程池会变为TIDYING状态。
当线程池变为TIDYING状态时，会执行钩子函数terminated()。
terminated()在ThreadPoolExecutor类中是空的，若用户想在线程池变为TIDYING时，进行相应的处理；可以通过重载terminated()函数来实现
5. TERMINATED:线程池彻底终止，就变成TERMINATED状态。

### 状态切换
1. RUNING:线程池的初始化状态是RUNNING。换句话说，线程池被一旦被创建，就处于RUNNING状态！
2. SHUTDOWN:调用线程池的shutdown()接口时，线程池由RUNNING -> SHUTDOWN。
3. STOP:调用线程池的shutdownNow()接口时，线程池由(RUNNING or SHUTDOWN ) -> STOP。
4. TIDYING:当线程池在SHUTDOWN状态下，阻塞队列为空并且线程池中执行的任务也为空时，就会由 SHUTDOWN -> TIDYING。
           当线程池在STOP状态下，线程池中执行的任务为空时，就会由STOP -> TIDYING。
5. TERMINATED:线程池处在TIDYING状态时，执行完terminated()之后，就会由 TIDYING -> TERMINATED。


## 拒绝策略
### 执行拒绝策略的情景
当任务添加到线程池中之所以被拒绝，可能是由于：
1. 线程池异常关闭。
2. 任务数量超过线程池的最大限制。

### ThreadPoolExecutor自带的四种拒绝策略
1. AbortPolicy:当任务添加到线程池中被拒绝时，它将抛出 RejectedExecutionException 异常。
2. CallerRunsPolicy: 当任务添加到线程池中被拒绝时，会在线程池当前正在运行的Thread线程池中处理被拒绝的任务。
3. DiscardOldestPolicy:当任务添加到线程池中被拒绝时，线程池会放弃等待队列中最旧的未处理任务，然后将被拒绝的任务添加到等待队列中。
4. DiscardPolicy:当任务添加到线程池中被拒绝时，线程池将丢弃被拒绝的任务。



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


## 相关ms小问题
1. 当一个线程池的，corePoolSize为5，maximumPoolSize为10,依次提交6个耗时较长的任务,此时会有多少个线程，线程池是如何执行的？
   + 前面5ge任务提交，均会生成一个core线程执行任务
   + 当低6个任务进入的时候，假如此时设置了BlockingQueue ，则会吧任务丢进BlockingQueue里面
   + 即还是5个core线程在执行任务
2. 如何设计计算线程池的参数？
```
tasks ：每秒的任务数，假设为500~1000个
taskcost：每个任务花费时间，假设为0.1s
responsetime：系统允许容忍的最大响应时间，假设为1s
---
1. corePoolSize = 每秒需要多少个线程处理？ 
  threadcount = tasks/(1/taskcost) =tasks*taskcost =  (500~1000)*0.1 = 50~100 个线程。corePoolSize设置应该大于50
  根据8020原则，如果80%的每秒任务数小于800，那么corePoolSize设置为80即可。
2. queueCapacity = (coreSizePool/taskcost)*responsetime
 计算可得 queueCapacity = 80/0.1*1 = 80。意思是队列里的线程可以等待1s，超过了的需要新开线程来执行
 切记不能设置为Integer.MAX_VALUE，这样队列会很大，线程数只会保持在corePoolSize大小，当任务陡增时，不能新开线程来执行，响应时间会随之陡增。
3. maxPoolSize = (max(tasks)- queueCapacity)/(1/taskcost)（最大任务数-队列容量）/每个线程每秒处理能力 = 最大线程数
   算可得 maxPoolSize = (1000-80)/10 = 92
4. rejectedExecutionHandler：根据具体情况来决定，任务不重要可丢弃，任务重要则要利用一些缓冲机制来处理
5. keepAliveTime和allowCoreThreadTimeout：采用默认通常能满足
 
```
4. 参数计算公式
    + tasks ：每秒的任务数;taskcost：每个任务花费时间;responsetime：系统允许容忍的最大响应时间,队列等待时间
    + 核心线程数量=任务数量/每秒处理任务数量->再根据8020原则，设置80%。(threadcount = tasks/(1/taskcost))
    + 队列大小=(核心线程数量/每个任务花费时间)*队列等待时间(queueCapacity = (coreSizePool/taskcost)*responsetime)
    + 最大线程数量=(最大任务数量-队列大小)/(每秒处理任务数量)(maxPoolSize = (max(tasks)- queueCapacity)/(1/taskcost))

5. 线程池大小设置
    + 如果是CPU密集型应用，则线程池大小设置为N+1
    + 如果是IO密集型应用，则线程池大小设置为2N+1

6. 线程池 coreSize 2  maxSize 4 ，此处不考虑队列，创建 满了a b c d4个线程，且所有任务线程同时处理完毕，  问最后回收哪两个线程？
    + 回收线程不确定，但是最终只留下 coreSize 个线程
    + https://mp.weixin.qq.com/s?__biz=MzU1OTgyMDc3Mg==&mid=2247483834&idx=1&sn=db7cec29acba533cce79ec476b224a82
    + 线程池在 Worker -> runWorker -> getTask() 流程的 getTask() 过程中去回收空闲线程






# Executors四种线程池
1. Executors.newCachedThreadPool()
    + 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
> new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                         60L, TimeUnit.SECONDS,
                         new SynchronousQueue<Runnable>());
> 该线程池中，核心线程数为0，则每次创建线程为临时线程
2. Executors.newFixedThreadPool(int nThreads)
    + 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
> new ThreadPoolExecutor(nThreads, nThreads,
                                        0L, TimeUnit.MILLISECONDS,
                                        new LinkedBlockingQueue<Runnable>());
3. Executors.newScheduledThreadPool()
    + 创建一个定长线程池，支持定时及周期性任务执行。
4. Executors.newSingleThreadExecutor()
    + 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
> new ThreadPoolExecutor(1, 1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>())



# Runnable、Callable、Future、FutureTask
## RUnnable
1. 其中Runnable应该是我们最熟悉的接口，它只有一个run()函数，用于将耗时操作写在其中，该函数没有返回值。
2. 然后使用某个线程去执行该runnable即可实现多线程，Thread类在调用start()函数后就是执行的是Runnable的run()函数。

## Callable
1. Callable与Runnable的功能大致相似，Callable中有一个call()函数，但是call()函数有返回值，而Runnable的run()函数不能将结果返回给客户程序。

## Future
1. Executor就是Runnable和Callable的调度容器，Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果、设置结果操作。get方法会阻塞，直到任务返回结果(Future简介)。

## FutureTask
1. FutureTask则是一个RunnableFuture<V>，而RunnableFuture实现了Runnbale又实现了Futrue<V>这两个接口，
2. 由于FutureTask实现了Runnable，因此它既可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行。并且还可以直接通过get()函数获取执行结果，该函数会阻塞，直到结果返回
3. 因此FutureTask既是Future、Runnable，又是包装了Callable( 如果是Runnable最终也会被转换为Callable )， 它是这两者的合体。


# 参考资料

[JUC 之ThreadPoolExecutor实现原理分析](https://mp.weixin.qq.com/s?__biz=Mzg5ODAwMTEyNA==&mid=2247483674&idx=1&sn=e491934e0c702f11765fffc569043e33&scene=21#wechat_redirect)

[怎么才算掌握了JDK中的线程池](https://mp.weixin.qq.com/s/vzKMNagVpweurpGxEJdStQ)