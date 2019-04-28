# nginx配置
1、依赖 gcc openssl-devel pcre-devel zlib-devel
	安装：
> yum install gcc openssl-devel pcre-devel zlib-devel -y

> wget http://nginx.org/download/nginx-1.15.12.tar.gz

安装Nginx
> mkdir -p /hadoop000/app/nginx

> sudo ./configure --prefix=/hadoop000/app/nginx && make && make install

默认安装目录：
whereis nginx

/usr/local/nginx

配置
```
http{
    log_format my_format '$remote_addr^A$msec^A$http_host^A$request_uri';
}
server{
    location = /log.gif {
              #default_type image/gif;
              #access_log /opt/data/access.log my_format;
              # 开启响应1x1空白图片
              empty_gif;
    }
}
```

命令
sbin/nginx
sbin/nginx -s reload
sbin/nginx -s stop

配置Nginx为系统服务，以方便管理
  1、在/etc/rc.d/init.d/目录中建立文本文件nginx
  2、在文件中粘贴下面的内容：
```
#!/bin/sh
#
# nginx - this script starts and stops the nginx daemon
#
# chkconfig:   - 85 15
# description:  Nginx is an HTTP(S) server, HTTP(S) reverse \
#               proxy and IMAP/POP3 proxy server
# processname: nginx
# config:      /etc/nginx/nginx.conf
# config:      /etc/sysconfig/nginx
# pidfile:     /var/run/nginx.pid

# Source function library.
. /etc/rc.d/init.d/functions

# Source networking configuration.
. /etc/sysconfig/network

# Check that networking is up.
[ "$NETWORKING" = "no" ] && exit 0

# nginx的安装目录
nginx="/usr/local/nginx/sbin/nginx"
prog=$(basename $nginx)

# nginx配置目录
NGINX_CONF_FILE="/usr/local/nginx/conf/nginx.conf"

[ -f /etc/sysconfig/nginx ] && . /etc/sysconfig/nginx

lockfile=/var/lock/subsys/nginx

make_dirs() {
   # make required directories
   user=`nginx -V 2>&1 | grep "configure arguments:" | sed 's/[^*]*--user=\([^ ]*\).*/\1/g' -`
   options=`$nginx -V 2>&1 | grep 'configure arguments:'`
   for opt in $options; do
       if [ `echo $opt | grep '.*-temp-path'` ]; then
           value=`echo $opt | cut -d "=" -f 2`
           if [ ! -d "$value" ]; then
               # echo "creating" $value
               mkdir -p $value && chown -R $user $value
           fi
       fi
   done
}

start() {
    [ -x $nginx ] || exit 5
    [ -f $NGINX_CONF_FILE ] || exit 6
    make_dirs
    echo -n $"Starting $prog: "
    daemon $nginx -c $NGINX_CONF_FILE
    retval=$?
    echo
    [ $retval -eq 0 ] && touch $lockfile
    return $retval
}

stop() {
    echo -n $"Stopping $prog: "
    killproc $prog -QUIT
    retval=$?
    echo
    [ $retval -eq 0 ] && rm -f $lockfile
    return $retval
}

restart() {
    configtest || return $?
    stop
    sleep 1
    start
}

reload() {
    configtest || return $?
    echo -n $"Reloading $prog: "
    killproc $nginx -HUP
    RETVAL=$?
    echo
}

force_reload() {
    restart
}

configtest() {
  $nginx -t -c $NGINX_CONF_FILE
}

rh_status() {
    status $prog
}

rh_status_q() {
    rh_status >/dev/null 2>&1
}

case "$1" in
    start)
        rh_status_q && exit 0
        $1
        ;;
    stop)
        rh_status_q || exit 0
        $1
        ;;
    restart|configtest)
        $1
        ;;
    reload)
        rh_status_q || exit 7
        $1
        ;;
    force-reload)
        force_reload
        ;;
    status)
        rh_status
        ;;
    condrestart|try-restart)
        rh_status_q || exit 0
            ;;
    *)
        echo $"Usage: $0 {start|stop|status|restart|condrestart|try-restart|reload|force-reload|configtest}"
        exit 2
esac
```

3、修改nginx文件的执行权限
	
	chmod +x nginx
	
	chmod -R 777 root/app/nginx
	
4、添加该文件到系统服务中去
	
	chkconfig --add nginx
	
	查看是否添加成功
	chkconfig --list nginx

启动，停止，重新装载
    
    service nginx start|stop|reload
    systemctl
    
修改nginx.conf配置文件，达到保存日志信息的目的
配置log.gif
(须与客户端js文件的tracker的请求serverUrl : 保持一致）


错误:
1).
    Failed to start SYSV: Nginx is an HTTP(S) server, HTTP(S) reverse proxy and IMAP/POP3 proxy server
    
    原因:
    1.
    /usr/local/nginx/sbin/nginx启动了nginx
    解决:
    kill -9 之前进程+

    2. 指定nginx目录错误
    修改配置并加载配置
    systemctl daemon-reload

2).
    Reloading nginx configuration (via systemctl):  Job for nginx.service invalid.
    
    systemctl status nginx.service -l
    
    Unit nginx.service cannot be reloaded because it is inactive

3).Nginx出现403 forbidden
https://blog.csdn.net/qq_35843543/article/details/81561240    