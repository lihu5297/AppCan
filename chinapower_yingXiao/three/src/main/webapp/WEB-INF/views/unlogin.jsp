<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@page import="org.zywx.coopman.system.Cache"%>
     <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Error - 没有权限</title>
<link href="static/css/index.css" rel="stylesheet" type="text/css" />
<script src="static/js/jquery.min.js"></script>
</head>
<body style="background:url(static/img/404back.png);margin:0;">
  <div class="error-main1">
      <div class="error-main-left">
      </div>
      <div class="error-main-right">
        <h2 id="type">登录失败，3秒后将跳转登陆界面</h2>
        <!-- <h3>可能是如下原因导致：</h3>
        <p><span></span>url输入错误</p>
        <p><span></span>连接错误</p> -->
        <button style="margin-left:110px;margin-top:55px" onclick="goback()">返回上一页</button>
        <button style="margin-top:55px" id="index"><a href="javascript:;">返回首页</a></button>
      </div>
      <div style="clear:both"></div>
  </div>
</body>
</html>
<script type="text/javascript">
	function getQueryString(name) {
	    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	    var r = window.location.search.substr(1).match(reg);
	    return  r != null ? unescape(r[2]) : null;
	}
	var err=getQueryString("err");
	if(err!=undefined){
	    $("#type").html(err)
	}
	function goback(){
	  history.go(-1);
	}
	$("#index").click(function(){
	  window.location.href="login";
	});

	setTimeout("remainTime()",3000);
	function remainTime(){
		window.location = "login";
	}
	var time = 3;
	setInterval("myInterval()",1000);//1000为1秒钟
    function myInterval()
    {
		time = time -1;
         $("#type").text("登录失败，"+time+"秒后将跳转登陆界面");
     }

	</script>
          		
