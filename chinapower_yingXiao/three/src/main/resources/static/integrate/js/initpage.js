var basePath = document.getElementById("basePath").value;
var status;
for(var i = 1 ;i <=3;i++){
	$("#saveButton"+i).click(function() {
		var name = $(this).attr("name");
		var ajax_url = basePath; //表单目标 
		var ajax_type = $("#editForm_"+name).attr('method'); //提交方法 
		var ajax_data = $("#editForm_"+name).serialize(); //表单数据 
		ajax_url += "setting/update?type=integrate&info="+name;
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
};

function changeStatus(status1){
	status = status1;
	
}

for(var i = 4 ;i <=5;i++){
$("#saveButton"+i).click(function() {
	idbackup = $("#idbackup").val();
	$.ajax({
		type:'post',
		url:basePath += "setting/changeStatus",
		data :{SYSStatus:status,id:idbackup},
		dataType:'json',
		success:function(msg){
			if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
				alert("更新成功");
				$("button[class='close']").click();
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
}

$("#saveButton0").click(function() {
	passWord = $("#password").val();
	idbackup = $("#idbackup").val();
	$.ajax({
		type:'post',
		url:basePath += "setting/changeKey",
		data :{password:passWord,id:idbackup},
		dataType:'json',
		success:function(msg){
			if (msg.status == 'success') { //msg 是后台调用action时，你穿过来的参数
				alert("更新成功");
				$("button[class='close']").click();
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