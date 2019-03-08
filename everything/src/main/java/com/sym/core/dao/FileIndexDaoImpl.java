package com.sym.core.dao;

import com.sym.core.model.Condition;
import com.sym.core.model.FileType;
import com.sym.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;
    public FileIndexDaoImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public void insert(Thing thing) {

        Connection connection = null;
        PreparedStatement statement = null;
        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备数据库语言
            String sql="insert into file_index(name,path,depth,file_type) values(?,?,?,?)";
            statement =connection.prepareStatement(sql);
            statement.setString(1,thing.getName());
            statement.setString(2,thing.getPath());
            statement.setInt(3,thing.getDepth());
            statement.setString(4,thing.getFileType().name());
            statement.executeUpdate();
        }catch (SQLException e){
        }
        finally {
            releaseResource(null,statement,connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备数据库语言
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select name,path,depth,file_type from FILE_INDEX ");
            sqlBuilder.append(" where ");
            sqlBuilder.append(" name like '%").append(condition.getName()).append("%' ");
            if(condition.getFileType()!=null){
                sqlBuilder.append(" and file_type = '")
                          .append(condition.getFileType().toUpperCase()).append("' ");
            }
            sqlBuilder.append(" order by depth ")
                      .append(condition.getOrderByAsc().equals(true) ? " asc " : " desc ");
            sqlBuilder.append(" limit ")
                      .append(condition.getLimit())
                      .append(" offset 0 ");
            System.out.println(sqlBuilder);
            statement =connection.prepareStatement(sqlBuilder.toString());
            //name:like , filetype : = ，limit ：limit offset ，orderbyAsc：order by

            resultSet = statement.executeQuery();
            while (resultSet.next()){
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType =resultSet.getString("file_type");
                thing.setFileType(FileType.lookBYName(fileType));
                things.add(thing);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            releaseResource(resultSet,statement,connection);
            return things;
        }
    }

    @Override
    public void delete(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备数据库语言
            String sql="delete from file_index where path like '"+thing.getPath()+"%'";
            statement =connection.prepareStatement(sql);
            statement.setString(1,thing.getPath());
            statement.executeUpdate();
        }catch (SQLException e){
        }
        finally {
            releaseResource(null,statement,connection);
        }
    }

    private void releaseResource(ResultSet resultSet,PreparedStatement statement,Connection connection){
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
