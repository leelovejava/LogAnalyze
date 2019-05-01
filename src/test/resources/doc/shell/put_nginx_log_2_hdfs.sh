#!/bin/bash

# 手动上传Nginx日志
# 计算昨天的日期
yesterday=$(date --date='1 days ago' +'%Y/%m/%d')

# 存放今天之前的所有的日志存放的目录
LOGS_PATH=/usr/local/nginx/user_logs/logs

# HDFS存放日志的目录
HDFS_LOGS_PATH=/event_logs/${yesterday}

export HADOOP_USER_NAME=admin

# 递归创建HDFS存储目录
/opt/modules/cdh/hadoop-2.5.0-cdh5.3.6/bin/hdfs dfs -mkdir -p ${HDFS_LOGS_PATH}

# 开始上传
/opt/modules/cdh/hadoop-2.5.0-cdh5.3.6/bin/hdfs dfs -put -f -p ${LOGS_PATH}/$(date -d "yesterday" +"%Y")/$(date -d "yesterday" +"%m")/$(date -d "yesterday" +"%d")/access_*.log ${HDFS_LOGS_PATH}