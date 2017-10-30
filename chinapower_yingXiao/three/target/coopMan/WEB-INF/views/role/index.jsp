<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"  %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
  <style type="text/css">
  	.green {background: #3da1a7;color:white;border-color: rgb(61, 161, 167) !important;}
  </style>
  <base href="<%=basePath%>">
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
  	<%@include file="../head.jsp" %>
    <div class="main">
    <%@include file="../left.jsp" %>
      <div class="right">
        <div class="location">
          <a href="javascript:;">用户管理</a>　/　<a href="javascript:;" class="active">权限设置</a>
        </div>
      	<div class="content">
          <div class="right-content">
            <div class="right-header">
              <button type="button" class="btn btn-primary customRole"  data-toggle="modal" data-target="#customRole">
			 	添加自定义角色
				</button>
            </div> 
            <table class="table table-striped table-bordered tables">
              <thead>
                <tr>
                    <th>角色</th>
                    <th>功能权限</th>
                    <th>操作</th>
                </tr>
              </thead>
              <tbody>
              	<c:forEach var="item" items="${list}" varStatus="wrap">
	              	<tr>
	                  <td><c:out value="${item.cnName }"/></td>
	                  <td>
	                  	<!--  
	                  	<c:forEach var="permissionType" items="${item.permissionTypes}"  varStatus="status">
	                  			<c:forEach var="permission" items="${permissionType.permission}"  varStatus="permissionstatus">
	                  				<c:if test="${permission.selected==1 }">
		                  				<c:out value="${permission.cnName }"/>,
	                  				</c:if>
	                  			</c:forEach>
	                  	</c:forEach>
	                  	-->
	                  	<c:forEach var="permissions" items="${item.permissions}"  varStatus="status" end="6">
	                  		<c:if test="${status.count<7 }">
		                  		<c:out value="${permissions.cnName }"></c:out>
		                  		<c:if test="${fn:length(item.permissions)>status.count }">
		                  			,
		                  		</c:if>
	                  		</c:if>
	                  		
	                  	</c:forEach>
	                  	<c:if test="${fn:length(item.permissions)>6}">
	                  			...
	                  	</c:if>
	                  	
	                  </td>
	                  <td>
	                  	   <!-- Button trigger modal -->
							<a href="javascript:;" class="editRole" roleId="<c:out value='${item.id }'/>" data-toggle="modal" data-target="#myModal">
							  编辑
							</a>
							<c:if test="${item.allowdel eq 'PERMIT'}">
								<a href="javascript:void(0);" class="delRole" roleId="<c:out value='${item.id }'/>">删除</a>
							</c:if>
	                  </td>
	                </tr>
              	</c:forEach>
                
              </tbody>
            </table>
          </div>
      	</div>
      	
      	
        <div class="clear"></div>
      </div>
    </div>
<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">编辑权限</h4>
      </div>
      <div class="modal-body editRolePermission">
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary saveChange">保存</button>
      </div>
    </div>
  </div>
</div>
<!-- custom role -->
<div class="modal fade" id="customRole" tabindex="-1" role="dialog" aria-labelledby="customRole">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="customRole">添加自定义角色</h4>
      </div>
      <div class="modal-body ">
        	<div style="margin-bottom:10px;"><font color="red">*</font>角色中文名称: <input type="text" id="roleCnName"/> </div>
        	<div style="margin-bottom:10px;"><font color="red">*</font>角色英文名称: <input type="text" id="roleEnName"/> </div>
        	<div class="customRoleContent"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary saveCustomRole">保存</button>
      </div>
    </div>
  </div>
</div>
<input type="hidden" id="roleId"/>
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/bootstrap.min.js"></script>
    <script src="static/js/init.js"></script>
    <script type="text/javascript">
    	$(function(){
    		$('.menuNav li').removeClass('active');
    		$('.menuNav li').eq(1).find('.nav-pills').show();
    		$('.menuNav li').eq(1).css('background-color','#383f4e');
    		//修改权限提交按钮
    	    $(".saveChange").click(function(){
    	    	var permissionIds = "";
    	    	$("span",".editRolePermission").each(function(){
    	    		if($(this).attr("isSelect")=="yes"){
    	    			permissionIds+=","+$(this).attr("permissionId");
    	    		}
    	    	});
    	    	permissionIds = permissionIds.substring(1);
    	    	var params = "roleId="+$("#roleId").val()+"&permissionIds="+permissionIds;
    	    	$.ajax({
    				type : 'post',
    				url : '<c:url value="/role/updateRole"/>',
    				data:params,
    				dataType : "json",
    				success : function(data) {
    					if (data.flag == 1) {
    						alert("成功");
    						window.location.reload();
    					}
    					if (data.flag == 0) {
    						alert("失败");
    					}
    				},
    				error : function() {
    				}
    			});
    	    	
    		});
        	//删除自定义角色
        	$(".delRole").click(function(){
        		
        		if (!confirm("你确定删除吗？")) { 
                    return;
                }
                else {          		
	        		var roleId = $(this).attr("roleId");
	        		$.ajax({
	    				type : 'post',
	    				url : '<c:url value="/role/delCustomRole/"/>'+roleId,
	    				dataType : "json",
	    				success : function(data) {
	    					if (data.flag == 1) {
	    						alert("成功");
	    						window.location.reload();
	    					}
	    					if (data.flag == 0) {
	    						alert("失败");
	    					}
	    				},
	    				error : function() {
	    				}
	    			});
                }
        	});
        	
        	//编辑角色
    		$(".editRole").click(function(){
        		var roleId = $(this).attr("roleId");
        		$("#roleId").val(roleId);
        		$.ajax({
    				type : 'get',
    				url : '<c:url value="/role/detailInfo/"/>'+roleId,
    				dataType : "json",
    				success : function(data) {
    					if (data.flag == 1) {
    						var bodyContent = "";
    						for(var i=0;i<data.role.permissionTypes.length;i++){
    							bodyContent+="<b>"+(i+1)+"、"+data.role.permissionTypes[i].cnName+"</b></br>";
    							for(var j=0;j<data.role.permissionTypes[i].permission.length;j++){
    								if(data.role.permissionTypes[i].permission[j].selected=="1"){
    									bodyContent+="<span class='green noselect' isSelect='yes' permissionId="+data.role.permissionTypes[i].permission[j].id+">"+data.role.permissionTypes[i].permission[j].cnName+"</span>";
    								}else if(data.role.permissionTypes[i].permission[j].selected=="0"){
    									bodyContent+="<span class='noselect'  isSelect='no' permissionId="+data.role.permissionTypes[i].permission[j].id+">"+data.role.permissionTypes[i].permission[j].cnName+"</span>";	
    								}
    							}
    							bodyContent+="</br>"
    						}
    						$(".editRolePermission").html(bodyContent);
    						$("span",".editRolePermission").bind("click",function(){
    							if($(this).attr("isSelect")=="yes"){
    								$(this).attr("isSelect","no")
    								$(this).removeClass("green");
    							}else{
    								$(this).attr("isSelect","yes")
    								$(this).addClass("green");
    							}
    						});
    					}
    					if (data.flag == 0) {
    						alert("失败");
    					}
    				},
    				error : function() {
    				}
    			});
        	});
    		//添加自定义角色
    		$(".customRole").click(function(){
    			
    			$.ajax({
    				type : 'get',
    				url : '<c:url value="/role/permissionTypes"/>',
    				dataType : "json",
    				success : function(data) {
    					if (data.flag == 1) {
    						var bodyContent = "";
    						for(var i=0;i<data.listType.length;i++){
    							bodyContent+=(i+1)+"、"+data.listType[i].cnName+"</br>";
    							for(var j=0;j<data.listType[i].permission.length;j++){
   									bodyContent+="<span class='noselect' isSelect='no' permissionId="+data.listType[i].permission[j].id+">"+data.listType[i].permission[j].cnName+"</span>";	
   									//bodyContent+="<span class='noselect'  isSelect='no' permissionId="+data.listType[i].permission[j].id+">"+data.listType[i].permission[j].cnName+"</span>";	
    							}
    							bodyContent+="</br>"
    						}
    						$(".customRoleContent").html(bodyContent);
    						$("span",".customRoleContent").bind("click",function(){
    							if($(this).attr("isSelect")=="yes"){
    								$(this).attr("isSelect","no")
    								$(this).removeClass("green");
    							}else{
    								$(this).attr("isSelect","yes")
    								$(this).addClass("green");
    							}
    						});
    					}
    					if (data.flag == 0) {
    						alert("失败");
    					}
    				},
    				error : function() {
    				}
    			});
    		});
    		
    		//添加自定义角色保存按钮
    		$(".saveCustomRole").click(function(){
    	    	var permissionIds = "";
    	    	$("span",".customRoleContent").each(function(){
    	    		if($(this).attr("isSelect")=="yes"){
    	    			permissionIds+=","+$(this).attr("permissionId");
    	    		}
    	    	});
    	    	permissionIds = permissionIds.substring(1);
    	    	var params = "enName="+$("#roleEnName").val()+"&cnName="+$("#roleCnName").val()+"&permissionIds="+permissionIds;
    	    	$.ajax({
    				type : 'post',
    				url : '<c:url value="/role/custom"/>',
    				data:params,
    				dataType : "json",
    				success : function(data) {
    					if (data.flag == 1) {
    						alert("成功");
    						window.location.reload();
    					}
    					if (data.flag == 0) {
    						alert("失败");
    					}
    				},
    				error : function() {
    				}
    			});
    	    	
    		});
    	});
    </script>
  </body>
</html>