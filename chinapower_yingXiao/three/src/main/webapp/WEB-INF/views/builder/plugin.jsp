<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><%=Cache.getSetting("SETTING").getPlatName() %> - 插件管理</title>
	<link rel="shortcut icon" href="static/images/favicon.ico">
	<link href="static/css/bootstrap.min.css" rel="stylesheet">
	<link href='static/css/style.css' rel='stylesheet'>
	<link rel="stylesheet" href="static/css/zTreeStyle/zTreeStyle.css" type="text/css" />
	<!--[if lt IE 9]>
	      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	    <![endif]-->
	<style>
		.plugin-left{
			text-align: right;
		    padding-right: 20px;
		    vertical-align: middle;
		    width: 120px;
		}
		.plugin-name{
			width:250px;
		}
		.txt-pug{
			width: 350px;
    		height: 100px;
    		resize: none;
		}
		.right .tables .engin-select{
			width: 177px;
			text-align: left;
		}
		.engin-select input{
			float: left;
		    margin-left: 10px;
		    margin-right: 10px;
		}
		table.plugin-table tr td{
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
				<a href="javascript:;">插件管理</a> / <a href="javascript:;" class="active">${title}</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						
								<div class="col-lg-3" style="width:50%">
							<div class="input-group" style="width:462px">
							<form action="plugin/list" method="get" id="searchForm">
								<input type="hidden" name="type" value="${type}">
								<input type="hidden" name="pageNo" value="${curPage }">
								<input type="hidden" name="pageSize" value="${pageSize }">
								<input type="text" class="form-control" placeholder="搜索插件" id="search" value="${search==null?'':search}" name="search" style="width:400px">
								<span class="input-group-btn">
						        	<button class="btn btn-primary" type="button" onclick="submitForm()" style="height: 34px;">查询</button>
								</span>
							</form>
						    </div><!-- /input-group -->
						</div>
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createNew" onclick="getPluginCategory()">新增插件</button>
						
					</div>
					<br />
					<!-- iOs引擎列表 -->
					<table class="table table-striped table-bordered tables">
						<thead>
							<tr>
								<th class="engin-select" style="width:8%"><input type="checkbox" click="selectAll" >全选</th>
								<th style="width:15%">插件名称</th>
								<th style="width:18%">中文名称</th>
								<th style="width:10%">分类</th>
								<th style="width:36%">插件描述</th>
								<th style="width:13%">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="${page.content}">
								<tr>
									<td><input type="checkbox" value="${item.id}"></td>
									<td><c:out value="${item.enName}" /></td>
									<td><c:out value="${item.cnName}" /></td>
									<td><c:out value="${item.categoryName}" /></td>
									<td><c:out value="${item.detail}" /></td>
									<td>
										<a href="plugin/version/list?pluginId=${item.id}">版本管理</a>
										<a class="editPlugin" href="javascript:viod(0);" data-toggle="modal" data-target="#editOld" onclick="editPlugin(${item.id},${item.categoryId})">编辑</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div><!-- End of right-content -->
				<div class="page">
					<button type="button" data-toggle="modal"
						data-target="#ensureDelete" style="float: left" onclick="getModel('delete',0)"
						class="btn btn-primary">删除</button>
					<span class="total">共<span>${total }</span>条
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
			</div><!-- End of content -->
			<div class="clear"></div>
		</div><!-- End of right -->
	</div><!-- End of main -->

	<!-- Add Engine modal -->
	<div class="modal fade" id="editOld" tabindex="-1" role="dialog" aria-labelledby="createNew">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">编辑${title}</h4>
				</div>
				<div class="modal-body">
					<form action="plugin/edit" method="post" id="EditPluginForm">
						<input type="hidden" name="id" id="pluginId" value="">
						<input type="hidden" name="categoryId_a" id="categoryId_a" value="">
						<input type="hidden" name="type" value="${type}">
						<table class="table plugin-table">
							<tr>
								<td></td>
								<td>插件名称</td>
								<td><input type="text" name="enName" id="enName" disabled="disabled"/></td>
							</tr>
							<tr>
								<td></td>
								<td>中文名称</td>
								<td><input type="text" name="cnName" id="cnName" disabled="disabled"/></td>
							</tr>
							<tr>
								<td></td>
				        		<td>插件分类</td>
				        		<td>
				        			<select name="categoryId" id="edit_categoryId">
				        			</select>
				        		</td>
							</tr>
							<tr>
								<td></td>
								<td>插件描述</td>
								<td><textarea name="detail" id="detail"></textarea></td>
							</tr>		
						</table>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="edit()">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Edit Engine modal -->
	<div class="modal fade" id="createNew" tabindex="-1" role="dialog" aria-labelledby="createNew">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">新增${title}</h4>
				</div>
				<div class="modal-body">
					<form action="plugin/upload" method="post" id="AddPluginForm" enctype="multipart/form-data">
						<input type="hidden" name="type" value="${type}">
						<table class="table">
							<tr>
								<td></td>
								<td class="plugin-left">插件名称</td>
								<td><input type="text" name="enName" id="enName1" class="plugin-name" placeholder="插件名称(4~20字符，支持英文、数字、下划线)"/></td>
							</tr>
							<tr>
								<td></td>
								<td class="plugin-left">中文名称</td>
								<td><input type="text" name="cnName" id="cnName1" class="plugin-name" placeholder="中文名称(2~20字符，支持中文、数字、下划线)"/></td>
							</tr>
							<tr>
								<td></td>
				        		<td class="plugin-left">插件分类</td>
				        		<td>
				        			<select name="categoryId" id="categoryId">
				        			</select>
				        		</td>
							</tr>
							<tr>
								<td></td>
								<td class="plugin-left">帮助手册</td>
								<td><input type="file" name="helpFile" /></td>
							</tr>
							<tr>
								<td></td>
								<td class="plugin-left">iOS插件</td>
								<td><input type="file" name="iosFile" /></td>
							</tr>
							<tr>
								<td></td>
								<td class="plugin-left">Android插件</td>
								<td><input type="file" name="androidFile" /></td>
							</tr>
							<tr>
								<td></td>
								<td class="plugin-left">插件描述</td>
								<td><textarea name="detail" class="txt-pug"></textarea></td>
							</tr>		
						</table>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="upload()">保存</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Edit Engine modal -->
	<div class="modal fade" id="ensureDelete" tabindex="-1" role="dialog" aria-labelledby="ensureDelete">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">删除插件确认</h4>
				</div>
				<div class="modal-body">
					<span>删除此插件对应的版本也都将被删除，是否确认删除？</span>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" onclick="deletePlugin()">删除</button>
				</div>
			</div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/jquery.form.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script type="text/javascript">
	var pageSize, pageNo, totalPage;
	function initpage() {
		pageSize = document.getElementById("prePageSize").value;
		pageNo = $("#curPage").val();
		totalPage = $("#totalPage").text();
		
		$("input[name='pageNo']").val(pageNo);
		$("input[name='pageSize']").val(pageSize);
	}

	function pagesCur(pagesize) {
		pageSize = pagesize;
		$("input[name='pageNo']").val(pageNo);
		$("input[name='pageSize']").val(pageSize);
		submitForm();
	}

	function pagesCur() {
		pageSize = $("#pageSize").val();
		$("input[name='pageNo']").val(pageNo);
		$("input[name='pageSize']").val(pageSize);
		submitForm();
	}

	function presCur(pageno) {
		pageNo = pageno;
		$("input[name='pageNo']").val(pageNo);
		$("input[name='pageSize']").val(pageSize);
		submitForm();
	}

	function pre(prepage) {
		if (prepage != 0 && (Number(pageNo) + Number(prepage))<=totalPage && (Number(pageNo) + Number(prepage))!=0) {
			pageNo = Number(pageNo) + Number(prepage);
		} else
			pageNo = 1;
		$("input[name='pageNo']").val(pageNo);
		$("input[name='pageSize']").val(pageSize);
		submitForm();
	}
	
	$(function(){
		initpage();
		$("#pageSize" + pageSize).attr("selected", "selected");
		
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(4).find('.nav-pills').show();
		$('.menuNav>li').eq(4).css('background-color','#383f4e');
	})
	$(".editPlugin").on("click",function(){
		
		var tds = $(this).parent().siblings("td");
		var option = {
				"url":"plugin/category/enable",
				"dataType":"json",
				"type":"get",
				"success":function(data){
					$("#edit_categoryId").html("");
					for(var i = 0 ; i < data.categorys.length;i++){
						var option = $("<option></option>");
						option.text(data.categorys[i].name);
						option.attr("id",data.categorys[i].id);
						option.attr("value",data.categorys[i].id);
						$("#edit_categoryId").append(option);
					}
					$("#enName").val(tds[1].childNodes[0].nodeValue);
					$("#cnName").val(tds[2].childNodes[0].nodeValue);
					$("#edit_categoryId").val($("#categoryId_a").val());
					$("#detail").val(tds[4].childNodes[0].nodeValue);
				},
				"error":function(){
					
				}
		};
		$.ajax(option);
	});
	
	function editPlugin(id,categoryId){
		$("#pluginId").val(id);
		$("#categoryId_a").val(categoryId);
	}
	
	function getPluginCategory(){
		var option = {
				"url":"plugin/category/enable",
				"dataType":"json",
				"type":"get",
				"success":function(data){
					$("#categoryId").html("");
					for(var i = 0 ; i < data.categorys.length;i++){
						var option = $("<option></option>");
						option.text(data.categorys[i].name);
						option.attr("id",data.categorys[i].id);
						option.attr("value",data.categorys[i].id);
						$("#categoryId").append(option);
					}
				},
				"error":function(){
					
				}
		};
		$.ajax(option);
		initValues();
	}
	
	function initValues(){
		$("input[type='text']").val("");
		$("textarea[name='detail']").val("");
	}
	
	function upload(){
		openload();
		var enName = $("#AddPluginForm input[name='enName']").val();
		var reg = new RegExp("^[\a-zA-z0-9_]");
		var reg1 = new RegExp("[^\^`]");
		if(enName=="") {
			alert('插件名称不能为空');
			closeload();
			return;
		} else {
			for (var i = 0; i < enName.length; i++) {
				var aChar = enName[i];
				if(!reg.test(aChar) || !reg1.test(aChar)) {
					alert('插件名称包含非法字符');
					closeload();
					return;
				}
			}
		}
		if((enName.length >21)||(enName.length < 4)){
			alert('插件名称限制在4~20字符之内');
			closeload();
			return;
		}	
		//中文名称
		var cnName = $("#AddPluginForm input[name='cnName']").val();
		var reg=/[^\d\.\u4e00-\u9fa5]/;//汉字  数字 . _ 
		if(cnName==""){
			alert('插件中文名称不能为空');
			closeload();
			return;
		} else {
			if(reg.test(cnName)) {
				alert('插件中文名称中包含非法字符');
				closeload();
				return;
			}
		}
		if((cnName.replace(/[^\x00-\xff]/g,"**").length >20)||(cnName.replace(/[^\x00-\xff]/g,"**").length< 2)){
			alert('插件中文名称限制在2~20字符之内');
			closeload();
			return;
		}		
		$('#AddPluginForm').ajaxSubmit({
			success:function(data){
				closeload();
				//alert(data.actionInfo);
				alert("添加成功")
				window.location.reload();
			},
			error:function(msg){
				closeload();
				alert("添加失败！"+msg.actionInfo);
			}
		});
	}
	
	function edit(){
		$('#EditPluginForm').ajaxSubmit({
			success:function(data){
				alert(data.actionInfo);
				window.location.reload();
			},
			error:function(msg){
				alert("添加失败！"+msg.actionInfo);
			}
		});
	}
	
	function submitForm(){
		var queryKey = $("#search").val();
		if(queryKey != ""){
			queryKey = encodeURIComponent(queryKey);
			//queryKey = encodeURIComponent(queryKey);
		}
		//$("#search").val(queryKey);
		$('#searchForm').submit();
	}
	
	function deletePlugin(){
		
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
		ids += "-1";
		
		ajax_data = {pluginIds:ids};
		ajax_url = "plugin/delete";
		$.ajax({
			url:ajax_url,
			dataType:"json",
			type:"post",
			data:ajax_data,
			success:function(data){
				if(data.status=="success"){
					alert("删除成功！");
					window.location.reload();
				}else
					alert("删除失败！");
			},
			error:function(data){
				alert(data);
			}
			
		});
	}
	
	$("input[click='selectAll']").click(function() {
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
	    	submitForm();
	    }
    })
	</script>
</body>
</html>