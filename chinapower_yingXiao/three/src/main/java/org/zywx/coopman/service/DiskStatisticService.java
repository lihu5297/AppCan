package org.zywx.coopman.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.DiskStatistic;

@Service
public class DiskStatisticService extends BaseService{
	
	@Autowired
	private SettingService settingService;

	@Value("${shellBasePath}")
	private String shellBasePath;
	
	public List<DiskStatistic> updateAndGetFromServer() {
		
		String cmd = "sh " + shellBasePath + "/coopdev_disk/statistic_disk.sh";
		String diskstatistic = this.execShell(cmd);
		
		Map<String,String> hostInfo = this.settingService.getHostInfo();
		Map<String, String> result = this.analysisResult(diskstatistic);
		log.info("result:"+result);
		String usedInfo = this.getUsedInfo("USED",result,hostInfo);
		String unUsedInfo = this.getUsedInfo("AVIALABLE",result,hostInfo);
		
		DiskStatistic diskStatistic = new DiskStatistic();
		List<DiskStatistic> dsOlds = this.diskStatisticDao.findByHostNameAndHost(hostInfo.get("hostname"),hostInfo.get("ip"));
		if(!dsOlds.isEmpty()){
			log.info("dsOlds:"+dsOlds.get(0).toString());
			diskStatistic = dsOlds.get(0);
			diskStatistic.setUnUsedInfo(unUsedInfo);
			diskStatistic.setUsedInfo(usedInfo);
		}else{
			diskStatistic.setHostName(hostInfo.get("hostname"));
			diskStatistic.setHost(hostInfo.get("ip"));
			diskStatistic.setUnUsedInfo(unUsedInfo);
			diskStatistic.setUsedInfo(usedInfo);
		}
		this.diskStatisticDao.save(diskStatistic);
		
		log.info("diskStatistic:"+diskStatistic.toString());
		
		List<DiskStatistic> statistic = (List<DiskStatistic>) this.diskStatisticDao.findAll();
		
		log.info("statistics:"+statistic.toString());
		
		return statistic;
		
	}

	private String getUsedInfo(String key, Map<String, String> result, Map<String, String> hostInfo) {
		StringBuffer info = new StringBuffer();
		info.append("");
		if(key.equals("AVIALABLE")){
			info.append(result.get("unUsedInfo"));
		}else{
			info.append(result.get("usedInfo"));
		}
		result.putAll(hostInfo);
		return info.toString();
	}

//	文件系统							容量		已用		可用		已用%%	挂载点	
//	/dev/mapper/VolGroup-lv_root	50G		43G		4.0G	92%		/	
//	tmpfs							3.9G	276K	3.9G	1%		/dev/shm	
//	/dev/sda1						485M	37M		423M	8%		/boot	
//	/dev/mapper/VolGroup-lv_home	42G		2.3G	37G		6%		/home
	
	private Map<String, String> analysisResult(String diskStatistic) {
		
		Map<Integer, String> head = new HashMap<>();
		List<Map<Integer, String>> list = new ArrayList<>();
		log.info("diskStatistic:\n"+diskStatistic);
		String tmp[] = diskStatistic.split("\n");
		int flag = 0;
		for(String str : tmp){
			if(str.trim()!=""){
				str = str.trim();
				int count = 0;
				Map<Integer, String> map = new HashMap<>();
				String line[] = str.split(" ");
				for(String node : line){
					if(node.trim().length()==0)
						continue;
					++count;
					if(flag==0){
						head.put(count, node);
					}else
						map.put(count, node);
				}
				if(flag!=0 && !map.isEmpty()){
					list.add(map);
				}
				++flag;
			}
		}
		
		return this.dealStatistic(list,head);
	}

	private Map<String, String> dealStatistic(List<Map<Integer, String>> list, Map<Integer, String> head) {
		Map<String, String> statistic = new HashMap<>();
		List<Map<Integer, String>> result = new ArrayList<>();
		
		for(Map<Integer, String> map : list){
			Map<Integer, String> res = new HashMap<>();
			for(int node : head.keySet()){
				res.put(node, map.get(node));
			}
			if(!res.isEmpty()){
				result.add(res);
			}
		}
		
		StringBuffer usedInfo = new StringBuffer("<table id='usedInfo'>");
		StringBuffer unUsedInfo = new StringBuffer("<table id='unUsedInfo'>");
		for(Map<Integer, String> map : result){
			usedInfo.append("<tr><td>"+map.get(6)+"</td><td>"+map.get(3)+"<td></tr>");
			unUsedInfo.append("<tr><td>"+map.get(6)+"</td><td>"+map.get(4)+"<td></tr>");
		}
		usedInfo.append("</table>");
		unUsedInfo.append("</table>");
		
		statistic.put("usedInfo", usedInfo.toString());
		statistic.put("unUsedInfo", unUsedInfo.toString());
		return statistic;
	}
	
	public static void main(String args[]){
		DiskStatisticService diskStatisticService = new DiskStatisticService();
		diskStatisticService.updateAndGetFromServer();
	}

	
	@SuppressWarnings("deprecation")
	public long getDiskStatisticFrequency() {
		String sql = "select tsc.value from T_SETTINGS_CONFIG tsc where tsc.`type`='FREQUENCY' and tsc.code = 'STATISTICAL_DISK' and tsc.del = 0 ";
		Long res = this.jdbcTpl.queryForLong(sql);
		if(null==res || res == 0){
			return 60;
		}else{
			return res;
		}
	}

}
