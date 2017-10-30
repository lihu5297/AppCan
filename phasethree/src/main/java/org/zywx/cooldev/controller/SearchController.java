package org.zywx.cooldev.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.BUG_SOLUTION;
import org.zywx.cooldev.commons.Enums.BUG_STATUS;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.service.SearchService;

@Controller
@RequestMapping(value = "/search")
public class SearchController extends BaseController {

	@Autowired
	private SearchService searchService;

	/**
	 * 
	 * @describe 全局搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 下午3:10:41 <br>
	 * @param request
	 * @param response
	 * @param type
	 * @param query
	 * @return <br>
	 * @throws ParseException
	 * @returnType ModelAndView
	 *
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET })
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @RequestParam(value = "type", required = false , defaultValue="0") int type,
			@RequestParam(value = "query") String query) {
		Long startsTime = Calendar.getInstance().getTimeInMillis();
		log.info(this.getClass()+": global search starts,query-->"+query);
		Map<String, Object> map = new HashMap<String, Object>();

		List<Map<String, Object>> list;
		try {
			String querySC = "";
			if (null == query || "" == query) {
				return this.getFailedModel("query is null");
			}else
				querySC = this.documentChapterService.getSpecialCharQuery(query);
			int ipageNo = 1;
			int ipageSize = 15;
			if (null != request.getParameter("pageNo")) {
				try {
					ipageNo = Integer.parseInt(request.getParameter("pageNo"));
					ipageSize = Integer.parseInt(request.getParameter("pageSize"));
				} catch (Exception e) {
					return this.getFailedModel("pageNo or pageSize is illagel");
				}
			}
			if (0 != type) {
				
				Map<String, Object> page = this.searchService.getSearchByType("%" + querySC + "%", loginUserId, type,
						ipageNo, ipageSize);
				list = (List<Map<String, Object>>) page.get("list");
				// 处理时间
				relativeDateFormat(list);
				Map<String, Object> allRes = this.searchService.getSearch("%" + querySC + "%", loginUserId);
				map.put("totalProject", allRes.get("totalProject"));
				map.put("totalTask", allRes.get("totalTask"));
				map.put("totalTeam", allRes.get("totalTeam"));
				map.put("totalDocument", allRes.get("totalDocument"));
				map.put("totalResource", allRes.get("totalResource"));
				map.put("totalBug", allRes.get("totalBug"));
				map.put("total", allRes.get("total"));
			} else {
				Map<String, Object> page = this.searchService.getSearch("%" + querySC + "%", loginUserId);
				list = (List<Map<String, Object>>) page.get("list");
				map.put("total", page.get("total"));
				map.put("totalProject", page.get("totalProject"));
				map.put("totalTask", page.get("totalTask"));
				map.put("totalTeam", page.get("totalTeam"));
				map.put("totalDocument", page.get("totalDocument"));
				map.put("totalResource", page.get("totalResource"));
				map.put("totalBug", page.get("totalBug"));
				
				//快速排序
				list = quickSort(list);
				
				//实现分页效果
				int size = list.size();
				List<Map<String, Object>> backup = new ArrayList<Map<String, Object>>();
				for(int i = 0 ; i < size ; i++){
					if(i>=(ipageNo-1)*ipageSize && i <= ipageNo*ipageSize-1){
						backup.add(list.get(i));
					}
				}
				relativeDateFormat(backup);
				
				list = backup;
			}
			map.put("list", list);
			Long endsTime = Calendar.getInstance().getTimeInMillis();
			log.info(this.getClass()+": global search total time : " + (endsTime - startsTime) + "ms");
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel("查询失败！错误信息："+e.getMessage());
		}
	}

	private void relativeDateFormat(List<Map<String, Object>> list) throws ParseException {
		if (list == null || list.size() < 1) {
			return;
		}
		TASK_STATUS[] taskStatus = TASK_STATUS.values();
	    BUG_STATUS[] bugStatus=BUG_STATUS.values();
	    BUG_SOLUTION[] bugSolutions=BUG_SOLUTION.values();
		TEAMTYPE[] teamType = TEAMTYPE.values();
		for (Map<String, Object> map : list) {
			Timestamp time = (Timestamp) map.get("createdAt");
			map.put("createdAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
			if(map.get("searchType").equals("TASK")){
				int a = Integer.parseInt(map.get("status").toString());
				map.put("status", taskStatus[a]);
			}else if(map.get("searchType").equals("TEAM")){
				int a = Integer.parseInt(map.get("type").toString());
				map.put("type", teamType[a]);
			}else if(map.get("searchType").equals("BUG")){
				int a = Integer.parseInt(map.get("status").toString());
				map.put("status",bugStatus[a]);
				if(map.get("solution")!=null){
					int b=Integer.parseInt(map.get("solution").toString());
					map.put("solution", bugSolutions[b]);
				}else{
					map.put("solution", "无");
				}
				
			}
			
		}

	}

	private List<Map<String, Object>> quickSort(List<Map<String, Object>> list) {

		@SuppressWarnings("unchecked")
		Map<String, Object>[] listA = list.toArray(new Map[] {});

		_quickSort(listA, 0, listA.length - 1);

		return Arrays.asList(listA);
	}

	public void _quickSort(Map<String, Object>[] list, int low, int high) {
		if (low < high) {
			int middle = getMiddle(list, low, high); // 将list数组进行一分为二
			_quickSort(list, low, middle - 1); // 对低字表进行递归排序
			_quickSort(list, middle + 1, high); // 对高字表进行递归排序
		}
	}

	public int getMiddle(Map<String, Object>[] list, int low, int high) {

		Map<String, Object> obj = list[low]; // 数组的第一个作为中轴
		Timestamp tmp = getTimestamp(obj);

		while (low < high) {

			while (low < high && getTimestamp(list[high]).getTime() < tmp.getTime()) {
				high--;
			}
			list[low] = list[high]; // 比中轴小的记录移到低端
			while (low < high && getTimestamp(list[low]).getTime() > tmp.getTime()) {
				low++;
			}
			while (low < high && getTimestamp(list[low]).getTime() == tmp.getTime()) {
				low++;
			}
			list[high] = list[low]; // 比中轴大的记录移到高端

		}
		list[low] = obj; // 中轴记录到尾
		return low; // 返回中轴的位置
	}

	private Timestamp getTimestamp(Map<String, Object> obj) {
		Timestamp times = new Timestamp(0L);
		times = (Timestamp) obj.get("createdAt");
		return times;
	}

}
