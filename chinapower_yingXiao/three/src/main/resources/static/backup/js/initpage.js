var pageSize, pageNo, totalPage;
var basePath = document.getElementById("basePath").value;
$(function($) {
	initpage();
	$("#pageSize" + pageSize).attr("selected", "selected");
    
});

function initpage() {
	pageSize = document.getElementById("prePageSize").value;
	pageNo = $("#curPage").val();
	totalPage = $("#totalPage").text();
}

function pagesCur(pagesize) {
	pageSize = pagesize;
	ajaxget();
}

function pagesCur(){
	pageSize = $("#pageSize").val();
	ajaxget();
}

function presCur(pageno) {
	pageNo = pageno;
	ajaxget();
}

function pre(prepage) {
	if (prepage != 0 && (Number(pageNo) + Number(prepage))<=Number(totalPage) && (Number(pageNo) + Number(prepage))!=0) {
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
	var href = "setting/backuplog?type=backup&" + "pageNo=" + pageNo + "&pageSize=" + pageSize;

	window.location.href = href;
}
$("#saveButton").click(function() {
	var name = $(this).attr("name");
	var ajax_url = basePath; //表单目标 
	var ajax_type = $("#editForm_"+name).attr('method'); //提交方法 
	var ajax_data = $("#editForm_"+name).serialize(); //表单数据 
	ajax_url += "setting/update?type=backup&info="+name;
	$("#editForm_"+name).attr("action", ajax_url);

	$.ajax({
		type : ajax_type, //表单提交类型 
		url : ajax_url, //表单提交目标 
		data : ajax_data, //表单数据
		dataType:'json',
		success : function(msg) {
			if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
				alert("更新成功");
				window.location.reload();
			} else {
				alert("更新失败");
			}
		},
		error:function(msg){
			alert(msg.status+","+msg.message);
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
	var ajax_url = basePath; //表单目标 
	ajax_url += "setting/backuplog/delete?type=backup";
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

function backup(){
	openload();
	var ajax_url = basePath; //表单目标 
	ajax_url += "setting/backup";
	$.ajax({
		type : 'get',
		url : ajax_url,
		dataType:'json',
		success : function(msg) {
			if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
				closeload();
				alert("正在后台处理中,你可以处理其他的事情,可以通过查看状态判断是否完成备份。");
				window.location.reload();
			} else {
				closeload();
				alert("备份失败,原因："+msg.message);
			}
		},
		error:function(msg){
			closeload();
			alert(msg.status+","+msg.message);
		}
	});
}
