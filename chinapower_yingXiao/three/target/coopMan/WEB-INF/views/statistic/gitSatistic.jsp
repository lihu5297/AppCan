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
				<a href="javascript:void(0);">GIT统计</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<div style="float:right">
			              <span>开始时间：</span>
			              <input id="startTime" name="startTime" type="text" placeholder="时间插件" value="${startTime }" style="height:30px;padding-left:5px">
			              <span style="margin-left: 8px;">结束时间：</span>
			              <input id="endTime" name="endTime" type="text" placeholder="时间插件" value="${endTime }" style="height:30px;padding-left:5px">
			              <span style="margin-left: 8px;">关键字：</span>
			              <input id="queryKey" name="search" type="text" placeholder="关键字查询" value="${queryKey }" style="height:30px;padding-left:5px">
			              <button type="button" onclick="query()" class="btn btn-primary">查询</button>
			               <button type="button" onclick="exportExcel()"  class="btn btn-primary" style="margin-left: 8px;">导出日志</button>
			             
			             </div>
		            </div> 
					<table class="table table-striped table-bordered tables">
						<thead>
							<tr>
								<th>帐号</th>
								<th>提交地址</th>
								<th>提交时间</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="git" items="${gits}">
								<tr>
									<td>${git.account }</td>
									<td>${git.gitRemoteUrl }</td>
									<td>${git.createdAt }</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="page">
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
		});
		
		
		 var pageSize, pageNo, totalPage;
		    var basePath = document.getElementById("basePath").value;
		    $(function($) {
		    	initpage();
		    	$("#pageSize" + pageSize).attr("selected", "selected");
		    });
		    function initpage() {
		    	pageSize = document.getElementById("prePageSize").value;
		    	pageNo = $("#curPage").val();
		    	startTime = $("#startTime").val();
		    	endTime = $("#endTime").val();
		    	queryKey = $("#queryKey").val();
		    	totalPage = $("#totalPage").text();
		    }

		    function pagesCur(pagesize) {
		    	pageSize = pagesize;
		    	ajaxget();
		    }

		    function pagesCur() {
		    	pageSize = $("#pageSize").val();
		    	ajaxget();
		    }

		    function presCur(pageno) {
		    	pageNo = pageno;
		    	ajaxget();
		    }

		    function pre(prepage) {
		    	if (prepage != 0 && (Number(pageNo) + Number(prepage))<=totalPage) {
		    		pageNo = Number(pageNo) + Number(prepage);
		    	} else
		    		pageNo = 1;
		    	ajaxget();
		    }

		    function query() {
		    	initpage();
		    	ajaxget();
		    }

		    function ajaxget() {
		    	var href = "git/statistic?" + "pageNo=" + pageNo + "&pageSize=" + pageSize;
		    	if(startTime != "" && endTime != ""){
		    		href += "&startTime="+startTime+"&endTime="+endTime;
		    	}
		    	if (queryKey != "") {
		    		queryKey = encodeURIComponent(queryKey);
		    		queryKey = encodeURIComponent(queryKey);
		    		href += "&search=" + queryKey;
		    	}
		    	window.location.href = href;
		    }
		    
		    function exportExcel(){
		    	
		    	initpage();
				var href = "git/export?" + "pageNo="+pageNo+"&pageSize="+pageSize ;
				if(startTime != "" && endTime != ""){
					href += "&startTime="+startTime+"&endTime="+endTime;
				}
				if(queryKey != ""){
					queryKey = encodeURIComponent(queryKey);
		    		queryKey = encodeURIComponent(queryKey);
		    		href += "&search=" + queryKey;
				}
				
				window.location.href = href;
		    }
	</script>
</body>
</html>