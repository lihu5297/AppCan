<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"  %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <base href="<%=basePath%>"/>
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
  	<jsp:include page="../head.jsp"></jsp:include>
    <div class="main">
      <jsp:include page="../left.jsp"></jsp:include>
      <div class="right">
        <div class="location">
          <a href="javascript:;">日志管理</a>　/　<a href="javascript:;" class="active">操作日志</a>
        </div>
        <div class="content">
          <div class="right-content">
            <div class="right-header">
              <div style="float:right">
              <span>开始时间：</span>
              <input id="startTime" type="text" placeholder="时间插件" style="height: 30px;padding-left: 5px;" value="${startTime }">
              <span style="margin-left:8px">结束时间：</span>
              <input id="endTime" type="text" placeholder="时间插件" style="height: 30px;padding-left: 5px;" value="${endTime }">
              <button type="button" onclick="query()" class="btn btn-primary">查询</button>
              <button type="button" onclick="exportExcel()" style="margin-left:8px" class="btn btn-primary">导出日志</button>
              </div>
            </div> 
            <table class="table table-striped table-bordered tables">
              <thead>
                <tr>
                    <th>服务器名称</th>
                    <th>操作日志</th>
                    <th>发生时间</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="log" items="${list}">
                <tr>
                  <td>${log.hostName}</td>
                 
                  <td><a data-toggle="modal" data-target="#logInfo" onclick="init('${log.id}')">异常详情</a> <input type="hidden" id="loginfo_${log.id}" value="${log.content}"/></td>
                  <td>${log.logDate}</td>
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
    
    <!-- warning -->
	<div class="modal fade" id="logInfo" tabindex="-1" role="dialog" aria-labelledby="createNew">
		<div class="modal-dialog" role="document" style="width:800px">
			<div class="modal-content" style="width:100%">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="warning_title">异常详情</h4>
				</div>
				<div class="modal-body" style="display:block;width:98%;overflow-x:auto">
					<div id="warning_title_span">提示内容</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
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
		$('.menuNav>li').eq(6).find('.nav-pills').show();
		$('.menuNav>li').eq(6).css('background-color','#383f4e');
	})
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
    	var href = "platformlog/list?" + "pageNo=" + pageNo + "&pageSize=" + pageSize;
    	if(startTime != "" && endTime != ""){
    		href += "&startTime="+startTime+"&endTime="+endTime;
    	}
    	window.location.href = href;
    }
    
    function init(id){
    	var content = $("#loginfo_"+id).val();
    	$("#warning_title_span").html(content);
    }
    </script>
    
  </body>
</html>