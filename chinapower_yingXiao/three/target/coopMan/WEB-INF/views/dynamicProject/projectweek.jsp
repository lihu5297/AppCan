<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@include file="../taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<style type="text/css">
.typeColor{
color:#EE7600 !important
}
.go{
 cursor:pointer
}
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
.tables tr th{
	min-width: 100px;
}
table.diskTable tbody tr td{
	padding-top: 0;
	padding-bottom: 0;
	vertical-align: middle;
}
#usedInfo tbody tr td{
	width:200px;
}
#unUsedInfo tbody tr td{
	width:200px;
}
</style>
<meta charset="utf-8">
<base href="<%=basePath%>" />
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 项目使用频率统计</title>
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
    <script src="static/js/echarts.common.min.js"></script>
    <style type="text/css">
    	span {margin-left:20px;}
    </style>
</head>
<body>
	<input type="hidden" value="<%=basePath%>" id="basePath">
	<%@include file="../head.jsp"%>
	<div class="main">
		<%@include file="../left.jsp"%>
		<div class="right">
		<form action="/dynamic/project/week" id="teamHourForm" method="post">
			<div class="location">
				<a href="javascript:;">数据管理</a>　/　<a href="javascript:;" class="active">项目使用频率统计</a>
			</div>
			<div>
				<span id="sum" class="go">总量</span><span id="avg" class="go">平均值</span>			
			</div>
			<div>
				 <span id="hour" class="go">每小时</span><span id="day" class="go">每天</span>	<span id="week" class="go">每周</span><span class="go" id="month">每月</span>
				 <span>时间：</span>
				 <input id="startTimeStr" type="text" placeholder="时间插件" value="${begin}"  style="height:30px;padding-left:5px">
				 <input type="hidden" name="begin" id="beginDate"/>
	              <span style="margin-left: 8px;">结束时间：</span>
	              <input id="endTimeStr" type="text" placeholder="时间插件" value="${end}" style="height:30px;padding-left:5px;">
	              <input type="hidden" name="end" id="endDate"/>
			</div>
			<div>
				<span id="all" class="go">全部动态统计</span><span id="task" class="go">任务动态统计</span>	
			</div>
			
			<div class="content">
          <div class="right-content">
            <div class="right-header">
              <span class="title">项目动态统计</span>
				<div style="float:right">
             
              <span style="margin-left: 8px;">关键字：</span>
              <input id="queryKey" type="text" placeholder="关键字查询" name="keyWords" value="${keyWords}" style="height:30px;padding-left:5px">
              <button type="button" onclick="query()" class="btn btn-primary">查询</button>
              </div>
            </div> 
            
            <!-- chart  begin-->
             <div id="main" style="width: 1000px;height:400px;"></div>
			
            <!--  chart  end -->
            
            <table class="table table-striped table-bordered tables" style="margin-top:40px;width:100px;">
              <thead>
                <tr>
                    <th>序号</th>
                    <th>项目名称</th>
                    <th>1</th>
                    <th>2</th>
                    <th>3</th>
                    <th>4</th>
                    <th>5</th>
                    <th>6</th>
                    <th>7</th>
                    <th>8</th>
                    <th>9</th>
                    <th>10</th>
                    <th>11</th>
                    <th>12</th>
                    <th>13</th>
                    <th>14</th>
                    <th>15</th>
                    <th>16</th>
                    <th>17</th>
                    <th>18</th>
                    <th>19</th>
                    <th>20</th>
                    <th>21</th>
                    <th>22</th>
                    <th>23</th>
                    <th>24</th>
                    <th>25</th>
                    <th>26</th>
                    <th>27</th>
                    <th>28</th>
                    <th>29</th>
                    <th>30</th>
                    <th>31</th>
                    <th>32</th>
                    <th>33</th>
                    <th>34</th>
                    <th>35</th>
                    <th>36</th>
                    <th>37</th>
                    <th>38</th>
                    <th>39</th>
                    <th>40</th>
                    <th>41</th>
                    <th>42</th>
                    <th>43</th>
                    <th>44</th>
                    <th>45</th>
                    <th>46</th>
                    <th>47</th>
                    <th>48</th>
                    <th>49</th>
                    <th>50</th>
                    <th>51</th>
                    <th>52</th>
                    <th>53</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="team" items="${list}" varStatus="status">
                <tr>
                  <td>
                  	<input type="checkbox" class="projectid" value="${team.projectid}"/>
                  	${status.index +1}
                  </td>
                  <td class="td_projectname_${team.projectid} projectname">${team.projectname}</td>
                  <td class="td_1_${team.projectid}">${team.week1}</td>
                  <td class="td_2_${team.projectid}">${team.week2}</td>
                  <td class="td_3_${team.projectid}">${team.week3}</td>
                  <td class="td_4_${team.projectid}">${team.week4}</td>
                  <td class="td_5_${team.projectid}">${team.week5}</td>
                  <td class="td_6_${team.projectid}">${team.week6}</td>
                  <td class="td_7_${team.projectid}">${team.week7}</td>
                  <td class="td_8_${team.projectid}">${team.week8}</td>
                  <td class="td_9_${team.projectid}">${team.week9}</td>
                  <td class="td_10_${team.projectid}">${team.week10}</td>
                  <td class="td_11_${team.projectid}">${team.week11}</td>
                  <td class="td_12_${team.projectid}">${team.week12}</td>
                  <td class="td_13_${team.projectid}">${team.week13}</td>
                  <td class="td_14_${team.projectid}">${team.week14}</td>
                  <td class="td_15_${team.projectid}">${team.week15}</td>
                  <td class="td_16_${team.projectid}">${team.week16}</td>
                  <td class="td_17_${team.projectid}">${team.week17}</td>
                  <td class="td_18_${team.projectid}">${team.week18}</td>
                  <td class="td_19_${team.projectid}">${team.week19}</td>
                  <td class="td_20_${team.projectid}">${team.week20}</td>
                  <td class="td_21_${team.projectid}">${team.week21}</td>
                  <td class="td_22_${team.projectid}">${team.week22}</td>
                  <td class="td_23_${team.projectid}">${team.week23}</td>
                  <td class="td_24_${team.projectid}">${team.week24}</td>
                  <td class="td_25_${team.projectid}">${team.week25}</td>
                  <td class="td_26_${team.projectid}">${team.week26}</td>
                  <td class="td_27_${team.projectid}">${team.week27}</td>
                  <td class="td_28_${team.projectid}">${team.week28}</td>
                  <td class="td_29_${team.projectid}">${team.week29}</td>
                  <td class="td_30_${team.projectid}">${team.week30}</td>
                  <td class="td_31_${team.projectid}">${team.week31}</td>
                  
                  <td class="td_32_${team.projectid}">${team.week32}</td>
                  <td class="td_33_${team.projectid}">${team.week33}</td>
                  <td class="td_34_${team.projectid}">${team.week34}</td>
                  <td class="td_35_${team.projectid}">${team.week35}</td>
                  <td class="td_36_${team.projectid}">${team.week36}</td>
                  <td class="td_37_${team.projectid}">${team.week37}</td>
                  <td class="td_38_${team.projectid}">${team.week38}</td>
                  <td class="td_39_${team.projectid}">${team.week39}</td>
                  <td class="td_40_${team.projectid}">${team.week40}</td>
                  <td class="td_41_${team.projectid}">${team.week41}</td>
                  <td class="td_42_${team.projectid}">${team.week42}</td>
                  <td class="td_43_${team.projectid}">${team.week43}</td>
                  <td class="td_44_${team.projectid}">${team.week44}</td>
                  <td class="td_45_${team.projectid}">${team.week45}</td>
                  <td class="td_46_${team.projectid}">${team.week46}</td>
                  <td class="td_47_${team.projectid}">${team.week47}</td>
                  <td class="td_48_${team.projectid}">${team.week48}</td>
                  <td class="td_49_${team.projectid}">${team.week49}</td>
                  <td class="td_50_${team.projectid}">${team.week50}</td>
                  <td class="td_51_${team.projectid}">${team.week51}</td>
                  <td class="td_52_${team.projectid}">${team.week52}</td>
                  <td class="td_53_${team.projectid}">${team.week53}</td>
                </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
          </div>
          
          <div class="page">
          	
            <span class="total">第${(pageNo-1)*pageSize+1}条 到 第${(pageNo*pageSize)>total?total:(pageNo*pageSize)}条 共${total}条</span>
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
                    |&lt;
                  </a>
                </li>
                <li>
                  <a href="javascript:void(0);" class="Previous">
                    &lt;
                  </a>
                </li>
                <li>
                  第
                  <input type="text" value="${pageNo}">
                  页 共${totalPages}页
                </li>
                <li>
                  <a href="javascript:void(0);" class="Next">
                  &gt;
                  </a>
                </li>
                <li>
                  <a href="javascript:void(0);" class="last">
                    |&gt;
                  </a>
                </li>
              </ul>
            </div>
          </div>
          <div class="clear"></div>
          <input type="hidden" name="pageNo" value="${pageNo}"/>
          <input type="hidden" name="viewType" id="viewType" value="${viewType }"/>
	<input type="hidden" name="dynamicType" id="dynamicType" value="${dynamicType }"/>
	<input type="hidden" name="classType" id="classType" value="${classType }"/>
          </form>
          
        
	
	</div>
</div>

	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/init.js"></script>
	<script type="text/javascript">
	$(".main .left .menuNav").children().eq(9).children().click();
	$(".main .left .menuNav").children().eq(9).find("a[name="+$.trim($("title").html().split("-")[1])+"]").addClass("typeColor");
	$("#${classType}").addClass("typeColor");
	$("#${dynamicType}").addClass("typeColor");
	$("#${viewType}").addClass("typeColor");
	      var error = "${error}";
	      if(null!=error && ""!=error){
	    	  alert(error);
	      }
			function beforeCommit(){
			 	$("#beginDate").val($("#startTimeStr").val());
			 	$("#endDate").val($("#endTimeStr").val());
			}
	
			function submitForm(){
		    	$(":input[name='pageNo']").val(1);
		    	beforeCommit();
				$("#teamHourForm").attr("action","dynamic/project/"+$("#classType").val()).submit();
			}
			function query(){
				beforeCommit();
				$("#teamHourForm").attr("action","dynamic/project/"+$("#classType").val()).submit();
			}
	
			function strToJson(str){ 
				var json = eval('(' + str + ')'); 
				return json; 
			} 
			function joinDataTD(teamId){
				var result="[";
				result +=$(".td_1_"+teamId).text();
				result +=","+$(".td_2_"+teamId).text();
				result +=","+$(".td_3_"+teamId).text();
				result +=","+$(".td_4_"+teamId).text();
				result +=","+$(".td_5_"+teamId).text();
				result +=","+$(".td_6_"+teamId).text();
				result +=","+$(".td_7_"+teamId).text();
				result +=","+$(".td_8_"+teamId).text();
				result +=","+$(".td_9_"+teamId).text();
				result +=","+$(".td_10_"+teamId).text();
				result +=","+$(".td_11_"+teamId).text();
				result +=","+$(".td_12_"+teamId).text();
				result +=","+$(".td_13_"+teamId).text();
				result +=","+$(".td_14_"+teamId).text();
				result +=","+$(".td_15_"+teamId).text();
				result +=","+$(".td_16_"+teamId).text();
				result +=","+$(".td_17_"+teamId).text();
				result +=","+$(".td_18_"+teamId).text();
				result +=","+$(".td_19_"+teamId).text();
				result +=","+$(".td_20_"+teamId).text();
				result +=","+$(".td_21_"+teamId).text();
				result +=","+$(".td_22_"+teamId).text();
				result +=","+$(".td_23_"+teamId).text();
				
				result +=","+$(".td_24_"+teamId).text();
				result +=","+$(".td_25_"+teamId).text();
				result +=","+$(".td_26_"+teamId).text();
				result +=","+$(".td_27_"+teamId).text();
				result +=","+$(".td_28_"+teamId).text();
				result +=","+$(".td_29_"+teamId).text();
				result +=","+$(".td_30_"+teamId).text();
				result +=","+$(".td_31_"+teamId).text();
				
				result +=","+$(".td_32_"+teamId).text();
				result +=","+$(".td_33_"+teamId).text();
				result +=","+$(".td_34_"+teamId).text();
				result +=","+$(".td_35_"+teamId).text();
				result +=","+$(".td_36_"+teamId).text();
				result +=","+$(".td_37_"+teamId).text();
				result +=","+$(".td_38_"+teamId).text();
				result +=","+$(".td_39_"+teamId).text();
				
				result +=","+$(".td_40_"+teamId).text();
				result +=","+$(".td_41_"+teamId).text();
				result +=","+$(".td_42_"+teamId).text();
				result +=","+$(".td_43_"+teamId).text();
				result +=","+$(".td_44_"+teamId).text();
				result +=","+$(".td_45_"+teamId).text();
				result +=","+$(".td_46_"+teamId).text();
				result +=","+$(".td_47_"+teamId).text();
				
				result +=","+$(".td_48_"+teamId).text();
				result +=","+$(".td_49_"+teamId).text();
				result +=","+$(".td_50_"+teamId).text();
				result +=","+$(".td_51_"+teamId).text();
				result +=","+$(".td_52_"+teamId).text();
				result +=","+$(".td_53_"+teamId).text();
				result +="]";
				return result;
			}
			$(function(){
				$("#startTimeStr").focus(function(){
					WdatePicker({maxDate:"#F{$dp.$D('endTimeStr')}",dateFmt:'yyyy-MM-dd'});
				})
				$("#endTimeStr").focus(function(){
					WdatePicker({minDate:"#F{$dp.$D('startTimeStr')}",dateFmt:'yyyy-MM-dd'});
				})
				$(".projectid").click(function(){
					makeChart();
				});
				
				$(".go").click(function(){
					var id = $(this).attr("id");
					
					if("sum"==id || id=="avg"){
						$("#viewType").val(id);
					}
					
					if("all"==id || id=="task"){
						$("#dynamicType").val(id);
					}
					if("hour"==id || id=="day" || id=="week" || id=="month"){
						$("#classType").val(id);
					}
					
					//var nextUrl = "<%=basePath%>dynamic/project/"+$("#classType").val()+"?viewType="+$("#viewType").val()+"&dynamicType="+$("#dynamicType").val()+"&date="+$("#startTimeStr").val();
					//alert(nextUrl);
					//window.location=nextUrl;
					beforeCommit();
					$("#teamHourForm").attr("action","dynamic/project/"+$("#classType").val()).submit();
				});
				
				
				//fen ye
				//回显每页显示多少条
	    		if(null!="${pageSize}"){
		    		$("#pageSize").val("${pageSize}");
	    		}
	    		//下一页
	    		$(".Next").click(function(){
	    			if("${totalPages > pageNo}"){
		    			$(":input[name='pageNo']").val("${pageNo+1}");
		    			beforeCommit();
		    			$("#teamHourForm").attr("action","dynamic/project/"+$("#classType").val()).submit();
	    			}
	    		});
	    		//上一页
	    		$(".Previous").click(function(){
	    			if("${pageNo!=1}"){
	    				$(":input[name='pageNo']").val("${pageNo-1}");
	    				beforeCommit();
	        			$("#teamHourForm").attr("action","dynamic/project/"+$("#classType").val()).submit();			
	    			}
	    			
	    		});
	    		//尾页
	    		$(".last").click(function(){
	    			if("${totalPages==pageNo}"=="true"){
	    				return false;
	    			}
	    			beforeCommit();
	    			$(":input[name='pageNo']").val("${totalPages}");
	    			$("#teamHourForm").attr("action","dynamic/project/"+$("#classType").val()).submit();
	    		});
				
			})
			
			function makeChart(count){
				var legendDataStr="[";
				var seriesStr = "[";
				var flagCount=0;
				$(".projectid").each(function(){
					var teamId = $(this).val();
					if($(this).get(0).checked){
						if(flagCount==count){
							return false;//跳出each循环
						}
						var teamName=$(".td_projectname_"+teamId).text();
						legendDataStr+="'"+teamName+"'"+",";
						seriesStr+="{name:'" +teamName+ "',type:'line',data:"+joinDataTD(teamId)+"},";
						flagCount++;
					}
				});
				if(legendDataStr.length>1){
					legendDataStr=legendDataStr.substring(0,legendDataStr.length-1);
				}
				legendDataStr+="]";
				legendDataStr = strToJson(legendDataStr);
				if(seriesStr.length>1){
					seriesStr=seriesStr.substring(0,seriesStr.length-1);
				}
				seriesStr+="]";
				seriesStr = strToJson(seriesStr);
				var itemStyle={  
	                    normal : {  
	                        color : '#FF4040',  
	                        lineStyle : {  
	                            width : 2  
	                        },  
	                        label : {  
	                            show : true,  
	                            position : 'top' 
	                        }  
	                    }  
	  
	                };
					seriesStr[0].itemStyle=itemStyle;
				//----------------------------------------------重新绘制图表-------------------------------------------
				var myChart = echarts.init(document.getElementById('main'));
	
				option = {
					tooltip : {
					    trigger: 'axis'
					},
					legend: {
					    data:legendDataStr
					},
					grid: {
					    left: '3%',
					    right: '4%',
					    bottom: '3%',
					    containLabel: true,
					    y:80
					},
					toolbox: {
					    feature: {
					        saveAsImage: {}
					    }
					},
					xAxis : [
					    {
					    	name:"日期",
					        type : 'category',
					        boundaryGap : false,
					        data : ['1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30','31','32','33','34','35','36','37','38','39','40','41','42','43','44','45','46','47','48','49','50','51','52','53']
					    }
					],
					yAxis : [
					    {
					    	name:"使用频率",
					        type : 'value'
					    }
					],
					 tooltip : {         // Option config. Can be overwrited by series or data
					        trigger: 'axis',
					        formatter: function (params,ticket,callback) {
					            var res =params[0].name;
					            for (var i = 0, l = params.length; i < l; i++) {
					            	var value=params[i].value?params[i].value:0;
					                res +='<br/>'+params[i].seriesName + ' : ' +value;
					            }
					            setTimeout(function (){
					                // 仅为了模拟异步回调
					                callback(ticket, res);
					            }, 1);
					            return 'loading...';
					        }
					    },
					series : seriesStr
					};
						
						
					myChart.setOption(option);
				//----------------------------------------------重新绘制图表-------------------------------------------
			}
	</script>
	
</body>
</html>