var basePath = document.getElementById("basePath").value;

$("#saveButton").click(function() {
	var name = $(this).attr("name");
	var ajax_url = basePath; //表单目标 
	var ajax_type = $("#editForm_"+name).attr('method'); //提交方法 
	var ajax_data = $("#editForm_"+name).serialize(); //表单数据 
	ajax_url += "setting/update?type=email&info="+name;
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

function testEmail(){
	openload();
	$.ajax({
		type:"get",
		dataType:"json",
		url:basePath+"setting/testemail",
		success:function(data){
			closeload();
			if(data.status=="success" && data.message==true){
				alert("检测结果：成功！");
			}else{
				alert("检测结果：失败！");
			}
		},
		error:function(msg){
			closeload();
			alert("检测失败！");
		}
	});
}

function testPersonalEmail(){
	openload();
	$.ajax({
		type:"get",
		dataType:"json",
		url:basePath+"setting/testpemail",
		success:function(data){
			closeload();
			if(data.status=="success" && data.message==true){
				alert("检测结果：成功！");
			}else{
				alert("检测结果：失败！");
			}
		},
		error:function(msg){
			closeload();
			alert("检测失败！");
		}
	});
}