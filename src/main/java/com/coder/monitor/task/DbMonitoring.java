package com.coder.monitor.task;

//import com.coder.monitor.service.MongoService;
import com.coder.monitor.service.WeChatService;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * 数据库(mysql & mongodb)监控 TODO 可以拆分二
 */
@Component
@EnableScheduling
public class DbMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(DbMonitoring.class);
    @Autowired
    private WeChatService weChatService;

    //    @Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
    public void checkDb() {
        logger.debug("check DB begin.");
        //如果有哪个有异常.组装进推送给公众号的string里
        StringBuilder message = new StringBuilder();

        //测试每个数据库的链接情况
        Config.initDataSourceList(Config.DB);

        if (Config.dataSourceInfoList != null && Config.dataSourceInfoList.size() > 0) {
            for (Config.DataSourceInfo dataSourceInfo : Config.dataSourceInfoList) {
                String url = dataSourceInfo.getUrl();
                String userName = dataSourceInfo.getUsername();
                String password = dataSourceInfo.getPassword();
                DbStatus dbStatus = executeCheck(url, userName, password);
                if (dbStatus.isStatus())
                    message.append(dbStatus.getMessage());
            }
        }
        if (!TextUtils.isBlank(message.toString())) {
            logger.debug("DB开始发送异常消息");
            weChatService.sendMessage(message.toString());
        }
        logger.debug("check DB end.");

    }

    //    @Scheduled(fixedDelay = 5000)
//    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
//    private void monitorMongo() {
//        String message = "mongo 异常:";
//        String oneHost = null;
//        String dbName = null;
//        boolean isSendMess = false;
//        try {
//            for (String key : MongoService.map.keySet()) {
//                MongoService.monitorOne(key, MongoService.map.get(key));
//                oneHost = key;
//                dbName = MongoService.map.get(key);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            message += oneHost;
//            message += ":";
//            message += dbName;
//            isSendMess = true;
//        }
//        if (isSendMess) {
//            weChatService.sendMessage(message);
//        }
//    }

    private DbStatus executeCheck(String url, String userName, String password) {
        DbStatus dbstatus = new DbStatus();
        Connection conn = null;
        for (int i = 0; i <= 2; i++) {
            try {
                conn = getConnection(url, userName, password);
                if (conn != null && !conn.isClosed()) {
                    break;
                }
            } catch (Exception e) {
                if (i >= 2) {
                    logger.error(e.getMessage());
                    dbstatus.setStatus();
                    dbstatus.setMessage("DB [" + url + "] create connection error：" + e.getMessage() + ".");
                    return dbstatus;
                }
            }
        }

        try {
            String message = executeSql(conn);
            dbstatus.setMessage(message);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            dbstatus.setStatus();
            dbstatus.setMessage("DB [" + url + "] execute query error:" + e.getMessage() + ";");
        }

        try {
            closeConnection(conn);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            dbstatus.setStatus();
            dbstatus.setMessage(dbstatus.getMessage() + "DB [\"+url+\"] close error.");
        }
        return dbstatus;
    }

    public Connection getConnection(String url, String userName, String password) throws Exception {
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverClassName);//指定连接类型
        DriverManager.setLoginTimeout(20000);//毫秒
        return DriverManager.getConnection(url, userName, password);//获取连接
    }

    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    public String executeSql(Connection conn) throws SQLException {
        String sql = "SELECT  USER()";
        // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
        Statement stmt = conn.createStatement();
//    String sql = "create table student(NO char(20),name varchar(20),primary key(NO))";
//    int result = stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
        ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
//        while (rs.next()){
//           String tableName= rs.getString(1);
//            System.out.println(tableName);
//        }
        stmt.close();
        if (rs == null) {
            return "show tables false,";
        } else {
            rs.close();
            return "";
        }
    }

    class DbStatus {
        private boolean status;//状态 true异常，false 正常
        private String message;//异常信息

        DbStatus() {
        }

        boolean isStatus() {
            return status;
        }

        void setStatus() {
            this.status = true;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
