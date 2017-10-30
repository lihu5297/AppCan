<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 平台形象</title>
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
					class="active">平台形象</a>
			</div>
			<div class="content">
			<div class="right-content">
				<div class="right-header" >
					<button type="button" class="btn btn-primary" name="INFO"
						id="saveButton">保存</button>
					<form method="post" action="" enctype="multipart/form-data"
						id="editForm_INFO">
						<table>
							<tr height="140px">
								<td><span style="margin:0 10px">平台形象：</span></td>
								<td style="position: relative;"><img height="100px" width="100px" id="logo"
									src="${set.platLogo==null||set.platLogo==''?'static/images/logo.jpg':set.platLogo}"
									name="logo" title="头像" alt="头像" style="background:#e9e9e9;";/>
									<span style="margin-bottom:10px;position:absolute;top: 94px;left: 120px;"><input onclick="openFileUpload()" type="button" value="上传"></span></td>
								<td><input id="logoFile" name="myfiles" type="file"
									value="" style="display: none" onchange="changeIcon()"> <input id="id"
									name="id" value="${set.id }" style="display: none" /> <input
									id="platLogo" name="platLogo" value="${set.platLogo }"
									style="display: none" /></td>
							</tr>
							<tr>
								<td><span style="margin:0 10px">平台名称：</span></td>
								<td><input name="platName" id="platName"
									value="${set.platName }" style="width: 300px;margin:25px 0px" /></td>
							</tr>
							<tr>
								<td><span style="margin:0 10px">协同开发地址：</span></td>
								<td><input name="webAddr" id="webAddr"
									value="${set.webAddr }" style="width: 300px;" /></td>
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
	<script src="static/platForm/js/initpage.js"></script>
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