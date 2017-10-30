var pageSize,pageNo,totalPage,startTime,endTime,queryKey;
$(function($){
	initpage();
	$("#pageSize"+pageSize).attr("selected","selected");
});

//$(document).on('change','#pageSize',function(){
//    pageSize = $(this).val();
//    ajaxget();
//})

function initpage(){
	pageSize = document.getElementById("prePageSize").value;
	pageNo = $("#curPage").val();
	totalPage = $("#totalPage").text();
	startTime = $("#startTime").val();
	endTime = $("#endTime").val();
	queryKey = $("#queryKey").val();
}

function pagesCur(pagesize){
	pageSize = pagesize;
	ajaxget();
}

function pagesCur(){
	pageSize = $("#pageSize").val();
	ajaxget();
}

function presCur(pageno){
	pageNo = pageno;
	ajaxget();
}
 
function pre(prepage){
	if (prepage != 0 && (Number(pageNo) + Number(prepage))<=Number(totalPage) && (Number(pageNo) + Number(prepage))!=0) {
		pageNo = Number(pageNo) + Number(prepage);
	} else
		pageNo = 1;
	ajaxget();
}

function query(){
	initpage();
	ajaxget();
}

function ajaxget(){
	var href = "operationlog?" + "pageNo="+pageNo+"&pageSize="+pageSize ;
	if(startTime!=""&&endTime==""||endTime!=""&&startTime==""){
		alert("开始时间或结束时间不可为空");
		window.location.href = "operationlog";
		 return;
	}
	if(startTime != "" && endTime != ""){
		href += "&startTime="+startTime+"&endTime="+endTime;
	}
	if(queryKey != ""){
		queryKey = encodeURIComponent(queryKey);
		queryKey = encodeURIComponent(queryKey);
		href += "&queryKey="+queryKey;
	}
	
	window.location.href = href;
}

function exportExcel(){
	initpage();
	var href = "operationlog/export?" + "pageNo="+pageNo+"&pageSize="+pageSize ;
	if(startTime!=""&&endTime==""||endTime!=""&&startTime==""){
		alert("开始时间或结束时间不可为空");
		window.location.href = "operationlog";
		 return;
	}
	if(startTime != "" && endTime != ""){
		href += "&startTime="+startTime+"&endTime="+endTime;
	}
	if(queryKey != ""){
		href += "&queryKey="+queryKey;
	}
	
	window.location.href = href;
}


