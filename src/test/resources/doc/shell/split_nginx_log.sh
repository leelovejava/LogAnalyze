#!/bin/bash

# 分割Nginx日志，按天保存
# 实时日志存放地
LOGS_PATH="/usr/local/nginx/user_logs"
# 生成旧日志存放地路径
OLD_LOGS_PATH=${LOGS_PATH}/logs/$(date -d "yesterday" +%Y)/$(date -d "yesterday" +%m)/$(date -d "yesterday" +%d)

# 创建旧日志存放目录，如果目录不存在（1、先判断，再创建，否则可能出错。2、使用-p递归操作即使存在也不会报错）
mkdir -p ${OLD_LOGS_PATH}

# 移动昨天的日志文件到指定目录
mv ${LOGS_PATH}/access.log ${OLD_LOGS_PATH}/access_$(date -d "yesterday" +%Y%m%d_%H%M%S).log

# 重启Nginx刷新文件句柄
kill -USR1 `cat /usr/local/nginx/logs/nginx.pid`
