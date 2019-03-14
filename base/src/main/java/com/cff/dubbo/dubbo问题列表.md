# 抛异常问题
1. 首先在common.jar包中创建自定义异常HelloException
```
public class HelloException extends RuntimeException {
 
     public HelloException() {
     }
 
     public HelloException(String message) {
         super(message);
     }
 
}
```
2. 创建对应的接口和实现类,接口在api.jar中
```
// 接口
public interface DemoService {
    String sayHello(String name);
}


// 实现类
public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
         throw new HelloException("啊啊啊 我异常啦");
    }
}
```

3. 在服务端调用，并捕获异常

```
public class DemoAction {
     private DemoService demoService;
     public void setDemoService(DemoService demoService) {
         this.demoService = demoService;
     }
     public void start() throws Exception {
         try {
             String hello = demoService.sayHello("你好，你是谁啊啊");
         }catch (HelloException helloException) {
              System.out.println("这里捕获helloException异常");
         }
     }
     
}
```
4. 当此时服务端调用接口的时候，发现，无法捕获到异常。  
   这是为啥呢？明明catch HelloException 异常。
5. 此时修改捕获类型，改成Exception，打印异常类型，发现是RuntimeException类型了。

6. 那么就需要查看下源码了，查看**ExceptionFilter.invoke()**方法

```
public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != invoker.getInterface()) {
                try {
                    Throwable exception = result.getException();

                    // 如果是checked异常，直接抛出
                    if (! (exception instanceof RuntimeException) && (exception instanceof Exception)) {
                        return result;
                    }
                    // 在方法签名上有声明，直接抛出
                    try {
                        Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                        Class<?>[] exceptionClassses = method.getExceptionTypes();
                        for (Class<?> exceptionClass : exceptionClassses) {
                            if (exception.getClass().equals(exceptionClass)) {
                                return result;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        return result;
                    }

                    // 未在方法签名上定义的异常，在服务器端打印ERROR日志
                    logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
                            + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
                            + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

                    // 异常类和接口类在同一jar包里，直接抛出
                    String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                    String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                    if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)){
                        return result;
                    }
                    // 是JDK自带的异常，直接抛出
                    String className = exception.getClass().getName();
                    if (className.startsWith("java.") || className.startsWith("javax.")) {
                        return result;
                    }
                    // 是Dubbo本身的异常，直接抛出
                    if (exception instanceof RpcException) {
                        return result;
                    }

                    // 否则，包装成RuntimeException抛给客户端
                    return new RpcResult(new RuntimeException(StringUtils.toString(exception)));
                } catch (Throwable e) {
                    logger.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost()
                            + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
                            + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
                    return result;
                }
            }
            return result;
        } catch (RuntimeException e) {
            logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
                    + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
                    + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
            throw e;
        }
    } // 如果是checked异常，直接抛出
```

7. 观察上述源码后，简单总结：

```
1.如果是checked异常，直接抛出,这是因为我们的HelloException是RuntimeException,不符合

2.在方法签名上有声明，直接抛出.很明显,我们接口并未声明该异常,不符合

3.异常类和接口类在同一jar包里，直接抛出.很明显,我们的异常类是在common.jar的,接口是在api.jar的,不符合

4.是JDK自带的异常，直接抛出

5.是Dubbo本身的异常(RpcException)，直接抛出.很明显,这个HelloException是我们自定义的,和RpcException几乎没有半毛钱关系.

6.否则，包装成RuntimeException抛给客户端.因为以上5点均不满足,所以该异常会被包装成RuntimeException异常抛出(重要)
```

8. 由于我们ZzzException 异常在common.jar上，而接口在api.jar包中，所以无法直接抛出，即包装成RuntimeException类型。
9. 解决方案：将异常和接口放在同一jar包

## 为什么这么设计

其实Dubbo的这个考虑,是基于序列化来考虑的.你想想,如果provider抛出一个仅在provider自定义的一个异常,那么该异常到达consumer,明显是无法序列化的.所以你注意看Dubbo的判断.我们来看下他的判断
```
1.checked异常和RuntimeException是不同类型,强行包装可能会出现类型转换错误,因此不包,直接抛出

2.方法签名上有声明.方法签名上有声明,如果这个异常是provider.jar中定义的,因为consumer是依赖api.jar的,而不是依赖provider.jar.那么编译都编译不过,如果能编译得过,说明consumer是能依赖到这个异常的,因此序列化不会有问题,直接抛出

3.异常类和接口类在同一jar包里.provider和consumer都依赖api,如果异常在这个api,那序列化也不会有问题,直接抛出

4.是JDK自带的异常，直接抛出.provider和consumer都依赖jdk,序列化也不会有问题,直接抛出

5.是Dubbo本身的异常(RpcException)，直接抛出.provider和consumer都依赖Dubbo,序列化也不会有问题,直接抛出

6.否则，包装成RuntimeException抛给客户端.此时,就有可能出现我说的那种,这个异常是provider.jar自定义的,那么provider抛出的时候进行序列化,因为consumer没有依赖provider.jar,所以异常到达consumer时,根本无法反序列化.但是包装成了RuntimeException异常则不同,此时异常就是JDK中的类了,到哪都能序列化.

```



[参考资料](https://mp.weixin.qq.com/s/Sa_lWU9x9pxXO6q1NqECBA)