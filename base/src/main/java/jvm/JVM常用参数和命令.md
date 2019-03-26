# JVM调优参数
## 堆参数
命令 | 含义
---|---
-Xms | 设置JVM启动最小内存大小
-Xmx | 设置JVM允许过程中最大内存大小
-Xmn | 设置新生代的内存大小，剩下的属于老年代
-XX:PermGen | 设置永久代的初始内存大小，1.8之后被废弃
-XX:MaxPermGen | 设置永久代的最大内存大小
-XX:SurvivorRatio | 设置Eden区和Survivor区的内存占比，默认为8
-XX:NewRatio | 设置年轻代和老年代的比例，默认为2

## 回收参数
1. -XX:+UseSerialGC 
   + 串行gc，新生代和老年代都使用串行，使用复制算法，逻辑高效简单，无线程切换开销
2. -XX:UseParallerGC
   + 并行gc。新生代多个线程并行回收，老年代使用单线程串行。-XX:UseParallerGCThreads=n,参数指定线程数，默认cpu数。新生代复制算法、老年代标记-压缩
3. -XX:UseParallerOldGC
   + 新生代和老年代使用多线程并行gc，其中老年代使用多线程和“标记－整理”算法
-XX:UseCornMarkSweepGC | 并发，短暂停顿的并发gc。
4. -XX:UseCornMarkSweepGC
   + 并发，短暂停顿的并发gc
   + 并发标记清理，CMS也是采用分代策略的，用于收集老年代的垃圾对象，并且分为好几个阶段来执行GC。在某些阶段(full gc)，应用的线程会被挂起，也就是stop-the-world。而在另外的阶段里，垃圾回收线程可以与应用的线程一起工作。
5. -XX:UseG1GC
   + 并行的，并发的和增量压缩短暂停顿的垃圾收集器。不区分新生代和老年代空间。它会把堆空间划分成多个大小相当的空间，当垃圾回收时候，它会优先收集存活对象最少的空间。因此叫 Garbage First
   
### 回收参数常用组合
Yong | Old | JVM参数Options 
---|---|---
Serial | Serial| -XX:+UseSerialGC  
Paraller scavenge | Paraller Old/Serial  | -XX:UseParallerGC ,-XX:UseParallerOldGC
Serial/Paraller scavenge | CMS | -XX:UseParallerNewGC , -XX:UseCornMarkSweepGC
G1 | --  | -XX:UseG1GC 




# 参考资料
1. [JVM面试问题系列](https://mp.weixin.qq.com/s/tfyHwbsNCTjvMGTrfQ0qwQ)