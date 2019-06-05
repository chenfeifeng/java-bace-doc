# CMS（Concurrent Mark Sweep）并发标记清除

## 使用场景
1. 希望垃圾尽早回收
2. 应用运行在cpu上，cpu有足够多资源
3. 有比较多生命周期长的对象
4. 希望应用的响应时间较短


## 使用参数
1. -XX:+UseConcMarkSweepGC  使用CMS收集器
2. -XX:+ UseCMSCompactAtFullCollection Full GC后，进行一次碎片整理；整理过程是独占的，会引起停顿时间变长
3. -XX:+CMSFullGCsBeforeCompaction  设置进行几次Full GC后，进行一次碎片整理
4. -XX:ParallelCMSThreads  设定CMS的线程数量（一般情况约等于可用CPU数量）


## 说明
1. CMS也采用分代策略，青年代用：ParNew 收集器，老年代用CMS收集器
2. 在某些阶段，应用的线程会被挂起，也就是stop-the-world。而在另外的阶段里，垃圾回收线程可以与应用的线程一起工作。
3. STW（stop-the-worl）发生阶段：初始标记， 重新标记。



## 搜集阶段
1. 初始标记-stw(CMS-initial-mark)
2. 并发标记(CMS-concurrent-mark)
3. 并发预清理(CMS-concurrent-preclean)
4. 重新标记-stw(CMS-remark)
5. 并发清理(CMS-concurrent-sweep)
6. 并发重置状态等待下一次CMS(CMS-concurrent-reset)

其中初始标记和并发标记会进行短暂的STW。
并发标记，并发清除和并发重置阶段：应用线程和垃圾回收线程一起工作，垃圾回收线程会占用部分CPU资源



## 缺点
1. 并发模式失败（Concurrent Module Failure）：
   + 由于CMS可以和应用线程一起工作，那么应用线程仍然需要申请内存，如果这个时候老年代不够用了。
   + 那么会有Concurrent Mode Failure 这样的日志输出，之后会进行一次Full GC的操作，所有的应用线程都会停止工作。
2. 降低吞吐量
   + 由于应用线程和回收线程一起工作，那么垃圾线程也会占用系统资源，会对应用的吞吐量造成一定的影响。
   + 为了保证垃圾回收过程中，应用线程有足够的内存可以使用，当堆内存的空间使用率达到68%的时候，CMS开始触发垃圾回收。
