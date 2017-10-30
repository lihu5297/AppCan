<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@page import="org.zywx.coopman.system.Cache"%>
     <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title><%=Cache.getSetting("SETTING").getPlatName() %></title>
    <link rel="shortcut icon" href="static/images/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="static/css/bootstrap.min.css" rel="stylesheet">
    <link href='static/css/style.css' rel='stylesheet'>
    <link rel="stylesheet" href="static/css/login.css">
    <!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
	<img src="static/img/loginbg.jpg" alt="" class="logobg">
	<div class="logo">
		<!-- <img id="imgLogo" src="static/img/logo.png" style="width:60px; height:60px;"  alt="">
		 --><img src="static/img/logoName.png" style="height:62px; width:auto;">
	</div>
	<div action="#" class="info">
		<form action="index" class="info" id="loginForm">
			<input type="text" name="account" class="userName" placeholder="用户名" id="userName">
			<input type="password" name="password" class="password" placeholder="登录密码" id="password">
			<!-- <input type="text" name="checkcode" class="checkcode fl" placeholder="验证码" id="CAPTCHA">
			<span class="code" id="CAPTCHAval" onclick="getCAPTCHA()"></span> -->
			<button class="btn btn-primary loginIn" onclick="$('#loginForm').submit()">登录</button>
		</form>
	</div>
	<div id="copyright">
		<p class="copyright">Copyright  2010-2017 All rights reserved <br>国家电网公司 版权所有</p>
	</div>
	<script type="text/javascript">
	var errMsg = "${err}";
	if(errMsg.length > 0){
		alert(errMsg);
	}
	document.onkeydown = function(event) {
		e = event ? event :(window.event ? window.event : null);
		if(e.keyCode == 13){
			$('#loginForm').submit();
		}
	};
	</script>
  </body>
</html>