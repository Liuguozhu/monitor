package com.coder.monitor.task;


import com.coder.monitor.controller.CommonController;
import com.coder.monitor.service.WeChatService;
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
    private WeChatService weChatService;

    //    @Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
    public void checkNetwork() {
        logger.debug("check Network begin.");
        String host = "www.baidu.com";
        boolean isReachable = false;
        try {
            InetAddress address = InetAddress.getByName(host);//ping this IP
//            String hostAddress = address.getHostAddress();
//            String hostName = address.getHostName();
            if (address instanceof java.net.Inet4Address) {
                System.out.println(host + " is ipv4 address");
            } else if (address instanceof java.net.Inet6Address) {
                System.out.println(host + " is ipv6 address");
            } else {
                System.out.println(host + " is unrecongized");
            }

            if (address.isReachable(1000)) {
                isReachable = true;
                System.out.println("SUCCESS - ping " + host + " with no interface specified");
            } else {
                System.out.println("FAILURE - ping " + host + " with no interface specified");
            }
            System.out.println("\n-------Trying different interfaces--------\n");

            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                System.out.println("Checking interface, DisplayName:" + ni.getDisplayName() +
                        ", Name:" + ni.getName());
                if (address.isReachable(ni, 0, 1000)) {
                    isReachable = true;
                    System.out.println("SUCCESS - ping " + host);
                } else {
                    System.out.println("FAILURE - ping " + host);
                }

                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    System.out.println("IP: " + ips.nextElement().getHostAddress());
                }
                System.out.println("-------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isReachable) {
            logger.debug("DB开始发送异常消息");
            weChatService.sendMessage("FAILURE - ping " + host);
        }
        logger.debug("check Network end.");
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
                    System.out.println("retIP=" + retIP);
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
            System.out.println("SUCCESS - connection established! Local: " +
                    localInetAddr.getHostAddress() + " remote: " +
                    remoteInetAddr.getHostAddress() + " port" + 80);
            isReachable = true;
        } catch (IOException e) {
            System.out.println("FAILRE - CAN not connect! Local: " +
                    localInetAddr.getHostAddress() + " remote: " +
                    remoteInetAddr.getHostAddress() + " port " + 80);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error occurred while closing socket..");
                }
            }
        }
        return isReachable;
    }

    public static void main(String[] args) {
        new NetworkMonitoring().checkNetwork();
    }
}
