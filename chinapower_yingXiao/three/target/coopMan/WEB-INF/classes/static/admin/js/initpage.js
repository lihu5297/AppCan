var pageSize, pageNo, queryKey,totalPage;
var basePath = document.getElementById("basePath").value;
$(function($) {
	initpage();
	loadAdminFiliale();
	$("#pageSize" + pageSize).attr("selected", "selected");

});

// $(document).on('change','#pageSize',function(){
// pageSize = $(this).val();
// ajaxget();
// })

function initpage() {
	pageSize = document.getElementById("prePageSize").value;
	pageNo = $("#curPage").val();
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
	if (prepage != 0 && (Number(pageNo) + Number(prepage))<=totalPage && (Number(pageNo) + Number(prepage))!=0) {
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
	var href = "manager?" + "pageNo=" + pageNo + "&pageSize=" + pageSize;
	if (queryKey != "") {
		queryKey = encodeURIComponent(queryKey);
		queryKey = encodeURIComponent(queryKey);
		href += "&queryKey=" + queryKey;
	}

	window.location.href = href;
}

function managerInfo(id) {
	$.ajax({
		type : "GET",
		url : basePath + "manager/" + id,
		data : "",
		dataType : "JSON",
		success : function(msg) {
			if (msg.status == "success") {
				data = msg.message;
				$("img[name='iconInfo']").attr("src", data.icon);
				$("span[name='accountInfo']").text(data.account);
				$("span[name='emailInfo']").text(data.email);
				$("span[name='userNameInfo']").text(data.userName);
				$("span[name='cellphoneInfo']").text(data.cellphone);
				$("span[name='filialeName']").text(data.filialeName);
				$("span[name='manageModuleInfo']").text(
						getModuleName(data.manageModule));
				$("span[name='addressInfo']").text(data.address);
				$("span[name='remarksInfo']").text(data.remarks);

				initEdit(msg);
			}
		}

	})
}

function getModuleName(data) {
	var moduleNames = "";
	for (var i = 0; i < data.length; i++) {
		if (i == 0) {
			moduleNames = data[i].cnName;
		} else {
			moduleNames += "、" + data[i].cnName;
		}
	}
	return moduleNames;
}

function initEdit(msg) {
	data = msg.message;
	$("img[name='iconInfo']").attr("src", data.icon);
	$("#managerId").val(data.id);
	document.getElementById("iconfile").value = data.icon;
	$("#account").val(data.account);
	$("#account").attr({"disabled":"disabled"});
	$("#password").val(data.password);
	$("#passwordEnsure").val(data.password);
	$("#email").val(data.email);
	$("#email").attr({"disabled":"disabled"});
	$("#userName").val(data.userName);
	$("#cellphone").val(data.cellphone);
	$("#filialeId").val(data.filialeId);

	$("#address").val(data.address);
	$("#remarks").val(data.remarks);

	initManageModuleChech(data.manageModule)
}

function openEdit() {
	$("#managerInfo").modal('hide');
	$("#managerInfo").on("hidden.bs.modal",function(){
		$("#openEditInfo").modal("show");
	});
	$("#openEditInfo .modal-title").text("编辑管理员信息");
	$("#saveButton").attr("name", "edit");
}

//编辑管理员信息初始化
function initManageModuleChech(data) {
	
	$.ajax({
		type : "GET",
		url : basePath + "module",
		data : "",
		dataType : "JSON",
		success : function(msg) {
			if (msg.status == "success") {
				var moduleData = msg.message;
				$("#manageModuleCheck").html("");
				for (var i = 0; i < moduleData.length; i++) {
					var span = $("<span>");
					var check = $("<input type='checkbox'>");
					//var label = $("<label>");
					check.val(moduleData[i].id);
					check.attr("id", moduleData[i].id);
					check.attr("name", "manageModule1");
					for (var j = 0; j < data.length; j++) {
						if (moduleData[i].id == data[j].id) {
							check.attr("checked", "true");
						}
					}
					span.append(check);
					//label.text(moduleData[i].cnName);
					span.append(moduleData[i].cnName);
					span.attr("title", moduleData[i].cnName);
					//span.append(label);
					$("#manageModuleCheck").append(span);
				}
			}
		}

	})

}

function initManageModuleInput() {
	var ids = "";
	$("input:checkbox[name='manageModule1']:checked").each(function(i) {
		if (0 == i) {
			ids = $(this).val();
		} else {
			ids += ("," + $(this).val());
		}
	});
	$("#manageModule").val(ids);
}

function addAdmin() {
	$("#openEditInfo .modal-title").text("添加管理员");
	$("#saveButton").attr("name", "save");
	$("#password").val('');
	$("#passwordEnsure").val('');
	$.ajax({
		type : "GET",
		url : basePath + "module",
		data : "",
		dataType : "JSON",
		success : function(msg) {
			if (msg.status == "success") {
				var moduleData = msg.message;
				$("#manageModuleCheck").html("");
				for (var i = 0; i < moduleData.length; i++) {
					var span = $("<span>");
					var check = $("<input type='checkbox'>");
					check.val(moduleData[i].id);
					check.attr("id", moduleData[i].id);
					check.attr("name", "manageModule1");
					span.append(check);
					span.append(moduleData[i].cnName);
					span.attr("title", moduleData[i].cnName);
					$("#manageModuleCheck").append(span);
				}
			}
		}

	});

	document.getElementById("managerId").value = "";
	document.getElementById("iconfile").value = "";
	$("#account").val("").removeAttr("disabled");
	$("#email").val("").removeAttr("disabled");
	$("#userName").val("");
	$("#cellphone").val("");
	$("#qq").val("");
	$("#manageModuleText").css("display", "none");

	$("#address").val("");
	$("#remarks").val("");
}


function changeIcon() {
	$.ajaxFileUpload({
		url : basePath+"manager/upload", //用于文件上传的服务器端请求地址
		secureuri : false, //一般设置为false
		fileElementId : "iconFile" , //文件上传控件的id属性  <input type="file" id="file" name="file" /> 注意，这里一定要有name值   
														//$("form").serialize(),表单序列化。指把所有元素的ID，NAME 等全部发过去
		dataType : 'text',//返回值类型 一般设置为json
		complete : function() {//只要完成即执行，最后执行
		},
		success : function(data, status) //服务器成功响应处理函数
		{
			data = data.replace(/<[^>]+>/g,"");
            if(data.substring(0, 1) == 0){     //0表示上传成功(后跟上传后的文件路径),1表示失败(后跟失败描述)
                $("img[id='icon']").attr("src", data.substring(2));
                $("#iconfile").val(data.substring(2));
                alert("图片上传成功");
            }else{
            	alert('图片上传失败，请重试！！');
            }
		},
		error : function(data, status, e)//服务器响应失败处理函数
		{
			alert(e);
		}
	});
};

function openFileUpload() {
	$("#iconFile").click();
}

$("#saveButton").click(function() {
	initManageModuleInput();
	
	if($("#account").val().trim()==""){
		alert('用户名不能为空');
		return false;
	}
	
	var filter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if (!filter.test($("#email").val())){
		alert('您的电子邮件格式不正确');
		return false;
	}
	
	if($("#password").val()!=$("#passwordEnsure").val()){
		alert("两次输入密码必须一致！");
		$("#password").focus();
		return;
	}
	
	if(""==$("#userName").val().trim()){
		alert("姓名不能为空");
		return;
	}

	var operation = $("#saveButton").attr("name");
	var ajax_url = basePath; // 表单目标
	var ajax_type = $("#editForm").attr('method'); // 提交方法
	var ajax_data = $("#editForm").serialize(); // 表单数据
	if (operation == "save") {
		ajax_url += "manager/save";
	} else {
		ajax_url += "manager/edit";
	}
	$("#editForm").attr("action", ajax_url);

	$.ajax({
		type : ajax_type, // 表单提交类型
		url : ajax_url, // 表单提交目标
		data : ajax_data, // 表单数据
		dataType : 'json',
		success : function(msg) {
			if (msg.status == 'success') { // msg 是后台调用action时，你穿过来的参数
				if (operation == "save") {
					alert("添加成功");
				} else {
					alert("更新成功");
				}
				$("button[class='close']").click();
				window.location.reload();
			} else {
				alert("更新失败");
			}
		}
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

function deleteData() {

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
	ids=(ids.substring(ids.length-1)==',')?ids.substring(0,ids.length-1):ids;
	var ajax_url = basePath; //表单目标 
	ajax_url += "manager/delete";
	$.ajax({
		type : 'post', //表单提交类型 
		url : ajax_url, //表单提交目标 
		data : {ids:ids}, //表单数据
		dataType:'json',
		success : function(msg) {
			if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
				alert("删除成功");
				window.location.reload();
			} else {
				alert("删除失败");
			}
		},
		error:function(msg){
			alert(msg.status+","+msg.message);
		}
	});
}

var resetid = null;
function resetpwd(ID){
	resetid = ID;
}
function resetPWDButton(){
	var password = $("#reset_password").val();
	$.ajax({
		type:"post",
		dataType:"json",
		url:basePath+ "manager/resetPWD/" + resetid,
		data:{password:password},
		success:function(data){
			if(data.status=="success"){
				alert("重置密码成功");
				$("#reSettingPWD_EMAIL").modal('hide');
			}else{
				alert("重置密码失败");
			}
		},
		error:function(){
			alert("重置密码失败");
		}
	});
}
function rewritePWDButton(){
	var password = $("#reset_password").val();
	$.ajax({
		type:"post",
		dataType:"json",
		url:basePath+ "manager/updatePWD/" + resetid,
		data:{password:password},
		success:function(data){
			if(data.status=="success"){
				alert("重置密码成功");
				$("#reSettingPWD").nodal('hide');
			}else{
				alert("重置密码失败");
			}
		},
		error:function(){
			alert("重置密码失败");
		}
	});
}
function loadAdminFiliale() {
	$.ajax({
		url : basePath +'filiale/findAll',
		dataType : "json",
		type : "get",
		success : function(data) {
			if(data.status=='success'){
				var temp = $("#filialeId");
				temp.html('');
				//temp.append("<option value='' selected>请选择</option>");
				$(data.message.filialeInfo).each(
						function(index, row) {
							temp.append("<option value='"+row.id+"'>"
									+ row.filialeName + "</option>");
						});
			}else{
				$.messager.alert("提示", data.message, 'info');
			}
		},
		error : function(xhr) {
			$.messager.alert("提示", '服务器处理异常，请重新提交！', 'info');
			return false;
		}
	});
}