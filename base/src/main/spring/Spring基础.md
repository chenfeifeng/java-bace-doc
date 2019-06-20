# IOC
1. Spring的控制反转（IOC），简单来说，就是运用反射技术，
    + 动态生成对象
    + 将配置文件中的属性值注入到对象中
2. 具体的流程为
    + Resource定位
        + 针对以上各种Resource资源，运用ResourceLoader定位。ResourceLoader是一个接口，用于实现不同的Resource加载策略。
    + BeanDefinition的载入和解析
        + 获取到Resource后，需要将Resource中对bean的定义转化为BeanDefinition。
        + BeanDefinition是对bean的描述，有属性值，构造参数和具体实现提供的其他信息。
        + 过程包括：1. 读取配置文件 2. 封装成BeanDefinition对象
    + BeanDefinition的注册
        + 构建完数据表示后，需要对这些数据进行注册。
        + 具体是调用BeanDefinitionRegistry接口的实现类，完成向容器的注册，就是存入BeanFactory中。
    + 依赖注入
        + 运用反射机制，在getBean()方法调用时，生成对应的bean对象。实例化方法分为，JVM反射，CGLIB
        + 如果BeanDefinition中lazy-init设置的是true，则会在初始化过程中创建注入而不是在getBean()这里。
        
        
# SpringBoot 自动装配
1. 主要注解
```
@SpringBootApplication：SpringBoot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot就会运行这个类的main方法来启动SpringBoot项目。
@SpringBootConfiguration：表名该类是一个Spring的配置类。
@Configuration：说明Spring的配置类也是Spring的一个组件。
@EnableAutoConfiguration：这个注解是开启自动配置的功能。
@AutoConfigurationPackage：这个注解是自动配置包，主要是使用的@Import来给Spring容器中导入一个组件 ，这里导入的是Registrar.class。
```
2. 通过Registrar的registerBeanDefinitions方法获取扫描的包路径
3. @SpringBootConfiguration：
    + 通过@Import导入了AutoConfigurationImportSelector.class ，找到selectImports()方法，
    + 他调用了getCandidateConfigurations()方法，在这里，这个方法又调用了Spring Core包中的loadFactoryNames()方法。
    + 这个方法的作用是，会查询META-INF/spring.factories文件中包含的JAR文件，然后将 autoconfig 包里的已经写好的自动配置加载进来。
    
 
# Spring的事务传播机制和隔离级别
## 传播机制
1. PROPAGATION_REQUIRED	
    + 表示当前方法必须在一个事务中运行。如果一个现有事务正在进行中，该方法将在那个事务中运行，否则就要开始一个新事务	
    + 有事务就用已有的，没有就重新开启一个
2. PROPAGATION_SUPPORTS
    + 表示当前方法不需要事务性上下文，但是如果有一个事务已经在运行的话，它也可以在这个事务里运行	
    + 有事务就用已有的，没有也不会重新开启
3. PROPAGATION_MANDATORY
    + 表示该方法必须运行在一个事务中。如果当前没有事务正在发生，将抛出一个异常	
    + 必须要有事务，没事务抛异常
4. PROPAGATION_REQUIRES_NEW	
    + 表示当前方法必须在它自己的事务里运行。一个新的事务将被启动，而且如果有一个现有事务在运行的话，则将在这个方法运行期间被挂起	
    + 开启新事务，若当前已有事务，挂起当前事务
5. PROPAGATION_NOT_SUPPORTED	
    + 表示该方法不应该在一个事务中运行。如果一个现有事务正在进行中，它将在该方法的运行期间被挂起	
    + 不需要事务，若当前已有事务，挂起当前事务
6. PROPAGATION_NEVER	
    + 表示当前的方法不应该在一个事务中运行。如果一个事务正在进行，则会抛出一个异常	
    + 不需要事务，若当前已有事务，抛出异常
7. PROPAGATION_NESTED	
    + 表示如果当前正有一个事务在进行中，则该方法应当运行在一个嵌套式事务中。被嵌套的事务可以独立于封装事务进行提交或回滚。如果封装事务不存在，行为就像PROPAGATION_REQUIRES一样	
    + 嵌套事务，如果外部事务回滚，则嵌套事务也会回滚！！！外部事务提交的时候，嵌套它才会被提交。嵌套事务回滚不会影响外部事务。

## 隔离级别
1. Read uncommitted 读未提交
2. Read committed 读提交
3. Repeatable read 重复读
4. Serializable (串行化)：可避免脏读、不可重复读、幻读的发生


---

# spring+mtbatis
1. 读取核心配置文件并返回InputStream流对象。
2. 根据InputStream流对象解析出Configuration对象，然后创建SqlSessionFactory工厂对象
3. 根据一系列属性从SqlSessionFactory工厂中创建SqlSession
4. 从SqlSession中调用Executor执行数据库操作&&生成具体SQL指令
5. 对执行结果进行二次封装
6. 提交与事务

1. 首先，SqlSessionFactoryBuilder去读取mybatis的配置文件，然后build一个DefaultSqlSessionFactory
2. 当我们获取到SqlSessionFactory之后，就可以通过SqlSessionFactory去获取SqlSession对象。
3. 可以调用SqlSession中一系列的select...,  insert..., update..., delete...方法轻松自如的进行CRUD操作
4. 通过MapperProxy动态代理咱们的dao， 也就是说， 当咱们执行自己写的dao里面的方法的时候，其实是对应的mapperProxy在代理
5. 通过SqlSession从Configuration中获取
6. SqlSession把包袱甩给了Configuration, 接下来就看看Configuration
7. Configuration不要这烫手的山芋，接着甩给了MapperRegistry
8. MapperProxyFactory是个苦B的人，粗活最终交给它去做了
