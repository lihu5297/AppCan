<%@page import="org.zywx.coopman.entity.Manager"%>
<%@page import="org.zywx.coopman.entity.module.Module"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div class="left">
        <ul class="menuNav">
          <li class="active">
            <a class="ajax-link" href="index.html"><i class="glyphicon glyphicon-home"></i><span> 首页</span></a>
          </li>
          <% 
          List<Module> modules = ((Manager)session.getAttribute("manager")).getManageModule(); 
          System.out.print(modules.size());
          for(Module module : modules){
        	  System.out.println("module:"+module.toString());
          		if(module.getChildrenModule()!=null && module.getChildrenModule().size()>0){
          %>
          <li>
              <a href="javascript:;"><i class="glyphicon <%=module.getUrl() %>"></i><span> <%=module.getCnName() %></span></a>
              <ul class="nav-pills">
              		<% for(Module modul : module.getChildrenModule()){%>
                  <li><a name="<%=modul.getCnName()%>" href="<%=modul.getUrl()%>"><%=modul.getCnName()%></a></li>
                  <%} %>
              </ul>
          </li>
          <%} }%>
          <!-- <li><a class="ajax-link" href="javascript:;"><i
                      class="glyphicon glyphicon-edit"></i><span> 流程管理</span></a>
              <ul class="nav-pills">
                  <li><a href="process/template/list">流程阶段</a></li>
                  <li><a href="javascript:;">任务流程</a></li>
              </ul>
          </li>
          <li><a class="ajax-link" href="javascript:;"><i class="glyphicon glyphicon-list-alt"></i><span> 引擎管理</span></a>
              <ul class="nav-pills">
                  <li><a href="engine/list?type=PUBLIC">公共引擎</a></li>
                  <li><a href="engine/list?type=PRIVATE">内部引擎</a></li>
              </ul>
          </li>
          <li><a class="ajax-link" href="javascript:;"><i class="glyphicon glyphicon-wrench"></i><span> 插件管理</span></a>
              <ul class="nav-pills">
                  <li><a href="plugin/list?type=PUBLIC">公共插件</a></li>
                  <li><a href="plugin/list?type=PRIVATE">内部插件</a></li>
                  <li><a href="plugin/category/list">插件分类</a></li>
              </ul>
          </li>
          <li><a class="ajax-link" href="javascript:;"><i class="glyphicon glyphicon-equalizer"></i><span> 统计管理</span></a>
              <ul class="nav-pills">
                  <li><a href="javascript:;">空间统计</a></li>
                  <li><a href="javascript:;">GIT统计</a></li>
                  <li><a href="javascript:;">打包统计</a></li>
              </ul>
          </li>
          <li><a class="ajax-link" href="javascript:;"><i
                      class="glyphicon glyphicon-align-justify"></i><span> 日志管理</span></a>
              <ul class="nav-pills">
                  <li><a href="operationlog">操作日志</a></li>
                  <li><a href="platformlog">平台日志</a></li>
              </ul>       
          </li>
          <li>
              <a class="ajax-link" href="javascript:;"><i class="glyphicon glyphicon-plus"></i><span> 账户管理</span></a>
              <ul class="nav-pills">
                  <li><a href="manager/super">超级管理员</a></li>
                  <li><a href="manager">管理员</a></li>
              </ul>
          </li>
          <li><a class="ajax-link" href="javascript:;"><i class="glyphicon glyphicon-calendar"></i><span> 平台设置</span></a>
              <ul class="nav-pills">
                  <li><a href="setting?type=platForm">平台形象</a></li>
                  <li><a href="setting/backuplog?type=backup">平台备份</a></li>
                  <li><a href="setting?type=integrate">接入设置</a></li>
                  <li><a href="setting?type=email">邮件设置</a></li>
                  <li><a href="setting?type=authorize">授权信息</a></li>
              </ul>
          </li> -->
        </ul>
      </div>
     
