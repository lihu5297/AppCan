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
<title><%=Cache.getSetting("SETTING").getPlatName() %> - 成员使用频率统计</title>
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
		<form action="/dynamic/user/day" id="projectHourForm" method="post">
			<div class="location">
				<a href="javascript:;">数据管理</a>　/　<a href="javascript:;" class="active">成员使用频率统计</a>
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
              <span class="title">成员动态统计</span>
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
                    <th>用户姓名</th>
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
                </tr>
              </thead>
              <tbody>
              <c:forEach var="project" items="${list}" varStatus="status">
                <tr>
                  <td>
                  	<input type="checkbox" class="projectid" value="${project.userid}"/>
                  	${status.index +1}
                  </td>
                  <td class="td_projectname_${project.userid} projectname">${project.username}</td>
                  <td class="td_1_${project.userid}">${project.day1}</td>
                  <td class="td_2_${project.userid}">${project.day2}</td>
                  <td class="td_3_${project.userid}">${project.day3}</td>
                  <td class="td_4_${project.userid}">${project.day4}</td>
                  <td class="td_5_${project.userid}">${project.day5}</td>
                  <td class="td_6_${project.userid}">${project.day6}</td>
                  <td class="td_7_${project.userid}">${project.day7}</td>
                  <td class="td_8_${project.userid}">${project.day8}</td>
                  <td class="td_9_${project.userid}">${project.day9}</td>
                  <td class="td_10_${project.userid}">${project.day10}</td>
                  <td class="td_11_${project.userid}">${project.day11}</td>
                  <td class="td_12_${project.userid}">${project.day12}</td>
                  <td class="td_13_${project.userid}">${project.day13}</td>
                  <td class="td_14_${project.userid}">${project.day14}</td>
                  <td class="td_15_${project.userid}">${project.day15}</td>
                  <td class="td_16_${project.userid}">${project.day16}</td>
                  <td class="td_17_${project.userid}">${project.day17}</td>
                  <td class="td_18_${project.userid}">${project.day18}</td>
                  <td class="td_19_${project.userid}">${project.day19}</td>
                  <td class="td_20_${project.userid}">${project.day20}</td>
                  <td class="td_21_${project.userid}">${project.day21}</td>
                  <td class="td_22_${project.userid}">${project.day22}</td>
                  <td class="td_23_${project.userid}">${project.day23}</td>
                  <td class="td_24_${project.userid}">${project.day24}</td>
                  <td class="td_25_${project.userid}">${project.day25}</td>
                  <td class="td_26_${project.userid}">${project.day26}</td>
                  <td class="td_27_${project.userid}">${project.day27}</td>
                  <td class="td_28_${project.userid}">${project.day28}</td>
                  <td class="td_29_${project.userid}">${project.day29}</td>
                  <td class="td_30_${project.userid}">${project.day30}</td>
                  <td class="td_31_${project.userid}">${project.day31}</td>
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
				//$("#projectHourForm").attr("action","dynamic/user/day").submit();
		    	$("#projectHourForm").attr("action","dynamic/user/"+$("#classType").val()).submit();
			}
			function query(){
				beforeCommit();
				//$("#projectHourForm").attr("action","dynamic/user/day").submit();
				$("#projectHourForm").attr("action","dynamic/user/"+$("#classType").val()).submit();
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
					
					
					if("all"==id || id=="task"){
						$("#dynamicType").val(id);
					}
					if("hour"==id || id=="day" || id=="week" || id=="month"){
						$("#classType").val(id);
					}
					
					//var nextUrl = "<%=basePath%>dynamic/user/"+$("#classType").val()+"?viewType="+$("#viewType").val()+"&dynamicType="+$("#dynamicType").val()+"&date="+$("#startTimeStr").val();
					//alert(nextUrl);
					//window.location=nextUrl;
					beforeCommit();
					$("#projectHourForm").attr("action","dynamic/user/"+$("#classType").val()).submit();
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
		    			//$("#projectHourForm").attr("action","dynamic/user/day").submit();
		    			$("#projectHourForm").attr("action","dynamic/user/"+$("#classType").val()).submit();
	    			}
	    		});
	    		//上一页
	    		$(".Previous").click(function(){
	    			if("${pageNo!=1}"){
	    				$(":input[name='pageNo']").val("${pageNo-1}");
	    				beforeCommit();
	        			//$("#projectHourForm").attr("action","dynamic/user/day").submit();
	    				$("#projectHourForm").attr("action","dynamic/user/"+$("#classType").val()).submit();
	    			}
	    			
	    		});
	    		//尾页
	    		$(".last").click(function(){
	    			if("${totalPages==pageNo}"=="true"){
	    				return false;
	    			}
	    			beforeCommit();
	    			$(":input[name='pageNo']").val("${totalPages}");
	    			//$("#projectHourForm").attr("action","dynamic/user/day").submit();
	    			$("#projectHourForm").attr("action","dynamic/user/"+$("#classType").val()).submit();
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
					        data : ['1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30','31']
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