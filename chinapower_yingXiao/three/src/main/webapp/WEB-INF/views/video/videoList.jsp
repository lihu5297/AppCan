<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><%=Cache.getSetting("SETTING").getPlatName() %> - 视频管理</title>
	<link rel="shortcut icon" href="static/images/favicon.ico">
	<link href="static/css/bootstrap.min.css" rel="stylesheet">
	<link href='static/css/style.css' rel='stylesheet'>
	<link rel="stylesheet" href="static/css/zTreeStyle/zTreeStyle.css" type="text/css" />
	<!--[if lt IE 9]>
	      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	    <![endif]-->
	<style>
		.right .tables .select-plu{
			width: 202px;
			text-align: left;
		}
		.select-plu input{
			float: left;
			margin-left: 5px;
			margin-right: 10px;
		}
		#ios tbody tr td{
			vertical-align: middle;
		}
		#android tbody tr td{
			vertical-align: middle;
		}
		#add_engine_tip{
			vertical-align: middle;
			width: 110px;
			text-align: right;
			height: 30px;
			border-top:0 none;
			padding-top: 15px;
    		padding-right: 10px;
		}
		.add_engine_txt{
			width: 300px;
			height: 30px;
		}
		.add_engine_file{
		    width: 300px;
		    position: absolute;
		    top: 8px;
		    left: 8px;
		    height: 30px;
		    opacity: 0;
		    filter: alpha(opacity=0);
		}
		.typeColor {
		    color: #EE7600 !important;
		}
	</style>
</head>
<body>
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:;">管理</a> / <a href="javascript:;" class="active">${title}</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createVideo" onclick="updateType()">新增视频</button>
						<div id="videoSortList">
						<button  type="button" class="btn btn-primary" data-toggle="modal" data-target="#videoSortJunior">视频排序</button>
						<button  type="button" class="btn btn-primary" data-toggle="modal" data-target="#videoSortMiddle">视频排序</button>
						<button  type="button" class="btn btn-primary" data-toggle="modal" data-target="#videoSortSenior">视频排序</button>
					    </div>
					</div>
					<ul class="nav nav-tabs" id="myTab">
						<li><a  id="cjunior" href="#junior">初级</a></li>
						<li><a  id="cmiddle" href="#middle">中级</a></li>
						<li><a  id="csenior" href="#senior">高级</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane" id="junior">
							<br/>
							<!-- 初级列表 -->
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
									    <th class="select-plu" style="width:5%;"><input type="checkbox" click="selectjunior" name="ios">全选</th>
										<th style="width:10%;">视频标题</th>
										<th style="width:10%;">视频描述</th>
										<th style="width:10%;">视频URL</th>
										<th style="width:10%;">发布状态</th>
										<th style="width:10%;">推荐状态</th>
										<th style="width:5%;">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${videoJuniorList}">
										<tr>
										    <td>
											<input type="checkbox" value="${item.id}">
											<td><c:out value="${item.title}" /></td>
											<td><c:out value="${item.description}" /></td>
											<td><c:out value="${item.downloadUrl}" /></td>
											<td>
												<c:if test="${item.status=='NOPUBLISH'}">未发布</c:if>
												<c:if test="${item.status=='PUBLISH'}">已发布</c:if>
											</td>
											<td>
											<c:if test="${item.tuijian=='NOTUIJIAN'}">未推荐</c:if>
											<c:if test="${item.tuijian=='TUIJIAN'}">推荐</c:if>
											</td>
											<td>
												<a href="javascript:void(0);" data-toggle="modal"
										data-target="#editVideo" onclick="videoDetail(${item.id})">编辑</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div class="page">
								<button type="button" data-toggle="modal"
									data-target="#warning" style="float: left" onclick="getModel()"
									class="btn btn-primary">删除</button>
								<span class="total">共<span>${juniorTotal}</span>条
								</span>
								
							</div>
						</div>
						<div class="tab-pane" id="middle">
							<br/>
							<!-- 中级列表 -->
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
									    <th class="select-plu" style="width:5%;"><input type="checkbox" click="selectmiddle" name="ios">全选</th>
										<th style="width:10%;">视频标题</th>
										<th style="width:10%;">视频描述</th>
										<th style="width:10%;">视频URL</th>
										<th style="width:10%;">发布状态</th>
										<th style="width:10%;">推荐状态</th>
										<th style="width:5%;">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${videoMiddleList}">
										<tr>
										    <td>
											<input type="checkbox" value="${item.id}">
											<td><c:out value="${item.title}" /></td>
											<td><c:out value="${item.description}" /></td>
											<td><c:out value="${item.downloadUrl}" /></td>
											<td>
												<c:if test="${item.status=='NOPUBLISH'}">未发布</c:if>
												<c:if test="${item.status=='PUBLISH'}">已发布</c:if>
											</td>
											<td>
											<c:if test="${item.tuijian=='NOTUIJIAN'}">未推荐</c:if>
											<c:if test="${item.tuijian=='TUIJIAN'}">推荐</c:if>
											</td>
											<td>
												<a href="javascript:void(0);" data-toggle="modal"
										data-target="#editVideo" onclick="videoDetail(${item.id})">编辑</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div class="page">
								<button type="button" data-toggle="modal"
									data-target="#warning" style="float: left" onclick="getModel('delete',0)"
									class="btn btn-primary">删除</button>
								<span class="total">共<span>${middleTotal }</span>条
								</span>
								
							</div>
						</div>
						<div class="tab-pane" id="senior">
							<br/>
							<!-- 高级列表 -->
							<table class="table table-striped table-bordered tables">
								<thead>
									<tr>
									    <th class="select-plu" style="width:5%;"><input type="checkbox" click="selectsenior" name="ios">全选</th>
										<th style="width:10%;">视频标题</th>
										<th style="width:10%;">视频描述</th>
										<th style="width:10%;">视频URL</th>
										<th style="width:10%;">发布状态</th>
										<th style="width:10%;">推荐状态</th>
										<th style="width:5%;">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${videoSeniorList}">
										<tr>
										    <td>
											<input type="checkbox" value="${item.id}">
											<td><c:out value="${item.title}" /></td>
											<td><c:out value="${item.description}" /></td>
											<td><c:out value="${item.downloadUrl}" /></td>
											<td>
												<c:if test="${item.status=='NOPUBLISH'}">未发布</c:if>
												<c:if test="${item.status=='PUBLISH'}">已发布</c:if>
											</td>
											<td>
											<c:if test="${item.tuijian=='NOTUIJIAN'}">未推荐</c:if>
											<c:if test="${item.tuijian=='TUIJIAN'}">推荐</c:if>
											</td>
											<td>
												<a href="javascript:void(0);" data-toggle="modal"
										data-target="#editVideo" onclick="videoDetail(${item.id})">编辑</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div class="page">
								<button type="button" data-toggle="modal"
									data-target="#warning" style="float: left" onclick="getModel('delete',0)"
									class="btn btn-primary">删除</button>
								<span class="total">共<span>${seniorTotal }</span>条
								</span>
								
							</div>
						</div>
					</div>
				</div><!-- End of right-content -->
			</div><!-- End of content -->
			<div class="clear"></div>
		</div><!-- End of right -->
	</div><!-- End of main -->
	<!-- 排序模态框 -->
    <div class="modal fade" id="videoSortSenior">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">视频排序</h4>
	      </div>
	      <div class="modal-body">
	        <table style="width:300px">
	            <c:forEach var="item" items="${videoSeniorList}">
					<tr>
					<td style="width:150px;color:#FF8000">
					<span style="display:none">${item.id}#${item.sort}</span>
					${item.title}
					</td>
					<td><input type="button" name="upMove" value="上移"/></td>
					<td><input type="button" name="downMove" value="下移"/></td>
					</tr>
				</c:forEach>
			</table>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	        <button type="button" onclick="updateSort('senior')" class="btn btn-primary">排序</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div>
	<!-- 排序模态框 -->
    <div class="modal fade" id="videoSortJunior">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">视频排序</h4>
	      </div>
	      <div class="modal-body">
	        <table style="width:300px">
	            <c:forEach var="item" items="${videoJuniorList}">
					<tr>
					<td style="width:150px;color:#FF8000">
					<span style="display:none">${item.id}#${item.sort}</span>
					${item.title}</td>
					<td><input type="button" name="upMove" value="上移"/></td>
					<td><input type="button" name="downMove" value="下移"/></td>
					</tr>
				</c:forEach>
			</table>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	        <button type="button" onclick="updateSort('junior')" class="btn btn-primary">排序</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div>
	<!-- 排序模态框 -->
    <div class="modal fade" id="videoSortMiddle">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">视频排序</h4>
	      </div>
	      <div class="modal-body">
	        <table style="width:300px">
	            <c:forEach var="item" items="${videoMiddleList}">
					<tr>
					<td style="width:150px;color:#FF8000">${item.title}
					<span style="display:none">${item.id}#${item.sort}</span>
					</td>
					<td><input type="button" name="upMove" value="上移"/></td>
					<td><input type="button" name="downMove" value="下移"/></td>
					</tr>
				</c:forEach>
			</table>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	        <button type="button" onclick="updateSort('middle')" class="btn btn-primary">排序</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div>
	<!-- Add Engine modal -->
	<div class="modal fade" id="createVideo" tabindex="-1" role="dialog" aria-labelledby="createVideo">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">新增视频</h4>
				</div>
				<div class="modal-body">
					<form action="video" method="post" id="addVideo" enctype="multipart/form-data" autocomplete="off">
						<table class="superadmintable">
							<tr>
								<td><span style="color:red">*</span>标题：</td>
								<td><input name="title" id="title" value="" /></td>
							</tr>
							<tr>
								<td><span style="color:red">*</span>描述：</td>
								<td><input name="description" id="description" value="" /></td>
							</tr>
							<tr>
								<td><span style="color:red">*</span>级别：</td>
								<td>
								<select name="type" id="type">
								<option value="JUNIOR" selected = "selected">初级</option> 
								<option value="MIDDLE">中级</option> 
								<option value="SENIOR">高级</option> 
								</select>
								</td>
							</tr>
							<tr>
							<td><span style="color:red">*</span>视频上传：</td>
							<td>
							<input type="file" name="videoZip" id="videoZip"/>
							<input type="text" style="display:none" name="downloadUrl" id="downloadUrl"/>
							</td>
							</tr>
							<tr>
							<td><span style="color:red"></span>发布：</td>
							<td>
							<select name="status" id="status">
								<option value="NOPUBLISH" selected = "selected">不发布</option> 
								<option value="PUBLISH">发布</option> 
								</select>
							</td>
							</tr>
							<tr>
							<td>推荐：</td>
							<td>
							<select name="tuijian" id="tuijian">
								<option value="NOTUIJIAN" selected = "selected">不推荐</option> 
								<option value="TUIJIAN">推荐</option> 
								</select>
							</td>
							</tr>
					    </table>
					</form>
				</div>
				<div class="modal-footer" style="border-top: 0 none;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="addVideo()">保存</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="editVideo" tabindex="-1" role="dialog" aria-labelledby="editVideo">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">编辑视频</h4>
				</div>
				<div class="modal-body">
					<table class="superadmintable">
							<tr>
								<td>标题：</td>
								<td><input name="etitle" id="etitle" value="" /></td>
							</tr>
							<tr>
								<td>描述：</td>
								<td><input name="edescription" id="edescription" value="" /></td>
							</tr>
							<tr>
								<td>级别：</td>
								<td>
								<select name="etype" id="etype">
								<option value="JUNIOR" selected = "selected">初级</option> 
								<option value="MIDDLE">中级</option> 
								<option value="SENIOR">高级</option> 
								</select>
								</td>
							</tr>
							<tr>
							<td>视频上传：</td>
							<td>
							<input type="file" name="videoZip" id="evideoZip"/>
							<input type="text" size="60" name="edownloadUrl" id="edownloadUrl"/>
							</td>
							</tr>
							<tr>
							<td><span style="color:red"></span>发布：</td>
							<td>
							<select name="estatus" id="estatus">
								<option value="NOPUBLISH" selected = "selected">不发布</option> 
								<option value="PUBLISH">发布</option> 
								</select>
							</td>
							</tr>
							<tr>
							<td>推荐：</td>
							<td>
							<select name="etuijian" id="etuijian">
								<option value="NOTUIJIAN" selected = "selected">不推荐</option> 
								<option value="TUIJIAN">推荐</option> 
								</select>
								<input type="hidden" name="sort" id="sort"/>
							</td>
							</tr>
					    </table>
				</div>
				<div class="modal-footer" style="border-top: 0 none;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="editVideo()">保存</button>
				</div>
			</div>
		</div>
	</div>
	<!-- warning -->
	<div class="modal fade" id="warning" tabindex="-1" role="dialog" aria-labelledby="createNew">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="warning_title">提示标题</h4>
				</div>
				<div class="modal-body">
					<span id="warning_title_span">提示内容</span>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" onclick="operation()">确定</button>
				</div>
			</div>
		</div>
	</div>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/jquery.form.js"></script>
	<script src="static/js/init.js"></script>
	<script>
		var type="";
		var quehuanT="";
		$(function() {
			$(".main .left .menuNav").children().eq(10).children().click();
			$("a[name='视频维护']").addClass("typeColor");
			// 标签页初始化显示(默认显示ios分组)
			//var link = $('#myTab a:first');
			//link.tab('show');
			type=getCookie("type")?getCookie("type"):"junior";
			$("#c"+type).tab('show');
			setCookie("quehuanT",type);
			videoSort(type);
			delCookie("type");
			$('#myTab a').click(function(e) {
				var qT=$(this).attr("id").substring(1);
				setCookie("quehuanT",qT);
				videoSort(qT);
				e.preventDefault();//阻止a链接的跳转行为 
				$(this).tab('show');
			});
		});
		function updateType(){
			$("#type").find("option").attr("selected",false);
			$("#type").find("option[value=" +getCookie("quehuanT").toUpperCase()+ "]").attr("selected", true);
		}
	    var thisId="";
		function videoDetail(id) {
			$.ajax({
				type : "GET",
				url :"video/" + id,
				data : "",
				dataType : "JSON",
				success : function(msg) {
					if (msg.status == "success") {
						thisId=id;
						data = msg.message;
						$("input[name='etitle']").val(data.title);
						$("input[name='edescription']").val(data.description);
						$("#etype").find("option[value=" + data.type+ "]").attr("selected", true);
						$("input[name='edownloadUrl']").val(data.downloadUrl);
						$("#estatus").find("option[value=" + data.status+ "]").attr("selected", true);
						$("#etuijian").find("option[value=" + data.tuijian+ "]").attr("selected", true);
						$(":hidden[name='sort']").val(data.sort);
					}
				}
			});
		}
		function addVideo(){
			openload();
			$('#addVideo').ajaxSubmit({
				success:function(data){
					if(data.status=="success"){
						closeload();
						setCookie("type",data.message.type.toLowerCase());
						window.location.reload();
					}else{
						alert(data.message);
					}
					
				},
				error:function(){
					closeload()
					alert("添加失败！");
				}
			});
		}
		function editVideo(){
			var params={};
			params.id=thisId;
			params.title=$("#etitle").val();
			params.description=$("#edescription").val();
			params.downloadUrl=$("#edownloadUrl").val();
			params.type=$("#etype").find("option:selected").val();
			params.status=$("#estatus").find("option:selected").val();
			params.tuijian=$("#etuijian").find("option:selected").val();
			params.sort=$("#sort").val();
			openload();
			$.ajax({
				type:"post",
				data:params,
				url:"video/edit",
				dataType : "JSON",
				success :function(data) {
					if(data.status=="success"){
						closeload();
						setCookie("type",params.type.toLowerCase());
						alert("更新成功");
						window.location.reload();
					}else{
						alert(data.message);
					}
				},
			});
		}
		$(function(){
			 jQuery(function(){
	                jQuery("#evideoZip").wrap('<form id="euploadZip" method="post" action="video/upload" enctype="multipart/form-data">');
	                jQuery("#evideoZip").change(function(){
	                    jQuery("#euploadZip").ajaxSubmit({
	                        dataType: "json",
	                        success: function(data){
	                           if(data.status="success"){
	                        	   $("#edownloadUrl").val(data.message.url);
	                           }else{
	                        	   alert(data.message);
	                           }
	                        },
	                        error: function(xhr){
	                            jQuery('#euploadZip').resetForm();
	                            return false;
	                        }
	                    });
	                });
	            });
			 jQuery(function(){
	                jQuery("#videoZip").wrap('<form id="uploadZip" method="post" action="video/upload" enctype="multipart/form-data">');
	                jQuery("#videoZip").change(function(){
	                    jQuery("#uploadZip").ajaxSubmit({
	                        dataType: "json",
	                        success: function(data){
	                           if(data.status="success"){
	                        	   $("#downloadUrl").val(data.message.url);
	                           }else{
	                        	   alert(data.message);
	                           }
	                        },
	                        error: function(xhr){
	                            jQuery('#uploadZip').resetForm();
	                            return false;
	                        }
	                    });
	                });
	            });	
		})
		function getModel(){
			$("#warning_title").text("删除视频提示");
			$("#warning_title_span").text("是否确认删除选择的视频？");
			
		}
		
		function operation(){
			var ajax_url ="";
			var ajax_data = {};
			var ids = "";
			$("tbody input[type='checkbox']").each(function() {
				if($(this).is(":checked")){
					ids+=$(this).val()+",";
				}
			});
			if(ids==""){
				alert("请选择要删除的数据");
				return;
			}
			ids=ids.substring(0,ids.length-1);
			ajax_url = "video/delete";
			ajax_data = {ids:ids};
			$.ajax({
				url:ajax_url,
				dataType:"json",
				type:"post",
				data:ajax_data,
				success:function(data){
					if(data.status=="success"){
						alert("删除成功！");
						window.location.reload();
					}else
						alert("删除失败！"+data.message);
				},
				error:function(data){
					alert(data);
				}
				
			});
		}
		
		$("input[click='selectjunior']").click(function() {
			$("#junior input[type='checkbox']").attr("checked",true);
		});
		$("input[click='selectmiddle']").click(function() {
			$("#middle input[type='checkbox']").attr("checked",true);
		});
		$("input[click='selectsenior']").click(function() {
			$("#senior input[type='checkbox']").attr("checked",true);
		});
		///设置cookie
		function setCookie(NameOfCookie, value, expiredays)
		{
		 //@参数:三个变量用来设置新的cookie:
		 //cookie的名称,存储的Cookie值,
		 // 以及Cookie过期的时间.
		 // 这几行是把天数转换为合法的日期
		 var ExpireDate = new Date ();
		 ExpireDate.setTime(ExpireDate.getTime() + (expiredays * 24 * 3600 * 1000));
		 // 下面这行是用来存储cookie的,只需简单的为"document.cookie"赋值即可.
		 // 注意日期通过toGMTstring()函数被转换成了GMT时间。
		 if(expiredays != 0){
		     document.cookie = NameOfCookie + "=" + escape(value) + ((expiredays == null) ? "" : "; expires=" + ExpireDate.toGMTString());
		 }else{
		    document.cookie = NameOfCookie + "=" + escape(value);
		 }
		 
		}
		//读取cookies 
		function getCookie(name) 
		{ 
		    var search = name + "=";
		    var returnvalue = "";
		    if(document.cookie.length>0){
		        offset = document.cookie.indexOf(search)
		        if (offset != -1) {
		            offset += search.length;
		            end = document.cookie.indexOf(";", offset);
		            if (end == -1)
		            end = document.cookie.length;
		            returnvalue=unescape(document.cookie.substring(offset, end));
		        }
		    }
		    return returnvalue;  
		} 
		//删除cookie
		function delCookie(name) 
		{ 
		    var exp = new Date(); 
		    exp.setTime(exp.getTime() - 1); 
		    var cval=getCookie(name); 
		    if(cval!=null) 
		        document.cookie= name + "="+cval+";expires="+exp.toGMTString(); 
		}
		//上移
		$("input[name='upMove']").bind("click",function(){
		var $this = $(this);
		var curTr = $this.parents("tr");
		var prevTr = $this.parents("tr").prev();
		if(prevTr.length == 0){
		alert("已经是第一行");
		return;
		}else{
		prevTr.before(curTr);
		sortNumber();//重新排序
		}
		});
		//下移
		$("input[name='downMove']").bind("click",function(){

		var $this = $(this);
		var curTr = $this.parents("tr");
		var nextTr = $this.parents("tr").next();
		if(nextTr.length == 0){
		alert("已经是最后一行");
		return;
		}else{
		nextTr.after(curTr);
		sortNumber();//重新排序
		}
		});
		function videoSort(qT){
			if(qT=='junior'){
				$("#videoSortList button").css("display","none");
				$("#videoSortList").find("button[data-target='#videoSortJunior']").css("display","block");
			}else if(qT=='middle'){
				$("#videoSortList button").css("display","none");
				$("#videoSortList").find("button[data-target='#videoSortMiddle']").css("display","block");
			}else{
				$("#videoSortList button").css("display","none");
				$("#videoSortList").find("button[data-target='#videoSortSenior']").css("display","block");
			}
		}
		function updateSort(type){
			var ids=[];
			var sorts=[];
			if(type=='junior'){
				$("#videoSortJunior table tr span").each(function(){
					idsAndSorts=$(this).text();
					ids.push(idsAndSorts.split("#")[0]);
					sorts.push(idsAndSorts.split("#")[0]);
				});
			}else if(type=='middle'){
				$("#videoSortMiddle table tr span").each(function(){
					idsAndSorts=$(this).text();
					ids.push(idsAndSorts.split("#")[0]);
					sorts.push(idsAndSorts.split("#")[0]);
				});
			}else{
				$("#videoSortSenior table tr span").each(function(){
					idsAndSorts=$(this).text();
					ids.push(idsAndSorts.split("#")[0]);
					sorts.push(idsAndSorts.split("#")[0]);
				});
			}
			var temp;
			for(var i=0;i<sorts.length;i++){
				for(var j=i+1;j<sorts.length;j++){
					if(sorts[i]<sorts[j]){
						temp=sorts[i];
						sorts[i]=sorts[j];
						sorts[j]=temp;
					}
				}
			}
			var params={"ids":ids.join(','),"sorts":sorts.join(',')};
			$.ajax({
				type:"post",
				data:params,
				url:"video/sort",
				dataType:"json",
				success:function(data){
					if(data.status="success"){
						alert("排序完成");
						window.location.reload();
					}else{
						alert(data.message);
					}
				},
				error:function(err){
					alert(err);
				}
			});
		}
	</script>
</body>
</html>