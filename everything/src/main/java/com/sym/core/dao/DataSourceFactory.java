package com.sym.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.sym.config.EverythingPlusConfig;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 工厂设计模式+单利
 */
public final class DataSourceFactory {
    private static volatile DruidDataSource dataSource;
    private DataSourceFactory(){}
    public static DataSource dataSource(){
        if(dataSource == null){
            synchronized (DataSourceFactory.class){
                if(dataSource == null){
                    dataSource = new DruidDataSource();

                    dataSource.setDriverClassName("org.h2.Driver");
                    //采用h2的嵌入式数据库，数据库以本地文件方式存储
                    //获取当前工厂路径
                    dataSource.setUrl("jdbc:h2:"+EverythingPlusConfig.getInstance().getH2IndexPath());
                    //Druid数据库连接池的可配置参数
                    dataSource.setValidationQuery("select now()");
                    //Or dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase(){
        //获取数据源，获取SQL语句,获取数据库连接和命令执行sql
        DataSource dataSource = DataSourceFactory.dataSource();
        //不采取读取绝对路径下文件。读取classpath路径下的文件
        try(InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("everything_plus.sql");){
            if (in == null) {
                throw new RuntimeException("Not read init database script please check in");
            }
            StringBuilder sqlBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in));){
                String line ;
                while ((line=reader.readLine())!=null){
                    if(!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sql = sqlBuilder.toString();
            //1.获取数据库连接
            Connection connection = dataSource.getConnection();
            //2.创建命令
            PreparedStatement statement =connection.prepareStatement(sql);
            //3.执行命令
            statement.execute();

            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
