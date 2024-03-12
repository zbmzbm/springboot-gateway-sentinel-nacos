#基于springboot+gateway+nacos+sentinel实现限流降级

## 项目介绍

基于springboot + gateway + nacos + sentinel 搭建的微服务网关
持久化限流降级规则到nacos

## 项目技术

- springboot
- gateway
- nacos
- sentinel

## 项目功能
应对高并发请求，我们打造接口一般的做法是缓存，限流，降级，熔断，黑白名单等。

- 限流：限制请求访问速率，避免服务被压垮
- 降级：当某个服务不可用时，返回一个友好的提示，避免服务被压垮
- 缓存：将热点数据存储到缓存中，提高访问速度
- 熔断：当某个服务不可用时，快速返回，避免服务被压垮
- 黑白名单：限制某些IP的访问，或者限制某些接口的访问

## 代码详细

### 网关配置
`server.port=8086
spring.application.name=gateway
spring.profiles.active=dev
spring.main.allow-bean-definition-overriding=true
spring.cloud.nacos.discovery.server-addr=localhost:8848
spring.cloud.sentinel.eager=true
spring.cloud.sentinel.transport.dashboard=localhost:8082
spring.cloud.sentinel.transport.port=8082
spring.cloud.sentinel.datasource.ds.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds.nacos.dataId=gateway-sentinel-flow-limit
spring.cloud.sentinel.datasource.ds.nacos.groupId=sentinel
spring.cloud.sentinel.datasource.ds.nacos.data-type=json
spring.cloud.sentinel.datasource.ds.nacos.rule-type=GW_FLOW
spring.cloud.sentinel.datasource.ds1.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds1.nacos.dataId=gateway-sentinel-degrade
spring.cloud.sentinel.datasource.ds1.nacos.groupId=sentinel
spring.cloud.sentinel.datasource.ds1.nacos.data-type=json
spring.cloud.sentinel.datasource.ds1.nacos.rule-type=DEGRADE
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.routes[0].id=demo
spring.cloud.gateway.routes[0].uri=lb://demo
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/demo/**
spring.cloud.gateway.routes[0].filters[0]=RewritePath=/api/?(?<segment>.*),/${segment}
`
### nacos配置
#### gateway-sentinel-flow-limit限流配置
`
[
{
"resource":"demo",
"limitApp":"default",
"grade":1,
"count":1,
"strategy":0,
"controlBehavior":0,
"clusterMode":"false"
}
]
`
#### gateway-sentinel-degrade降级配置
`[
{
"resource": "demo",
"count": 1000,
"grade": 0,
"timeWindow": 10,
"minRequestAmount": 10,
"statIntervalMs": 1000,
"slowRatioThreshold": 0.2
}
]`

配置完后会自动同步到sentinel控制台

