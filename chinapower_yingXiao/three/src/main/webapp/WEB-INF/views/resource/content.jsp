<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
.modal-body p {
	height: 24px;
	padding: 0 32px;
	cursor: pointer;
	color: rgb(255, 255, 255);
}

.over_icon {
	z-index: 999;
	margin-top: -24px;
	background-color: rgb(144, 152, 167);
}

table.man-tables tbody tr td {
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
				<a href="javascript:void(0);">资源管理</a> / <a
					href="javascript:void(0);" class="active">内容管理</a>
			</div>
			<div class="content">
			
				<div class="right-content">
					<div class="right-header">
						<button type="button" data-toggle="modal" class="btn btn-primary"
							data-target="#openEditInfo1" onclick="addTypeShow()">添加内容</button>

					</div>
					<table class="table table-striped table-bordered tables man-tables">
						<thead>
							<tr>
								<th style="width: 7%;"><input
									style="float: left; margin-left: 5px;" type="checkbox"
									onclick="selectAll()" id="selectAll" />全选</th>
								<th style="width: 5%;">序号</th>
								<th style="width: 20%;">名称</th>
								<th style="width: 15%;">类别</th>
								<th style="width: 10%;">版本</th>
								<th style="width: 10%;">操作人</th>
								<th style="width: 20%;">操作时间</th>
								<th style="width: 10%;">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="content" varStatus="conCount"
								items="${contentInfo}">
								<tr>
									<td><input type="checkbox" value="${content.id }"
										name="${content.id }" /></td>
									<td>${conCount.index+1}</td>
									<td>${content.resName }</td>
									<td>${content.typeName }</td>
									<td>${content.resVersion }</td>
									<td>${content.creator }</td>
									<td>${content.updatedAt }</td>
									<td><a href="javascript:void(0);" data-toggle="modal"
										data-target="#typeInfo" onclick="openEditShow(${content.id})">编辑</a>
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
							<li><a href="javascript:void(0);" class="Previous"
								onclick="pre(1)"> > </a></li>
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
	<!-- <button id="contentInfo" data-toggle="modal" data-target="#openEditInfo">打开编辑框</button> -->
	<div class="modal fade" id="openEditInfo1" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel1">编辑内容</h4>
				</div>
				<div class="modal-body" style="height:540px">
					<form method="post" action="" id="editForm">
						<div style="min-height: 100px;float: left;margin: 0;width:100%;max-height: 500px;overflow-x:hidden;overflow-y:auto;">
							<input id="contentId" name="id" value="" style="display: none" />
							<input id="fileIds" name="fileIds" value="" style="display: none" />
							<table class="superadmintable">
								<tr>
									<td>名称：</td>
									<td><input name="resName" id="resName" value="" style="width:360px" class="form-control"/></td>
								</tr>
								<tr>
									<td>类别：</td>
									<td>
										<select style="width: 128px;margin-right:10px;" id="resType"
										name="resType" class="form-control"></select>
									</td>
									<td>版本：</td>
									<td><input class="form-control" name="resVersion" id="resVersion" value="" />
									</td>
								</tr>
								<tr>
									<td>描述：</td>
									<td><textarea name="resDesc" id="resDesc" value="" class="form-control" row="3" style="width:360px;    resize: none;"></textarea></td>
								</tr>
								<tr>
									<td>可见网省：</td>
									<td>
										<table border="0px" style="width: 100%;"  id="provinceTable">
                        					<tr></tr>
                    					</table>
									</td>
								</tr>
								<tr>
									<td>附件：</td>
									<td id="fileDiv" colspan="2"></td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>
										<div class="row">
											<div class="col-sm-12">
												<a id="uppicBtn">上传附件</a>
											</div>
										</div>
									</td>
								</tr>								
							</table>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="save"
						id="saveButton">保存</button>
				</div>
			</div>
		</div>
	</div>
	<div id="upload" style="display: none">
		<form method="post" action="" id="upForm" name="upForm"
			enctype="multipart/form-data">
			<input type="file" name="file" id="file"
				onchange="chooseFileUp(this.value)" accept="*" />
		</form>
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
	<script src="static/js/jquery.form.js"></script>
	<script>
	var basePath = document.getElementById("basePath").value;
	$(function(){
		loadContentType();
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
		$("#myInfoModalLabel1").text("添加内容");
		$("#saveButton").attr("name", "save");
		$("#contentId").val("");
		$("#resName").val("");
		$("#resVersion").val("");
		$("#resDesc").val("");
		$("#manageModuleText").css("display", "none");
		$("#fileDiv").html('');
		//查询网省信息，并展示到弹出窗口中
		addHTML("0","0");
		$('input[name="chenckProvince"]').each(function(){ 
			alert(this.val());
			if(this.val()==1){
				$(this).attr("checked",'true'); 
			}
		}); 
	}
	
	//分页查询
	var pageSize,pageNo,totalPage;
	$(function($){
		initpage();
		$("#pageSize"+pageSize).attr("selected","selected");
	});

	//$(document).on('change','#pageSize',function(){
//	    pageSize = $(this).val();
//	    ajaxget();
	//})

	function initpage(){
		pageSize = document.getElementById("prePageSize").value;
		pageNo = $("#curPage").val();
		totalPage = $("#totalPage").text();
	}

	function pagesCur(pagesize){
		pageSize = pagesize;
		ajaxget();
	}

	function pagesCur(){
		pageSize = $("#pageSize").val();
		ajaxget();
	}

	function presCur(pageno){
		pageNo = pageno;
		ajaxget();
	}
	 
	function pre(prepage){
		if (prepage != 0 && (Number(pageNo) + Number(prepage))<=Number(totalPage) && (Number(pageNo) + Number(prepage))!=0) {
			pageNo = Number(pageNo) + Number(prepage);
		} else
			pageNo = 1;
		ajaxget();
	}

	function query(){
		initpage();
		ajaxget();
	}

	function ajaxget(){
		var href = "resource/findContent?"+ "type=web&" + "pageNo="+pageNo+"&pageSize="+pageSize ;
		window.location.href = href;
	}
	

    function openEditShow(id) {
		addHTML("1",id);
		
		$("#openEditInfo1").modal('show');
		$("#myInfoModalLabel1").text("编辑内容");
	}
    
    //保存
    $("#saveButton").click(function() {
    	if($("#resType").val().trim()==""){
    		alert('类型不能为空');
    		return false;
    	}
    	if($("#resName").val().trim()==""){
    		alert('名称不能为空');
    		return false;
    	}   	

    	var ajax_url = basePath + "resource/saveContent"; // 表单目标
    	var ajax_type = $("#editForm").attr('method'); // 提交方法
    	var fileIds = getAllFileId();
    	$("#fileIds").val(fileIds);
    	
    	var ajax_data = $("#editForm").serialize(); // 表单数据
    	ajax_data=ajax_data+"&"+"chenckProvince=1";
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
    	ajax_url += "resource/delContent";
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
	$("#uppicBtn").click(function() {
        $("#file").click();

        //uploadFile();

    })
    function chooseFileUp(urlVal) {
        //上传
        //设定问见上传地址
        var fileUpUrl = basePath + "upload/contentFile";
        if (urlVal) {
            document.getElementById("upForm").action = fileUpUrl;
            //上传附件
            $("#upForm").ajaxSubmit({
                dataType : 'json',
                success : function(data) {
                    if (data.retCode == "0") {//上传成功
                        var fileUrl = data.fileUrl;
                        //上传返回的文件路劲
                        var fileName = data.fileOriginalName;
                        var fileId = data.fileId;
                        //上传后封装页面
                        var html = '<div class="row attachmentPathDiv" style="width:360px" id="' + fileId + '"><div class="col-sm-9" title="'+fileName+'">'+fileName+'</div><div class="col-sm-3"><a class="delBtn">删除</a></div></div>'
                        $("#fileDiv").append(html);
                        $(".delBtn").click(function() {
                        	deleteFile($(this).parent().parent().attr("id"));
                            $(this).parent().parent().remove();
                        })
                    } else {//上传失败的提示
                        alert(data.retInfo);
                    }
                },
                error : function(err) {
                    $.checkConfirm("文件上传出错！");
                    return;
                },
                complete : function() {

                }
            })
        }
    }

    function getAllFileId() {//获取所有的附件的ID去入库
        var allFileId=[];
        $(".attachmentPathDiv").each(function(){
            var fileId=$(this).attr("id");
            allFileId.push(fileId);
        });
		return allFileId;
    }
    function deleteFile(fileId) {
    	var ajax_url = basePath; //表单目标 
    	ajax_url += "resource/delFile";
    	$.ajax({
    		type : 'post', //表单提交类型 
    		url : ajax_url, //表单提交目标 
    		data : {fileId:fileId}, //表单数据
    		dataType:'json',
    		success : function(msg) {
    			
    		},
    		error:function(msg){
    			alert(msg.status+","+msg.message);
    		}
    	});
    }
    function loadContentType() {
    	$.ajax({
    		url : basePath +'resource/findTypeAll',
    		dataType : "json",
    		type : "get",
    		success : function(data) {
    			var temp = $("#resType");
    			temp.html('');
    			$(data.message).each(
    					function(index, row) {
    						temp.append("<option value='"+row.id+"'>"
    								+ row.typeName + "</option>");
    					});
    		},
    		error : function(xhr) {
    			$.messager.alert("提示", '服务器处理异常，请重新提交！', 'info');
    			return false;
    		}
    	});
    }
    function addHTML(flag,id) {
        var htmlShow="";
        $.ajax({
			type : "GET",
			url : basePath + "filiale/findAll",
			data : "",
			dataType : "JSON",
			success : function(msg) {
				if (msg.status == "success") {
					data = msg.message;
					$(data.filialeInfo).each(
	   					function(index, row) {
	   						if(index==0){
	   			                htmlShow+="<tr>";
	   			                htmlShow+='<td style="width: 90px;"><input type="checkbox" name="chenckProvince" id="chenckProvince" value="'+row.id+'"/>'+row.simpleName+'</td>'
	   			            }else if (index % 5 == 0&&index!=0) {
	   			                htmlShow+="</tr><tr>";
	   			                htmlShow+='<td style="width:90px;"><input type="checkbox" name="chenckProvince" id="chenckProvince" value="'+row.id+'"/>'+row.simpleName+'</td>'
	   			            }else{
	   			               htmlShow+='<td style="width: 90px;"><input type="checkbox" name="chenckProvince" id="chenckProvince" value="'+row.id+'"/>'+row.simpleName+'</td>' 
	   			            }
	   					}
   					);
					var zuihou=5-i%5;
			        for(var i=0;i<zuihou;i++){
			            htmlShow+="<td  style='width: 90px;'>&nbsp</td>";
			        }
			        htmlShow+="</tr>";
			        $("#provinceTable").html(htmlShow);
			      //总部管理必须被选择
					$('input[name="chenckProvince"]').each(function(){ 
						if($(this).val()==1){
							$(this).attr("checked","true"); 
							$(this).attr("disabled","true");
						}
					}); 
			        //执行回显
			        if(flag==1){
			        	$.ajax({
							type : "GET",
							url : basePath + "resource/findContent/" + id,
							data : "",
							dataType : "JSON",
							success : function(msg) {
								if (msg.status == "success") {
									var data = msg.message;
									$("#contentId").val(data.id);
									$("#resName").val(data.resName);
									$("#resType").val(data.resType);
									$("#fileDiv").html('');
									
									//选中初始化权限
									if(data.chenckProvince!=null){
										$.each(data.chenckProvince, function(i,val){ 
											//alert($('input[name="chenckProvince"]'));
											$('input[name="chenckProvince"]').each(function(){ 
												if(val==$(this).val()){ 
													$(this).attr("checked","true"); 
												}
												/* if($(this).val()==1){
													$(this).attr("checked","true"); 
												} */
											}); 
										});
									}
									    
									var html = '';
									if(data.fileList!=null){
										$(data.fileList).each(
							   					function(index, row) {
							   						html += '<div class="row attachmentPathDiv" style="width:360px" id="' + row.id + '"><div class="col-sm-9" title="'+row.originalName+'">'+row.originalName+'</div><div class="col-sm-3"><a class="delBtn">删除</a></div></div>'
							   					}
						   					);
									}
									
									 $("#fileDiv").append(html);
									 $(".delBtn").click(function() {
				                     	deleteFile($(this).parent().parent().attr("id"));
				                         $(this).parent().parent().remove();
				                     })
									$("#resVersion").val(data.resVersion);
									$("#resDesc").val(data.resDesc);
								}else{
									alert(msg.message);
								}
							}
						})
			        }
				}else{
					alert(msg.message);
				}
			}
		})
        return true;

    }
	</script>
</body>
</html>