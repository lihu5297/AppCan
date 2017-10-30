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
				<a href="javascript:void(0);">打包统计</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<div style="float: right">
								<span>开始时间：</span> 
								<input id="startTime" name="startTime"
									type="text" placeholder="时间插件" value="" style="height:30px;padding-left:5px"> 
								<span style="margin-left: 8px;">结束时间：</span>
								<input id="endTime" name="endTime" type="text"
									placeholder="时间插件" value="" style="height:30px;padding-left:5px">
								<button type="button" onclick="query()" style="margin-left: 8px;" class="btn btn-primary">查询</button>
								<!-- <button type="button" onclick="exportExcel()">导出日志</button> -->
						</div>
					</div>
					<table class="table table-striped table-bordered tables">
						<thead>
							<tr>
								<th>日期</th>
								<th>打包次数</th>
								<th>打包失败次数</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="package123" items="${packages}" varStatus="status">
									<tr>
									<c:forEach items="${package123}" var="entry">
										<td>${entry.value}</td>
									</c:forEach>
									<c:forEach items="${package123}" var="test">
											<c:if test="${test.key=='dateTime' }" ><td><a href="javascript:void(0)" data-toggle="modal" data-target="#info" onclick="info('${test.value}')">失败详情</a></td></c:if>
										</c:forEach>
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
	<!-- #info -->
	<div class="modal fade" id="info" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content" style="max-height:700px;overflow-y:scroll;overflow-x:overflow:hidden;">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">打包失败详情</h4>
				</div>
				<div class="modal-body">

					<table class="table table-striped table-bordered tables">
						<thead>
							<tr>
								<th>打包失败帐号</th>
								<th>打包失败时间</th>
								<th>打包失败日志</th>
							</tr>
						</thead>
						<tbody id="moreInfo">
							<!--<tr >
								 <td><span id="accountInfo" name="accountInfo"></span></td>
							<td><span id="emailInfo" name="emailInfo"></span></td>
							<td><span id="userNameInfo" name="userNameInfo"></span></td> 
							</tr>-->
						</tbody>
					</table>
				</div>
			</div>
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
		function info(date) {
			var option = {
				"url" : "package/statistic/info?date=" + date,
				"type" : "get",
				"dataType" : "json",
				"success" : function(msg) {
					if (msg.status == "success") {
						initData(msg.message);
					}
				},
				"error" : function(msg) {
					alert("查询失败！");
				}
			};
			$.ajax(option);
		}

		function initData(data) {
			$("#moreInfo").html("");
			for (var i = 0; i < data.length; i++) {
				var tr = $("<tr></tr>");
				var tdAccount = $("<td></td>");
				var tdTime = $("<td></td>");
				var tdLog = $("<td></td>");
				var aTag = $("<a></a>");

				tdAccount.append(data[i].account);
				tdTime.append(data[i].dateTime);
				
				aTag.attr("href",data[i].buildLogUrl);
				aTag.append("Log日志");
				tdLog.append(aTag);

				tr.append(tdAccount);
				tr.append(tdTime);
				tr.append(tdLog);

				$("#moreInfo").append(tr);
			}
		}
		
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
	    	var href = "package/statistic?" + "pageNo=" + pageNo + "&pageSize=" + pageSize;
	    	if(startTime!=""&&endTime==""||endTime!=""&&startTime==""){
	    		alert("开始时间或结束时间不可为空");
	    		window.location.href = "package/statistic";
	    		 return;
	    	}
	    	if(startTime != "" && endTime != ""){
	    		href += "&startTime="+startTime+"&endTime="+endTime;
	    	}
	    	window.location.href = href;
	    }
	    
	    function exportExcel(){
	    	
	    }
	</script>
</body>
</html>