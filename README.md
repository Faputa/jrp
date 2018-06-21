# java reverse proxy

## 解决什么问题
* 将对公网服务器的访问代理到本地，简而言之就是一个分布式的反向代理

* 目前仅支持tcp代理，理论上基于tcp的一切网络服务都可代理

### 1.原理
* jrp分为服务端和客户端两个部分，服务端通常部署在公网，客户端部署在内网，客户端和服务端相连接。

* 一个服务器可以服务多个客户端，一个客户端又可服务多个本地机器，每个本地机器都可请求一个服务器端口用于监听外部请求，并指定本机上的一个端口用于接收前者转发过来的数据

* 服务端接收到外部tcp连接之后就将数据流转发到客户端，客户端再将数据流发送给本地机器。同样的客户端将收到本地机器的数据流转发给服务端，再由服务端发送给外部连接，从而实现代理

### 2.用法
#### 首先配置运行服务端：
* 生成SSL证书

    生成的证书和密码（你可以使用项目自带的证书和密码）：
    ```shell
    keytool -genkey -keystore 你的证书名字.jks -storepass 你的证书密码 -keyalg RSA -keypass 你的证书密码
    ```
* 将证书放置到jrp-server/resource/下面
* 配置jrp-server/resource/server.json
    ```javascript
    {
        "sslKeyStore":"resource/你的证书名字.jks",
        "sslKeyStorePassword":"你的证书密码",
        "port":4443,/*服务端用以监听客户端的端口*/
        "token":"CMwClAfRttJtFJgo4jYh4PJ4giR8uJxhdkfcjOB1",/*认证Token*/
        "enableLog":true/*是否开启日志*/
    }
    ```
* 运行jrp-server

    可以通过两种方式运行，直接运行start.bat/sh或者注册到服务运行（仅windows）。
    
    需保证服务器上面装有java环境，如果无法直接取得java环境，需在start脚本中指定java。

#### 配置运行客户端
* 配置jrp-client/resource/client.json
    ```javascript
    {
        "tunnelList":[/*需代理的本地机器列表*/
            {
                "remotePort":12345,/*请求服务器监听的端口，对服务器该端口的访问将被转发到本机*/
                "localHost":"127.0.0.1",/*本机位于内网中的ip*/
                "localPort":8080/*本机的监听端口，对代理的访问将被转发至该端口*/
            }
        ],
        "serverHost":"lelepark.com",/*服务器域名（ip也行）*/
        "serverPort":4443,/*服务器监听客户端的端口*/
	    "authToken":"CMwClAfRttJtFJgo4jYh4PJ4giR8uJxhdkfcjOB1",/*认证Token*/
        "enableLog":true,/*是否打印日志*/
        "pingTime":60000/*主连接心跳包周期，太短服务器压力太大，太长主连接可能睡死*/
    }
    ```
* 运行jrp-client

    直接运行start.bat/sh

## 代码结构
代码主要分为三个部分

    jrp-common
    jrp-server
    jrp-client
其中jrp-server为服务端，jrp-client为客户端，jrp-common为公共部分，被服务端和客户端所依赖

其他部分

    lib
    release
lib为第三方依赖包目录，release为发布目录，包括服务端和客户端，可直接运行
