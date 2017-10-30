<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
.modal-body p{
height:24px;
padding:0 32px;
cursor:pointer;
color:rgb(255,255,255);
}
.over_icon{
z-index:999;
margin-top:-24px;
background-color:rgb(144,152,167);
}
table.man-tables tbody tr td{
	vertical-align: middle;
}
</style>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title>协同开发后台</title>
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
	<input type="hidden" value="<%=basePath%>" id="basePath">
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
			<div class="location">
				<a href="javascript:void(0);">资源管理</a> / <a href="javascript:void(0);"
					class="active">模板上传</a>
			</div>
			<div class="content">
				<div class="right-content">
					<div class="right-header">
						 <iframe id="downExcelModule" style="display: none;" src="" uri=""> 
								</iframe>
					</div>
					<table class="table table-striped table-bordered tables man-tables">
						<thead>
							<tr>
								<th style="width:25%;">名称</th>
								<th style="width:25%;">上传者</th>
								<th style="width:25%;">更新时间</th>
								<th style="width:25%;">操作</th>
							</tr>
						</thead>
						<tbody>
						
							<c:forEach var="templet" items="${templetInfo}">
								

								<tr>
									<td>${templet.temName }</td>
									<td>${templet.creatorName }</td>
									<td>${templet.updatedAt }</td>
									<td><a class="uploadFile" data-id="${templet.id}">上传</a> 
									<%-- <a class="downloadFile" onclick="window.location.href='${templet.fileUrl}'">下载</a> --%>
										<a class="downloadFile" href="javascript:void(0);" uri="${templet.fileUrl}">下载</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	<div id="upload" style="display: none">
		<form method="post" action="" id="upForm" name="upForm"
			enctype="multipart/form-data">
			<input type="file" name="file" id="file"
				onchange="chooseFileUp(this.value)" accept="*" />
		</form>
	</div>
	<!-- Small modal -->
	<button id="messageInfoButton" type="button" class="btn btn-primary"
		data-toggle="modal" data-target=".bs-example-modal-sm"
		style="display: none">Small modal</button>

	<div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog"
		aria-labelledby="mySmallModalLabel">
		<div class="modal-dialog modal-sm">
			<div class="modal-content" id="messageInfo">...</div>
		</div>
	</div>
	<script src="static/js/init.js"></script>
	<script src="static/js/jquery.form.js"></script>
	<script>
	var basePath = document.getElementById("basePath").value;
	$(function(){
		$('.menuNav>li').removeClass('active');
		$('.menuNav>li').eq(10).find('.nav-pills').show();
		$('.menuNav>li').eq(10).css('background-color','#383f4e');
	})
	$(".btn-niu input").keydown(function(e){
	    if(e.keyCode==13){
	    	query();
	    }
    })
    var $operdown="",$operdownId="";
    $(".uploadFile").click(function() {
        $("#file").click();
        $operdown=$(this).parent().children(".downloadFile");
        $operdownId = $(this).attr("data-id");
    });
    $(".downloadFile").click(function(){
    	//alert($(this).attr("uri"));
    	$("#downExcelModule").attr("src",$(this).attr("uri"));
	});
     function chooseFileUp(urlVal) {
        //上传
        //设定问见上传地址
        alert(urlVal);
        var templetId = $operdownId;//$("#templetId").val();
        var fileUpUrl = basePath + "upload/templetFile/"+templetId;
        if (urlVal) {
            document.getElementById("upForm").action = fileUpUrl;
            //上传附件
            $("#upForm").ajaxSubmit({
                dataType : 'json',
                success : function(data) {
                    if (data.retCode == "0") {//上传成功
                        //上传返回的文件路劲
                        var fileUrl = data.fileUrl;
                        $operdown.attr("onclick","window.location.href='"+fileUrl+"'");
                        alert('上传成功！');
                    } else {//上传失败的提示
                        alert(data.retInfo);
                    }
                },
                error : function(err) {
                    $.checkConfirm("文件上传出错！");
                    return;
                },
                complete : function() {

                }
            })
        }
    }
	</script>
</body>
</html>