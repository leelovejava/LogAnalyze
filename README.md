# 日志分析

## 分析

### 1、总述
    
    用户基本信息分析模块、浏览器信息分析模块、地域信息分析模块、用户浏览深度分析模块、外链数据分析模块、订单分析模块以及事件分析模块。下面就每个模块进行最终展示的分析

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