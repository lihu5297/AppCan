<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
.modal-body p{
height:24px;
padding:0 32px;
cursor:pointer;
color:rgb(255,255,255);
}
.over_icon{
z-index:999;
margin-top:-24px;
background-color:rgb(144,152,167);
}
table.man-tables tbody tr td{
	vertical-align: middle;
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
			<div class="location">
				<a href="javascript:void(0);">资源管理</a> / <a href="javascript:void(0);"
					class="active">资源类别</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<button type="button" class="btn btn-primary"
						 onclick="addTypeShow()">添加新类别</button>
						
					</div>
					<table class="table table-striped table-bordered tables man-tables">
						<thead>
							<tr>
								<th style="width:7%;"><input style="float: left;margin-left: 5px;" type="checkbox" onclick="selectAll()"
									id="selectAll" />全选</th>
								<th style="width:80%;">资源类别</th>
								<th style="width:13%;">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="rtype" items="${typeInfo}">
								<tr>
									<td><input type="checkbox" value="${rtype.id }"
										name="${rtype.id }" /></td>
									<td>${rtype.typeName }</td>
								
									<td><a href="javascript:void(0);" onclick="openEditShow(${rtype.id})">编辑</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="page">
					<button type="button" data-toggle="modal"
						data-target="#ensureDelete" style="float: left"
						class="btn btn-primary">删除</button>
					<span class="total">共<span>${total}</span>条
					</span>
					<div class="fr">
						<input type="hidden" id="prePageSize" value=${pageSize }>
						<span class="fl showpage">每页显示 <select name="pageSize"
							id="pageSize">
								<option id="pageSize10" selected="selected" value=10
									onclick="pagesCur(10)">10</option>
								<option id="pageSize20" value=20 onclick="pagesCur(20)">20</option>
								<option id="pageSize30" value=30 onclick="pagesCur(30)">30</option>
								<option id="pageSize50" value=50 onclick="pagesCur(50)">50</option>
						</select>条
						</span>
						<ul id="page">
							<li><a href="javascript:void(0);" class="Previous"
								onclick="presCur(0)"> |< </a></li>
							<li><a href="javascript:void(0);" class="Previous"
								onclick="pre(-1)"> < </a></li>
							<li>第 <input type="text" value="${curPage }" id="curPage">
								页 共<span id="totalPage">${totalPage }</span>页
							</li>
							<li><a href="javascript:void(0);" class="Previous" onclick="pre(1)">
									> </a></li>
							<li><a href="javascript:void(0);" class="Next"
								onclick="presCur(${totalPage })"> |> </a></li>
						</ul>
					</div>
				</div>

			</div>
			<div class="clear"></div>
		</div>
	</div>
	<!-- #add typeInfo or edit typeInfo -->
	<!-- <button id="typeInfo" data-toggle="modal" data-target="#openEditInfo">打开编辑框</button> -->
	<div class="modal fade" id="openEditInfo" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel1">编辑类别信息</h4>
				</div>
				<div class="modal-body" style="height:100px">
					<form method="post" action="" id="editForm">
						<div
							style="min-height: 100px; float: left; margin: 20px; margin-left: 81px">
							<input id="typeId" name="id" value="" style="display: none" />
							<table class="superadmintable">
								<tr>
									<td>分类名称：</td>
									<td>
										<input class="form-control" name="typeName" id="typeName" value="" style="width:240px"/>
									</td>
								</tr>
							</table>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="save"
						id="editButton">保存</button>
				</div>
			</div>
		</div>
	</div>

	<!-- #add typeInfo or edit typeInfo -->
	<div class="modal fade" id="ensureDelete" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">删除提示</h4>
				</div>
				<div class="modal-body">是否删除选择的数据？</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="save"
						id="saveButton" onclick="deleteData()">确定</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Small modal -->
	<button id="messageInfoButton" type="button" class="btn btn-primary"
		data-toggle="modal" data-target=".bs-example-modal-sm"
		style="display: none">Small modal</button>

	<div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog"
		aria-labelledby="mySmallModalLabel">
		<div class="modal-dialog modal-sm">
			<div class="modal-content" id="messageInfo">...</div>
		</div>
	</div>
	<script src="static/js/init.js"></script>
	<script>
	var basePath = document.getElementById("basePath").value;
	$(function(){
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(10).find('.nav-pills').show();
		$('.menuNav>li').eq(10).css('background-color','#383f4e');
	})
	$(".btn-niu input").keydown(function(e){
	    if(e.keyCode==13){
	    	query();
	    }
    })
    
    function addTypeShow() {
    	$("#openEditInfo").modal("show");
		$("#myInfoModalLabel1").text("添加类别");
		$("#editButton").attr("name", "save");
		$("#typeName").val("");
		$("#typeId").val("");
		$("#manageModuleText").css("display", "none");
	}
	
    function openEditShow(id) {
		//$("#typeInfo").click();
		$("#openEditInfo").modal('show');
		$("#myInfoModalLabel1").text("编辑类别");
		$("#editButton").attr("name", "edit");
		$.ajax({
			type : "GET",
			url : basePath + "resource/findType/" + id,
			data : "",
			dataType : "JSON",
			success : function(msg) {
				if (msg.status == "success") {
					data = msg.message;
					$("#typeName").val(data.typeName);
					$("#typeId").val(data.id);
				}else{
					alert(msg.message);
				}
			}
		})
	}
    
   /*  $("#saveButton").click(function() {
    	 
    	if($("#typeName").val().trim()==""){
    		alert('分类名称不能为空');
    		return false;
    	}else if($("#typeName").val().trim().length>10){
    		alert('分类名称不能超过10个字符');
    		return false;
    	}
    	var ajax_url = basePath + "resource/saveType"; // 表单目标
    	var ajax_type = $("#editForm").attr('method'); // 提交方法
    	var ajax_data = $("#editForm").serialize(); // 表单数据
    	$("#editForm").attr("action", ajax_url);

    	$.ajax({
    		type : ajax_type, // 表单提交类型
    		url : ajax_url, // 表单提交目标
    		data : ajax_data, // 表单数据
    		dataType : 'json',
    		success : function(msg) {
    			if (msg.status == 'success') { // msg 是后台调用action时，你穿过来的参数
    				alert("添加成功");
    				$("button[class='close']").click();
    				window.location.reload();
    			} else {
    				alert(msg.message);
    			}
    		}
    	});
    }); */
    $("#editButton").click(function() {
    	 
    	if($("#typeName").val().trim()==""){
    		alert('分类名称不能为空');
    		return false;
    	}else if($("#typeName").val().trim().length>10){
    		alert('分类名称不能超过10个字符');
    		return false;
    	}
    	var ajax_url,data,message;
    	var name=$("#editButton").attr('name');
    	if(name=='edit'){
	    	ajax_url = basePath + "resource/updateType"; // 表单目标
	    	data={typeId:$("#typeId").val(),typeName:$("#typeName").val()};
	    	message="修改成功";
    	}else if(name=='save'){
    		ajax_url = basePath + "resource/saveType"; // 表单目标
    		data={typeName:$("#typeName").val()};
    		message="添加成功";
    	}else{
    		return false;
    	}
    	var ajax_type = $("#editForm").attr('method'); // 提交方法
    	$("#editForm").attr("action", ajax_url);
		 
    	$.ajax({
    		type : ajax_type, // 表单提交类型
    		url : ajax_url, // 表单提交目标
    		data :data, // 表单数据
    		dataType : 'json',
    		success : function(msg) {
    			if (msg.status == 'success') { // msg 是后台调用action时，你穿过来的参数
    				alert(message);
    				$("button[class='close']").click();
    				window.location.reload();
    			} else {
    				alert(msg.message);
    			}
    		}
    	});
    });
    function deleteData() {
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
    	var ajax_url = basePath; //表单目标 
    	ajax_url += "resource/delType";
    	$.ajax({
    		type : 'post', //表单提交类型 
    		url : ajax_url, //表单提交目标 
    		data : {ids:ids.substring(0,ids.length-1)}, //表单数据
    		dataType:'json',
    		success : function(msg) {
    			if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
    				alert("删除成功");
    				window.location.reload();
    			} else {
    				alert(msg.message);
    			}
    		},
    		error:function(msg){
    			alert(msg.status+","+msg.message);
    		}
    	});
    }
	</script>
</body>
</html>