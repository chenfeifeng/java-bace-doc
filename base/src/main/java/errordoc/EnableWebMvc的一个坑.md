# @EnableWebMvc的坑
## 注解作用说明

## 返回实体类
```java
@Data
public class DemoBo implements Serializable {
    
    /**
     * 名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;

}
```

## 接口
```java
@RestController
@RequestMapping(value = "/demo", produces = "application/json;charset=UTF-8")
@Api("DemoController相关的api")
public class DemoController {

    

    @RequestMapping(value = "test")
    public DemoBo test(){
        DemoBo db=new DemoBo();
        db.setName("cccc");
        db.setGmtCreate(new Date());
        db.setGmtModify(new Date());
        return db;
    }
}
```

## Appmain未添加@EnableWebMvc 注解，返回结果
```
{
    "name": "cccc",
    "gmtCreate": "2019-04-24T12:19:24.012+0000",
    "gmtModify": "2019-04-24T12:19:24.012+0000"
}
```
为了前端更好的处理时间，希望统一返回时间戳

## Appmain添加@EnableWebMvc 注解，返回结果
```
{
    "name": "cccc",
    "gmtCreate": 1556108461870,
    "gmtModify": 1556108461870
}
```
---
