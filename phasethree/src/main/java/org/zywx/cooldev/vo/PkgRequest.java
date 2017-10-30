package org.zywx.cooldev.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分平台的具体打包请求
 * 
 * Author: chenggang.du
 * $Revision$
 */
public class PkgRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// iphone横竖屏可取的值
	private static String[] UIInterfaceOrientationValue = {
        "UIInterfaceOrientationPortrait",   //竖屏
        "UIInterfaceOrientationPortraitUpsideDown", //
        "UIInterfaceOrientationLandscapeLeft",  //横屏
        "UIInterfaceOrientationLandscapeRight"  //
        };

	private static String[] DeviceFamilyValue = {
	    "iphone",
	    "pad",
	    "iphone/pad"
	};
	
    private String taskId;  //打包请求的任务id
	
    private String appId;
    private String appKey;
	private String appName;
	private String appVersion;
    private String channelCode; //应用的渠道编号

	/*
	 * 以下图片信息打包管理器确保已存在，不为空
	 */
    private String absIconLoc; //应用的icon, 打包完成可删除

    private String absIphone3ImgLoc;  //iphone 
    private String absIphone4ImgLoc;
    private String absIphone5ImgLoc;

    private String absIpadLandscapeLeftImg1024Loc; // ipad
    private String absIpadLandscapeLeftImg2048Loc;
    private String absIpadPortraitImg1024Loc;
    private String absIpadPortraitImg2048Loc;

    private String absStartupFrontImg240Loc; // android
    private String absStartupFrontImg480Loc;
    private String absStartupBgImgLoc;
    private String absStartupWaterImgLoc;
    private String absStartupBgColor;

    private boolean useBgImg;   //使用背景图片还是背景颜色
    private String absWidgetDir;    // widget源文件的绝对目录 
    private String absPkgFileName;  //打包生成的安装文件的文件名,绝对路径
    private String platform;    //打包平台
    //打包平台细分
    private String deviceFamily = DeviceFamilyValue[0]; 
    
    private String wgOneVersion;    //widgetOne版本号
    
    private String absSignatureFileLoc;    //iphone签名文件位置
    private String absCertFileLoc;     //iphone证书文件位置
    private String certFilePasswd;  //iphone证书密码
    private String iphoneAppIds;    //iphone打包用属性

    private String absTestSignatureFileLoc;  //签名文件地址
    private String absTestCertFileLoc;  //认证文件地址
    private String testCertFilePasswd; //密码
    private String testCertFileId;  //iphone编译上传的认证文件Id
    
    /*
     * 证书类型
     *  
     * 发布：publishCert   - > 发布 -> absSignatureFileLoc
     * 开发：developeCert  - > 企业 -> absTestSignatureFileLoc
     * 越狱：noCert
     */
    private String  typeCert; 
    
    private boolean showStatusBar = true;  //是否显示状态栏
    
    //手机横竖屏启动  
    private String uiOrientation = UIInterfaceOrientationValue[0];     
    
    private List<String>  pluginNameList;    //用户选择的插件的列表

    private long sequenceId;    //识别队列信息的顺序id
    
    private String pkgAccessUrl;      //打包成功时，具体的应用安装包的下载地址
    
    /**  
     * @Fields pkgExtraCtrlParams 
     * 可能会附件的其他控制参数，可通过此字符串传递 
     */
    private String pkgExtraCtrlParams;

    private Date createdAt;

    public String getPkgAccessUrl() {
        return pkgAccessUrl;
    }

    public void setPkgAccessUrl(String pkgAccessUrl) {
        this.pkgAccessUrl = pkgAccessUrl;
    }

    public String getWgOneVersion() {
        return wgOneVersion;
    }

    public void setWgOneVersion(String wgOneVersion) {
        this.wgOneVersion = wgOneVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAbsWidgetDir() {
        return absWidgetDir;
    }

    public void setAbsWidgetDir(String absWidgetDir) {
        this.absWidgetDir = absWidgetDir;
    }

    public String getAbsPkgFileName() {
        return absPkgFileName;
    }

    public void setAbsPkgFileName(String absPkgFileName) {
        this.absPkgFileName = absPkgFileName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isUseBgImg() {
        return useBgImg;
    }

    public void setUseBgImg(boolean useBgImg) {
        this.useBgImg = useBgImg;
    }

    public String getAbsIconLoc() {
        return absIconLoc;
    }

    public void setAbsIconLoc(String absIconLoc) {
        this.absIconLoc = absIconLoc;
    }

    public String getAbsStartupFrontImg480Loc() {
        return absStartupFrontImg480Loc;
    }

    public void setAbsStartupFrontImg480Loc(String absStartupFrontImg480Loc) {
        this.absStartupFrontImg480Loc = absStartupFrontImg480Loc;
    }

    public String getAbsStartupBgImgLoc() {
        return absStartupBgImgLoc;
    }

    public void setAbsStartupBgImgLoc(String absStartupBgImgLoc) {
        this.absStartupBgImgLoc = absStartupBgImgLoc;
    }

    public String getAbsStartupBgColor() {
        return absStartupBgColor;
    }

    public void setAbsStartupBgColor(String absStartupBgColor) {
        this.absStartupBgColor = absStartupBgColor;
    }

    public String getAbsStartupWaterImgLoc() {
        return absStartupWaterImgLoc;
    }

    public void setAbsStartupWaterImgLoc(String absStartupWaterImgLoc) {
        this.absStartupWaterImgLoc = absStartupWaterImgLoc;
    }

    public String getAbsSignatureFileLoc() {
        return absSignatureFileLoc;
    }

    public void setAbsSignatureFileLoc(String absSignatureFileLoc) {
        this.absSignatureFileLoc = absSignatureFileLoc;
    }

    public String getAbsCertFileLoc() {
        return absCertFileLoc;
    }

    public void setAbsCertFileLoc(String absCertFileLoc) {
        this.absCertFileLoc = absCertFileLoc;
    }

    public String getCertFilePasswd() {
        return certFilePasswd;
    }

    public void setCertFilePasswd(String certFilePasswd) {
        this.certFilePasswd = certFilePasswd;
    }

    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }

    public String getIphoneAppIds() {
        return iphoneAppIds;
    }

    public void setIphoneAppIds(String iphoneAppIds) {
        this.iphoneAppIds = iphoneAppIds;
    }

    public String getDeviceFamily() {
        return deviceFamily;
    }

    public void setDeviceFamily(String deviceFamily) {
        for (int i=0; i<DeviceFamilyValue.length; i++) {
            if (DeviceFamilyValue[i].equals(deviceFamily)) {
                this.deviceFamily = deviceFamily;
                break;
            }
        }
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public List<String> getPluginNameList() {
        return pluginNameList;
    }

    public void setPluginNameList(List<String> pluginNameList) {
        this.pluginNameList = pluginNameList;
    }

    public String getUiOrientation() {
        return uiOrientation;
    }

    public void setUiOrientation(String uiOrientation) {
        for (int i=0; i<UIInterfaceOrientationValue.length; i++) {
            if (UIInterfaceOrientationValue[i].equals(uiOrientation)) {
                this.uiOrientation = uiOrientation;
                break;
            }
        }
    }

    public String getAbsIphone3ImgLoc() {
        return absIphone3ImgLoc;
    }

    public void setAbsIphone3ImgLoc(String absIphone3ImgLoc) {
        this.absIphone3ImgLoc = absIphone3ImgLoc;
    }

    public String getAbsIphone4ImgLoc() {
        return absIphone4ImgLoc;
    }

    public void setAbsIphone4ImgLoc(String absIphone4ImgLoc) {
        this.absIphone4ImgLoc = absIphone4ImgLoc;
    }

    public String getAbsIphone5ImgLoc() {
        return absIphone5ImgLoc;
    }

    public void setAbsIphone5ImgLoc(String absIphone5ImgLoc) {
        this.absIphone5ImgLoc = absIphone5ImgLoc;
    }

    public String getAbsIpadLandscapeLeftImg1024Loc() {
        return absIpadLandscapeLeftImg1024Loc;
    }

    public void setAbsIpadLandscapeLeftImg1024Loc(
            String absIpadLandscapeLeftImg1024Loc) {
        this.absIpadLandscapeLeftImg1024Loc = absIpadLandscapeLeftImg1024Loc;
    }

    public String getAbsIpadLandscapeLeftImg2048Loc() {
        return absIpadLandscapeLeftImg2048Loc;
    }

    public void setAbsIpadLandscapeLeftImg2048Loc(
            String absIpadLandscapeLeftImg2048Loc) {
        this.absIpadLandscapeLeftImg2048Loc = absIpadLandscapeLeftImg2048Loc;
    }

    public String getAbsIpadPortraitImg1024Loc() {
        return absIpadPortraitImg1024Loc;
    }

    public void setAbsIpadPortraitImg1024Loc(String absIpadPortraitImg1024Loc) {
        this.absIpadPortraitImg1024Loc = absIpadPortraitImg1024Loc;
    }

    public String getAbsIpadPortraitImg2048Loc() {
        return absIpadPortraitImg2048Loc;
    }

    public void setAbsIpadPortraitImg2048Loc(String absIpadPortraitImg2048Loc) {
        this.absIpadPortraitImg2048Loc = absIpadPortraitImg2048Loc;
    }

    public String getAbsStartupFrontImg240Loc() {
        return absStartupFrontImg240Loc;
    }

    public void setAbsStartupFrontImg240Loc(String absStartupFrontImg240Loc) {
        this.absStartupFrontImg240Loc = absStartupFrontImg240Loc;
    }

    public String getAbsTestSignatureFileLoc() {
        return absTestSignatureFileLoc;
    }

    public void setAbsTestSignatureFileLoc(String absTestSignatureFileLoc) {
        this.absTestSignatureFileLoc = absTestSignatureFileLoc;
    }

    public String getAbsTestCertFileLoc() {
        return absTestCertFileLoc;
    }

    public void setAbsTestCertFileLoc(String absTestCertFileLoc) {
        this.absTestCertFileLoc = absTestCertFileLoc;
    }

    public String getTestCertFilePasswd() {
        return testCertFilePasswd;
    }

    public void setTestCertFilePasswd(String testCertFilePasswd) {
        this.testCertFilePasswd = testCertFilePasswd;
    }

    public String getTestCertFileId() {
        return testCertFileId;
    }

    public void setTestCertFileId(String testCertFileId) {
        this.testCertFileId = testCertFileId;
    }

    public String getTypeCert() {
        return typeCert;
    }

    public void setTypeCert(String typeCert) {
        this.typeCert = typeCert;
    }

    public String getPkgExtraCtrlParams() {
        return pkgExtraCtrlParams;
    }

    public void setPkgExtraCtrlParams(String pkgExtraCtrlParams) {
        this.pkgExtraCtrlParams = pkgExtraCtrlParams;
    }

   
}
