package com.sms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class test { private String className="A";private String url="B";private String user="C";private String password="D";private Connection connection;private Statement statement;private ResultSet resultSet;public Connection getConn() {try{Class.forName(className);connection=DriverManager.getConnection(url, user,password);}catch (ClassNotFoundException e) {e.printStackTrace(); } catch (SQLException e) {e.printStackTrace();}return connection;}public ResultSet executeQuery(String sql) {connection=getConn();try {statement = connection.createStatement();resultSet = statement.executeQuery(sql);} catch (SQLException e) {e.printStackTrace();}return resultSet;}public static void main(String[] args) {test test = new test();ResultSet set = test.executeQuery("select * from T");}}