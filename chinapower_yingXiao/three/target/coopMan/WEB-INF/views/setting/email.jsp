<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
#editForm_EMAIL tr{
	height:40px;
}</style>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 邮件设置</title>
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
					class="active">邮件设置</a>
			</div>
			<div class="content">
			<div class="right-header">
				
				<div class="modal-body">
					<form method="post" action="" enctype="multipart/form-data"
						id="editForm_EMAIL">
						<div style="min-height: 100px;">

							<%-- <input id="id" name="id" value="${set.id==null?1:set.id }" style="display: none" /> --%>
							<input id="id" name="id" value="${set.id }" style="display: none" />
							<table>
								<tr>
									<td>邮件服务：</td>
									<td>
									<c:if test="${set.emailServerStatus=='OPEN' }"><input name="emailServerStatus" id="emailServerStatus1"
										type="radio" value="OPEN" checked="checked"/>启用 <input
										name="emailServerStatus" id="emailServerStatus2" type="radio"
										value="CLOSE" />不启用
										</c:if>
										<c:if test="${set.emailServerStatus=='CLOSE' }"><input name="emailServerStatus" id="emailServerStatus1"
										type="radio" value="OPEN" />启用 <input
										name="emailServerStatus" id="emailServerStatus2" type="radio"
										value="CLOSE" checked="checked"/>不启用
										</c:if>
										</td>
								</tr>
								<tr>
									<td>服务器类型：</td>
									<td><input name="emailServerType" id="emailServerType"
										value="${set.emailServerType }" /></td>
								</tr>
								<tr>
									<td>邮件服务器：</td>
									<td><input name="emailServerUrl" id="emailServerUrl"
										value="${set.emailServerUrl }" /><button type="button" onclick="testEmail()">检测</button></td>
								</tr>
								<tr>
									<td>端口号：</td>
									<td><input name="emailServerPort" id="emailServerPort"
										value="${set.emailServerPort }" /></td>
								</tr>
								<tr>
									<td>发件人邮箱：</td>
									<td><input name="emailAccount" id="emailAccount"
										value="${set.emailAccount }" /><button type="button" onclick="testPersonalEmail()">检测邮箱</button></td>
								</tr>
								<tr>
									<td>发件人邮箱密码：</td>
									<td><input name="emailPassword" id="emailPassword"
										value="${set.emailPassword }" /></td>
								</tr>

							</table>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<!-- <button type="button" class="btn btn-default" data-dismiss="modal">取消</button> -->
					<button type="button" class="btn btn-primary" name="EMAIL"
						id="saveButton">保存</button>
				</div>
			</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script src="static/email/js/initpage.js"></script>
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