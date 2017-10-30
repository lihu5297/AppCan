<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@include file="taglib.jsp"  %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
  <base href="<%=basePath%>">
    <meta charset="utf-8">
    <title><%=Cache.getSetting("SETTING").getPlatName() %></title>
    <link rel="shortcut icon" href="static/images/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>
    <link href="static/css/bootstrap.min.css" rel="stylesheet">
    <link href='static/css/style.css' rel='stylesheet'>
    <!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
  	<%@include file="head.jsp" %>
    <div class="main">
      <%@include file="left.jsp" %>
      <div class="right">
      		
      	
      	
      	
      	
        <div class="clear"></div>
      </div>
    </div>
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/bootstrap.min.js"></script>
    <script src="static/js/init.js"></script>
  </body>
</html>