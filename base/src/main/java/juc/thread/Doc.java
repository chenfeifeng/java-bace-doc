package src.main.java.juc.thread;


/**
 * @author cff
 * @version 1.0
 * @description @TODO
 * @date 2019-01-15 11:24
 */
public class Doc {

    /**
     * 1、ThreadPoolExecutor中常用参数有哪些，作用是什么？任务提交后，ThreadPoolExecutor会按照什么策略去创建线程用于执行提交任务？
     * 2、ThreadPoolExecutor有哪些状态，状态之间流转是什么样子的？
     * 3、ThreadPoolExecutor中的线程哪个时间点被创建？是任务提交后吗？可以在任务提交前创建吗？
     * 4、ThreadPoolExecutor中创建的线程哪个时间被启动？
     * 5、ThreadPoolExecutor竟然是线程池那么他是如何做到重复利用线程的？
     * 6、ThreadPoolExecutor中创建的同一个线程同一时刻能执行多个任务吗？如果不能是通过什么机制保证ThreadPoolExecutor中的同一个线程只能执行完一个任务，才会机会去执行另一个任务？
     * 7、ThreadPoolExecutor中关闲线程池的方法shutdown与shutdownNow的区别是什么？
     * 8、通过submit方法向ThreadPoolExecutor提交任务后，当所有的任务都执行完后不调用shutdown或shutdownNow方法会有问题吗？
     * 9、ThreadPoolExecutor有没有提供扩展点，方便在任务执行前或执行后做一些事情？
     */

    /**
     * 1. ThreadPoolExecutor中常用参数有哪些，作用是什么？任务提交后，ThreadPoolExecutor会按照什么策略去创建线程用于执行提交任务？
     */
    public void test1() {
        /**
         * int corePoolSize--核心线程数
         * int maximumPoolSize,--最大工作线程数
         * long keepAliveTime,--表示空闲线程的存活时间。（当线程池中线程数量超过corePoolSize时，允许等待多长时间从workQueue中拿任务）
         * TimeUnit unit, --keepAliveTime对应的时间单位，为TimeUnit类
         * BlockingQueue<Runnable> workQueue,  阻塞队列，当线程池中线程数超过corePoolSize时，用于存储提交的任务。
         * ThreadFactory threadFactory, --线程池采用，该线程工厂创建线程池中的线程。
         * RejectedExecutionHandler handler--为RejectedExecutionHandler，当线程线中线程超过maximumPoolSize时采用的，拒绝执行处理器。
         */
        // corePoolSize，maximumPoolSize，keepAliveTime，TimeUnit，Blockqueue，factory，handel
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(100,
                200,
                3000l,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(2000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 2、ThreadPoolExecutor有哪些状态，状态之间流转是什么样子的？
     */
    public void test2() {
        /**
         * runing：运行中，接收新的任务或处理队列中的任务。
         * shutdown:关闭，不再接受新任务，但会处理队列中的任务。值为0。
         * stop: 停止，不在接受新的任务，也不会处理队列中的任务，并中断正在执行中的任务
         * tidying:所有任务已结束，队列大小为0，转变为tidying状态的线程将执行terminated()方法
         * terminated: 结束terminated()已被执行完
         *
         */
        /**
         * 流程：
         * RUNGING->shutdown()->SHUTDOWN->TIDTYING->terminated()->TERMINATED
         * RUNGING->shutdownNow()->STOP->TIDTYING->terminated()->TERMINATED
         * RUNGING->shutdown()->SHUTDOWN->shutdownNow()->STOP->TIDTYING->terminated()->TERMINATED
         */
    }


    /**
     * 3、ThreadPoolExecutor中的线程哪个时间点被创建？是任务提交后吗？可以在任务提交前创建吗？
     */
    public void test3() {
        /**
         *
         *   一般在任务被提交后，线程池会利用线程工厂去创建线程，
         *   但当线程池中线程数已为corePoolSize时或maxmumPoolSize时不会。
         *   可以在任务提交前通过prestartCoreThread方法或prestartAllCoreThreads方法预先创建核心线程
         *
         */
    }

    /**
     * ThreadPoolExecutor中创建的线程哪个时间被启动？
     */
    public void test4() {
        /**
         * 线程池中线程实现是在addWorker方法中被创建的。
         * 创建后完，该线程就被启动。
         * 线程池中被创建的线程被封装到了Worker对象中，而Worker类又实现了Runnable接口，
         * 线程池中的线程又引用了worker。
         * 当线程被start后实际就有机会等待操作系统调度执行Worker类的run方法。
         */
    }

    /**
     * 5、ThreadPoolExecutor竟然是线程池那么他是如何做到重复利用线程的？
     */
    public void test5() {
        /**
         * 一旦线程池通过ThreadFactory创建好线程后，就会将创建的线程封装到了Worker对象中，同时启动该线程。
         * 新创建的线程会执行刚提交的任务，同时会不断地从workerQueue中取出任务执行。
         * 线程池的线程复用正是通过不断地从workerQueue中取出任务来执行达到的。
         * 源码分析见runWorkers方法分析。
         */
    }

    /**
     * 6、ThreadPoolExecutor中创建的同一个线程同一时刻能执行多个任务吗？
     * 如果不能是通过什么机制保证ThreadPoolExecutor中的同一个线程只能执行完一个任务，才会机会去执行另一个任务？
     */
    public void test6() {
        /**
         * 同时一时刻不能执行多个任务，只有一个任务执行完时才能去执行另一个任务。
         * 上面说到线程池中通过ThreadFacory创建的线程最后会被封装到Worker中，
         * 而该线程又引用了Worker，start线程后，任务其实是在Worker中的run方法中被执行，
         * 最终run又将任务执行代理给ThreadPoolExecutor的runWorker方法。
         */
        /**
         * Worder一方面实现了Runnable，另一方面又继承了AQS。
         * 通过实现AQS，Worker具有了排它锁的语义，每次在执行提交任务时都会先lock操作，执行完任务后再做unlock操作。
         * 正是这个加锁与解锁的操作，保证了同一个线程要执行完当前任务才有机再去执行另一个任务。
         */
    }

    /**
     * 7、ThreadPoolExecutor中关闲线程池的方法shutdown与shutdownNow的区别是什么？
     */
    public void test7() {
        /**
         * shutdown方法是将线程池的状态设置为SHUTDOWN，此时新任务不能被提交（提交会抛出异常），
         * workerQueue的任务会被继续执行，同时线程池会向那些空闲的线程发出中断信号。
         * 空闲的线程实际就不没在执行任务的线程。
         * 如何被封装在worker里的线程能加锁，这里这个线程实现会就空闲的。下面是向空闲的线程发出中断信号源码。
         */
        /**
         * private void interruptIdleWorkers(boolean onlyOne) {
         *         final ReentrantLock mainLock = this.mainLock;
         *         mainLock.lock();
         *         try {
         *             for (Worker w : workers) {
         *                 Thread t = w.thread;
         *                 //w.tryLock()用于加锁，看线程是否在执行任务
         *                 if (!t.isInterrupted() && w.tryLock()) {
         *                     try {
         *                         t.interrupt();
         *                     } catch (SecurityException ignore) {
         *                     } finally {
         *                         w.unlock();
         *                     }
         *                 }
         *                 if (onlyOne)
         *                     break;
         *             }
         *         } finally {
         *             mainLock.unlock();
         *         }
         *     }
         */
        /**
         * shutdownNow方法是将线程池的状态设置为STOP，此时新任务不能被提交（提交会抛出异常），线程池中所有线程都会收到中断的信号。
         * 具体线程会作出什么响应，要看情况，如果线程因为调用了Object的wait、join方法或是自身的sleep方法而阻塞，那么中断状态会被清除，
         * 同时抛出InterruptedException。
         * 其它情况可以参考Thread.interrupt方法的说明。shutdownNow方法向所有线程发出中断信息源码如下：
         */
        /**
         * private void interruptWorkers() {
         *     final ReentrantLock mainLock = this.mainLock;
         *     //加锁操作保证中断过程中不会新woker被创建
         *     mainLock.lock();
         *     try {
         *          for (Worker w : workers)
         *            w.interruptIfStarted();
         *     } finally {
         *           mainLock.unlock();
         *     }
         * }
         */
    }

    /**
     * 8、通过submit方法向ThreadPoolExecutor提交任务后，当所有的任务都执行完后不调用shutdown或shutdownNow方法会有问题吗？
     */
    public void test8() {
        /**
         * 如果没指核心线程允许超时将会有问题。
         * 核心线程允许超时是指在从wokerQueue中获取任务时，采用的阻塞的获取方式等待任务到来，
         * 还是通过设置超时的方式从同步阻塞队列中获取任务。
         * 即是通通过BlockingQueue的poll方法获取任务还是take方法获取任务。
         * 可参考之前的源码分析中的getTask方法分析。
         * 如果不调用shutdown或shutdownNow方法，
         * 核心线程由于在getTask方法调用BlockingQueue.take方法获取任务而处于一直被阻塞挂起状态。
         * 核心线程将永远处于Blocking的状态，导致内存泄漏，主线程也无法退出，除非强制kill。
         * 试着运行如下程序会发现，程序无法退出。
         */
        ExecutorService executorService = new ThreadPoolExecutor(3,
                3, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread name " + Thread.currentThread().getName());
            }
        });
        /**
         * 所在使用线程池时一定要记得根本具体场景调用shutdown或shutdownNow方法关闭线程池。
         * shutdown方法适用于提交任务都要被执行完的场景，shutdownNow方法适用于不关心提交任务是否执行完的场景。
         */
    }

    /**
     * 9、ThreadPoolExecutor有没有提供扩展点，方便在任务执行前或执行后做一些事情？
     */
    public void test9() {
        /**
         * 线程池提供了三个扩展点，分别是提交任务的run方法或是call方法被调用前与被调后，
         * 即beforeExecutor与afaterExecutor方法；
         * 另外一个扩展点是线程池的状态从TIDYING状态流转为TERMINATED状态时terminated方法会被调用。
         */
    }

    /**
     * 疑问：
     * 1. 线程池如何回收闲置线程
     * 2. 线程池的submit和execute方法的区别
     * 3. shutdown和shutdownNow的使用--->关联问题7
     */

    //public static void main(String[] args) throws Exception{
    //    BlockingQueue queue = new LinkedBlockingQueue(100);
    //    ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 100,
    //            3, TimeUnit.SECONDS, queue);
    //
    //
    //    for(int i=0;i<200;i++){
    //        executor.execute( new Runnable() {
    //            public void run() {
    //                try {
    //                    System.out.println("存活："+executor.getActiveCount());
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //                System. out.println(String. format("thread %d finished", this.hashCode())+"---"+executor.getActiveCount());
    //            }
    //        });
    //    }
    //}

    public static void method1() {
        BlockingQueue queue = new LinkedBlockingQueue(100);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 200,
                10, TimeUnit.SECONDS, queue);
        for ( int i = 0; i < 1000; i++) {
            executor.execute( new Runnable() {
                public void run() {
                    try {
                        System.out.println("存活："+executor.getActiveCount());
                        for ( int j = 0; j < 20; j++) {
                            //System. out.println( this.hashCode() + ":" + j);
                            Thread.sleep(200l);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System. out.println(String. format("thread %d finished", this.hashCode())+"---"+executor.getActiveCount());
                }
            });
        }
    }

    //public static void main(String[] args) throws Exception{
    //    ExecutorService pool = Executors.newFixedThreadPool(2);
    //    pool.execute(new RunnableTest("Task1"));
    //
    //    Future future = pool.submit(new CallBackTest("Task2"));
    //
    //    System.out.println("==========");
    //
    //    try {
    //        if (future.get() == null) {
    //            //如果Future's get返回null，任务完成
    //            System.out.println("任务完成");
    //        }
    //    }catch (Exception e) {
    //        //否则我们可以看看任务失败的原因是什么
    //        System.out.println("submit:"+e.getCause().getMessage());
    //    }
    //
    //
    //    System.exit(0);
    //}

}

class RunnableTest implements Runnable {

    private String taskName;

    public RunnableTest(final String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println("Inside " + taskName);
        try {
            Thread.sleep(3000l);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("RuntimeException from inside " + taskName);
    }

}

class CallBackTest implements Callable<String> {

    private String taskName;

    public CallBackTest(final String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String call() throws Exception {
        System.out.println("Inside " + taskName);
        try {
            Thread.sleep(3000l);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("RuntimeException from inside " + taskName);
    }


    /**
     * 1. workers
     *     workers是HashSet<Work>类型，即它是一个Worker集合。而一个Worker对应一个线程，也就是说线程池通过workers包含了"一个线程集合"。当Worker对应的线程池启动时，它会执行线程池中的任务；当执行完一个任务后，它会从线程池的阻塞队列中取出一个阻塞的任务来继续运行。
     *     wokers的作用是，线程池通过它实现了"允许多个线程同时运行"。
     *
     * 2. workQueue
     *     workQueue是BlockingQueue类型，即它是一个阻塞队列。当线程池中的线程数超过它的容量的时候，线程会进入阻塞队列进行阻塞等待。
     *     通过workQueue，线程池实现了阻塞功能。
     *
     * 3. mainLock
     *     mainLock是互斥锁，通过mainLock实现了对线程池的互斥访问。
     *
     * 4. corePoolSize和maximumPoolSize
     *     corePoolSize是"核心池大小"，maximumPoolSize是"最大池大小"。它们的作用是调整"线程池中实际运行的线程的数量"。
     *     例如，当新任务提交给线程池时(通过execute方法)。
     *           -- 如果此时，线程池中运行的线程数量< corePoolSize，则创建新线程来处理请求。
     *           -- 如果此时，线程池中运行的线程数量> corePoolSize，但是却< maximumPoolSize；则仅当阻塞队列满时才创建新线程。
     *           如果设置的 corePoolSize 和 maximumPoolSize 相同，则创建了固定大小的线程池。如果将 maximumPoolSize 设置为基本的无界值（如 Integer.MAX_VALUE），则允许池适应任意数量的并发任务。在大多数情况下，核心池大小和最大池大小的值是在创建线程池设置的；但是，也可以使用 setCorePoolSize(int) 和 setMaximumPoolSize(int) 进行动态更改。
     *
     * 5. poolSize
     *     poolSize是当前线程池的实际大小，即线程池中任务的数量。
     *
     * 6. allowCoreThreadTimeOut和keepAliveTime
     *     allowCoreThreadTimeOut表示是否允许"线程在空闲状态时，仍然能够存活"；而keepAliveTime是当线程池处于空闲状态的时候，超过keepAliveTime时间之后，空闲的线程会被终止。
     *
     * 7. threadFactory
     *     threadFactory是ThreadFactory对象。它是一个线程工厂类，"线程池通过ThreadFactory创建线程"。
     *
     * 8. handler
     *     handler是RejectedExecutionHandler类型。它是"线程池拒绝策略"的句柄，也就是说"当某任务添加到线程池中，而线程池拒绝该任务时，线程池会通过handler进行相应的处理"。
     *
     *
     *
     * 综上所说，线程池通过workers来管理"线程集合"，每个线程在启动后，会执行线程池中的任务；当一个任务执行完后，它会从线程池的阻塞队列中取出任务来继续运行。阻塞队列是管理线程池任务的队列，当添加到线程池中的任务超过线程池的容量时，该任务就会进入阻塞队列进行等待。
     */

}