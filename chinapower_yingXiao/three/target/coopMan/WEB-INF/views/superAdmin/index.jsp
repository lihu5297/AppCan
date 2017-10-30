<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
.superadmintable input{
	min-width: 230px;
}
</style>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title>协同开发后台</title>
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
			<div class="right-header">

				<div style="height:36px;margin-top: 20px;">
					<a class="title" href="javascript:;">用户管理</a>&emsp;<span class="title">/</span>&emsp;<span class="title">平台运维人员</span>
					<button type="button" class="btn btn-primary "
						data-toggle="modal" data-target="#openEditInfo"
						onclick="openEdit()">编辑</button>
					<button type="button" class="btn btn-primary " onclick="resetpwd(${manager.id })"
						data-toggle="modal" data-target="#reSettingPWD<%=Cache.getSetting("SETTING").getEmailServerStatus().compareTo(EMAIL_STATUS.OPEN)==0?"_EMAIL":"" %>"
						>重置密码</button>
				</div>
				<div style="width: 120px; height: 120px;margin:20px; float: left">
					<img height="120px" width="120px" id="iconInfo" src="${manager.icon==''||manager.icon==null?'static/images/logo.jpg':manager.icon }" name="iconInfo" title="头像" alt="头像" />
				</div>
				<div style="min-height: 100px;float:left;margin: 20px;">
					<table class="superadmintable">
						<tr>
							<td>用户名：</td>
							<td><span id="accountInfo" name="accountInfo">${manager.account }</span></td>
						</tr>
						<tr>
							<td>邮箱：</td>
							<td><span id="emailInfo" name="emailInfo">${manager.email }</span></td>
						</tr>
						<tr>
							<td>姓名：</td>
							<td><span id="userNameInfo" name="userNameInfo">${manager.userName }</span></td>
						</tr>
						<tr>
							<td>手机号：</td>
							<td><span id="cellphoneInfo" name="cellphoneInfo">${manager.cellphone }</span></td>
						</tr>
					</table>
				</div>
				<div class="clear"></div>
				<div style="margin:20px;">
					<table class="superadmintable">
						<tr>
							<td>所在地：</td>
							<td><span id="addressInfo" name="addressInfo">${manager.address }</span></td>
						</tr>
						<tr>
							<td>备注：</td>
							<td><span id="remarksInfo" name="remarksInfo">${manager.remarks }</span></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>

	<!-- #add managerInfo or edit managerInfo -->
	<div class="modal fade" id="openEditInfo" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">编辑管理员信息</h4>
				</div>
				<div class="modal-body">
					<form method="post" action="" enctype="multipart/form-data"
						id="editForm">
						<div style="width: 120px; height: 120px; float: left;margin:20px" onclick="">
							<img id="icon" width="120px;" height="120px" src="${manager.icon }" name="iconInfo" title="头像" alt="头像" style="border: 1px solid #a9a9a9;"/><span
								onclick="openFileUpload()">编辑头像</span> <input type="file"
								value="" id="iconFile" name="myfiles" style="display: none" onchange="changeIcon()"/>
						</div>
						<input type="text" value="${manager.icon }" id="iconfile" name="icon"
							style="display: none" />
						<div style="min-height: 100px;float: left;margin:20px;margin-left:30px">
							<input id="managerId" name="id" value="${manager.id }"
								style="display: none" />
							<table class="superadmintable">
								<tr>
									<td><span style="color:red">*</span>用户名：</td>
									<td><input name="account" id="account"
										value="${manager.account }" disabled="disabled"/></td>
								</tr>
								<tr>
									<td><span style="color:red">*</span>邮箱：</td>
									<td><input name="email" id="email"
										value="${manager.email }" disabled="disabled"/></td>
								</tr>
								<tr>
									<td>姓名：</td>
									<td><input name="userName" id="userName"
										value="${manager.userName }" /></td>
								</tr>
								<tr>
									<td>手机号：</td>
									<td><input name="cellphone" id="cellphone"
										value="${manager.cellphone }" /></td>
								</tr>
							</table>
						</div>
						<div class="clear"></div>
						<div style="margin:20px">
							<table class="superadmintable">
								<tr>
									<td>所在地：</td>
									<td><input name="address" id="address"
										value="${manager.address }" style="width:400px" /></td>
								</tr>
								<tr>
									<td>备注：</td>
									<td><input name="remarks" id="remarks"
										value="${manager.remarks }" style="width:400px"/></td>
								</tr>
							</table>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="edit"
						id="saveButton">保存</button>
				</div>
			</div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script src="static/superAdmin/js/initpage.js"></script>
	<script src="static/admin/js/ajaxFileUpload.js"></script>
	<script>
	$(function(){
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(7).find('.nav-pills').show();
		$('.menuNav>li').eq(7).css('background-color','#383f4e');
	})
	</script>
</body>
</html>