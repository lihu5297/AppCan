<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"  %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
  <base href="<%=basePath%>">
    <meta charset="utf-8">
    <title><%=Cache.getSetting("SETTING").getPlatName() %> - 待审核用户</title>
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
        #selectAll{
            float: left;
            margin-left: 10px;
            margin-right: 10px;
        }
        .right .tables .select-all{
            width: 170px;
            text-align: left;
        }
    </style>
  </head>
  <body>
  <input type="hidden" value="<%=basePath%>" id="basePath">
  	<%@include file="../head.jsp" %>
    <div class="main">
    <%@include file="../left.jsp" %>
      <div class="right">
        <div class="location">
          <a href="javascript:;">用户管理</a>　/　<a href="javascript:;" class="active">待审核用户</a>
        </div>
        <form action="user/list" id="userForm" method="post">
        <input type="hidden" name="status" value="AUTHSTR"/>
        <div class="content">
          <div class="right-content">
            <div class="right-header">
              <div class="input-group btn-niu">
                <input type="text" name="search" value="${search}" class="form-control" placeholder="昵称/账号">
                <span class="input-group-btn">
                  <button class="btn btn-default" type="button"  onclick="submitForm()">查询</button>
                </span>
              </div>
            </div> 
            <table class="table table-striped table-bordered tables">
              <thead>
                <tr>
                    <th class="select-all" style="width:8%"><input type="checkbox" id="selectAll">全选</th>
                    <th>用户名</th>
                    <th>邮箱</th>
                    <th>接入平台</th>
                    <th>手机号</th>
                    <th>QQ</th>
                    <th>操作</th>
                </tr>
              </thead>
              <tbody>
              	<c:forEach var="item" items="${list.content}">
	              	<tr>
	                  <td><input type="checkbox" class="listCheck" value="${item.id}"></td>
	                  <td><c:out value="${item.account }"/></td>
	                  <td><c:out value="${item.email }"/></td>
	                  <td><c:out value="${item.joinPlat }"/> </td>
	                  <td><c:out value="${item.cellphone }"/></td>
	                  <td><c:out value="${item.qq }"/></td>
	                  <td>
	                     <a href="javascript:void(0);return false;" class="editUser" userId="${item.id }"  data-toggle="modal" data-target="#createNew">用户信息</a>
	                 	 <a href="javascript:;" id='passId' userId="${item.id }" data-toggle="modal" onclick="userId(${item.id })" data-target="#ensureCheckPass">通过</a>
	                 	 <a href="javascript:;" id='refuseId' userId="${item.id }" data-toggle="modal" onclick="userId(${item.id })" data-target="#ensureCheckRefuse">拒绝</a>
                     </td>
	                </tr>
              	</c:forEach>
                
              </tbody>
            </table>
          </div>
          <div class="page">
          <a href="javascript:void(0);" class="delRecord" onclick="delRecord()">删除 </a>
          <a href="javascript:void(0);" class="batchPass" onclick="batchOperate('pass')">批量通过 </a>
          <a href="javascript:void(0);" class="batchRefuse" onclick="batchOperate('refuse')">批量拒绝</a>
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
                    |<
                  </a>
                </li>
                <li>
                  <a href="javascript:void(0);" class="Previous">
                    <
                  </a>
                </li>
                <li>
                  第
                  <input type="text" value="${list.number+1}">
                  页 共${list.totalPages}页
                </li>
                <li>
                  <a href="javascript:void(0);" class="Next">
                  >
                  </a>
                </li>
                <li>
                  <a href="javascript:void(0);" class="last">
                    |>
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
        
    <!-- User Modal -->
<div class="modal fade" id="createNew" tabindex="-1" role="dialog" aria-labelledby="createNew">
  <div class="modal-dialog" role="document" style="width:500px">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">待审核用户信息</h4>
      </div>
      <div class="modal-body">
      	<form action="user/create" method="post" id="newUserForm">
        <table class="table">
        	<tr>
        		<td style="width: 72px;">用户名</td>
        		<td>
        			<span name="account"></span>
        		</td>
        	</tr>
        	<tr>
        		<td >邮箱</td>
        		<td>
        			<span name="email"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>级别</td>
        		<td>
        			<span name="userlevel"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>姓名</td>
        		<td>
        			<span name="userName"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>性别</td>
        		<td>
        			<span name="gender"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>手机号</td>
        		<td>
        			<span name="cellphone"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>QQ</td>
        		<td>
					<span name="qq"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>接入平台</td>
        		<td>
					<span name="joinPlat"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>所在地</td>
        		<td>
        			<span name="address"></span>
        		</td>
        	</tr>
        	<tr>
        		<td>备注<input type="hidden" name="id"/></td>
        		<td>
        			<span name="remark"></span>
        		</td>
        	</tr>
        </table>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>    

	<!-- #ensureCheckPass -->
	<div class="modal fade" id="ensureCheckPass" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">提示</h4>
				</div>
				<div class="modal-body">确定要通过此用户审核？<br>通过后此用户可登录至协同开发，默认角色为参与成员</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="save"
						id="saveButton" onclick="checkUser('pass','passId')">确定</button>
				</div>
			</div>
		</div>
	</div>
	<!-- # ensureCheckRefuse -->
	<div class="modal fade" id="ensureCheckRefuse" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myInfoModalLabel">提示</h4>
				</div>
				<div class="modal-body">确定要拒绝此用户审核？<br>拒绝后此用户不再显示在该列表内，无法再找回。</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" name="save"
						id="saveButton" onclick="checkUser('refuse','refuseId')">确定</button>
				</div>
			</div>
		</div>
	</div>
        
        
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/bootstrap.min.js"></script>
    <script src="static/js/init.js"></script>
    <script type="text/javascript" src="static/js/ztree/jquery.ztree.core-3.5.js"></script>
    <script type="text/javascript" src="static/js/ztree/jquery.ztree.excheck-3.5.min.js"></script>
    <script type="text/javascript">
		$('.menuNav li').removeClass('active');
		$('.menuNav li').eq(1).find('.nav-pills').show();
		$('.menuNav li').eq(1).css('background-color','#383f4e');
	    var userId;
	    function userId(id){
	    	userId = id;
	    }
	    function submitForm(){
	    	$(":input[name='pageNo']").val(1);
			$("#userForm").attr("action","user/list").submit();
		}
		$(".btn-niu input").keydown(function(e){
		    if(e.keyCode==13){
		    	submitForm();
		    }
	    })
	    //checkUser('${item.id}','pass')
	    function checkUser(operate,opId){
	    	id = userId;
	    	$.ajax({
	    		type:'get',
	    		url:'user/check?id='+id+"&operate="+operate,
	    		dataType:'json',
	    		success:function(data){
	    			if(data.status=='success'){
	    				alert("操作成功");
	    				location.reload();
	    			}
	    		},
	    		error:function(data){
	    			
	    		}
	    	});
	    }
		function batchOperate(type){
			var userIds = [];
    		$(".listCheck:checked").each(function(){
    			$.ajax({
    	    		type:'get',
    	    		async:false,
    	    		url:'user/check?id='+$(this).val()+"&operate="+type,
    	    		dataType:'json',
    	    		success:function(data){
    	    			if(data.status=='success'){
    	    				console.log("操作成功");
    	    			}
    	    		},
    	    		error:function(data){
    	    			
    	    		}
    	    	});
    		});
    		alert("操作成功")
			window.location.reload();
		}
		
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
    		
    		//编辑用户时候,查询用户信息
    		$(".editUser").click(function(){
    			var id = $(this).attr("userId");
    			$.ajax({
    				type : 'get',
    				url : '<c:url value="/user/check/"/>'+id,
    				dataType : "json",
    				success : function(data) {
    					if (data.flag == 1) {
    						$("span[name='account']").text(ifNull(data.user.account));
    						$("span[name='email']").text(ifNull(data.user.email));
    						$("span[name='cellphone']").text(ifNull(data.user.cellphone));
    						$("span[name='qq']").text(ifNull(data.user.qq));
    						$("span[name='address']").text(ifNull(data.user.address));
    						$("span[name='remark']").text(ifNull(data.user.remark));
    						$("span[name='userName']").text(ifNull(data.user.userName));
    						$("span[name='userlevel']").text(ifNull(data.user.userlevel));
    						$("span[name='joinPlat']").text(ifNull(data.user.joinPlat));
    						if(data.user.gender=='MALE'){
    							$("span[name='gender']").text("女");
    						}else if(data.user.gender=='FEMALE'){
    							$("span[name='gender']").text("男");
    						}
    						
    						$(":hidden[name='id']").text(ifNull(data.user.id));
    					}
    					if (data.flag == 0) {
    						alert("error");
    					}
    				},
    				error : function() {
    				}
    			});
    		});
    	});
    	
    	$("#selectAll").click(function() {
    		var checked = $(this).is(":checked");
    		if (checked == undefined || checked == true) {
    			$("tbody input[type='checkbox']").each(function() {
    				if(!$(this).is(":checked")){
    					$(this).click();
    				}
    				
    			})
    		} else {
    			$("tbody input[type='checkbox']").each(function() {
    				if($(this).is(":checked")){
    					$(this).click();
    				}
    			});
    		}
    	});
    	
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
    		if(confirm("确定要删除用户?删除后用户无法再找回.")){
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
    						alert("error");
    					}
    				},
    				error : function() {
    				}
    			});
    		}
    		
    		
    	}
    	
    </script>
  </body>
</html>