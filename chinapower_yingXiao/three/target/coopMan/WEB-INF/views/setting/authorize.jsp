<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 授权信息</title>
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
	<input type="hidden" value="<%=basePath%>" id="basePath">
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:;">平台设置</a> / <a href="javascript:;"
					class="active">授权信息</a>
			</div>
			<div class="content">
			<div class="right-content">
				<div class="right-header" style="margin-top:30px">
					
					<form method="post" action="" enctype="multipart/form-data"
						id="editForm_AUTH">
						<table>
						<tr>
								<td><span style="margin:0 10px">license有效日期截止：</span></td>
								<td>${set.authDeadTime }</td>
							</tr>
							<tr height="140px">
								<td><span style="margin:0 10px 0 66px">license文件：</span></td>
								<td><input style="width:500px;height:30px" id="authorizePath" name="authorizePath" value="${set.authorizePath}" type="text" />
									<span style="margin-bottom:10px"><input onclick="openFileUpload()" type="button" value="浏览" style="border-top-left-radius: 0px;border-bottom-left-radius: 0px;" class="btn btn-primary"></span></td>
								<td><input id="authorizepath" name="myfiles" type="file" value="" style="display: none"> 
									<input id="id" name="id" value="${set.id }" style="display: none" /> 
									<button type="button" class="btn btn-primary" name="AUTH" id="saveButton">更新</button>
							</tr>
							
						</table>
					</form>
					
				</div>
				
			</div>
			
		</div>
		<div class="clear"></div>
	</div>
	
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script src="static/authorize/js/initpage.js"></script>
	<script src="static/admin/js/ajaxFileUpload.js"></script>
	<script>
		$(function(){
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(8).find('.nav-pills').show();
			$('.menuNav>li').eq(8).css('background-color','#383f4e');
		})
	</script>
</body>
</html>