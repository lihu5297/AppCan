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
		#AddProcessTemplateForm .table tr td{
			border-top: 0 none;
		}
		#AddProcessTemplateForm .table tr td input{
			width: 300px;
			height: 30px;
		}
		#processIndex tr td,
		#processIndex tr th{
			vertical-align: middle;
		}
	</style>
</head>
<body>
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:;">流程管理</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#AddProcessTemplate">创建新流程</button>
					</div>
					<ul class="nav nav-tabs" id="myTab">
						<c:forEach var="tpl" items="${tplList}">
							<li><a href="#tpl${tpl.id}" id="tppl${tpl.id}">${tpl.name}</a></li>
						</c:forEach>
					</ul>
					<input type="hidden" value="${processTemplateId}" id="processTemplateId1">
					<div class="tab-content">
						<c:forEach var="tpl" items="${tplList}">
							<div class="tab-pane" id="tpl${tpl.id}">
								<c:if test="${tpl.id != 1}">
									<br/>
									<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#AddProcessConfig">创建新流程阶段</button>
								</c:if>
								<c:if test="${tpl.status == 'ENABLE'}">
<%-- 									<button id="changeStatusSourceId" processId="${tpl.id}" operate="DISABLE" type="button" class="btn btn-primary" data-toggle="modal" data-target="#changeStatus_DISABLE" onclick="selectId(${tpl.id})" >禁用</button> --%>
								</c:if>
								<c:if test="${tpl.status == 'DISABLE'}">
									<button id="changeStatusSourceId" processId="${tpl.id}" operate="ENABLE" type="button" class="btn btn-primary" data-toggle="modal" data-target="#changeStatus_ENABLE" onclick="selectId(${tpl.id})">启用流程</button>
									<c:if test="${tpl.name!='默认流程' }">
										<!--  
											<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#DELProcessConfig" onclick="selectId(${tpl.id})">删除</button>
										-->
										<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#DELProcess" onclick="selectId(${tpl.id})">删除流程</button>
									</c:if>
								</c:if>
								<br/>
								<table class="table table-striped table-bordered tables" id="processIndex1">
									<thead>
										<tr>
											<th style="width:7%;"><input style="float: left;margin-left: 5px;" type="checkbox" onclick="selectAll()"
												id="selectAll" />全选</th>
											<th style="width:10%;">阶段序号</th>
											<th style="width:15%;">流程阶段</th>
											<th style="width:15%;">流程阶段创建人</th>
											<th style="width:15%;">流程阶段负责人</th>
											<th style="width:15%;">流程阶段参与人</th>
											<th style="width:20%;">操作</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="config" items="${tpl.processConfigList}">
											<tr id="process${config.sequence}">
												<td><input type="checkbox" value="${config.id }" name="${config.id}"><c:out value="${config.id}" /></td>
												
												<td><c:out value="${config.sequence}" /></td>
												<td><c:out value="${config.name}" /></td>
												<td><c:out value="${config.creatorRoleNameList}" /></td>
												<td><c:out value="${config.managerRoleNameList}" /></td>
												<td><c:out value="${config.memberRoleNameList}" /></td>
												<td >
													<!-- <button type="button" class="btn btn-primary btn-xs" onclick="showConfig(${config.id})">配置角色</button> -->
<a href="javascript:;" class="btn btn-primary btn-xs"  onclick="showConfig(${config.id})" style="float:left;margin-left:70px;">配置角色</a>
<c:if test="${tpl.name!='默认流程' }">
	<c:if test="${1 != config.sequence}">
		<a href="javascript:;" class="btn btn-primary btn-xs"  onclick="upDown(${config.id},${config.sequence},0,'${config.name}',${tpl.processConfigList.size()},${tpl.id})"  style="float:left;">上移</a>
	</c:if>
	<c:if test="${tpl.processConfigList.size()!=config.sequence }">
		<a href="javascript:;" class="btn btn-primary btn-xs"  onclick="upDown(${config.id},${config.sequence},1,'${config.name}',${tpl.processConfigList.size()},${tpl.id})"  style="float:left;">下移</a>
	</c:if>
</c:if>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								
								<div class="page">
									<button type="button" data-toggle="modal"
										data-target="#ensureDelete" style="float: left"
										class="btn btn-primary">删除</button>
									<span class="total">共<span>${tpl.total }</span>条
									</span>
									<div class="fr">
										<input type="hidden" id="prePageSize" value=${tpl.pageSize }>
										<span class="fl showpage">每页显示 <select name="pageSize"
											id="pageSize${tpl.id}" onchange="pagesCur()">
												<option id="pageSize10" value=10>10</option>
												<option id="pageSize20" value=20>20</option>
												<option id="pageSize30" value=30>30</option>
												<option id="pageSize50" value=50>50</option>
										</select>条
										</span>
										<ul id="page">
											<li><a href="javascript:void(0);" class="Previous"
												onclick="presCur(0)"> |< </a></li>
											<li><a href="javascript:void(0);" class="Previous"
												onclick="pre(-1)"> < </a></li>
											<li>第 <input type="text" value="${tpl.curPage }" id="curPage">
												页 共<span id="totalPage${tpl.id}" >${tpl.totalPage }</span>页
											</li>
											<li><a href="javascript:void(0);" class="Previous" onclick="pre(1)">
													> </a></li>
											<li><a href="javascript:void(0);" class="Next"
												onclick="presCur(${tpl.totalPage })"> |> </a></li>
										</ul>
									</div>
								</div>
							
							
							</div>
							
							
							
						</c:forEach>
					</div>
				</div><!-- End of right-content -->
			</div><!-- End of content -->
			<div class="clear"></div>
		</div><!-- End of right -->
	</div><!-- End of main -->
	
	<!-- Add ProcessTemplate modal -->
	<div class="modal fade" id="AddProcessTemplate" tabindex="-1" role="dialog" aria-labelledby="AddProcessTemplate">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">新增流程</h4>
				</div>
				<div class="modal-body">
					<form id="AddProcessTemplateForm" target="hidden_iframe" onsubmit="return false;">
						<table class="table">
							<tr>
								<td style="width: 60px;vertical-align: middle;text-align: right;">流程</td>
								<td><input type="text" id="name" name="name" /></td>
							</tr>
						</table>
					</form>
				</div>
				<div class="modal-footer" style="border-top: 0 none;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="addProcessTemplate()">保存</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Add ProcessConfig modal -->
	<div class="modal fade" id="AddProcessConfig" tabindex="-1" role="dialog" aria-labelledby="AddProcessConfig">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">新增流程阶段</h4>
				</div>
				<div class="modal-body">
					<form id="AddProcessConfigForm" target="hidden_iframe" onsubmit="return false;">
						<!-- 默认值根据 "默认标签页" 的processTemplateId 而定 -->
						<input type="hidden" id="processTemplateId" name="processTemplateId" value="${processTemplateId}" />
						<table class="table">
							<tr>
								<td></td>
								<td>流程阶段</td>
								<td><input type="text" name="name" /></td>
							</tr>
						</table>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="addProcessConfig()">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- #changeStatus modal -->
	<div class="modal fade" id="changeStatus_ENABLE" tabindex="-1" role="dialog" aria-labelledby="AddProcessConfig">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">启用流程</h4>
				</div>
				<div class="modal-body">
					<p>启用流程确认？</p>
					<p>启用该流程，其他流程讲变成禁用状态；是否继续启用？</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="changeStatus('ENABLE')">启用</button>
				</div>
			</div>
		</div>
	</div>
	<!-- #changeStatus modal -->
	<div class="modal fade" id="changeStatus_DISABLE" tabindex="-1" role="dialog" aria-labelledby="AddProcessConfig">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">禁用流程</h4>
				</div>
				<div class="modal-body">
					<p>禁用流程确认？</p>
					<p>禁用该流程，是否继续禁用？</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="changeStatus('DISABLE')">禁用</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- #DELProcess modal -->
	<div class="modal fade" id="DELProcess" tabindex="-1" role="dialog" aria-labelledby="AddProcessConfig">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">删除流程</h4>
				</div>
				<div class="modal-body">
					<p>删除流程确认</p>
					<p>删除该流程，将不可恢复？</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="DELProcess()">删除</button>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="roleSettings" tabindex="-1" role="dialog" aria-labelledby="roleSettings">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">角色配置</h4>
				</div>
				<div class="modal-body">
					<form id="roleSettingsForm" target="hidden_iframe">
						<input type="hidden" id="processConfigId" name="id" value="-1"/>
						<div class="form-group">
							<label class="col-sm-3 control-label" style="height:34px; line-height:34px;">流程阶段创建人</label>
							<div class="col-sm-3"><select multiple="multiple" class="form-control" id="creatorRoleList" name="creatorRoleStr"></select></div>
						</div>
						<div class="clear"></div>
						<div class="form-group">
							<label class="col-sm-3 control-label" style="height:34px; line-height:34px;">流程阶段负责人</label>
							<div class="col-sm-3"><select multiple="multiple" class="form-control" id="managerRoleList" name="managerRoleStr"></select></div>
						</div>
						<div class="clear"></div>
						<div class="form-group">
							<label class="col-sm-3 control-label" style="height:34px; line-height:34px;">流程阶段参与人</label>
							<div class="col-sm-3"><select multiple="multiple" class="form-control" id="memberRoleList" name="memberRoleStr"></select></div>
						</div>
						<div class="clear"></div>
					</form>
				</div>
				<div class="clear"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="editProcessConfig()">保存</button>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" value="<%=basePath%>" id="basePath">
	
	<iframe id="hidden_iframe" style="display:none"></iframe>

	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/bootstrap-multiselect.js"></script>
	<script src="static/js/init.js"></script>
	<script>
	
		var pageSize, pageNo, totalPage;
		if(document.getElementById("basePath")){
			var basePath = document.getElementById("basePath").value;
		}
	
		function initpage(type) {
			pageSize = document.getElementById("prePageSize").value;
			pageNo = $("#curPage").val();
			totalPage = $("#totalPage"+$('#processTemplateId1').val()).text();
		}
		function pagesCur() {
			/* if(pgsize){
				pageSize = pgsize;
			}else{
				pageSize = $("#pageSize").val();
			} */
			pageSize = $("#pageSize"+$('#processTemplateId1').val()).val();
			
			ajaxget();
		}
	
		function presCur(pageno) {
			pageNo = pageno;
			ajaxget();
		}
	
		function pre(prepage) {
			if (prepage != 0 && (Number(pageNo) + Number(prepage))<=totalPage && (Number(pageNo) + Number(prepage))!=0) {
				pageNo = Number(pageNo) + Number(prepage);
			} else if(pageNo == totalPage){
				return;
			}else{
				pageNo = 1;
			}
			ajaxget();
		}
		//获取数据
		function ajaxget() {
			var processTemplateId = $('#processTemplateId1').val();
			var href = "/process/template/list?processTemplateId="+processTemplateId+ "&pageNo=" + pageNo + "&pageSize=" + pageSize;
			
			window.location.href = basePath+href;
			
			
			/* $('#myTab a').click(function(e) {
				e.preventDefault();  // 阻止链接的跳转行为 
				var linkObj = $(this);
				setProcessTemplateId(linkObj);
				linkObj.tab('show'); // 显示当前选中的链接及关联的content
			}); */
		}
		
	
		$(function(){
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(2).find('.nav-pills').show();
			$('.menuNav>li').eq(2).css('background-color','#383f4e');
		})
		
		$(function() {
			// 标签页初始化显示(显示默认流程标签页，id=1)
			var proTpl = getQueryString("processTemplateId");
			var pSize = getQueryString("pageSize");
			if(pSize){
				$("#pageSize"+proTpl).val(pSize);
			}
			if(proTpl){
				var link = $("#tppl"+proTpl);
				link.tab('show');
			}else{
				var link = $('#myTab a:first');
				setProcessTemplateId(link);
				link.tab('show');
			}
			$('#myTab a').click(function(e) {
				e.preventDefault();  // 阻止链接的跳转行为 
				var linkObj = $(this);
				setProcessTemplateId(linkObj);
				linkObj.tab('show'); // 显示当前选中的链接及关联的content
				
				//页面跳转
				var processTemplateId = $('#processTemplateId1').val();
				var href = "/process/template/list?processTemplateId="+processTemplateId+ "&pageNo=1&pageSize=10";
				window.location.href = basePath+href;
			});
			
			//分页逻辑
			initpage();
			//$("#pageSize" + pageSize).attr("selected", "selected");
			
		});
		
		/* $(function($) {
			initpage();
			$("#pageSize" + pageSize).attr("selected", "selected");
		}); */
		
		
		function setProcessTemplateId(linkObj) {
			var href = linkObj.attr("href");
			var processTemplateId = href.substr(4); // #tpl1, #tpl2, #tpl3...
			$('#processTemplateId,#processTemplateId1').val(processTemplateId);
		}
		
		function addProcessConfig() {
			var options = {
				"type" : 'POST',
				"url"  : 'process/config',
				"data" : $('#AddProcessConfigForm').serialize(),
				"success" : function(ret) {
					 
					if(ret.status=="success"){
						$("#AddProcessConfig").modal("hide");
						$(".modal-backdrop.fade").remove();
						//$("#processIndex1 tbody").append("<tr><td>"+ret.message.sequence+"</td><td>"+ret.message.name+"</td><td>"+ret.message.creatorRoleNameList+"</td><td>"+ret.message.managerRoleNameList+"</td><td>"+ret.message.memberRoleNameList+"</td><td><a href='javascript:;' class='btn btn-primary btn-xs' onclick=''>配置角色</a></td></tr>");
						alert("添加成功");
						window.location.reload();
					}else{
						$("#AddProcessConfig").modal("hide");
						$(".modal-backdrop.fade").remove();
						alert(ret.message);
						window.location.reload();
					}
				}
			}
			if($("#AddProcessConfigForm input[name='name']").val()==""){
				alert("名称不可为空");
				return;
			}
			if($("#AddProcessConfigForm input[name='name']").val().length>20){
				alert("名称不可超过20个字符");
				return;
			}
			$.ajax(options);
		}
		
		function addProcessTemplate() {
			if($.trim($('#AddProcessTemplateForm input[name="name"]').val()) == ""){
				alert('流程名称不能为空');
				return;
			}
			var options = {
				"type" : 'POST',
				"url"  : 'process/template',
				"data" : $('#AddProcessTemplateForm').serialize(),
				"success" : function(ret) {
					if(ret.status=="success"){
						//$("#AddProcessConfig").modal("hide");
						$(".modal-backdrop.fade").remove();
						alert("添加成功");
						
						var processTemplateId = ret.message;
						var href = "/process/template/list?processTemplateId="+processTemplateId+"&pageNo=1&pageSize=10";
						window.location.href = basePath+href;
						
						//window.location.reload();
					}else{
						//$("#AddProcessConfig").modal("hide");
						$(".modal-backdrop.fade").remove();
						alert(ret.message);
						window.location.reload();
					}
				}
			}
			if($("#name").val().length>20){
				alert("名称不可超过20个字符");
				return;
			}
			$.ajax(options);
		}
		
		function editProcessConfig() {
			var options = {
				"type" : 'POST',
				"url"  : 'process/config/edit',
				"data" : $('#roleSettingsForm').serialize(),
				"success" : function(ret) {
					window.location.reload();
					//var linkObj = $('#myTab a [href="#tpl' + $('#processTemplateId').val() + '"]')
					//setProcessTemplateId(linkObj);
					//linkObj.tab('show'); // 显示当前选中的链接及关联的content
				}
			}
			$.ajax(options);
		}
		
		function showConfig(configId) {
			$('#processConfigId').val(configId);
			$.ajax({
		        type : 'GET',
		        url  : 'process/config/'+ configId,
		        error: function(request) {
		            alert("获取数据失败");
		        },
		        success: function(data) {
		        	var message = data.message;
		        	var allRoleList    = message.roleList; 
		        	var creatorRoleStr = message.creatorRoleStr;
		        	var managerRoleStr = message.managerRoleStr;
		        	var memberRoleStr  = message.memberRoleStr
		        	
		        	initSelectElement('creatorRoleList', allRoleList, creatorRoleStr.split(','));
		        	initSelectElement('managerRoleList', allRoleList, managerRoleStr.split(','));
		        	initSelectElement('memberRoleList',  allRoleList, memberRoleStr.split(',') );
		        	
		        	$('#roleSettings').modal('toggle');
		        }
		    });
		}
		
		//updown上移0、下移1
		function upDown(configId,configSequence,updown,configName,configCount,processId){
			//如果是第一个元素，不允许上移
			if(1==configSequence&&0==updown){
				alert(configName+"是第一个流程阶段，不能够上移");
				return;
			}
			//如果是最后一个元素，不允许下移
			if(configCount==configSequence&&1==updown){
				alert(configName+"是最后一个流程阶段，不能够下移");
				return;
			}
			
			//上移时，和上一个流程阶段交换位置序号；下移时，和下一个流程阶段交换位置序号
			var operationName="";
			if(0==updown){
				operationName="上移";
			}else if(1==updown){
				operationName="下移";
			}
			var options={
					"type" : 'POST',
					"url" : 'process/upDown',
					"data" : {"configId":configId,"configSequence":configSequence,"updown":updown,"configCount":configCount,"processId":processId},
					"success" : function(data){
						if(data.status=="success" && data.message.affected==2){
							alert(operationName+"成功");
							window.location.reload();
						}else{
							alert(operationName+"失败");
						}
					}
			}
			$.ajax(options);
		}
		
		function initSelectElement(selectElementId, data, selected) {
			// data format : [{"id":"","name":"","array"},{},...]
        	var innerHTML = '';
        	var len = data.length;
        	for(var i = 0; i < len; i++) {
        		var obj = data[i];
        		innerHTML += '<option value="' + obj.id + '">' + obj.cnName + '</option>';
        	}
        
        	$('#' + selectElementId).html(innerHTML).multiselect({
        		maxHeight:500,
        		numberDisplayed: 5
        	});
        	
        	$('#' + selectElementId + " option").each(function() {
	   			 $(this).prop('selected', false);
	   		});
        	if(selected.length > 0) {
        		$('#' + selectElementId).multiselect('select', selected).multiselect('refresh');
        	}
        	
        	$(".multiselect").click(function(){
    			$(this).parent().attr("class","open");
    		});
	   		
		}
		var proId ;
		function selectId(processid){
			proId = processid;
		}
		
		function changeStatus(status){
			var statusName="";
			if(status=="ENABLE"){
				statusName="启动";
			}else
				statusName="禁用";
			var options={
					"type" : 'POST',
					"url" : 'process/changestatus/'+proId,
					"data" : { 'status' : status },
					"success" : function(data){
						if(data.status=="success" && data.message.affected==1){
							alert(statusName+"成功");
							window.location.reload();
						}else{
							alert(statusName+"失败");
						}
					}
			}
			$.ajax(options);
		}
		
		function DELProcess(){
			var options={
					"type" : 'POST',
					"url" : 'process/delete/'+proId,
					"success" : function(data){
						if(data.status=="success" && data.message.affected==1){
							alert("删除成功");
							var href = "/process/template/list?processTemplateId="+1+"&pageNo=1&pageSize=10";
							window.location.href = basePath+href;
							//window.location.reload();
						}else{
							alert("删除失败");
						}
					},
					"error":function(data){
						alert(data);
					}
			}
			$.ajax(options);
		}
		
		//流程阶段全选
		$("#selectAll").click(function() {
			var checked = $(this).is(":checked");
			if (checked == undefined || checked == true) {
				$("tbody input[type='checkbox']").each(function() {
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
		function getQueryString(name) {
		    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
		    var r = window.location.search.substr(1).match(reg);
		    return r != null ? unescape(r[2]) : null;
		}

		
	</script>
</body>
</html>