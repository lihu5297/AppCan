<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
.right-header tr,.modal-body tr{
height:32px;
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
				<a href="javascript:;">平台设置</a> / <a href="javascript:;"
					class="active">平台备份</a>
			</div>
			<div class="content">
			<div class="right-content">
			<div class="right-header">

				<div style="height:36px">
					<button type="button" class="btn btn-primary"
						data-toggle="modal" data-target="#openEditInfo"
						onclick="openEdit()">编辑备份策略</button>
					<button type="button" class="btn btn-primary" onclick="backup()">手动备份</button>
				</div>
				<b>全量备份策略</b>
					<table>
					<tr>
									<td>备份间隔：</td>
									<td><span id="platIntervalInfo" name="platIntervalInfo" >${set.platInterval }天</span>
									</td>
								</tr>
								<tr>
									<td>备份时间：</td>
									<td><span id="platExecuteTimeInfo" name="platExecuteTimeInfo" >${set.platExecuteTime_hour }:${set.platExecuteTime_minutes }</span>
									</td>
								</tr>
								<tr>
									<td>备份路径：</td>
									<td><span id="platBackupPathInfo" name="platBackupPathInfo">${set.platBackupPath }</span></td>
								</tr>
						</table>
				
				
			</div>			
			
				<b>备份记录</b>
					 <table class="table table-striped table-bordered tables">
              <thead>
                <tr>
                	<th style="width: 94px;text-align: left;"><input type="checkbox" onclick="selectAll()" id="selectAll" style="float: left;margin-left: 12px;margin-right: 10px;"/>全选</th>
                    <th>服务器名称</th>
                    <th>服务器IP</th>
                    <th>备份路径</th>
                    <th>备份描述</th>
                    <th>状态</th>
                    <th>备份时间</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="log" items="${list}">
                <tr>
                  <td><input type="checkbox" value="${log.id }" name="${admin.id }"></td>
                  <td>${log.serverName}</td>
                  <td>${log.serverIp}</td>
                  <td>${log.backupPath}</td>
                  <td>${log.backupDetail}</td>
                  <td>${log.status=='ONGOING'?'备份中':'完成'}</td>
                  <td>${log.backupTime}</td>
                </tr>
                </c:forEach>
              </tbody>
            </table>
				</div>
				<div class="page">
					<button type="button" class="btn btn-primary"
						data-toggle="modal" data-target="#ensureDelete" style="float:left">删除</button>
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
							<li><a href="javascript:;" class="Previous"
								onclick="presCur(0)"> |&lt; </a></li>
							<li><a href="javascript:;" class="Previous"
								onclick="pre(-1)"> &lt; </a></li>
							<li>第 <input type="text" value="${curPage }" id="curPage">
								页 共<span id="totalPage">${totalPage }</span>页
							</li>
							<li><a href="javascript:;" class="Previous" onclick="pre(1)">
									&gt; </a></li>
							<li><a href="javascript:;" class="Next"
								onclick="presCur(${totalPage })"> |&gt; </a></li>
						</ul>
					</div>
				</div>
				
				</div>
				<div class="clear"></div>
		</div>
	</div>

	<!-- #add managerInfo or edit managerInfo -->
	<div class="modal fade" id="openEditInfo" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">编辑备份策略</h4>
				</div>
				<div class="modal-body">
					<form method="post" action="" enctype="multipart/form-data"
						id="editForm_BACKUP">
						
						<div style="min-height: 100px;">


							<%-- <input id="id" name="id" value="${set.id==null?1:set.id }" style="display: none" /> --%>
							<input id="id" name="id" value="${set.id }" style="display: none" />
							<table>
								<tr>
									<td>备份间隔：</td>
									<td><input id="platInterval" name="platInterval" value="${set.platInterval }" style="width:120px" type="number"/>天 （不填默认为1周）
									</td>
								</tr>
								<tr>
									<td>备份时间：</td>
									<td><input id="platExecuteTime_hour" name="platExecuteTime_hour" value="${set.platExecuteTime_hour }" type="number"  style="width:50px"/>:
									<input id="platExecuteTime_minutes" name="platExecuteTime_minutes" value="${set.platExecuteTime_minutes }" type="number"  style="width:50px"/>
									</td>
								</tr>
								<tr>
									<td>备份路径：</td>
									<td><input id="platBackupPath" name="platBackupPath" value="${set.platBackupPath }"  style="width:380px"/></td>
								</tr>
							</table>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="BACKUP"
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
				<div class="modal-body">
					
						
						<div style="min-height:30px;">
							是否删除选择的数据？
						</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="BACKUP"
						id="saveButton" onclick="deleteData()">确定</button>
				</div>
			</div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script src="static/backup/js/initpage.js"></script>
	<script src="static/admin/js/ajaxFileUpload.js"></script>
	<script>
		$(function(){
			
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(8).find('.nav-pills').show();
			$('.menuNav>li').eq(8).css('background-color','#383f4e');
			
			$("#platInterval").change(function(){
				if(this.value<=0){
					alert("周期必须大于0！");
					$(this).val(1);
				}
			})
		})
	</script>
</body>
</html>