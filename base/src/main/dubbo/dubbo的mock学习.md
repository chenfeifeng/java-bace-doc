# dubbo的mock学习
## 本地伪装
> 1.Mock通常用于服务降级,比如某验权服务,当服务提供方全部挂掉后,客户端不抛出异常,而是通过Mock数据返回授权失败.

> 2.Mock是Stub的一个子集,便于服务提供方在客户端执行容错逻辑,因经常需要在出现RpcException(比如网络失败,超时等)时进行容错,而在出现业务异常(比如登录用户名密码错误)时不需要容错，如果用Stub,可能就需要捕获并依赖RpcException类,而用Mock就可以不依赖RpcException,因为它的约定就是只有出现RpcException时才执行.

### 配置
```
<dubbo:service interface="com.foo.BarService" mock="true" />
或者
<dubbo:service interface="com.foo.BarService" mock="com.foo.BarServiceMock" />
如果简单的忽略异常:
<dubbo:service interface="com.foo.BarService" mock="return null" />
```
---

## 实现
1. 客户端配置
```
	<dubbo:reference id="testService" interface="cff.api.TestService" validation="false" protocol="dubbo" timeout="20000" mock="cff.api.TestServiceMock"/>
```
2. 实现Mock类(对每个方法实现Mock处理)
```
public class TestServiceMock implements TestService {

	@Override
	public List<Tenant> listAll() {
		System.out.println("错误5");
		return null;
	}
}
```
2. 将Mock类放在api包里面
3. 消费端配置
```
	<dubbo:reference id="testService" interface="cff.api.TestService" validation="false" protocol="dubbo" timeout="20000" mock="cff.api.TestServiceMock"/>

```
4. 当消费端调用服务端的时候,比如服务端超时或者其他错误,消费端不会进行报错处理,会执行mock里面对应的方法,进行处理
```
    @Autowired
	private TestService testService;
	
	public Object get(){
		return testService.listAll();
	}

```
5. 例子:
> 服务端进行超时处理
```
public List<Test> listAll() {
	try {
		Thread.sleep(21000l);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	return testlogic.listAll();
}
```
正常情况下,消费端会进行超时异常报错,现在当超时异常时候会进行Mock处理

```
错误5

null
```

从而保证消费端不产生异常消息
