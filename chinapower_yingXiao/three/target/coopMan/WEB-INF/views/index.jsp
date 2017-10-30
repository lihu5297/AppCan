<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@include file="taglib.jsp"  %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title><%=Cache.getSetting("SETTING").getPlatName() %></title>
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
  	<%@include file="head.jsp" %>
    <div class="main">
     <%@include file="left.jsp" %>
      <div class="right">
        <div class="location">
          <a href="javascript:;">用户管理</a>　/　<a href="javascript:;" class="active">协同用户</a>
        </div>
        <div class="content">
          <div class="right-content">
            <div class="right-header">
              <span class="title">协同用户</span>
              <a href="javascript:;" class="downModle">下载导入用户模板</a>
              <button type="button" class="btn btn-primary">导入用户</button>
              <button type="button" class="btn btn-primary">导出用户</button>
              <button type="button" class="btn btn-primary">创建新用户</button>
              <div class="input-group btn-niu">
                <input type="text" class="form-control" placeholder="用户名/邮箱/角色">
                <span class="input-group-btn">
                  <button class="btn btn-default" type="button">查询</button>
                </span>
              </div>
            </div> 
            <table class="table table-striped table-bordered tables">
              <thead>
                <tr>
                    <th><input type="checkbox">全选</th>
                    <th>用户名</th>
                    <th>邮箱</th>
                    <th>角色</th>
                    <th>姓名</th>
                    <th>接入平台</th>
                    <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
                <tr>
                  <td><input type="checkbox"></td>
                  <td>hongxin.fang</td>
                  <td>hongxin.fang@163.com</td>
                  <td>项目创建者</td>
                  <td>panpan</td>
                  <td>公司内部</td>
                  <td><a href="javascript">用户信息</a><a href="javascript">重置密码</a><a href="javascript">停用</a></td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="page">
            <span class="total">第1条 到 第10条 共133条</span>
            <div class="fr">
              <span class="fl showpage">每页显示
                <select name="" id="">
                  <option>10</option>
                  <option>20</option>
                  <option>30</option>
                </select>条
              </span>
              <ul id="page">
                <li>
                  <a href="#" class="Previous">
                    |<
                  </a>
                </li>
                <li>
                  <a href="#" class="Previous">
                    <
                  </a>
                </li>
                <li>
                  第
                  <input type="text">
                  页 共14页
                </li>
                <li>
                  <a href="#" class="Previous">
                  >
                  </a>
                </li>
                <li>
                  <a href="#" class="Next">
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
  </body>
</html>