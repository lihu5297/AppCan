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
#manageModuleCheck>span{
	padding:2px;
	width:22%;
	float:left;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
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
				<a href="javascript:void(0);">账户管理</a> / <a href="javascript:void(0);"
					class="active">平台管理员</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<button type="button" data-toggle="modal" class="btn btn-primary"
							data-target="#openEditInfo" onclick="addAdmin()">添加管理员</button>
						<div class="input-group btn-niu">
							<input id="queryKey" type="text" name="search" class="form-control"
								placeholder="关键字查询" value="${queryKey }"> <span
								class="input-group-btn">
								<button class="btn btn-default" type="button" onclick="query()">查询</button>
							</span>
						</div>
					</div>
					<table class="table table-striped table-bordered tables man-tables">
						<thead>
							<tr>
								<th style="width:7%;"><input style="float: left;margin-left: 5px;" type="checkbox" onclick="selectAll()"
									id="selectAll" />全选</th>
								<th style="width:10%;">用户名</th>
								<th style="width:10%;">姓名</th>
								<th style="width:10%;">单位</th>
								<th style="width:40%;">管理模块</th>
								<th style="width:18%;">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="admin" items="${list}">
								<tr>
									<td><input type="checkbox" value="${admin.id }"
										name="${admin.id }" /></td>
									<td>${admin.account }</td>
									<td>${admin.userName }</td>
									<td>${admin.filialeName }</td>
									<td><c:forEach var="module" items="${admin.manageModule }"
											varStatus="num">
											<c:if test="${num.count!=1}">、</c:if>
											<span>${module.cnName}</span>
										</c:forEach></td>
									<td><a href="javascript:void(0);" data-toggle="modal"
										data-target="#managerInfo" onclick="managerInfo(${admin.id})">管理员信息</a>
										<a href="javascript:void(0);" data-toggle="modal"
										data-target="#reSettingPWD<%=Cache.getSetting("SETTING").getEmailServerStatus().compareTo(EMAIL_STATUS.OPEN)==0?"_EMAIL":"" %>" onclick="resetpwd(${admin.id })">重置密码</a></td>
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
								<option id="pageSize10" selected="selected" value=10 onclick="pagesCur(10)">10</option>
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

	<!-- #managerInfo -->
	<div class="modal fade" id="managerInfo" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close" id="closeMsg">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">管理员信息</h4>
				</div>
				<div class="modal-body">
					<div style="width: 120px; height: 120px; float: left; margin: 20px">
						<img id="iconInfo" width="120px" height="120px" src="<%=basePath%>/static/images/default.jpg"
							name="iconInfo" title="头像" alt="头像" />
					</div>
					<div style="height: 10px;">
						<button type="button" class="btn btn-primary"
							style="float: right"
							 onclick="openEdit()" id="editInfo">编辑</button>
					</div>
					<div style="min-height: 100px; float: left; margin: 20px">
						<table class="superadmintable">
							<tr>
								<td>用户名：</td>
								<td><span id="accountInfo" name="accountInfo"></span></td>
							</tr>
							<tr>
								<td>邮箱：</td>
								<td><span id="emailInfo" name="emailInfo"></span></td>
							</tr>
							<tr>
								<td>姓名：</td>
								<td><span id="userNameInfo" name="userNameInfo"></span></td>
							</tr>
							<tr>
								<td>手机号：</td>
								<td><span id="cellphoneInfo" name="cellphoneInfo"  maxLength="11"></span></td>
							</tr>
							<tr>
								<td>所属单位：</td>
								<td><span id="filialeName" name="filialeName"></span></td>
							</tr>
						</table>
					</div>
					<div class="clear"></div>
					<div style="margin: 20px;">
						<table class="superadmintable">
							<tr>
								<td valign="top">管理模块：</td>
								<td><span id="manageModuleInfo" name="manageModuleInfo"></span>
								</td>
							</tr>
							<tr>
								<td>所在地：</td>
								<td><span id="addressInfo" name="addressInfo"></span></td>
							</tr>
							<tr>
								<td>备注：</td>
								<td><span id="remarksInfo" name="remarksInfo"></span></td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- #add managerInfo or edit managerInfo -->
	<button style="display:none" id="openEditInfo1" data-toggle="modal" data-target="#openEditInfo">打开编辑框</button>
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
				<div class="modal-body" style="height: 580px;">
					<form method="post" action="" enctype="multipart/form-data"
						id="editForm" style="overflow: auto;max-height: 550px;">
						<div style="width: 120px; height: 120px; float: left; margin: 20px;"
							onclick="">
						<div><img id="icon" width="120px" height="120px" src=""
								name="iconInfo" title="头像" alt="头像" style="border:1px solid #a9a9a9"/></div>
								<div class="over_icon"><p onclick="openFileUpload()" >编辑头像</p></div> <input type="file"
								value="" id="iconFile" name="myfiles" style="display: none" onchange="changeIcon()"/>
						</div>
						<input type="text" value="" id="iconfile" name="icon"
							style="display: none" />
						<div
							style="min-height: 100px; float: left; margin: 20px; margin-left: 81px">
							<input id="managerId" name="id" value="" style="display: none" />
							<table class="superadmintable">
								<tr>
									<td><span style="color:red">*</span>用户名：</td>
									<td><input name="account" id="account" value="" /></td>
								</tr>
								<tr>
									<td><span style="color:red">*</span>邮箱：</td>
									<td><input name="email" id="email" value="" /></td>
								</tr>
								<tr>
									<td>
									<%=Cache.getSetting("SETTING").getEmailServerStatus().compareTo(EMAIL_STATUS.CLOSE)==0?"<span style='color:red'>*</span>":"" %>
									密码：</td>
									<td><input type="password" name="password" id="password" value="" /></td>
								</tr>
								<tr>
									<td style="word-break:keep-all;white-space:nowrap;">
									<%=Cache.getSetting("SETTING").getEmailServerStatus().compareTo(EMAIL_STATUS.CLOSE)==0?"<span style='color:red'>*</span>":"" %>
									确认密码：</td>
									<td><input type="password" name="passwordEnsure" id="passwordEnsure" value="" /></td>
								</tr>
								<tr>
									<td><span style="color:red">*</span>姓名：</td>
									<td><input name="userName" id="userName" value="" /></td>
								</tr>
								<tr>
									<td>所属单位</td>
									<td><select style="width: 128px;" id="filialeId" name="filialeId"></select></td>
								</tr>
								<tr>
									<td>手机号：</td>
									<td><input name="cellphone" id="cellphone" value="" maxLength="11"/></td>
								</tr>
							</table>
						</div>
						<div class="clear"></div>
						<div class="margin:20px">
							<table class="superadmintable">
								<tr>
									<td valign="top">管理模块：</td>
									<td>
										<div id="manageModuleCheck"></div> <input name="modules"
										id="manageModule" value="" style="display: none">
									</td>
								</tr>
								<tr>
									<td>所在地：</td>
									<td><input name="address" id="address" value=""
										style="width: 400px" /></td>
								</tr>
								<tr>
									<td>备注：</td>
									<td><input name="remarks" id="remarks" value=""
										style="width: 400px" /></td>
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
	<script src="static/admin/js/initpage.js"></script>
	<script src="static/admin/js/ajaxFileUpload.js"></script>
	<script>
	$(function(){
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(7).find('.nav-pills').show();
		$('.menuNav>li').eq(7).css('background-color','#383f4e');
	})
	$(".btn-niu input").keydown(function(e){
	    if(e.keyCode==13){
	    	query();
	    }
    })
	</script>
</body>
</html>