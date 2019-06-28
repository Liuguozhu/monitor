package com.coder.monitor.task;


import com.coder.monitor.service.SendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

@Component
@EnableScheduling
public class NetworkMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(NetworkMonitoring.class);
    @Autowired
    private SendMessageService sendMessageService;

    //    @Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
    public void checkNetwork() {
        logger.debug("check Network begin.");
        String host = "www.baidu.com";
        if (!isNodeReachable(host)) {
            logger.debug("DB开始发送异常消息");
            sendMessageService.sendMessage("FAILURE - ping " + host);
        }
        logger.debug("check Network end.");

//        boolean isReachable = false;
//        try {
//            InetAddress address = InetAddress.getByName(host);//ping this IP
////            String hostAddress = address.getHostAddress();
////            String hostName = address.getHostName();
//            if (address instanceof java.net.Inet4Address) {
//                logger.info(host + " is ipv4 address");
//            } else if (address instanceof java.net.Inet6Address) {
//                logger.info(host + " is ipv6 address");
//            } else {
//                logger.info(host + " is unrecognized");
//            }
//
//
//            /*
//            Test whether that address is reachable. Best effort is made by the implementation to try to reach the host, but firewalls and server configuration may block requests resulting in a unreachable status while some specific ports may be accessible.
//            A typical implementation will use ICMP ECHO REQUESTs if the privilege can be obtained,
//            otherwise it will try to establish a TCP connection on port 7 (Echo) of the destination host.
//            上面英文第二行的意思是，如果本机的权限允许，那么isReachable采用跟PING一样的方式取连接远程服务器。 但是默认情况下，你绝逼没有这个权限。
//            上面英文第三行的意思是，要是没有权限，那么尝试TCP连接远程服务器的7号端口。所以，如果你能够telnet ip 7成功的话，isReachable返回就是true了。
//             */
//            if (address.isReachable(1000)) {
//                isReachable = true;
//                logger.info("SUCCESS - ping " + host + " with no interface specified");
//            } else {
//                logger.info("FAILURE - ping " + host + " with no interface specified");
//            }
//            logger.info("\n-------Trying different interfaces--------\n");
//
//            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
//            while (netInterfaces.hasMoreElements()) {
//                NetworkInterface ni = netInterfaces.nextElement();
//                logger.info("Checking interface, DisplayName:" + ni.getDisplayName() + ", Name:" + ni.getName());
//                if (address.isReachable(ni, 0, 1000)) {
//                    isReachable = true;
//                    logger.info("SUCCESS - ping " + host);
//                } else {
//                    logger.info("FAILURE - ping " + host);
//                }
//
//                Enumeration<InetAddress> ips = ni.getInetAddresses();
//                while (ips.hasMoreElements()) {
//                    logger.info("IP: " + ips.nextElement().getHostAddress());
//                }
//                logger.info("-------------------------------------------");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (!isReachable) {
//            logger.debug("DB开始发送异常消息");
//            sendMessageService.sendMessage("FAILURE - ping " + host);
//        }
//        logger.debug("check Network end.");
    }

    private static boolean isNodeReachable(String hostname) {
        try {
            return 0 == Runtime.getRuntime().exec("ping -c 1 " + hostname).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    //    @Scheduled(fixedDelay = 5000)
//    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
    public void checkNetwork2() {
        logger.debug("check Network begin.");
        String ip = "www.baidu.com";
        String retIP;
        try {
            InetAddress remoteAddress = InetAddress.getByName(ip);//ping this IP
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> localAddrs = ni.getInetAddresses();
            while (localAddrs.hasMoreElements()) {
                InetAddress localAddr = localAddrs.nextElement();
                if (isReachable(localAddr, remoteAddress)) {
                    retIP = localAddr.getHostAddress();
                    logger.info("retIP=" + retIP);
                    break;
                }
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        logger.debug("check Network end.");
    }

    private boolean isReachable(InetAddress localInetAddr, InetAddress remoteInetAddr) {

        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();
            // 端口号设置为 0 表示在本地挑选一个可用端口进行连接
            SocketAddress localSocketAddr = new InetSocketAddress(localInetAddr, 0);
            socket.bind(localSocketAddr);
            InetSocketAddress endpointSocketAddr =
                    new InetSocketAddress(remoteInetAddr, 80);
            socket.connect(endpointSocketAddr, 5000);
            logger.info("SUCCESS - connection established! Local: " +
                    localInetAddr.getHostAddress() + " remote: " +
                    remoteInetAddr.getHostAddress() + " port" + 80);
            isReachable = true;
        } catch (IOException e) {
            logger.info("FAILRE - CAN not connect! Local: " +
                    localInetAddr.getHostAddress() + " remote: " +
                    remoteInetAddr.getHostAddress() + " port " + 80);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.info("Error occurred while closing socket..");
                }
            }
        }
        return isReachable;
    }

    public static void main(String[] args) {
        new NetworkMonitoring().checkNetwork();
    }
}
