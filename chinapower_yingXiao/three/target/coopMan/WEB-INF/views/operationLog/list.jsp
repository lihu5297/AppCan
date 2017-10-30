<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
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
  	<%@include file="../head.jsp" %>
    <div class="main">
    	<%@include file="../left.jsp" %>
      <div class="right">
        <div class="location">
          <a href="javascript:;">日志管理</a>　/　<a href="javascript:;" class="active">操作日志</a>
        </div>
        <div class="content">
          <div class="right-content">
            <div class="right-header">
              <span class="title">操作日志</span>
				<div style="float:right">
              <span>开始时间：</span>
              <input id="startTime" type="text" placeholder="时间插件" value="${startTime }" style="height:30px;padding-left:5px">
              <span style="margin-left: 8px;">结束时间：</span>
              <input id="endTime" type="text" placeholder="时间插件" value="${endTime }" style="height:30px;padding-left:5px">
              <span style="margin-left: 8px;">关键字：</span>
              <input id="queryKey" type="text" placeholder="关键字查询" value="${queryKey }" style="height:30px;padding-left:5px">
              <button type="button" onclick="query()" class="btn btn-primary">查询</button>
              <button type="button" onclick="exportExcel()" style="margin-left: 8px;" class="btn btn-primary">导出日志</button>
              </div>
            </div> 
            <table class="table table-striped table-bordered tables">
              <thead>
                <tr>
                    <th>账号</th>
                    <th>ip</th>
                    <th>操作日志</th>
                    <th>操作时间</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="log" items="${list}">
                <tr>
                  <td>${log.account}</td>
                  <td>${log.ip}</td>
                  <td>${log.operationLog}</td>
                  <td>${log.createdAt}</td>
                </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
          <div class="page">
            <span class="total">共<span>${total}</span>条</span>
            <div class="fr">
            <input type="hidden" id="prePageSize" value=${pageSize }>
              <span class="fl showpage">每页显示
                <select name="pageSize" id="pageSize" onchange="pagesCur()">
                  <option id="pageSize10" selected="selected" value=10 onclick="pagesCur(10)">10</option>
                  <option id="pageSize20" value=20 onclick="pagesCur(20)">20</option>
                  <option id="pageSize30" value=30 onclick="pagesCur(30)">30</option>
                  <option id="pageSize50" value=50 onclick="pagesCur(50)">50</option>
                </select>条
              </span>
              <ul id="page">
                <li>
                  <a href="javascript:;" class="Previous" onclick="presCur(0)">
                    |<
                  </a>
                </li>
                <li>
                  <a href="javascript:;" class="Previous" onclick="pre(-1)">
                    <
                  </a>
                </li>
                <li>
                  第
                  <input type="text" value="${curPage}" id="curPage">
                  页 共<span id="totalPage">${totalPage}</span>页
                </li>
                <li>
                  <a href="javascript:;" class="Previous" onclick="pre(1)">
                  >
                  </a>
                </li>
                <li>
                  <a href="javascript:;" class="Next" onclick="presCur(${totalPage})">
                    |>
                  </a>
   				 </li>
         	 </ul>
            </div>
          </div>
          
        </div>
        <div class="clear"></div>
      </div>
    </div>
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/bootstrap.min.js"></script>
    <script src="static/js/init.js"></script>
    <script src="static/operationLog/js/initpage.js"></script>
    <script>
    $(function(){
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(6).find('.nav-pills').show();
		$('.menuNav>li').eq(6).css('background-color','#383f4e');
	})
    </script>
  </body>
</html>