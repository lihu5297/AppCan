<%@page import="org.zywx.coopman.system.Cache"%>
<%@page import="org.zywx.coopman.commons.Enums.EMAIL_STATUS"%>
<%
String urlpath = request.getContextPath();
String basepath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+urlpath+"/";
%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <div class="navbar-default" role="navigation">  
      <a class="navbar1" href="index.html"> 
          <img alt="Charisma Logo" src="<%=Cache.getSetting("SETTING").getPlatLogo()!=null && Cache.getSetting("SETTING").getPlatLogo()!="" ? Cache.getSetting("SETTING").getPlatLogo() : "static/images/logo.jpg" %>" class="hidden-xs"/>
          <span class="logoW"><%=Cache.getSetting("SETTING").getPlatName() %></span>
      </a>
      <div class="header-right">
          <a class="exit" href="logout">
              退出
          </a>
      </div>
      <div class="header-right">
          <img src="<%=session.getAttribute("icon")!=null && session.getAttribute("icon")!="" ? session.getAttribute("icon") : "static/images/1.png" %>" alt="<%=session.getAttribute("userName") %>" class="photo">
          <span><%=session.getAttribute("userName") %></span>
      </div>
    </div>
    
    <!-- #reSettingPWD_EMAIL -->
	<div class="modal fade" id="reSettingPWD_EMAIL" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">提示</h4>
				</div>
				<div class="modal-body">
					<table>
						<tr>
							<td>确定重置密码？</td>

						</tr>
						<tr>
							<td>系统将默认分配密码发送邮件给此用户。</td>

						</tr>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="edit"
						id="resetPWDButton" onclick="resetPWDButton()">确定</button>
				</div>
			</div>
		</div>
	</div>
	
    <!-- #reSettingPWD -->
	<div class="modal fade" id="reSettingPWD" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">重置密码</h4>
				</div>
				<div class="modal-body">
					<table>
						<tr> 
							<td>新密码:</td>
							<td><input name="password" value="" type="password" id="reset_password"/></td>

						</tr>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="edit"
						id="rewritePWDButton" onclick="rewritePWDButton()">确定</button>
				</div>
			</div>
		</div>
	</div>
	<!--预加载-->
	<div class="mengban" id="mengb" style="display:none;"></div>
	<div id="loading">  
	</div> 
	
	
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script  type="text/javascript" src="<%=basepath %>/static/js/My97DatePicker/WdatePicker.js"></script>	
	<script>
		//预加载调用
	    function openload() {
	        $('#mengb').show();
	        $('#loading').show();
	        $('#loading').html('<img src="static/img/upload.jpg" width="62" height="62"/>');
	    }
	    function closeload() {
	        $('#mengb').hide();
	        $('#loading').hide();
	    }
		$(function(){
			$("#startTime").focus(function(){
				WdatePicker({maxDate:"#F{$dp.$D('endTime')}",dateFmt:'yyyy-MM-dd HH:mm:ss'});
			})
			$("#endTime").focus(function(){
				WdatePicker({minDate:"#F{$dp.$D('startTime')}",dateFmt:'yyyy-MM-dd HH:mm:ss'});
			})
		})
	/*
	各自实现resetPWDButton()和rewritePWDButton()方法;  
	1.手写密码的输入框id是reset_password，根据这个获取密码的值;
	2.通过邮件重置密码的自己实现其中的过程。
	*/
	</script>