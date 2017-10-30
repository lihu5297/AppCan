<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><%=Cache.getSetting("SETTING").getPlatName() %> - 插件版本管理</title>
	<link rel="shortcut icon" href="static/images/favicon.ico">
	<link href="static/css/bootstrap.min.css" rel="stylesheet">
	<link href='static/css/style.css' rel='stylesheet'>
	<link rel="stylesheet" href="static/css/zTreeStyle/zTreeStyle.css" type="text/css" />
	<!--[if lt IE 9]>
	      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	    <![endif]-->
</head>
<body>
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:;">插件管理</a> / <a href="javascript:;" class="active">${title}</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createNew">更新版本</button>
					</div>
					<ul class="nav nav-tabs" id="myTab">
						<li><a href="#ios">iOS</a></li>
						<li><a href="#android">Android</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane" id="ios">
							<br/>
							<!-- iOs引擎列表 -->
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
										<th>插件版本</th>
										<th>更新描述</th>
										<th>更新时间</th>
										<th>状态</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${iosList}">
										<tr>
											<td><c:out value="${item.versionNo}" /></td>
											<td><c:out value="${item.versionDescription}" /></td>
											<td><c:out value="${item.updatedAt}" /></td>
											<td>
												<c:if test="${item.uploadStatus=='SUCCESS'}">成功</c:if>
												<c:if test="${item.uploadStatus=='ONGOING'}">上传中</c:if>
												<c:if test="${item.uploadStatus=='FAILED'}">失败</c:if>
											</td>
											<c:if test="${item.status=='ENABLE'}">
											<td><a href="javascript:void(0);" onclick="stopPlugin(${item.id},'DISABLE')">停用</a></td>
											</c:if>
											<c:if test="${item.status=='DISABLE'}">
											<td><a href="javascript:void(0);" onclick="stopPlugin(${item.id},'ENABLE')">启用</a></td>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
						<div class="tab-pane" id="android">
							<br/>
							<!-- 安卓引擎列表 -->
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
										<th>插件版本</th>
										<th>更新描述</th>
										<th>更新时间</th>
										<th>状态</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${androidList}">
										<tr>
											<td><c:out value="${item.versionNo}" /></td>
											<td><c:out value="${item.versionDescription}" /></td>
											<td><c:out value="${item.updatedAt}" /></td>
											<td>
												<c:if test="${item.uploadStatus=='SUCCESS'}">成功</c:if>
												<c:if test="${item.uploadStatus=='ONGOING'}">上传中</c:if>
												<c:if test="${item.uploadStatus=='FAILED'}">失败</c:if>
											</td>
											<c:if test="${item.status=='ENABLE'}">
											<td><a href="javascript:void(0);" onclick="stopPlugin(${item.id},'DISABLE')">停用</a></td>
											</c:if>
											<c:if test="${item.status=='DISABLE'}">
											<td><a href="javascript:void(0);" onclick="stopPlugin(${item.id},'ENABLE')">启用</a></td>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div><!-- End of right-content -->
			</div><!-- End of content -->
			<div class="clear"></div>
		</div><!-- End of right -->
	</div><!-- End of main -->

	<!-- Add Engine modal -->
	<div class="modal fade" id="createNew" tabindex="-1" role="dialog" aria-labelledby="createNew">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">更新版本</h4>
				</div>
				<div class="modal-body">
					<form action="plugin/upload" method="post" id="AddPluginForm" enctype="multipart/form-data">
						<input type="hidden" name="type" value="${type}">
						<input type="hidden" name="id" value="${pluginId }">
						<input type="hidden" id="add_engine_osType" name="osType" value="${osType}">
						<table class="table">
							<tr>
								<td></td>
								<td id="add_engine_tip"></td>
								<td><input id="pluginZipFile" type="file" name="engineZipFile" /></td>
							</tr>
						</table>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="submitForm()">保存</button>
				</div>
			</div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/jquery.form.js"></script>
	<script src="static/js/init.js"></script>
	<script>
		$(function(){
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(4).find('.nav-pills').show();
			$('.menuNav>li').eq(4).css('background-color','#383f4e');
		})
		$(function() {
			// 标签页初始化显示(默认显示ios分组)
			var link = $('#myTab a:first');
			setAddEngineParam(link);
			link.tab('show');

			$('#myTab a').click(function(e) {
				e.preventDefault();//阻止a链接的跳转行为 
				var linkObj = $(this);
				setAddEngineParam(linkObj);
				linkObj.tab('show');//显示当前选中的链接及关联的content
			});
			
			// 引擎操作提示
			var tip = "${actionInfo}";
			if(tip.length > 0) {
				alert(tip);
			}
			
		});
		function setAddEngineParam(linkObj) {
			var osType = linkObj.attr("href");
			if(osType == '#ios') {
				$('#add_engine_osType').val('IOS');
				$("#pluginZipFile").attr("name",'iosFile');
				$('#add_engine_tip').html('请上传iOS插件');
			} else if(osType == '#android') {
				$('#add_engine_osType').val('ANDROID');
				$("#pluginZipFile").attr("name",'androidFile');
				$('#add_engine_tip').html('请上传Android插件');
			}
		}
		
		function submitForm(){
			openload();
			$('#AddPluginForm').ajaxSubmit({
				success:function(data){
					closeload();
					//alert(data.actionInfo);
					alert("添加成功");
					window.location.reload();
				},
				error:function(){
					closeload();
					alert("添加失败！");
				}
			});
		}
		
		function stopPlugin(id,status){
			var option = {
					"url":"plugin/status/pluginV",
					"dataType":"json",
					"type":"post",
					"data":{"pluginVId":id,"status":status},
					"success":function(data){
						if(data.status == 'success'){
							alert("操作成功！");
							window.location.reload();
						}
					},
					"error":function(){
						alert("操作失败！");
					}
			};
			if(confirm("是否要执行此操作？")){
				$.ajax(option);
			}
		}
	</script>
</body>
</html>