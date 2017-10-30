
    /**  
     * @Description: 
     * @author jingjian.wu
     * @date 2015年8月25日 下午5:25:52
     */
    
package org.zywx.cooldev.system;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


    /**
 * @Description: 
 * @author jingjian.wu
 * @date 2015年8月25日 下午5:25:52
 *
 */

public class CustomMappingJacksonJsonView extends MappingJackson2JsonView{

	@Override  
    protected Object filterModel(Map<String, Object> model) {  
        Map<?, ?> result = (Map<?, ?>) super.filterModel(model);  
        if (result.size() == 1) {  
            return result.values().iterator().next();  
        } else {  
            return result;  
        }  
    }
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String loginUserIdStr = request.getHeader("loginUserId");
//		long loginUserId = Long.parseLong(loginUserId);
		
		Object message = model.get("message");
		Object status = model.get("status");
		Object total = model.get("total");
		Object category = model.get("category");
		Object position = model.get("position");
		String method = request.getMethod();
		model.clear();
		
		if ("GET".equals(method.toUpperCase()) && model.get("status") != null
				&& String.valueOf(model.get("status")).equals(
						"success")) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("role1", 1);
			map.put("role2", 1);
			map.put("role3", 1);
			map.put("role4", 1);
			map.put("role5", 1);
			model.put("permissions", map);
		}
		model.put("message", message);
		model.put("status", status);
		if(total!=null){
			model.put("total", total);
		}
		if(category!=null){
			model.put("category", category);
		}
		if (position != null) {
			model.put("position", position);
		}
		//还是再外层返回permission  然后遍历 authService.getPermissionList()返回就可以
		super.renderMergedOutputModel(model, request, response);
	}
}
