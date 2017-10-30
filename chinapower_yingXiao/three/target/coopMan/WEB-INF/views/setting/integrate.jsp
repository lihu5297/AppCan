<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
#editForm_EMM tr,#editForm_EMMTEST tr{
	height:36px;
}
table input{
	width:400px;
}
.emmPub .emmPub-hd{
	text-align: right;
	padding-right: 5px;
}
.emmPub td span{
	color: #ccc;
	font-size: 12px;
}
</style>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 接入设置</title>
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
				<a href="javascript:;">账户管理</a> / <a href="javascript:;"
					class="active">接入设置</a>
			</div>
			<div class="content">
			<div class="right-header">
				<!-- Nav tabs -->
				<ul class="nav nav-tabs" role="tablist">
					<li role="presentation" class="active"><a href="#home"
						aria-controls="home" role="tab" data-toggle="tab">账号系统接入</a></li>
					<li role="presentation"><a href="#profile"
						aria-controls="profile" role="tab" data-toggle="tab">EMM正式环境接入</a></li>
					<li role="presentation"><a href="#messages"
						aria-controls="messages" role="tab" data-toggle="tab">EMM测试环境接入</a></li>
				</ul>
				<input id="idbackup" name="id" value="${set.id }"
					style="display: none" />
				<!-- Tab panes -->
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="home">
						<div style="height: 36px;margin-top:6px;">
							<button type="button" data-toggle="modal" class="btn btn-primary"
								data-target="#openEditInfo" onclick="">接入系统</button>
						</div>
						<div style="min-height: 100px;">
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
										<th>接入系统</th>
										<th>接入时间</th>
										<th>操作</th>

									</tr>
								</thead>
								<tbody>
									<tr>
										<td><span id="SYSdoMainInfo" name="SYSdoMainInfo">${set.SYSdoMain }</span>
										</td>
										<td><span id="SYSIntegrateTimeInfo"
											name="SYSIntegrateTimeInfo">${set.SYSIntegrateTime }</span></td>

										<td><c:if test="${set.SYSStatus=='NORMAL' }">
												<a href="javascript:;" data-toggle="modal"
													data-target="#changeStatus_FORBIDDEN"
													onclick="changeStatus('FORBIDDEN')">停用</a>
											</c:if> <c:if test="${set.SYSStatus=='FORBIDDEN' }">
												<a href="javascript:;" data-toggle="modal"
													data-target="#changeStatus_NORMAL"
													onclick="changeStatus('NORMAL')">启用</a>
											</c:if> <a href="javascript:;" data-toggle="modal"
											data-target="#changeKey">更新KEY</a></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>


					<!-- #add settingInfo or edit settingInfo -->
					<div class="modal fade" id="openEditInfo" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<h4 class="modal-title" id="myInfoModalLabel">账号系统接入</h4>
								</div>
								<div class="modal-body">
									<form method="post" action="" enctype="multipart/form-data"
										id="editForm_SYS">
										<%-- <input id="id" name="id" value="${set.id==null?1:set.id }" style="display: none" /> --%>
										<input id="id" name="id" value="${set.id }"
											style="display: none" />

										<table>
											<tr>
												<td style="padding: 0 8px;">接入系统：</td>
												<td style="padding: 0 8px;"><input id="SYSdoMain" style="width:300px;height:30px;" name="SYSdoMain"
													value="${set.SYSdoMain }" /></td>
											</tr>
										</table>
									</form>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">取消</button>
									<button type="button" class="btn btn-primary" name="SYS"
										id="saveButton1">保存</button>
								</div>
							</div>
						</div>
					</div>




					<div role="tabpanel" class="tab-pane" id="profile">
						<div class="modal-body">
							<form method="post" action="" enctype="multipart/form-data"
								id="editForm_EMM">

								<div style="min-height: 100px;">


									<%-- <input id="id" name="id" value="${set.id==null?1:set.id }" style="display: none" /> --%>
									<input id="id" name="id" value="${set.id }"
										style="display: none" />

									<table class="emmPub">
										<tr>
											<td class="emmPub-hd">EMM平台访问地址：</td>
											<td><input id="EMMAccessUrl" name="EMMAccessUrl"
												value="${set.EMMAccessUrl }" />
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">数据上报接口地址：</td>
											<td><input id="EMMDataReportUrl" name="EMMDataReportUrl"
												value="${set.EMMDataReportUrl }" />
												<span>配置此接口，上报一些运营数据，便于统计分析</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">数据统计接口地址：</td>
											<td><input id="EMMDataStatisticUrl"
												name="EMMDataStatisticUrl"
												value="${set.EMMDataStatisticUrl }" />
												<span>配置此接口，用于收集运营需要的统计数据</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">推送绑定接口地址：</td>
											<td><input id="EMMPushBindUrl" name="EMMPushBindUrl"
												value="${set.EMMPushBindUrl }" />
												<span>配置此接口，用于推送时绑定用户</span>
												</td>
										</tr>
										
										<tr>
											<td class="emmPub-hd">android推送地址：</td>
											<td><input id="EMMAndroidPushUrl"
												name="EMMAndroidPushUrl" value="${set.EMMAndroidPushUrl }" />
												<span>配置此接口，用于Android推送</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">设备管理接口：</td>
											<td><input id="EMMDeviceManageUrl"
												name="EMMDeviceManageUrl" value="${set.EMMDeviceManageUrl }" />
												<span>配置此接口，用于管理设备数据</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">内容管理接口：</td>
											<td><input id="EMMContentManageUrl"
												name="EMMContentManageUrl" value="${set.EMMContentManageUrl }" />
												<span>配置此接口，用于管理内容数据</span>
												</td>
										</tr>
									</table>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">取消</button>
							<button type="button" class="btn btn-primary" name="EMM"
								id="saveButton2">保存</button>
						</div>

					</div>





					<div role="tabpanel" class="tab-pane" id="messages">
						<div class="modal-body">
							<form method="post" action="" enctype="multipart/form-data"
								id="editForm_EMMTEST">

								<div style="min-height: 100px;">


									<%-- <input id="id" name="id" value="${set.id==null?1:set.id }" style="display: none" /> --%>
									<input id="id" name="id" value="${set.id }"
										style="display: none" />
									<table class="emmPub">
										<tr>
											<td class="emmPub-hd">EMM平台访问地址：</td>
											<td><input id="EMMTestAccessUrl" name="EMMTestAccessUrl"
												value="${set.EMMTestAccessUrl }" /></td>
										</tr>
										<tr>
											<td class="emmPub-hd">数据上报接口地址：</td>
											<td><input id="EMMTestDataReportUrl" name="EMMTestDataReportUrl"
												value="${set.EMMTestDataReportUrl }" />
												<span>配置此接口，上报一些运营数据，便于统计分析</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">数据统计接口地址：</td>
											<td><input id="EMMTestDataStatisticUrl"
												name="EMMTestDataStatisticUrl"
												value="${set.EMMTestDataStatisticUrl }" />
												<span>配置此接口，用于收集运营需要的统计数据</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">推送绑定接口地址：</td>
											<td><input id="EMMTestPushBindUrl" name="EMMTestPushBindUrl"
												value="${set.EMMTestPushBindUrl }" />
												<span>配置此接口，用于推送时绑定用户</span>
												</td>
										</tr>
										
										<tr>
											<td class="emmPub-hd">android推送地址：</td>
											<td><input id="EMMTestAndroidPushUrl"
												name="EMMTestAndroidPushUrl" value="${set.EMMTestAndroidPushUrl }" />
												<span>配置此接口，用于Android推送</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">设备管理接口：</td>
											<td><input id="EMMTestDeviceManageUrl"
												name="EMMTestDeviceManageUrl" value="${set.EMMTestDeviceManageUrl }" />
												<span>配置此接口，用于管理设备数据</span>
												</td>
										</tr>
										<tr>
											<td class="emmPub-hd">内容管理接口：</td>
											<td><input id="EMMTestContentManageUrl"
												name="EMMTestContentManageUrl" value="${set.EMMTestContentManageUrl }" />
												<span>配置此接口，用于管理内容数据</span>
												</td>
										</tr>
									</table>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">取消</button>
							<button type="button" class="btn btn-primary" name="EMMTEST"
								id="saveButton3">保存</button>
						</div>
					</div>

				</div>
			</div>
			</div>
			
			<div class="clear"></div>
		</div>
	</div>




	<!-- #add managerInfo or edit managerInfo -->
	<div class="modal fade" id="changeStatus_FORBIDDEN" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">提示</h4>
				</div>
				<div class="modal-body">
					<table>
						<tr>
							<td>确定要停用？</td>

						</tr>
						<tr>
							<td>停用后当前接入系统的帐号将无法登录。</td>

						</tr>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="edit"
						id="saveButton4">确定</button>
				</div>
			</div>
		</div>
	</div>

	<!-- #add managerInfo or edit managerInfo -->
	<div class="modal fade" id="changeStatus_NORMAL" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">提示</h4>
				</div>
				<div class="modal-body">
					<table>
						<tr>
							<td>确定要启用？</td>

						</tr>
						<tr>
							<td>启用后当前接入系统的帐号将可以登录。</td>

						</tr>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="edit"
						id="saveButton5">确定</button>
				</div>
			</div>
		</div>
	</div>
	<!-- #add managerInfo or edit managerInfo -->
	<div class="modal fade" id="changeKey" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">更新KEY</h4>
				</div>
				<div class="modal-body">
					<table>
						<tr>
							<td>您的密码：</td>

							<td><input type="password" name="password" value=""
								id="password"></td>

						</tr>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="edit"
						id="saveButton0">提交</button>
				</div>
			</div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script src="static/integrate/js/initpage.js"></script>
	<script src="static/admin/js/ajaxFileUpload.js"></script>

	<script type="text/javascript">
		$(function(){
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(8).find('.nav-pills').show();
			$('.menuNav>li').eq(8).css('background-color','#383f4e');
		})
		$('#myTabs a').click(function(e) {
			e.preventDefault()
			$(this).tab('show')
		});
	</script>
</body>
</html>