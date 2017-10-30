<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><%=Cache.getSetting("SETTING").getPlatName() %> - 插件分类管理</title>
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
				<a href="javascript:;">插件管理</a> / <a href="javascript:;" class="active">插件分类管理</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<div class="col-lg-3" style="width:50%">
							<div class="input-group" style="width:462px">
							<form action="plugin/category/list" method="post" id="searchForm">
								<input type="text" class="form-control" placeholder="搜索插件" id="search" value="${search==null?'':search}" name="search" style="width:400px">
								<span class="input-group-btn">
						        	<button class="btn btn-primary" type="button" onclick="$('#searchForm').submit()" style="height: 34px;">查询</button>
								</span>
							</form>
						    </div><!-- /input-group -->
						</div>
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createNew">新增插件分类</button>
						
					</div>
					<br />
					<!-- 插件分类列表 -->
					<table class="table table-striped table-bordered tables">
						<thead>
							<tr>
								<th style="width:100px;text-align: left;"><input style="float: left;margin-right: 10px;margin-left: 15px;" type="checkbox" onclick="selectAll()"
									id="selectAll" >全选</th>
								<th style="width:45.5%;">插件分类</th>
								<th style="width:45.5%;">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="${categorys}">
								<tr>
									<td><input type="checkbox" value="${item.id}"></td>
									<td><c:out value="${item.name}" /></td>
									<td>
										<c:if test="${item.status=='DISABLE'}">
											<a href="javascript:;" onclick="changeCategory('${item.id}','ENABLE')">启用</a>
										</c:if>
										<c:if test="${item.status!='DISABLE'}">
											<a href="javascript:;" onclick="changeCategory('${item.id}','DISABLE')">禁用</a>
										</c:if>
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
            <span class="total">共<span>${total}</span>条</span>
          </div>
				<!-- End of right-content -->
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
					<h4 class="modal-title">添加新分类</h4>
				</div>
				<div class="modal-body">
					<form action="plugin/category" method="post" id="AddPluginCategoryForm" >
						<input type="hidden" name="id" value="">
						<table class="table">
							<tr>
								<td style="width: 100px;vertical-align: middle;text-align: right;border: 0 none;">分类名称：</td>
								<td style="border: 0 none;"><input type="text" name="name" style="width:300px;height:30px;"/></td>
							</tr>
						</table>
					</form>
				</div>
				<div class="modal-footer" style="border: 0 none;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="saveCategory()">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- #add managerInfo or edit managerInfo -->
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
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script type="text/javascript">
	$(function(){
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(4).find('.nav-pills').show();
		$('.menuNav>li').eq(4).css('background-color','#383f4e');
	})
	function changeCategory(id,status){
		var option = {
				"type":"post",
				"url":"plugin/category/"+id,
				"data":{"id":id,"status":status},
				"dataType":"json",
				"success":function(data){
					alert("操作成功!");
					window.location.reload();
				}
		};	
		$.ajax(option);
	}
	function saveCategory(){
		//$('#AddPluginCategoryForm').submit();
		var ajax_type = $("#AddPluginCategoryForm").attr('method'); // 提交方法
		var name = $("#AddPluginCategoryForm input[name='name']").val(); // 表单数据
		if(name==""){
			alert("名称不可为空");
			$("button[class='close']").click();
			window.location.reload();
		}
		if( name.length>20){
			alert("名称过长");
			$("button[class='close']").click();
			window.location.reload();
		}
		var ajax_url = 'plugin/category'; // 表单目标
		$.ajax({
			type : ajax_type, // 表单提交类型
			url : ajax_url, // 表单提交目标
			data : {name:name}, // 表单数据
			dataType : 'json',
			success : function(data) {
				//if (data.status == 'failed') { // msg 是后台调用action时，你穿过来的参数
					alert(data.message);
					$("button[class='close']").click();
					window.location.reload();
				 
			}
		});
	}
	
	function deleteData() {
		var ids = "";
		$("tbody input[type='checkbox']").each(function() {
			if($(this).is(":checked")){
				ids+=$(this).val()+",";
			}
		});
		var ajax_url = "plugin/category/del";
		$.ajax({
			type : 'post', //表单提交类型 
			url : ajax_url, //表单提交目标 
			data : {ids:ids}, //表单数据
			dataType:'json',
			success : function(msg) {
				if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
					alert("删除成功");
					window.location.reload();
				} else {
					alert("删除失败");
				}
			},
			error:function(msg){
				alert(msg.status+","+msg.message);
			}
		});
	};;
	
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
	$("#search").keydown(function(e){
	    if(e.keyCode==13){
	    	$('#searchForm').submit();
	    }
    })
	</script>
</body>
</html>