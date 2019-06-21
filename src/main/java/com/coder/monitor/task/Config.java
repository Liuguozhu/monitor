package com.coder.monitor.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取resource配置，TODO 后续修改为通过后台配置
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    public static final String DB = "dataSource";
    static final String REDIS = "redis";
    static final String MQ = "ActiveMQ";
    static List<DataSourceInfo> dataSourceInfoList = null;
    static List<RedisInfo> redisInfoList = null;
    static List<String> activeMQInfoList = null;

    static void initDataSourceList(String resourceName) {

        String xmlName = "";
        if (DB.equals(resourceName))
            xmlName = "db.xml";
        if (REDIS.equals(resourceName))
            xmlName = "redis.xml";
        if (MQ.equals(resourceName))
            xmlName = "activeMQ.xml";

        String path;
        if (System.getProperties().getProperty("os.name").contains("Mac OS X")
                || System.getProperties().getProperty("os.name").contains("Linux")) {
            path = "/" + Config.class.getResource("/").toString().replace("file:/", "").replace("%20", " ")  + "monitor/" + xmlName;
        } else {
            path = "/" + Config.class.getResource("/").toString().replace("file:/", "").replace("%20", " ")  + "monitor/" + xmlName;
//            path = System.getProperties().getProperty("user.dir") + "/src/main/resources/base/" + xmlName;
        }
        readXml(path, resourceName);
    }

    private static void readXml(String path, String sourceName) {
        File f = new File(path);

        DocumentBuilder db;
        DocumentBuilderFactory dbf;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            Document dt = db.parse(f);
            Element element = dt.getDocumentElement();
//            System.out.println("根元素：" + element.getNodeName());
            if (DB.equals(sourceName))
                parseDataSource(element);
            if (REDIS.equals(sourceName))
                parseRedis(element);
            if (MQ.equals(sourceName))
                parseActiveMQ(element);

        } catch (Exception e) {
            logger.error("dataSource init error=" + e.getMessage());
        }
    }

    public static void parseDataSource(Element element) {
        dataSourceInfoList = new ArrayList<DataSourceInfo>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node1 = childNodes.item(i);
            if (!"dataSource".equals(node1.getNodeName()))
                continue;
            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            NodeList nodeDetail = node1.getChildNodes();
            for (int j = 0; j < nodeDetail.getLength(); j++) {
                Node detail = nodeDetail.item(j);
                if ("url".equals(detail.getNodeName()))
                    dataSourceInfo.setUrl(detail.getTextContent());
                if ("username".equals(detail.getNodeName()))
                    dataSourceInfo.setUsername(detail.getTextContent());
                if ("password".equals(detail.getNodeName()))
                    dataSourceInfo.setPassword(detail.getTextContent());
            }
            dataSourceInfoList.add(dataSourceInfo);
        }
//            System.out.println("数据库个数 " + dataSourceInfoList.size());
    }

    public static void parseRedis(Element element) {
        redisInfoList = new ArrayList<RedisInfo>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node1 = childNodes.item(i);
            if (!"redis".equals(node1.getNodeName()))
                continue;
            RedisInfo redisInfo = new RedisInfo();
            NodeList nodeDetail = node1.getChildNodes();
            for (int j = 0; j < nodeDetail.getLength(); j++) {
                Node detail = nodeDetail.item(j);
                if ("ip".equals(detail.getNodeName()))
                    redisInfo.setIp(detail.getTextContent());
                if ("port".equals(detail.getNodeName()))
                    redisInfo.setPort(detail.getTextContent());
            }
            redisInfoList.add(redisInfo);
        }
//        System.out.println("redis个数 " + redisInfoList.size());
    }

    public static void parseActiveMQ(Element element) {
        activeMQInfoList = new ArrayList<String>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node1 = childNodes.item(i);
            if (!"activeMQ".equals(node1.getNodeName()))
                continue;
            NodeList nodeDetail = node1.getChildNodes();
            for (int j = 0; j < nodeDetail.getLength(); j++) {
                Node detail = nodeDetail.item(j);
                if ("url".equals(detail.getNodeName()))
                    activeMQInfoList.add(detail.getTextContent());
            }
        }
//        System.out.println("activeMQ个数 " + redisInfoList.size());
    }


    static class DataSourceInfo {
        private String url;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class RedisInfo {
        private String ip;
        private String port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }


    public static void main(String[] args) {
//        initDataSourceList(DB);
//        initDataSourceList(REDIS);
        initDataSourceList(MQ);
    }

}
