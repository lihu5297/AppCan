var basePath = document.getElementById("basePath").value;
$(function() {
	$("input[type='checkbox']").click(function() {
		alert("checkbox changed");
	});
});

function changeIcon() {
	$.ajaxFileUpload({
		url : basePath+"manager/upload", //鐢ㄤ簬鏂囦欢涓婁紶鐨勬湇鍔″櫒绔姹傚湴鍧�
		secureuri : false, //涓�鑸缃负false
		fileElementId : "iconFile" , //鏂囦欢涓婁紶鎺т欢鐨刬d灞炴��  <input type="file" id="file" name="file" /> 娉ㄦ剰锛岃繖閲屼竴瀹氳鏈塶ame鍊�   
														//$("form").serialize(),琛ㄥ崟搴忓垪鍖栥�傛寚鎶婃墍鏈夊厓绱犵殑ID锛孨AME 绛夊叏閮ㄥ彂杩囧幓
		dataType : 'text',//杩斿洖鍊肩被鍨� 涓�鑸缃负json
		complete : function() {//鍙瀹屾垚鍗虫墽琛岋紝鏈�鍚庢墽琛�
		},
		success : function(data, status) //鏈嶅姟鍣ㄦ垚鍔熷搷搴斿鐞嗗嚱鏁�
		{
			data = data.replace(/<[^>]+>/g,"");
            if(data.substring(0, 1) == 0){     //0琛ㄧず涓婁紶鎴愬姛(鍚庤窡涓婁紶鍚庣殑鏂囦欢璺緞),1琛ㄧず澶辫触(鍚庤窡澶辫触鎻忚堪)
                $("img[id='icon']").attr("src", data.substring(2));
                $("#iconfile").val(data.substring(2));
                alert("鍥剧墖涓婁紶鎴愬姛");
            }else{
            	alert('鍥剧墖涓婁紶澶辫触锛岃閲嶈瘯锛侊紒');
            }
		},
		error : function(data, status, e)//鏈嶅姟鍣ㄥ搷搴斿け璐ュ鐞嗗嚱鏁�
		{
			alert(e);
		}
	});
};

function openFileUpload() {
	$("#iconFile").click();
}

$("#saveButton").click(function() {
	var operation = $("#saveButton").attr("name");
	var ajax_url = basePath; //琛ㄥ崟鐩爣 
	var ajax_type = $("#editForm").attr('method'); //鎻愪氦鏂规硶 
	var ajax_data = $("#editForm").serialize(); //琛ㄥ崟鏁版嵁 
	if (operation == "save") {
		ajax_url += "manager/super/save";
	} else {
		ajax_url += "manager/super/edit";
	}
	$("#editForm").attr("action", ajax_url);

	$.ajax({
		type : ajax_type, //琛ㄥ崟鎻愪氦绫诲瀷
		url : ajax_url, //琛ㄥ崟鎻愪氦鐩爣
		data : ajax_data, //琛ㄥ崟鏁版嵁
		dataType:'json',
		success : function(msg) {
			if (msg.status == 'success') { //msg 鏄悗鍙拌皟鐢╝ction鏃讹紝浣犵┛杩囨潵鐨勫弬鏁�
				if(operation=="save"){
					alert("娣诲姞鎴愬姛");
				}else{
					alert("鏇存柊鎴愬姛");
				}
				$("button[class='close']").click();
				window.location.reload();
			} else {
				alert("鏇存柊澶辫触");
			}
		}
	});
});


var resetid = null;
function resetpwd(ID){
	resetid = ID;
}
function resetPWDButton(){
	var password = $("#reset_password").val();
	alert(resetid);
	$.ajax({
		type:"post",
		dataType:"json",
		url:basePath+ "manager/resetPWD/" + resetid,
		data:{password:password},
		success:function(data){
			if(data.status=="success"){
				alert("重置密码成功");
				$("button[class='close']").click();
				$(".modal-backdrop.fade").remove();
			}else{
				alert("重置密码失败");
			}
		},
		error:function(){
			
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
				alert("閲嶇疆瀵嗙爜鎴愬姛");
				$("button[class='close']").click();
			}else{
				alert("閲嶇疆瀵嗙爜澶辫触");
			}
		},
		error:function(){
			
		}
	});
}
