<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"  %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
  <base href="<%=basePath%>">
    <meta charset="utf-8">
    <title><%=Cache.getSetting("SETTING").getPlatName() %> - 用户管理</title>
    <link rel="shortcut icon" href="static/images/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>
    <link href="static/css/bootstrap.min.css" rel="stylesheet">
    <link href='static/css/style.css' rel='stylesheet'>
    <link rel="stylesheet" href="static/css/zTreeStyle/zTreeStyle.css" type="text/css"/>
    <!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
      .user-info{
        text-align: right;
        padding-right: 20px;
        vertical-align: middle;
      }
      .user-ri{
        width:458px;
      }
      #userMs{
        width: 430px;
        min-height: 100px;
        resize: none;
      }
      .userlevel{
        min-width: 120px;
      }
      .user-ri input[type="text"]{
        width: 250px;
      }
      table.userTable tbody tr td{
        vertical-align: middle;
      }
    </style>
  </head>
  <body>
  	<%@include file="../head.jsp" %>
    <div class="main">
    <%@include file="../left.jsp" %>
      <div class="right">
        <div class="location">
          <a href="javascript:;">用户管理</a>　/　<a href="javascript:;" class="active">协同用户</a>
        </div>
        <form action="user/list" id="userForm" method="post">
        <div class="content">
          <div class="right-content">
            <div class="right-header">
              <span class="title">协同用户</span>
              <iframe id="downExcelModule" style="display:none;" src="" uri="${excelModulePath}">
              		
              </iframe>
             	<!-- 企业版且和EMM4.0对接，用户是从EMM4.0系统中获取，通过RPC获取用户 -->
				<c:if test="${integration==true && serviceFlag=='enterprise'}">
					<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#imporgFromEmm" id="invokeTree">
					  导入用户
					</button>
				</c:if>
				<!-- 企业版不和EMM4.0对接，用户是从自己添加或导入 -->
				<c:if test="${integration==false && serviceFlag=='enterprise'}">
				 	<a href="javascript:void(0);" class="downModle">下载导入用户模板</a>
				 	<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#importFromExcel">
					  导入用户
					</button>
					<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#createNew" id="crtNewUser">
					  创建新用户
					</button>
				</c:if>
				<!-- 
				<c:if test="${integration==false && serviceFlag=='enterpriseEmm3'}">
					<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#imporgFromEmm" id="invokeTree">
					  导入用户
					</button>
				</c:if>
				 -->
				 <!-- 高校版，用户从EXCEL导入或自己创建，登录通过线上sso，用户多一个昵称，nickName-->
				<c:if test="${serviceFlag=='efficient'}">
				 	<a href="javascript:void(0);" class="downModle">下载导入用户模板</a>
				 	<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#importFromExcel">
					  导入用户
					</button>
					<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#createNew" id="crtNewUser">
					  创建新用户
					</button>
				</c:if>
				 <!-- 和EMM3407版本对接，目前功能可能有问题-->
				<c:if test="${serviceFlag=='enterpriseEmm3'}">
				 	<a href="javascript:void(0);" class="downModle">下载导入用户模板</a>
				 	<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#importFromExcel">
					  导入用户
					</button>
					<button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#createNew" id="crtNewUser">
					  创建新用户
					</button>
				</c:if>
              <button type="button" class="btn btn-primary" id="exportAll">导出用户</button>
              
              <div class="input-group btn-niu">
                <input type="text" name="search" value="${search}" class="form-control" placeholder="用户名/姓名/邮箱/单位" style="width: 180px;">
                <span class="input-group-btn">
                  <button class="btn btn-default" type="button"  onclick="submitForm()" style="border-radius: 0 4px 4px 0;">查询</button>
                </span>
                <select name="status" class="form-control" style="width:100px;border-radius: 4px;" id="userStatus">
                	<option value="">全部</option>
                	<option value="NORMAL">启用</option>
                	<option value="FORBIDDEN">停用</option>
                </select>
              </div>
            </div> 
            <table class="table table-striped table-bordered tables userTable">
              <thead>
                <tr>
                    <th style="width:8%;text-align: left;"><input type="checkbox" onclick="switchMainCheck(this)" style="float: left;margin: 4px 10px 0;">全选</th>
                    <th style="width:5%;">序号</th>
                    <th style="width:15%;">用户名</th>
                    <th style="width:18%;">邮箱</th>
                    <th style="width:25%;">所属单位</th>
                    <th style="width:13%;">姓名</th>
                    <th style="width:18%;">操作</th>
                </tr>
              </thead>
              <tbody>
              	<c:forEach var="item" varStatus="itemCount" items="${list.content}">
	              	<tr>
	                  <td><input type="checkbox" class="listCheck" value="${item.id}"></td>
	                  <td>${itemCount.index+1}</td>
	                  <td><c:out value="${item.account }"/></td>
	                  <td><c:out value="${item.email }"/></td>
	                  <td><c:out value="${item.filialeName }"/></td>
	                  <td><c:out value="${item.userName }"/></td>
	                  <td>
	                  	<a href="javascript:void(0);return false;" class="editUser" userId="${item.id }"  data-toggle="modal" data-target="#createNew">用户信息</a>
	                 	 <c:if test="${serviceFlag =='enterpriseEmm3' }">
	                		 <c:if test="${item.receiveMail=='PERMIT' && emailStatus =='OPEN' && !empty item.email}">
			                 	 <a href="javascript:void(0);return false;" onclick="resetPwd('${item.id}','${item.email }')" >重置密码</a>
		                 	 </c:if>
		                 	 <c:if test="${item.receiveMail=='DENIED' || emailStatus =='CLOSE' || empty item.email}">
								<a href="javascript:void(0);" data-toggle="modal" data-target="#updatePwd" onclick="setUserId('${item.id}')">重置密码</a>
		                 	 </c:if>
	                 	 </c:if>
	                      <c:if test="${item.status=='NORMAL' }">
		                 	 <a href="javascript:void(0);"   onclick="statusChange('${item.id}','disable');">停用</a>
	                 	 </c:if>
	                 	 <c:if test="${item.status=='FORBIDDEN' }">
	                 	 	<a href="javascript:void(0);"  onclick="statusChange('${item.id}','enable');">启用</a>
	                 	 </c:if>
                     </td>
	                </tr>
              	</c:forEach>
                
              </tbody>
            </table>
          </div>
          <div class="page">
          	<a href="javascript:void(0);" class="delRecord" onclick="delRecord()">删除 </a>
            <span class="total">第${list.number*list.size+1}条 到 第${(list.number+1)*list.size>list.totalElements?list.totalElements:(list.number+1)*list.size}条 共${list.totalElements}条</span>
            <div class="fr">
              <span class="fl showpage">每页显示
                <select name="pageSize" id="pageSize" onchange="submitForm()">
                  <option>10</option>
                  <option>20</option>
                  <option>30</option>
                </select>条
              </span>
              <ul id="page">
                <li>
                  <a href="javascript:void(0);" class="begin" onclick="submitForm()">
                    |&lt;
                  </a>
                </li>
                <li>
                  <a href="javascript:void(0);" class="Previous">
                    &lt;
                  </a>
                </li>
                <li>
                  第
                  <input type="text" value="${list.number+1}">
                  页 共${list.totalPages}页
                </li>
                <li>
                  <a href="javascript:void(0);" class="Next">
                  &gt;
                  </a>
                </li>
                <li>
                  <a href="javascript:void(0);" class="last">
                    |&gt;
                  </a>
                </li>
              </ul>
            </div>
          </div>
          
        </div>
        <input type="hidden" name="pageNo" value="${list.number+1}"/>
        </form>
        <div class="clear"></div>
      </div>
    </div>
    <!-- new User Modal -->
<div class="modal fade" id="createNew" tabindex="-1" role="dialog" aria-labelledby="createNew">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><span name="titleFlag">创建新用户</span></h4>
      </div>
      <div class="modal-body">
      	<form action="user/create" method="post" id="newUserForm">
        <table class="table table-striped table-bordered tables">
        	<tr>
        		<td class="user-info"><font color="red">*</font>  用户名</td>
        		<td class="user-ri">
        			<input type="text" name="account"/>
        		</td>
        	</tr>
        	<tr>
        		<td class="user-info"><font color="red">*</font>邮箱</td>
        		<td class="user-ri">
        			<input type="email" name="email"/>
        		</td>
        	</tr>
        	<tr class="crtPwdTr">
        		<td class="user-info">
        			<c:if test="${emailStatus =='CLOSE' }">
        				<font color="red">*</font>
        			</c:if>
        			密码
        		</td>
        		<td class="user-ri">
        			<input type="password" name="password" id="crtPwd"/>
        		</td>
        	</tr>
        	<tr class="crtPwdTr">
        		<td class="user-info">
        			<c:if test="${emailStatus =='CLOSE' }">
        				<font color="red">*</font>
        			</c:if>
        			确认密码
        		</td>
        		<td class="user-ri">
        			<input type="password" name="passwordok" id="crtPwdOk"/>
        		</td>
        	</tr>
        	<tr>
        		
        		<td class="user-info"><font color="red">*</font>姓名</td>
        		<td class="user-ri">
        			<input type="text" name="userName"/>
        		</td>
        	</tr>
        	<tr>
        		<td class="user-info">所属单位</td>
        		<td class="user-ri">
        			<select style="width: 128px;" id="filialeId"  " name="filialeId"></select>
        		</td>
        	</tr>
        	<tr>
        		
        		<td class="user-info">性别</td>
        		<td class="user-ri">
        			<input type="radio" name="gender" value="MALE" checked="checked"/>男
        			<input type="radio" name="gender" value="FEMALE"/>女
        		</td>
        	</tr>
        	<tr>
        		
        		<td class="user-info">手机号</td>
        		<td class="user-ri">
        			<input type="text" name="cellphone"/>
        		</td>
        	</tr>
        	<tr>
        		<td class="user-info">初始化权限</td>
        		<td class="user-ri">
        			<c:forEach var="permission" items="${userPermission}">
        				<input type='checkbox' name="initPer"  value="${permission.id }">${permission.cnName }
        			</c:forEach>
        		</td>
        	</tr>
        	<tr>
        		<td class="user-info">所在地</td>
        		<td class="user-ri" colspan="2">
        			<input type="text" name="address"/>
        		</td>
        	</tr>
        	<tr>
        		<td class="user-info">备注<input type="hidden" name="id"/></td>
        		<td class="user-ri" colspan="2">
        			<textarea rows="3" id="userMs" cols="38" name="remark"></textarea>
        		</td>
        	</tr>
        </table>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary saveUserChange">保存</button>
      </div>
    </div>
  </div>
</div>
<!-- EMM导入用户 -->
<div class="modal fade" id="imporgFromEmm" tabindex="-1" role="dialog" aria-labelledby="imporgFromEmm">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">导入用户</h4>
      </div>
      <div class="modal-body">
        <table class="table">
        	<tr>
        		<td>组织机构</td>
        		<td>
        		</td>
        	</tr>
        	<tr>
        		<td>
        			<input type="text" name="keywords"/>
        		</td>
        		<td>
        			<input type="button" value="搜索" id="searcdEmm"/>
        			<input type="button" value="显示组织机构" id="showOrg"/>
        		</td>
        	</tr>
        	<tr>
        		<td colspan="2">
        			<!-- 树控件 -->
        			<ul id="treeDemo" class="ztree" style="width:260px; overflow:auto;"></ul>
        			<div id="listPerson" style="display:none;">
        				<form action="user/searchEmmPerson" method="post" id="formSearchPerson">
        					<table id="emmSearchResult" class="table table-striped table-bordered tables">
        						<tr>
        							<td></td>
        							<td>唯一标识<input type="hidden" name="formKeyWords"/></td>
        							<td>邮箱</td>
        							<td>手机号</td>
        						</tr>
        					</table>
        				</form>
        			</div>
        		</td>
        	</tr>
        	
        </table>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary submitEmmUser">保存</button>
      </div>
    </div>
  </div>
</div>

<!-- 从EXCEL导入用户 -->
<div class="modal fade" id="importFromExcel" tabindex="-1" role="dialog" aria-labelledby="importFromExcel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Excel导入用户</h4>
      </div>
      <div class="modal-body">
      <form method="post" action="user/import" id="importUserFromExcel" enctype="multipart/form-data">
        <table class="table">
        	<tr>
        		<td>excel</td>
        		<td>
        			<input type="file" name="file"/>
        		</td>
        	</tr>
        	
        </table>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary submitExcelUser">保存</button>
      </div>
    </div>
  </div>
</div>
    <!-- 重置密码 -->
<div class="modal fade" id="updatePwd" tabindex="-1" role="dialog" aria-labelledby="updatePwd">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">重置密码</h4>
      </div>
      <div class="modal-body">
      	<form action="user/resetPwd" method="post">
        <table class="table">
        	<tr>
        		<td>新密码</td>
        		<td>
        			<input type="hidden" name="userId"/>
        			<input type="password" name="password" id="UPDATEPWD" value=""/>
        		</td>
        	</tr>
        	<tr>
        		<td>确认新密码</td>
        		<td>
        			<input type="password" name="passwordok" id="UPDATEPWDOK" value=""/>
        		</td>
        	</tr>
        </table>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary saveNewPwd">保存</button>
      </div>
    </div>
  </div>
</div>
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/bootstrap.min.js"></script>
    <script src="static/js/jquery.form.js"></script>
    <script src="static/js/init.js"></script>
    <script type="text/javascript" src="static/js/ztree/jquery.ztree.core-3.5.js"></script>
    <script type="text/javascript" src="static/js/ztree/jquery.ztree.excheck-3.5.min.js"></script>
    <script type="text/javascript">
   	 	var basePath = "<%=basePath%>";
   	 	var status = "${userStatus}";
    	$('.menuNav li').removeClass('active');
		$('.menuNav li').eq(1).find('.nav-pills').show();
		$('.menuNav li').eq(1).css('background-color','#383f4e');
		loadAdminFiliale();
	    function submitForm(){
	    	$(":input[name='pageNo']").val(1);
			$("#userForm").attr("action","user/list").submit();
		}
	    function isChinese(temp){
			var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/;
   			return reg.test(temp);
		}
		$(".btn-niu input").keydown(function(e){
		    if(e.keyCode==13){
		    	submitForm();
		    }
	    })
	  	$("select#userStatus").change(function(){
	  		submitForm(); 
	    });
    	$(function(){
    		//回显每页显示多少条
    		if(null!="${list.size}"){
	    		$("#pageSize").val("${list.size}");
    		}
    		//下一页
    		$(".Next").click(function(){
    			if("${hasNext}"=="true"){
	    			$(":input[name='pageNo']").val("${list.number+2}");
	    			$("#userForm").attr("action","user/list").submit();
    			}
    		});
    		//上一页
    		$(".Previous").click(function(){
    			if("${hasPrevious}"=="true"){
    				$(":input[name='pageNo']").val("${list.number}");
        			$("#userForm").attr("action","user/list").submit();			
    			}
    			
    		});
    		//尾页
    		$(".last").click(function(){
    			if("${list.totalPages==0}"=="true"){
    				return false;
    			}
    			$(":input[name='pageNo']").val("${list.totalPages}");
    			$("#userForm").attr("action","user/list").submit();
    		});
    		//导出用户
    		$("#exportAll").click(function(){
    			$("#userForm").attr("action","user/export").submit();
    		});
    		//添加新用户
    		$(".saveUserChange").click(function(){
    			var account = $(":input[name='account']").val();
    			var email = $(":input[name='email']").val();
    			var userName = $(":input[name='userName']").val();
    			var nickName= $(":input[name='nickName']").val();
    			if("${serviceFlag}"!="enterpriseEmm3"){
    				if(""==$.trim(account)){
        				alert("用户名不可以为空");
        				return false;
        			}
        			if(isChinese(account)){
        				alert("用户名不可以为中文");
        				return false;
        			}
    			}
    			if("${serviceFlag}"=="efficient"){
    				if(""==$.trim(nickName)){
        				alert("昵称不可以为空");
        				return false;
        			}
    			}
    			if(""==$.trim(email)){
    				alert("邮箱不可以为空");
    				return false;
    			}
    			if(""==$.trim(userName)){
    				alert("姓名不可以为空");
    				return false;
    			}
    			/**
    			if("${emailStatus}"=="CLOSE"){
    				if($("#crtPwd").val()==""){
    					alert('密码不可以为空');
    					return false;
    				}
    				if($("#crtPwd").val()!=$("#crtPwdOk").val()){
    					alert('两次密码不一致');
    					return false;
    				}
    			}**/
    			if($("#crtPwd").val()!="" && $("#crtPwdOk").val()!=""){
    				if($("#crtPwd").val()!=$("#crtPwdOk").val()){
    					alert('两次密码不一致');
    					return false;
    				}
    			}
    			if("${serviceFlag}"=="efficient"){
    				var ajax_url = '<c:url value="/user/createEfficient"/>' // 表单目标
    			}else{
    				var ajax_url = '<c:url value="/user/create"/>' // 表单目标
    			}
    			var ajax_type = $("#newUserForm").attr('method'); // 提交方法
    			var ajax_data = $("#newUserForm").serialize(); // 表单数据
    			$.ajax({
    				type : ajax_type, // 表单提交类型
    				url : ajax_url, // 表单提交目标
    				data : ajax_data, // 表单数据
    				dataType : 'json',
    				success : function(msg) {
    					if (msg.flag == '1') { // msg 是后台调用action时，你穿过来的参数
    						alert("操作成功");
    						$("button[class='close']").click();
    						window.location.reload();
    					} else if (msg.flag == '0'){
    						alert(msg.msg);
    					}else{
    						alert("操作失败");
    					}
    				}
    			});
    		});
    		
    		//编辑用户时候,查询用户信息
    		$(".editUser").click(function(){
    			$("span[name='titleFlag']").text("用户信息");
    			//if("${emailStatus}"=="CLOSE"){
    				//无论是否开启关闭邮件服务，都隐藏密码。
    				$(".crtPwdTr").hide();//将密码，确认密码两行隐藏掉
    			//}
    				$('input[name="initPer"]').each(function(){
    					$(this).removeProp("checked"); 
						$(this).removeProp("disabled");
					});
    			var id = $(this).attr("userId");
    			$.ajax({
    				type : 'get',
    				url : '<c:url value="/user/"/>'+id,
    				dataType : "json",
    				success : function(data) {
    					if (data.flag == 1) {
    						$(":input[name='account']").val(data.user.account).attr("readonly","readonly");
    						$(":input[name='email']").val(data.user.email).attr("readonly","readonly");
    						$(":input[name='cellphone']").val(data.user.cellphone);
    						$(":input[name='qq']").val(data.user.qq);
    						$(":input[name='address']").val(data.user.address);
    						$(":input[name='remark']").val(data.user.remark);
    						$(":input[name='userName']").val(data.user.userName);
    						$("#filialeId").val(data.user.filialeId);
    						if("${serviceFlag}"=="efficient"){
    							$(":input[name='nickName']").val(data.user.nickName).attr("readonly","readonly");
    						}
    						$(":input[name='userlevel']").val(data.user.userlevel);
    						
    						/* if(data.user.userlevel=='NORMAL'){
    							$("#NORMAL").attr("selected","selected");
    						}else{
    							$("#ADVANCE").attr("selected","selected");
    						} */
    						
    						$(":hidden[name='joinPlat']").val(data.user.joinPlat);
    						if(data.user.joinPlat=='INNER'){
	    						$("#joinPlatStr").html('公司内部');
    						}else if(data.user.joinPlat=='APPCAN'){
    							$("#joinPlatStr").html('APPCAN');
    						}
    						if(data.user.gender=='MALE'){
    							$("input[type='radio'][value='MALE']").get(0).checked = true;
    						}else if(data.user.gender=='FEMALE'){
    							$("input[type='radio'][value='FEMALE']").get(0).checked = true;
    						}
    						
    						$(":hidden[name='id']").val(data.user.id);
    						
    						//选中初始化权限
    						$.each(data.user.initPer, function(i,val){
								$('input[name="initPer"]').each(function(){ 
									if(val==$(this).val()){
										//$(this).attr("checked",'true'); 
										$(this).prop("checked",'true');
									}
									if(data.user.filialeId!=1){
										if($(this).val()!=160){
											$(this).removeProp("checked"); 
											 $(this).prop("disabled",true);
										 }else{
											$(this).prop("disabled",false);
										 }
									}else{
										if($(this).val()==160){
											$(this).removeProp("checked"); 
											 $(this).prop("disabled",true);
										 }else{
				    						 $(this).prop("disabled",false); 
										 }
									}
    							}); 
    						});    
    					}
    					if (data.flag == 0) {
    						alert("error");
    					}
    				},
    				error : function() {
    				}
    			});
    		});
    		 
    		$("#filialeId").change(function () {
    			var val= $("#filialeId").val();
    			$('input[name="initPer"]').each(function(){ 
    				if(val!=1){
						 if($(this).val()!=160){
							 $(this).removeProp("checked"); 
							 $(this).prop("disabled",true);
						 }else{
							$(this).prop("disabled",false);
						 }
    				}else{
    					if($(this).val()==160){
							$(this).removeProp("checked"); 
							 $(this).prop("disabled",true);
						 }else{
    					 $(this).prop("disabled",false); 
						 }
    				}
					
				}); 
			});
    		//导入用户
    		$("#invokeTree").click(function(){
    			$.ajax({
    				type : 'get',
    				url : '<c:url value="/user/fromemm"/>',
    				dataType : "json",
    				async:false,
    				success : function(data) {
    					if (data.flag == 1) {
    						zNodes = data.listOrg;
    					}
    					if (data.flag == 0) {
    						alert("error");
    					}
    				},
    				error : function() {
    				}
    			});
    			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
    		});
    		
    		//---------------------------------tree --------------------------------------
    		function zTreeBeforeCheck(treeId, treeNode) {
    			if(treeNode.parentFlag!=undefined){
    				//点击的是机构
    				alert("请选择人员");
    				return false;
    			}
    			if(!treeNode.checked){//准备选中,暂时还没选中
    				var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
    			    if(treeNode.parentFlag==undefined){//点击人
    			    	var uniqueField = treeNode.uniqueField;//唯一标示  作为 账号
    			    	var email = treeNode.email;//邮箱
    			    	var name = treeNode.name;//用户姓名
    			    	var mobileNo = treeNode.mobileNo;//手机号
    			    	var userSex = treeNode.userSex;//性别 male female
    			    	if(null==email||""==email){
    			    		alert(name+",没有邮箱,无法添加");
    			    		treeNode.checked=false;
    			    		return false;
    			    	}
    			    }
    			}
    			return true;
			};

    		function zTreeOnCheck(event, treeId, treeNode) {
    			if(treeNode.checked){
    				var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
    			    if(treeNode.parentFlag==undefined){//点击人
    			    	var uniqueField = treeNode.uniqueField;//唯一标示  作为 账号
    			    	var email = treeNode.email;//邮箱
    			    	var name = treeNode.name;//用户姓名
    			    	var mobileNo = treeNode.mobileNo;//手机号
    			    	var userSex = treeNode.userSex;//性别 male female
    			    	if(null==email||""==email){
    			    		alert(name+",没有邮箱,无法添加");
    			    		treeNode.checked=false;
    			    		return false;
    			    	}
    			    	/* if(null==mobileNo||""==mobileNo){
    			    		alert(name+",没有手机号,无法添加");
    			    		treeNode.checked=false;
    			    		return false;
    			    	} */
    			    	//校验用户名
    			    	var patrn=/^[a-zA-Z0-9]{1}([a-zA-Z0-9]|[._@])*[a-zA-Z0-9]$/; 
    			    	if (!patrn.exec(uniqueField)){
    			    		alert(name+",用户名包含字母、数字、 _、.和@，并且开始和结束只能是字母或数字");
    			    		treeNode.checked=false;
    			    		return false 
    			    	} 
    			    }
    			}
			    return true;
			};
    		function zTreeOnClick(event, treeId, treeNode) {
			    var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			    if(treeNode.parentFlag==undefined){//点击人
			    	//alert(JSON.stringify(treeNode));
			    	/*var uniqueField = treeNode.uniqueField;//唯一标示  作为 账号
			    	var email = treeNode.email;//邮箱
			    	var name = treeNode.name;//用户姓名
			    	var mobileNo = treeNode.mobileNo;//手机号
			    	var userSex = treeNode.userSex;//性别 male female
			    	if(null==email||""==email){
			    		alert(name+",没有邮箱,无法添加");
			    		return false;
			    	}*/
			    	
			    }else{//点击机构
			    	$.ajax({
	    				type : 'get',
	    				url : '<c:url value="/user/emmDetail/"/>'+treeNode.orgId,
	    				dataType : "json",
	    				async:false,
	    				success : function(data) {
	    					if (data.flag == 1) {
	    						var addNodes = data.listPerson;
	    						if(!treeNode.parentFlag){//最下面机构
	    							treeObj.removeChildNodes(treeNode);
		    						treeObj.addNodes(treeNode, addNodes);
	    						}
	    						
	    					}
	    					if (data.flag == 0) {
	    						alert("error");
	    					}
	    				},
	    				error : function() {
	    				}
	    			});
			    }
			};
			var zNodes;
    		var setting = {
    				data: {
    					simpleData: {
    						enable: true,
    						idKey:"orgId",
    						pIdKey:"parentId"
    					}
    				},
    				check: {
    					enable: true,
    					chkStyle: "checkbox",
    					chkboxType :{ "Y": "", "N": "" }//设置不关联勾选
    					//autoCheckTrigger: true//设置自动关联勾选时是否触发 beforeCheck / onCheck 事件回调函数
    				},
    				callback: {
    					onCheck: zTreeOnCheck,
    					onClick: zTreeOnClick,
    					beforeCheck: zTreeBeforeCheck
    				}
    			};

    			
			//--------------------------------tree-----------------------------------------------
			//-------------------搜索人员------------------
			/**抱歉，用户导入失败！
您导入的用户邮箱不能为空。
或   您导入的用户手机号不能为空。
或   用户名包含字母、数字、 _、.和@，并且开始和结束只能是字母或数字*/
			$("#searcdEmm").click(function(){
				var keywords = $.trim($(":input[name='keywords']").val());
				$("#treeDemo").hide();
				$("#listPerson").show();
				//$(":hidden[name='formKeyWords']").val(keywords);
				//$("#formSearchPerson").submit();
				$.ajax({
    				type : 'post',
    				url : '<c:url value="/user/searchEmmPerson"/>',
    				dataType : "json",
    				data:"formKeyWords="+keywords,
    				async:false,
    				success : function(data) {
    					if (data.flag == 1) {
    						$(".cleartr").remove();
    						for(var i=0;i<data.listPerson.length;i++){
    							var personId =data.listPerson[i].id;
								var uniqueField =data.listPerson[i].uniqueField;
								var email =data.listPerson[i].email;
								var mobileNo =data.listPerson[i].mobileNo;
								var rowStr = "<tr class='cleartr'><td><input type='checkbox' value='"+personId+"' class='personId' uniqueField='"+uniqueField+"'  email='"+email+"' mobileNo='"+mobileNo+"'  /></td><td>"+uniqueField+"</td><td>"+email+"</td><td>"+mobileNo+"</td></tr>";
								$("#emmSearchResult").append(rowStr);
								$(".personId").bind("click",validCheck);//绑定时间,当选中的时候,判断是否符合条件
    						}
    						//alert("导入成功！");
    					}
    					if (data.flag == 0) {
    						alert("导入失败！");
    					}
    				},
    				error : function() {
    				}
    			});
				
			});
			$("#showOrg").click(function(){
				$("#treeDemo").show();
				$("#listPerson").hide();
			});
			
			//-------------------搜索人员------------------
			
			//提交从EMM选取过来的人
			$(".submitEmmUser").click(function(){
				var searchFlag = $("#listPerson").css("display");
				if("none"==searchFlag){//组织机构选中的结果
					var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
					var nodes = treeObj.getCheckedNodes(true);
					var personIds = "";
					for(var i=0;i<nodes.length;i++){
						personIds+=nodes[i].id+",";
					}
					if(personIds.length>0){
						personIds=personIds.substring(0,personIds.length-1);
					}
					$.ajax({
	    				type : 'post',
	    				url : '<c:url value="/user/postUser"/>',
	    				dataType : "json",
	    				data:"personIds="+personIds,
	    				async:false,
	    				success : function(data) {
	    					if (data.flag == 1) {
	    						var emailLength = data.emailList.length;
	    						var emailStr = "";
	    						for(var i=0;i<emailLength;i++){
	    							emailStr+=data.emailList[i]+",";
	    						}
	    						alert(emailLength+"人被添加成功."+emailStr);
	    					}
	    					if (data.flag == 0) {
	    						alert("error");
	    					}
	    				},
	    				error : function() {
	    				}
	    			});
				}else if("block"==searchFlag){//查询出来的结果
					var personIds = "";
					var nodes = $(".personId:checked").each(function(){
						personIds+=$(this).val()+",";
					});
					if(personIds.length>0){
						personIds=personIds.substring(0,personIds.length-1);
					}
					$.ajax({
	    				type : 'post',
	    				url : '<c:url value="/user/postUser"/>',
	    				dataType : "json",
	    				data:"personIds="+personIds,
	    				async:false,
	    				success : function(data) {
	    					if (data.flag == 1) {
	    						var emailLength = data.emailList.length;
	    						var emailStr = "";
	    						for(var i=0;i<emailLength;i++){
	    							emailStr+=data.emailList[i]+",";
	    						}
	    						alert(emailLength+"人被添加成功."+emailStr);
	    					}
	    					if (data.flag == 0) {
	    						alert("error");
	    					}
	    				},
	    				error : function() {
	    				}
	    			});
				}
				
				
			});
			//重置密码,没有开通接收邮件服务的用户
			$(".saveNewPwd").click(function(){
				if($("#UPDATEPWD").val()==""){
					alert('密码不可以为空');
					return false;
				}
				if($("#UPDATEPWD").val()!=$("#UPDATEPWDOK").val()){
					alert('两次密码不一致');
					return false;
				}
				$.ajax({
					type : 'post', 
					url : '<c:url value="/user/resetPwd"/>', //
					data : "id="+$(":hidden[name='userId']").val()+"&pwd="+$("#UPDATEPWD").val(), // 表单数据
					dataType : 'json',
					success : function(msg) {
						if (msg.status == 'success') { // msg 是后台调用action时，你穿过来的参数
							alert("操作成功");
							$("#UPDATEPWD").val("");
							$("#UPDATEPWDOK").val("");
							window.location.reload();
						} else {
							alert("操作失败");
							$("#UPDATEPWD").val("");
							$("#UPDATEPWDOK").val("");
							
						}
					}
				});
				
			});
			//下载导入用户模板
			$(".downModle").click(function(){
				$("#downExcelModule").attr("src",$("#downExcelModule").attr("uri"));
			});
			
			$(".submitExcelUser").click(function(){
				$("#importUserFromExcel").ajaxSubmit({
					dataType:"json",
					success:function(data){
						if(data.flag==1){
							alert("导入成功！");
							window.location.reload();
						}else if (data.flag==2){
							alert(data.msg);
							window.location.reload();
						}else{
							alert("导入失败！");
						}
					},
					error:function(data){
						
					}
				});
			});
			$("#crtNewUser").click(function(){
				$("span[name='titleFlag']").text("创建新用户");
				$(".crtPwdTr").show();//创建用户时候显示密码及确认密码
				$(":input[name='account']").val("").removeAttr("readonly");
				$(":input[name='email']").val("").removeAttr("readonly");
				$(":input[name='nickName']").val("").removeAttr("readonly");
				$(":input[name='cellphone']").val("");
				$(":input[name='qq']").val("");
				$(":input[name='address']").val("");
				$(":input[name='remark']").val("");
				$(":input[name='userName']").val("");
				$(":hidden[name='id']").val("");
				
				$(":hidden[name='joinPlat']").val("INNER");
				$("#joinPlatStr").html('公司内部');
			});
    	});
    	//启用,停用
    	function statusChange(id,operation){
    		if('disable'==operation){
    			if(!confirm('确定要停用此用户?\u000d停用后此用户无法再登录至协同开发.')){
    				return false;
    			}
    		}
    		if('enable'==operation){
    			if(!confirm("确定要启用此用户?\u000d启用后此用户有权登录至协同开发.")){
    				return false;
    			}
    		}
    		var ajax_url = '<c:url value="/user/statusChange"/>' ;// url地址
			$.ajax({
				type : 'post', 
				url : ajax_url, //
				data : "id="+id+"&operate="+operation, // 表单数据
				dataType : 'json',
				success : function(msg) {
					if (msg.status == 'success') { // msg 是后台调用action时，你穿过来的参数
						alert("操作成功");
						window.location.reload();
					} else {
						alert("操作失败");
					}
				}
			});
		}
    	
    	//重置密码,开通接收邮件的情况
    	function resetPwd(userId,email){
    		if(null==email || ""==email){
    			alert("此用户邮箱为空,无法发送邮件");
    			return false;
    		}
    		if(!confirm("确定重置密码?\u000d系统将默认分配密码发送邮件给此用户.")){
				return false;
			}
    		$.ajax({
				type : 'post', 
				url : '<c:url value="/user/resetPwd"/>', //
				data : "id="+userId, // 表单数据
				dataType : 'json',
				success : function(msg) {
					if (msg.status == 'success') { // msg 是后台调用action时，你穿过来的参数
						alert("操作成功");
						window.location.reload();
					} else {
						alert("操作失败");
					}
				}
			});
    	}
    	//重置密码,没有开通邮件服务,设置被操作用户的id
    	function setUserId(userId){
			$(":hidden[name='userId']").val(userId);
		}
    	
    	//从EMM中搜索列表中筛选出人之后,选择的时候,如果此用户的一些信息不完整,则不让选中
    	function validCheck(){
    		if($(this).get(0).checked){//将要选中
    			var email = $(this).attr("email");
        		var mobileNo = $(this).attr("mobileNo");
        		var uniqueField = $(this).attr("uniqueField");
        		if(null==email||""==email){
    	    		alert("没有邮箱,无法添加");
    	    		$(this).get(0).checked=false;
    	    		return false;
    	    	}
    	    	/* if(null==mobileNo||""==mobileNo){
    	    		alert("没有手机号,无法添加");
    	    		$(this).get(0).checked=false;
    	    		return false;
    	    	} */
    	    	//校验用户名
    	    	var patrn=/^[a-zA-Z0-9]{1}([a-zA-Z0-9]|[._@])*[a-zA-Z0-9]$/; 
    	    	if (!patrn.exec(uniqueField)){
    	    		alert("用户名包含字母、数字、 _、.和@，并且开始和结束只能是字母或数字");
    	    		$(this).get(0).checked=false;
    	    		return false 
    	    	}
    		}
    	}
    	//删除记录
    	function delRecord(){
    		var userIds = "";
    		var selectedCount =0;
    		$(".listCheck:checked").each(function(){
    			userIds +=$(this).val()+",";
    			selectedCount++;
    		});
    		
    		if(userIds.length>0){
    			userIds=userIds.substring(0,userIds.length-1);
			}else{
				alert("请选择要删除的人员");
				return false;
			}
    		if(confirm("确定要删除用户?\u000d删除后用户无法再找回.")){
    			$.ajax({
    				type : 'post',
    				url : '<c:url value="/user/delUsers"/>',
    				dataType : "json",
    				data:"userIds="+userIds,
    				async:false,
    				success : function(data) {
    					if (data.flag == 1) {
    						var count = data.count;
    						if(selectedCount==1 && count==0){//只选择了一个用户,并且此用户不能被删除
    							alert("此用户在协同开发上还有相关联的内容,无法删除!");
    							return false;
    						}else if(count==0){//选择了多个用户,这些用户都不能被删除
    							alert("这些用户在协同开发上还有相关联的内容,无法删除!");
    							return false;
    						}
    						//有用户被删除的情况
    						alert(count+"人被删除.");
    						if(selectedCount>count){
    							alert("其余"+(selectedCount-count)+"名用户在协同开发上还有相关联的内容,无法删除!");
    						}
    						window.location.reload();
    					}
    					if (data.flag == 0) {
    						alert(data.error);
    					}
    				},
    				error : function() {
    				}
    			});
    		}
    		
    		
    	}
    	
    	//全选/全不选
    	function switchMainCheck(obj){
    		$(".listCheck").each(function(){
    			$(this).get(0).checked=obj.checked;
    		});
    	}
    	//查询用户所属单位
    	function loadAdminFiliale() {
    		$.ajax({
    			url : basePath +'filiale/findAll',
    			dataType : "json",
    			type : "get",
    			success : function(data) {
    				var temp = $("#filialeId");
    				temp.html('');
    				//temp.append("<option value='' selected>请选择</option>");
    				$(data.message.filialeInfo).each(
    						function(index, row) {
    							temp.append("<option value='"+row.id+"'>"
    									+ row.filialeName + "</option>");
    						});
    			},
    			error : function(xhr) {
    				$.messager.alert("提示", '服务器处理异常，请重新提交！', 'info');
    				return false;
    			}
    		});
    		if(status == ''){
    			$("#userStatus").val('');
    		}else{
    			$("#userStatus").val(status);
    		}
    	}
    	//查询用户初始化权限
    	function loadInitPermission() {
    		$.ajax({
    			url : basePath +'user/findUserInitPermision',
    			dataType : "json",
    			type : "get",
    			success : function(data) {
    				var temp = $("#filialeId");
    				temp.html('');
    				//temp.append("<option value='' selected>请选择</option>");
    				$(data.message.filialeInfo).each(
    						function(index, row) {
    							temp.append("<option value='"+row.id+"'>"
    									+ row.filialeName + "</option>");
    						});
    			},
    			error : function(xhr) {
    				$.messager.alert("提示", '服务器处理异常，请重新提交！', 'info');
    				return false;
    			}
    		});
    	}
    </script>
  </body>
</html>