<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>协同开发后台 - 流程管理</title>
	<link rel="shortcut icon" href="static/images/favicon.ico">
	<link href="static/css/bootstrap.min.css" rel="stylesheet">
	<link href="static/css/bootstrap-multiselect.css" rel="stylesheet">
	<link href='static/css/style.css' rel='stylesheet'>
	<!--[if lt IE 9]>
	      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	    <![endif]-->
	<style>
		.right .btn-xs{
			float: initial;
			background: none;
			border: 0 none;
			font-size: 14px;
			margin-right: 0;
		}
	</style>
</head>
<body>
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:;">任务流程</a>
			</div>
			<div class="content">
				<div class="right-content">
								<table class="table table-striped table-bordered tables">
									<thead>
										<tr>
											<th>前置任务动作</th>
											<th>当前任务状态</th>
											<th>下一步任务动作</th>
											<th>操作</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="task" items="${tasks}">
											<tr>
												<td>
													<c:forEach var="preTask" items="${task.preTasks}">
														<c:out value="${preTask.name}" />、
													</c:forEach>
												</td>
												<td>
													<c:forEach var="curStatus" items="${task.curStatus}">
														<c:if test="${curStatus=='WAITING' }"> 
															<c:out value="待执行" />、
														</c:if>
														<c:if test="${curStatus=='ONGOING' }"> 
															<c:out value="进行中" />、
														</c:if>
														<c:if test="${curStatus=='REJECTED' }"> 
															<c:out value="已驳回" />、
														</c:if>
														<c:if test="${curStatus=='FINISHED' }"> 
															<c:out value="已完成" />、
														</c:if>
														<c:if test="${curStatus=='SUSPENDED' }"> 
															<c:out value="已搁置" />、
														</c:if>
														<c:if test="${curStatus=='CLOSED' }"> 
															<c:out value="已关闭" />、
														</c:if>
													</c:forEach>
												<td>
													<c:out value="${task.name}" />
												</td>
												<td>
													<!-- <button type="button" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#editForm" onclick="initModal(${task.id},'${task.name}')">编辑</button> -->
													<a href="javascript:;" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#editForm" onclick="initModal(${task.id},'${task.name}')">编辑</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
					<!-- End of right-content -->
			</div><!-- End of content -->
			<div class="clear"></div>
		</div><!-- End of right -->
	</div><!-- End of main -->
	
	<!-- Add ProcessTemplate modal -->
	<div class="modal fade" id="editForm" tabindex="-1" role="dialog" aria-labelledby="editForm">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">编辑下一步动作</h4>
				</div>
				<div class="modal-body">
					<form id="AddProcessTemplateForm" action="process/task/save" method="post">
						<table class="table">
							<tr>
								<td>
								<input name="taskId" id="taskId" value="" type="hidden">
								当前任务动作：</td>
								<td><div id="preTask" name="preTask"></div></td>
							</tr>
							<tr>
								<td>当前任务状态：</td>
								<td><div id="curStatus" name="curStatus"></div></td>
							</tr>
							<tr>
								<td>下一步任务动作：</td>
								<td><div id="nextTask" name="nextTask"></div></td>
							</tr>
						</table>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="save()">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/bootstrap-multiselect.js"></script>
	<script src="static/js/init.js"></script>
	<script>	
		var taskConfigId;
		var taskConfigName;
		var statusArray = new Array();
		var statusHasArray = new Array();
		var statusNowArray = new Array();
		$(function(){
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(2).find('.nav-pills').show();
			$('.menuNav>li').eq(2).css('background-color','#383f4e');
		})
		function initModal(id,name){
			taskConfigId = id;
			taskConfigName = name;
			var option ={
					"url":"process/taskConfig?taskConfigId="+id,
					"type":"get",
					"dataType":"json",
					"success":function(msg){
						if(msg.status=="success"){
							initData(msg.message);
						}
					},
					"error":function(msg){
						alert("查询失败！");
					}
			};
			$.ajax(option);
		}
		
		function initData(data){
			$("#preTask").html("");
			$("#curStatus").html("");
			$("#nextTask").html(taskConfigName);
			$("#taskId").val(taskConfigId);
			
			var allTaskconfigExcept = data.allTaskconfigExcept;
			var preTaskConfig = data.preTaskConfig;
			for(var i = 0 ; i < allTaskconfigExcept.length ; i++){
				
				statusArray[i] = allTaskconfigExcept[i].status;
				
				var checkBox = $("<input type='checkbox'/>");
				checkBox.attr("id","preTask_"+allTaskconfigExcept[i].id);
				checkBox.attr("value",allTaskconfigExcept[i].id);
				checkBox.attr("name","preTask");
				checkBox.attr("onclick","checkBoxSelect("+i+","+allTaskconfigExcept[i].id+",'"+allTaskconfigExcept[i].status+"')");
				
				var span = $("<span></span>");
				span.text(allTaskconfigExcept[i].name);
				$("#preTask").append(checkBox);
				$("#preTask").append(span);
			}
			for(var i = 0 ; i < preTaskConfig.length ; i++){
				statusHasArray[i] = preTaskConfig[i].status;
				
				$("#preTask_"+preTaskConfig[i].id).attr("checked", "true");
				
				var span = $("<span></span>");
				var status = getStatusCN(preTaskConfig[i].status);
				span.text(status+"、");
				span.attr("id","status_"+preTaskConfig[i].id);
				$("#curStatus").append(span);
			}
			
			for(var i = 0 ; i <statusHasArray.length ; i++){
				for(var j = 0 ; j <statusArray.length ; j++){
					if(statusArray[j] != null){
						statusNowArray[j] = statusArray[j];
					}
					if(statusHasArray[i]==statusArray[j]){
						statusArray[j] = null;
					}
				}
			}
			
			for(var i = 0 ; i <statusArray.length ; i++){
				for(var j = 0 ; j <statusNowArray.length ; j++){
					if(statusArray[i]==statusNowArray[j]){
						statusNowArray[j] = null;
					}
				}
			}
			
		}
		
		function getStatusCN(curStatus){
			if(curStatus=='WAITING'){
				return "待执行";
			}else if(curStatus=='ONGOING'){
				return "进行中";
			}else if(curStatus=='REJECTED'){
				return "已驳回";
			}else if(curStatus=='FINISHED'){
				return "已完成";
			}else if(curStatus=='SUSPENDED'){
				return "已搁置";
			}else if(curStatus=='CLOSED'){
				return "已关闭";
			}
		}
		
		function checkBoxSelect(num,id,status){
			if(statusNowArray[num]==status){
				statusNowArray[num] = null;
			}else{
				statusNowArray[num] = status;
			}
			
			$("#curStatus").html("");
			for(var i = 0 ; i < statusNowArray.length ; i++){
				
				var span = $("<span></span>");
				if(statusNowArray[i]!=null){
					var status = getStatusCN(statusNowArray[i]);
					span.text(status+"、");
					span.attr("id","status_"+id);
					$("#curStatus").append(span);
				}
			}
		}
		
		function save(){
			var option ={
					"url":"process/task/save",
					"type":"post",
					"data":$('#AddProcessTemplateForm').serialize(),
					"dataType":"json",
					"success":function(msg){
						if(msg.status=="success"){
							alert("保存成功");
							window.location.reload();
						}
					},
					"error":function(msg){
						alert("查询失败！");
					}
			};
			$.ajax(option);
		}
	</script>
</body>
</html>