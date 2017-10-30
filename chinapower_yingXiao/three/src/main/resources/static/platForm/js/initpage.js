var basePath = document.getElementById("basePath").value;

function changeIcon() {
	$.ajaxFileUpload({
		url : basePath+"setting/upload/logo", //用于文件上传的服务器端请求地址
		secureuri : false, //一般设置为false
		fileElementId : "logoFile" , //文件上传控件的id属性  <input type="file" id="file" name="file" /> 注意，这里一定要有name值   
														//$("form").serialize(),表单序列化。指把所有元素的ID，NAME 等全部发过去
		dataType : 'text',//返回值类型 一般设置为json
		complete : function() {//只要完成即执行，最后执行
		},
		success : function(data, status) //服务器成功响应处理函数
		{
			data = data.replace(/<[^>]+>/g,"");
            if(data.substring(0, 1) == 0){     //0表示上传成功(后跟上传后的文件路径),1表示失败(后跟失败描述)
            	$("img[id='logo']").attr("src", "");
                $("img[id='logo']").attr("src", data.substring(2));
                $("#platLogo").val(data.substring(2));
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
	$("#logoFile").click();
}

$("#saveButton").click(function() {
	var name = $(this).attr("name");
	var ajax_url = basePath; //表单目标 
	var ajax_type = $("#editForm_"+name).attr('method'); //提交方法 
	var ajax_data = $("#editForm_"+name).serialize(); //表单数据 
	ajax_url += "setting/update?type=platForm&info="+name;
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
