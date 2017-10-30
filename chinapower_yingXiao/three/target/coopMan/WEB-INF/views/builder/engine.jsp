<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><%=Cache.getSetting("SETTING").getPlatName() %> - 引擎管理</title>
	<link rel="shortcut icon" href="static/images/favicon.ico">
	<link href="static/css/bootstrap.min.css" rel="stylesheet">
	<link href='static/css/style.css' rel='stylesheet'>
	<link rel="stylesheet" href="static/css/zTreeStyle/zTreeStyle.css" type="text/css" />
	<!--[if lt IE 9]>
	      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	    <![endif]-->
	<style>
		.right .tables .select-plu{
			width: 202px;
			text-align: left;
		}
		.select-plu input{
			float: left;
			margin-left: 5px;
			margin-right: 10px;
		}
		#ios tbody tr td{
			vertical-align: middle;
		}
		#android tbody tr td{
			vertical-align: middle;
		}
		#add_engine_tip{
			vertical-align: middle;
			width: 110px;
			text-align: right;
			height: 30px;
			border-top:0 none;
			padding-top: 15px;
    		padding-right: 10px;
		}
		.add_engine_txt{
			width: 300px;
			height: 30px;
		}
		.add_engine_file{
		    width: 300px;
		    position: absolute;
		    top: 8px;
		    left: 8px;
		    height: 30px;
		    opacity: 0;
		    filter: alpha(opacity=0);
		}
	</style>
</head>
<body>
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:;">引擎管理</a> / <a href="javascript:;" class="active">${title}</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createNew">新增引擎</button>
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
										<th class="select-plu" style="width:7%;"><input type="checkbox" click="selectAll" name="ios">全选</th>
										<th style="width:10%;">引擎版本</th>
										<th style="width:40%;">引擎描述</th>
										<th style="width:10%;">更新时间</th>
										<th style="width:10%;">上传状态</th>
										<th style="width:10%;">内核</th>
										<th style="width:5%;">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${iosEngineList}">
										<tr>
											<td><c:if test="${item.status!='ENABLE'}">
													<input type="checkbox" value="${item.id}">
												</c:if></td>
											<td><c:out value="${item.versionNo}" /></td>
											<td><c:out value="${item.versionDescription}" /></td>
											<td><c:out value="${item.updatedAt}" /></td>
											<td>
												<c:if test="${item.uploadStatus=='SUCCESS'}">成功</c:if>
												<c:if test="${item.uploadStatus=='ONGOING'}">上传中</c:if>
												<c:if test="${item.uploadStatus=='FAILED'}">失败</c:if>
											</td>
											<td><c:out value="${item.kernel}" /></td>
											<td>
												<%-- <a href="javascript" data-toggle="modal" data-target="#warning" onclick="getModel('delete',${item.id})">删除</a> --%>
												<c:if test="${item.status=='ENABLE'}">
								 					<a href="javascript" data-toggle="modal" data-target="#warning" onclick="getModel('DISABLE',${item.id})">停用</a>
												</c:if>
												<c:if test="${item.status!='ENABLE'}">
													<a href="javascript" data-toggle="modal" data-target="#warning" onclick="getModel('ENABLE',${item.id})">启用</a>
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div class="page">
								<button type="button" data-toggle="modal"
									data-target="#warning" style="float: left" onclick="getModel('delete',0)"
									class="btn btn-primary">删除</button>
								<span class="total">共<span>${iosTotal }</span>条
								</span>
								
							</div>
						</div>
						<div class="tab-pane" id="android">
							<br/>
							<!-- 安卓引擎列表 -->
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
										<th style="width:7%;" class="select-plu"><input type="checkbox" click="selectAll" name="android">全选</th>
										<th style="width:10%;">引擎版本</th>
										<th style="width:40%;">引擎描述</th>
										<th style="width:10%;">更新时间</th>
									    <th style="width:10%;">上传状态</th>
										<th style="width:10%;">内核</th>
										<th style="width:5%;">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${androidEngineList}">
										<tr>
											<td><c:if test="${item.status!='ENABLE'}">
													<input type="checkbox" value="${item.id}">
												</c:if></td>
											<td><c:out value="${item.versionNo}" /></td>
											<td><c:out value="${item.versionDescription}" /></td>
											<td><c:out value="${item.updatedAt}" /></td>
											<td>
												<c:if test="${item.uploadStatus=='SUCCESS'}">成功</c:if>
												<c:if test="${item.uploadStatus=='ONGOING'}">上传中</c:if>
												<c:if test="${item.uploadStatus=='FAILED'}">失败</c:if>
											</td>
											<td><c:out value="${item.kernel}" /></td>
											<td>
												<%-- <a href="javascript" data-toggle="modal" data-target="#warning" onclick="getModel('delete',${item.id})">删除</a> --%>
												<c:if test="${item.status=='ENABLE'}">
													<a href="javascript" data-toggle="modal" data-target="#warning" onclick="getModel('DISABLE',${item.id})">停用</a>
												</c:if>
												<c:if test="${item.status!='ENABLE'}">
													<a href="javascript" data-toggle="modal" data-target="#warning" onclick="getModel('ENABLE',${item.id})">启用</a>
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div class="page">
								<button type="button" data-toggle="modal"
									data-target="#warning" style="float: left" onclick="getModel('delete',0)"
									class="btn btn-primary">删除</button>
								<span class="total">共<span>${androidTotal }</span>条
								</span>
								
							</div>
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
					<h4 class="modal-title">新增引擎</h4>
				</div>
				<div class="modal-body">
					<form action="engine/upload" method="post" id="AddEngineForm" enctype="multipart/form-data">
						<input type="hidden" name="type" value="${type}">
						<input type="hidden" id="add_engine_osType" name="osType" value="${osType}">
						<table class="table">
							<tr>
								<td id="add_engine_tip"></td>
								<td style="width: 316px;position: absolute;border-top:0 none;">
									<input type="text" class="add_engine_txt" />
									<input type="file" class="add_engine_file" name="engineZipFile" />
								</td>
							</tr>
						</table>
					</form>
				</div>
				<div class="modal-footer" style="border-top: 0 none;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="submitForm()">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- warning -->
	<div class="modal fade" id="warning" tabindex="-1" role="dialog" aria-labelledby="createNew">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="warning_title">提示标题</h4>
				</div>
				<div class="modal-body">
					<span id="warning_title_span">提示内容</span>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="operation()">确定</button>
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
			$(".add_engine_file").change(function(){
				$(".add_engine_txt").val($(this).val());
			})
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(3).find('.nav-pills').show();
			$('.menuNav>li').eq(3).css('background-color','#383f4e');
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
				$('#add_engine_tip').html('iOS引擎');
			} else if(osType == '#android') {
				$('#add_engine_osType').val('ANDROID');
				$('#add_engine_tip').html('Android引擎');
			}
			
		}
		
		var modelEglish = "";
		var modelName = "";
		var engineId="";
		function getModel(model,id){
			modelEglish = model;
			engineId = id;
			if(model=='delete'){
				modelName = "删除";
			}else if(model == "DISABLE"){
				modelName = "禁用";
			}else{
				modelName = "启用";
			}
			
			$("#warning_title").text(modelName+"引擎提示");
			$("#warning_title_span").text("是否确认"+modelName+"该引擎？");
			
		}
		
		function operation(){
			var ajax_url ="";
			var ajax_data = {};
			if(modelEglish == 'delete'){
				var ids = "";
				$("tbody input[type='checkbox']").each(function() {
					if($(this).is(":checked")){
						ids+=$(this).val()+",";
					}
				});
				if(ids==""){
					alert("请选择要删除的数据");
					return;
				}
				ajax_url = "engine/"+modelEglish;
				ajax_data = {engineIds:ids};
			}else{
				ajax_url = "engine/status/"+engineId;
				ajax_data = {status:modelEglish};
			}
			
			$.ajax({
				url:ajax_url,
				dataType:"json",
				type:"post",
				data:ajax_data,
				success:function(data){
					if(data.status=="success" && data.message.affected==1){
						alert(modelName+"成功！");
						window.location.reload();
					}else
						alert(modelName+"失败！");
				},
				error:function(data){
					alert(data);
				}
				
			});
		}
		
		$("input[click='selectAll']").click(function() {
			var type =  $(this).attr("name");
			var checked = $(this).is(":checked");
			if (checked == undefined || checked == true) {
				$("div[id='"+type+"'] tbody input[type='checkbox']").each(function() {
					if(!$(this).is(":checked")){
						$(this).click();
					}
					
				})
			} else {
				$("tbody input[type='checkbox']").each(function() {
					if($(this).is(":checked")){
						$(this).click();
					}
				});
			}
		});
		
		function submitForm(){
			openload();
			$('#AddEngineForm').ajaxSubmit({
				success:function(data){
					closeload()
					alert(data.actionInfo);
					window.location.reload();
				},
				error:function(){
					closeload()
					alert("添加失败！");
				}
			});
		}
	</script>
</body>
</html>