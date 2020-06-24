# reliable
   [http://reliable.xream.io](http://reliable.xream.io)

[![license](https://img.shields.io/github/license/x-ream/reliable.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![maven](https://img.shields.io/maven-central/v/io.xream.reliable/reliable.svg)](https://search.maven.org/search?q=io.xream)

    mq transaction, with tcc option


## code annotation
    (x7/x7-reyc/io.xream.x7.reliable:)
    @EnableReliabilityManagement
    @ReliableProducer
    @ReliableOnConsumed

## code config
    implements DtoConverter
    
## spring boot properties
    reliable.app=reliable-app (k8s service name)
    #reliable.app=http://ip:7717 (ip:port)
    
## maven dependency
```xml
<reliable.version>1.1.11.RELEASE</reliable.version>

<dependency>
    <groupId>io.xream.reliable</groupId>
    <artifactId>reliable-spring-boot-starter</artifactId>
    <version>${reliable.version}</version>
</dependency>

```  
