package org.zywx.cooldev.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.util.qr.MatrixToImageWriter;


/**
 * 二维码
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/qr")
public class QrCodeController extends BaseController {
	
	@Value("${qrCodePrefix}")
	private String qrCodePrefix;
	
	/**
	 * img标签的src属性指向这个地址就可以生成二维码
	 * @param data 二维码数据
	 * 
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public void generateImg(String data,HttpServletResponse response) {
		try {
				MatrixToImageWriter.writeToResponse(qrCodePrefix+data, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
