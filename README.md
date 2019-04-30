# 日志分析

## 分析

### 1、总述
    
    用户基本信息分析模块、浏览器信息分析模块、地域信息分析模块、用户浏览深度分析模块、外链数据分析模块、订单分析模块以及事件分析模块

### 2、概念:
    
    1.用户/访客：表示同一个浏览器代表的用户。唯一标示用户
    
    2.会员：表示网站的一个正常的会员用户。
    
    3.会话：一段时间内的连续操作，就是一个会话中的所有操作。
    
    4.Pv：访问页面的数量
    
    5.在本次项目中，所有的计数都是去重过的。比如：活跃用户/访客，计算uuid的去重后的个数
    
### 3、用户基本信息分析模块
    
    用户基本信息分析模块主要是从用户/访客和会员两个主要角度分析浏览相关信息，包括但不限于新增用户，活跃用户，总用户，新增会员，活跃会员，总会员以及会话分析等

用户分析
    
    该分析主要分析新增用户、活跃用户以及总用户的相关信息。
    
    新访客:老访客(活跃访客中) =  1:7~10 

会员分析
    
    该分析主要分析新增会员、活跃会员以及总会员的相关信息
        
会话分析
    
    该分析主要分析会话个数、会话长度和平均会话长度相关的信息

Hourly分析
    
    该分析主要分析每天每小时的用户、会话个数以及会话长度的相关信息 
    
### 4、浏览器信息分析模块
    
    在用户基本信息分析的基础上添加一个浏览器这个维度信息

浏览器会员分析

    同会员分析

浏览器会话分析
   
    同会话分析

浏览器PV分析
   
    分析各个浏览器的pv值

### 5、地域信息分析模块
    
    主要分析各个不同省份的用户和会员情况。
    
    活跃访客地域分析
    
    分析各个不同地域的活跃访客数量   

### 各区域热门商品统计分析

    各区域Top3商品统计
        实现每天统计出各个区域的top3热门商品。该模块可以让企业管理层看到公司售卖的商品的整体情况，从而对公司的商品相关的战略进行调整。主要使用Spark SQL实现
        
### 6、用户访问深度分析模块
   
    该模块主要分析用户的访问记录的深度

外链会话(跳出率)分析
    
    分析会话相关信息，并计算相关会话跳出率的情况
    
浏览器信息分析模块
    
    在用户基本信息分析的基础上添加一个浏览器这个维度信息。
    
浏览器用户分析
    
    同用户分析  

### 7、外链数据分析模块
     
    主要分析各个不同外链端带来的用户访问量数据。
    
    外链偏好分析
    
    分析各个外链带来的活跃访客数量   

  跳出率分析
      
      分析各个不同地域的跳出率情况
    
### 8、订单数据分析模块
    
  主要分析订单的相关情况   
    
### 9、事件分析模块
  
  例如: 订单相关事件

### 10、广告点击流量实时统计
    
    简介:
    
        该模块负责实时统计公司的广告流量，包括广告展现流量和广告点击流量。
    
        实现动态黑名单机制，以及黑名单过滤；
    
        实现滑动窗口内的各城市的广告展现流量和广告点击流量的统计；
    
        实现每个区域每个广告的点击流量实时统计；
    
        实现每个区域top3点击量的广告的统计。主要使用Spark Streaming实现
    
    需求:   
    
        广告统计用户黑名单
    
        广告点击实时统计
    
        实时统计各省最热广告
    
        最近一小时广告点击趋势统计

### 数据可视化

常见的可视化框架
1）echarts
2）highcharts
3）D3.js
4）HUE 
5）Zeppelin
  
## 埋点SDK

### 埋点

[智能推荐之埋点技术应用与实践](https://mp.weixin.qq.com/s/kHp_LWc2OnVdPJqnDwY_pQ)

[美团点评前端无痕埋点实践](https://mp.weixin.qq.com/s/ybf9eIJuvOJFRPql4WWh1w)

[数据采集技术揭秘：手把手教你全埋点技术解决方案](https://mp.weixin.qq.com/s/fCeJzVveMIdDFwg4wfOgLQ)

[【经验】如何做好数据埋点](https://mp.weixin.qq.com/s/ry-uXBUSA6RUbnwWXCmluQ)

Java SDK: 订单支付和退款

JS SDK 

      pageview事件        用户基本信息分析、浏览器信息分析、地域信息分析、外链数据分析、用户浏览深度分析	

      chargeRequest事件   订单信息分析	

      event事件           事件分析

      launch事件

将analytics.js集成到你想收集收集的页面即可。
集成方式主要分为以下两种：

    1) 第一种方式
        `将analytics.js集成到所有页面的的头部，然后通过提供的方法调用进行数据收集。
        <script src='//www.bjsxt.com/js/common/analytics.js'></script>
        <script>__AE__.setMemberId("123456");</script>

    2) 第二种方式
        `使用javascript代码，异步引入analytics.js文件，在引入之前可以通过_aelog_设置会员id
        <script type="text/javascript">
            var _aelog_ = _aelog_ || window._aelog_ || [];
            _aelog_.push(['member_id', '123456']); // 如果此时用户已经登录，那么通过才参数指定用户id
            window._aelog_ = _aelog_;
        
            (function() {
                var amwae = document.createElement('script');
                amwae.type = 'text/javascript';
                amwae.async = true;
                amwae.src = 'resources/js/analytics.js'; // 指定链接
                var script = document.getElementsByTagName('script')[0];
                script.parentNode.insertBefore(amwae, script);
            })();
        </script>
注意：当用户登录后，请调用__AE__.setMemberId('123456')方法进行会员id的设置，方便用户数据的收集。

## 数据处理流程
1）数据采集
	Flume： web日志写入到HDFS

2）数据清洗
	脏数据
	Spark、Hive、MapReduce 或者是其他的一些分布式计算框架  
	清洗完之后的数据可以存放在HDFS(Hive/Spark SQL)

3）数据处理
	按照我们的需要进行相应业务的统计和分析
	Spark、Hive、MapReduce 或者是其他的一些分布式计算框架

4）处理结果入库
	结果可以存放到RDBMS、NoSQL

5）数据的可视化
	通过图形化展示的方式展现出来：饼图、柱状图、地图、折线图
	ECharts、HUE、Zeppelin

### 3、数据清洗
   [数据清洗的一些梳理](https://zhuanlan.zhihu.com/p/20571505)
   [ETL数据清洗工具总结](https://blog.csdn.net/xiaoshunzi111/article/details/51881740)
   
    预处理阶段
    阶段1: 去除/补全有缺失的数据
    阶段2: 去除/修改格式和内容错误的数据
    阶段3: 去除/修改逻辑错误的数据
    阶段4: 去除不需要的数据
    阶段5: 关联性验证
    
## 技术选型
   
   IP解析:纯真IP数据库(qqwry.dat) https://blog.csdn.net/qq_19936739/article/details/73771028
   
   存储格式的选择：http://www.infoq.com/cn/articles/bigdata-store-choose/
   
   压缩格式的选择：https://www.ibm.com/developerworks/cn/opensource/os-cn-hadoop-compression-analysis/

   