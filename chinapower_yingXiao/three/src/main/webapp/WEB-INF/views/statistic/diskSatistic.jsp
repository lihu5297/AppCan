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
.tables tr th{
	min-width: 100px;
}
table.diskTable tbody tr td{
	padding-top: 0;
	padding-bottom: 0;
	vertical-align: middle;
}
#usedInfo tbody tr td{
	width:200px;
}
#unUsedInfo tbody tr td{
	width:200px;
}
</style>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 空间统计</title>
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
				<a href="javascript:void(0);">空间统计</a>
			</div>
			<div class="content">
				<div class="right-content">
					<table class="table table-striped table-bordered tables diskTable">
						<thead>
							<tr>
								<th style="width:10%;vertical-align: middle;">服务器名称</th>
								<th style="width:10%;vertical-align: middle;">服务器地址</th>
								<th style="width:40%;vertical-align: middle;">已用空间</th>
								<th style="width:40%;vertical-align: middle;">剩余空间</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="disk" items="${statistic}">
								<tr>
									<td>${disk.hostName }</td>
									<td>${disk.host }</td>
									<td>${disk.usedInfo }</td>
									<td>${disk.unUsedInfo }</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

			</div>
			<div class="clear"></div>
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

	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script>
		$(function(){
			$('.menuNav>li').removeClass('active');
			$('.menuNav>li').eq(5).find('.nav-pills').show();
			$('.menuNav>li').eq(5).css('background-color','#383f4e');
		})
	</script>
</body>
</html>