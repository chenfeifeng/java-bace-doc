# 内存结构
## 堆（线程共享）
### 年轻代（比例）
   + eden区：8
   + From Survivor:1
   + T0 Survivor:1
### 老年代


## 栈(线程私有)
1. 生命周期与线程相同

### java虚拟机栈
1. 局部变量表存放了编译期可知的各种基本数据类型（boolean、byte、char、short、int、float、long、double）、
对象引用（reference类型，它不等同于对象本身，根据不同的虚拟机实现，它可能是一个指向对象起始地址的引用指针，也可能指向一个代表对象的句柄或者其他与此对象相关的位置）
和returnAddress类型（指向了一条字节码指令的地址）。
2. 每一个方法被调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程
3. 64为的dubbo和long占用2个空间(Slot)


### 本地方法栈
1. 虚拟机使用到的Native方法服务

## 方法区(线程私有)
1. 用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据


## 栈帧
1. 每个线程会创建一个栈
2. 一个栈=多个栈帧(栈帧(Stack Frame)是用于支持虚拟机进行方法调用和方法执行的数据结构，它是虚拟机运行时数据区的虚拟机栈(Virtual Machine Stack)的栈元素。)
3. 一个方法=一个栈帧
4. 包括：
   + 局部变量区域
   + 操作栈
   + 动态链接
   + 返回地址



### 运行时常量池
1. 运行时常量池（Runtime Constant Pool）是方法区的一部分
2. Class文件中除了有类的版本、字段、方法、接口等描述等信息外，还有一项信息是常量池
（Constant Pool Table），用于存放编译期生成的各种字面量和符号引用，
这部分内容将在类加载后存放到方法区的运行时常量池中

---

# 类的加载机制
1. 类的加载指的是将类的.class文件中的二进制数据读入到内存中，将其放在运行时数据区的方法区内，
然后在堆区创建一个java.lang.Class对象，用来封装类在方法区内的数据结构

## 类的生命周期
1. 其中类加载的过程包括了加载、验证、准备、解析、初始化五个阶

## 类的加载器
1. 启动类加载器：Bootstrap ClassLoader，负责加载存放在JDK\jre\lib(JDK代表JDK的安装目录，下同)下，
或被-Xbootclasspath参数指定的路径中的，并且能被虚拟机识别的类库（如rt.jar，所有的java.*开头的类均被Bootstrap ClassLoader加载）。启动类加载器是无法被Java程序直接引用的
2. 扩展类加载器：Extension ClassLoader，该加载器由sun.misc.Launcher$ExtClassLoader实现，它负责加载DK\jre\lib\ext目录中，或者由java.ext.dirs系统变量指定的路径中的所有类库（如javax.*开头的类），开发者可以直接使用扩展类加载器。
3. 应用程序类加载器:Application ClassLoader，该类加载器由sun.misc.Launcher$AppClassLoader来实现，它负责加载用户类路径（ClassPath）所指定的类，开发者可以直接使用该类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。
4. 用户自定义加载器：User ClassLoader

### 加载机制
1. 全盘负责
   + 当一个类加载器负责加载某个Class时，
   + 该Class所依赖的和引用的其他Class也将由该类加载器负责载入，
   + 除非显示使用另外一个类加载器来载入
2. 父类委派
   + 先让父类加载器试图加载该类，只有在父类加载器无法加载该类时才尝试从自己的类路径中加载该类
3. 缓存机制
   + 缓存机制将会保证所有加载过的Class都会被缓存，当程序中需要使用某个Class时，类加载器先从缓存区寻找该Class，只有缓存区不存在，系统才会读取该类对应的二进制数据，并将其转换成Class对象，存入缓存区。
   + 这就是为什么修改了Class后，必须重启JVM，程序的修改才会生效
   
### 双亲委派模型
工作流程：如果一个类加载器收到了类加载的请求，首先不会尝试去加载这个类，而是把这个类交给父加载器加载，依次向上。
所以所有的类加载最终应都被传递到顶层加载器，只有当父类加载器，找不到的时候，即无法完成该加载，子加载器才会尝试自己去加载该类。

**双亲委派机制:**
1. 当AppClassLoader加载一个class时，它首先不会自己去尝试加载这个类，而是把类加载请求委派给父类加载器ExtClassLoader去完成。
2. 当ExtClassLoader加载一个class时，它首先也不会自己去尝试加载这个类，而是把类加载请求委派给BootStrapClassLoader去完成。
3. 如果BootStrapClassLoader加载失败（例如在$JAVA_HOME/jre/lib里未查找到该class），会使用ExtClassLoader来尝试加载；
4. 若ExtClassLoader也加载失败，则会使用AppClassLoader来加载，如果AppClassLoader也加载失败，则会报出异常ClassNotFoundException。
5. 各加载器加载时候，首先会去缓存中进行查找，如果找不到会重新加载。


过程： 类的双亲加载机制
   + 当前加载器的缓存查找
   + 找不到，父级加载器中进行查找。
   + 父级优先加载当前加载器的缓存
   + 找不到，在往上父级查找
   + 如果父级都查找不到，最后查找自己的。



---

# JVM笔记
## 对象存活判断
1. 引用计数：每个对象都有一个引用数据属性，新增一个应用则+1，计数为0是可回收。此方法简单，无法解决对象相互循环引用的问题。比如A中引用B和B中引用A
2. 可达性分析：从GC ROOT开始向下搜索，搜索走过的过程叫做引用链。当一个对象到GC ROOT没有引用链时候，说明这个对象可以被回收了。

## 垃圾收集算法
### 标记-清除算法
1. 标记出需要清除的对象，标记完成之后，统一进行清除。
2. 总结：
   + 效率低：标记和清除效率低下
   + 空间问题：标记-清除之后，会导致内存空间不连续，当某个较大的内存存入时，会提交触发另一次GC。
### 复制算法
1. 将可用内存分层两块，当一块内存用完时，将存活对象复制到另一块内存上，然后将原来内存快进行清理
2. 总结：
    + 实现简单，效率高
    + 浪费内存空间
### 标记-压缩算法
1. 标记出需要清除的对象，标记完之后，将存活对象压缩到内存一端，然后直接清理边界外的内存
2. 总结：
   + 属于标记-清除的升级版
### 分代收集算法
1. GC分代的基本假设：绝大部分对象的生命周期都非常短暂，存活时间短。
2. “分代收集”（Generational Collection）算法，把Java堆分为新生代和老年代，这样就可以根据各个年代的特点采用最适当的收集算法。
   + 在新生代中，每次垃圾收集时都发现有大批对象死去，只有少量存活，那就选用复制算法，只需要付出少量存活对象的复制成本就可以完成收集。
   + 而老年代中因为对象存活率高、没有额外空间对它进行分配担保，就必须使用“标记-清理”或“标记-整理”算法来进行回收。
   
---

## 垃圾收集器
### Serial收集器
1. 单个线程回收，新生代、老年代使用串行回收
2. 新生代复制算法、老年代标记-压缩；垃圾收集的过程中会Stop The World（服务暂停）
3. 参数控制：
   + -XX:+UseSerialGC  串行收集器


### ParNew收集器
1. 多个线程进行回收，新生代并行，老年代串行回收
2. 新生代：复制速发，老年代：标记-压缩
3. 参数控制：
   + -XX:+UseParNewGC  ParNew收集器
   + -XX:ParallelGCThreads 限制线程数量


### Parallel收集器
1. Parallel Scavenge收集器类似ParNew收集器，Parallel收集器更关注系统的吞吐量。
2. 可以通过参数来打开自适应调节策略，虚拟机会根据当前系统的运行情况收集性能监控信息，动态调整这些参数以提供最合适的停顿时间或最大的吞吐量；
3. 也可以通过参数控制GC的时间不大于多少毫秒或者比例
4. 新生代复制算法、老年代标记-压缩
5. 参数控制：
   + -XX:+UseParallelGC  使用Parallel收集器+ 老年代串行
   
   
### Parallel Old 收集器
1. Parallel Old是Parallel Scavenge收集器的老年代版本，使用多线程和“标记－整理”算法。
2. 参数控制： 
   + -XX:+UseParallelOldGC 使用Parallel收集器+ 老年代并行
   
### CMS收集器
1. 详见CMS算法笔记

### G1收集器
1. 详见G1算法笔记


---
# 各收集器一般使用场景
1. 青年代
   + Serial
   + Parnew
   + Parallel
   + G1
2. 老年代
   + CMS
   + Serial Old
   + Parallel Old
   + G1


---
# Eden空间和两块Survivor空间的工作流程
现在假定有新生代Eden，Survivor A， Survivor B三块空间和老生代Old一块空间。
```
// 分配了一个对象
放在eden区
// eden区域满了，只能进行Minor GC
把Eden区域的存活对象拷贝到 Survivor A，然后清空了Eden区域
// eden区域又满了，只能进行Minor GC
把Eden区和Survivor A区的存活对象copy到Survivor B区，然后清空Eden区和Survivor A区
// 又分配了一个又一个对象
放到Eden区
// eden区又域满了，只能进行Minor GC
把Eden区和Survivor B区的存活对象copy到Survivor A区，然后清空Eden区和Survivor B区
//...
//...
// 有的对象来回在Survivor A区或者B区呆了比如15次，就被分配到老年代Old区
// 有的对象太大，超过了Eden区，直接被分配在Old区
// 有的存活对象，放不下Survivor区，也被分配到Old区
// ...
// 在某次Minor GC的过程中突然发现：
// 老年代Old区也满了，这是一次大GC(老年代GC：Major GC)
Old区慢慢的整理一番，空间又够了
// 继续Minor GC

```

---
# 常用的JVM调优参数
```
-Xmx:最大JVM可用内存 例：-Xmx4g
-Xms:最小JVM可用内存 例：Xms4g
-Xmn:年轻代最小内存   例：-Xmn2560m
-XX:PermSize:永久代内存大小，该值太大会导致fullGC时间过长，太小将增加fullGC频率，例：-XX:PermSize=128m
-Xss：线程栈大小，太大将导致JVM可建的线程数量减少，例：-Xss256k
-XX:+DisableExplicitGC：禁止手动fullGC，如果配置，则System.gc()将无效，比如在为DirectByteBuffer分配空间过程中发现直接内存不足时会显式调用System.gc()
-XX:+UseConcMarkSweepGC：一般PermGen是不会被GC，如果希望PermGen永久代也能被GC，则需要配置该参数
-XX:+CMSParallelRemarkEnabled：GC进行时标记可回收对象时可以并行remark-XX:+UseCMSCompactAtFullCollection 表示在fullGC之后进行压缩，CMS默认不压缩空间
-XX:+UseFastAccessorMethods：对原始类型进行快速优化
-XX:+UseCMSInitiatingOccupancyOnly：关闭预期开始的晋升率的统计
-XX:CMSInitiatingOccupancyFraction：使用cms作为垃圾回收，并设置GC百分比，例：-XX:CMSInitiatingOccupancyFraction=70（使用70％后开始CMS收集）
-XX:+PrintGCDetails：打印GC的详细信息
-XX:+PrintGCDateStamps：打印GC的时间戳
-Xloggc：指定GC文件路径
```
