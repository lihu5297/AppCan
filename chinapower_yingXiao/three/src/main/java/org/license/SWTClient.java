package org.license;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class SWTClient extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new SWTClient();
	}

	private JLabel lableType;
	private JLabel lable00;
	private JLabel lable0;
	private JLabel lable1;
	private JLabel lable2;
	private JLabel lable22;
	private JLabel lable8;
	private JLabel lable88;
	private JTextField tf_date;
	private JTextField tf_appcount;
	private JTextField tf_version;
	private JTextField tf_devcount;
	private JTextField tf_info;
	private JTextField tf_entcount;

	private JLabel timeLable;
	private JTextField tf_beginDate;

	private JLabel lable33;
	private JLabel lable3;
	private JLabel lable4;
	private JLabel lable5;
	private JLabel lable6;
	private JLabel lableEntCount;
	private JTextField tf_ip;
	private JTextField tf_mac;
	private JTextField tf_code;

	private JComboBox jb_licenseType;

	private JComboBox jb_productname;

	private JButton bt1;
	private JButton bt2;
	private JButton bt3;
	private JButton bt4;
	private JButton bt5;
//	private JButton bt6;
	private JTextArea tarea;

	private JFileChooser fileChooser;

	public void createLicense() {

		String date = tf_date.getText();
		String beginDate = tf_beginDate.getText();
		String appcount = tf_appcount.getText();
		String productversion = tf_version.getText();
		String devcount = tf_devcount.getText();
		String info = tf_info.getText();
		String entcount = tf_entcount.getText();

		String ip = tf_ip.getText();
		String mac = tf_mac.getText();
		// String code = tf_code.getText();

		String productName = jb_productname.getSelectedItem().toString();

		String licenseType = jb_licenseType.getSelectedItem().toString();

		if (productversion == null || productversion.length() == 0) {
			showErrorMsg("product version can not null");
			return;
		}
		if (date == null || date.length() == 0 || date.equals("-1")) {
			date = "9999-12-20";
		}

		if (beginDate == null || beginDate.length() == 0
				|| beginDate.equals("-1")) {
			showErrorMsg("起始日期不能为空！");
			return;
		}

		if (appcount == null || appcount.length() == 0) {
			appcount = "-1";
		}
		if (devcount == null || devcount.length() == 0) {
			devcount = "-1";
		}
		if (info == null || info.length() == 0) {
			info = "The test group";
		}
		if (entcount == null || entcount.length() == 0) {
			entcount = "-1";
		}

		if (licenseType.equals("正式版")) {
			if (ip == null || ip.length() == 0) {
				showErrorMsg("ip can not null");
				return;
			}
			if (mac == null || mac.length() == 0) {
				showErrorMsg("mac can not null");
				return;
			}
		}

		productversion = productversion.trim();
		ip = ip.trim();
		mac = mac.trim();
		// code = code.trim();
		date = date.trim();
		beginDate = beginDate.trim();
		appcount = appcount.trim();
		devcount = devcount.trim();
		info = info.trim();
		entcount = entcount.trim();

		if (licenseType.equals("测试版")) {
			ip = "255.255.255.255";
			mac = "FF:FF:FF:FF:FF:FF";
			// code = "";
		}

		String checkInfo = CommonTools.checkInfo(ip, mac, beginDate, date,
				appcount, devcount, info, entcount);

		if (checkInfo != null && checkInfo.length() > 0) {
			showErrorMsg(checkInfo);
			return;
		}

		License license = new License(beginDate, date, productversion,
				appcount, devcount, info, entcount);

		// String keyMD5 = LicenseCreator.getKeyMD5(ip, mac, code,productName);
		String keyMD5 = LicenseCreator.getKeyMD5(ip, mac, productName);

		log.info("ip: " + ip + "; mac: " + mac + "; code:---"
				+ "; productName: " + productName + "; keyMD5: " + keyMD5);

		String licenseStr = LicenseCreator.createLicense(license, keyMD5);
		tarea.setText(licenseStr);
		log.info("license:" + LicenseCreator.decLicense(licenseStr, keyMD5));
	}

	public void showErrorMsg(String errorMsg) {
		log.info("error: " + errorMsg);
		JOptionPane.showMessageDialog(tarea, errorMsg, "error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void initDefault() {

		tf_beginDate.setText(CommonTools.getCurDate());
		tf_date.setText(CommonTools.getCurDate());
		tf_version.setText("3.0");
		tf_appcount.setText("100");
		tf_devcount.setText("10000");
		tf_info.setText("AppCan平台授权");

		// tf_ip.setText(CommonTools.getCurIpAddr());
		// tf_mac.setText(CommonTools.getCurMacAddr());

		tf_ip.setText("");
		tf_mac.setText("");
		tf_code.setText("");

		jb_licenseType.setSelectedIndex(0);
		jb_productname.setSelectedIndex(0);

		tarea.setText("");
		tarea.setBackground(Color.WHITE);
		bt1.setEnabled(true);
	}

	public void reset() {
		initDefault();
		log.info("click reset button!");
	}

	public void copy() {
		String license = tarea.getText();
		if (license != null && license.length() > 0) {
			StringSelection stsel = new StringSelection(license);
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(stsel, stsel);
		} else {
			showErrorMsg("license is null");
		}
	}

	/**
	 * 导入信息,获取info内容
	 * 
	 * @author yang.lu
	 */
	public String loadFile() {
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("请选择要导入的文件");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		String RC4info = "";
		int intRetVal = fileChooser.showOpenDialog(null);
		if (intRetVal == JFileChooser.APPROVE_OPTION) {
			// 获取到文件路径
			String localFile = fileChooser.getSelectedFile().toString();
			// 读取文件信息
			RC4info = readFile(localFile);
			if ("".equals(RC4info) || null == RC4info) {
				showErrorMsg("file is null");
				return "";
			}
		}
		return RC4info;
	}

	/**
	 * 加密更新文件
	 */
	public void encryptFile() {
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("请选择要加密的更新文件");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		int intRetVal = fileChooser.showOpenDialog(null);
		if (intRetVal == JFileChooser.APPROVE_OPTION) {
			// 获取到文件路径
			String localFile = fileChooser.getSelectedFile().toString();
			log.info("res path------" + localFile);
			// 进行加密
			String key = "3g2winçè·¨å¹³å°!@#";
			try {
				FileTools.encrypt(localFile, key);
				showErrorMsg("加密完成！");
			} catch (Exception e) {
				log.info("res encry error!!!" + e);
				showErrorMsg("加密失败！");
			}
		}
	}

	public void setInfo(String info) {
		if (info != null && info.length() > 0) {
			String[] infos = info.split("@");
			if (infos.length == 3) {
				tf_ip.setText(infos[0]);
				tf_mac.setText(infos[1]);
				tf_code.setText(infos[2]);
			} else if (infos.length == 2) {
				tf_ip.setText(infos[0]);
				tf_mac.setText(infos[1]);
			} else {
				showErrorMsg("product type is error");
			}

		}
	}

	public String readFile(String filePath) {
		String info = "";
		Reader reader = null;
		BufferedReader br = null;
		InputStream paramInputStream = null;
		try {
			paramInputStream = new FileInputStream(filePath);
			reader = new InputStreamReader(paramInputStream, "GBK");
			br = new BufferedReader(reader);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			info = sb.toString();
		} catch (Exception e) {
			return "";
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
				if (paramInputStream != null) {
					paramInputStream.close();
				}
			} catch (IOException e) {
				return "";
			}
		}

		return info;
	}

	public void importFile() {
		String license = tarea.getText();
		if (license != null && license.length() > 0) {
			fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("选择导出文件路径");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);

			int intRetVal = fileChooser.showOpenDialog(null);
			if (intRetVal == JFileChooser.APPROVE_OPTION) {
				String localFile = fileChooser.getSelectedFile().toString();
				log.info(localFile + "; license:" + license);
				writeFile(localFile, license);
			} else {
				showErrorMsg("no selection path");
			}
		} else {
			showErrorMsg("license is null");
		}
	}

	public void writeFile(String path, String data) {
		File f = new File(path + "\\STMLicense.dat");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f, false), "UTF-8"));
			output.write(data);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// record log
	public Logger log = Logger.getLogger("xxx");

	public void setLogingProperties() {
		FileHandler fh = null;
		try {
			if (fh == null) {
				fh = new FileHandler(System.getProperty("user.dir")
						+ "\\license.log", true);
			}
			log.addHandler(fh);// export log file
			fh.setFormatter(new SimpleFormatter());// export format
		} catch (SecurityException e) {
			log.log(Level.SEVERE, "security error", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "read log file eroor", e);
		}
	}

	// 图形化界面
	public void actionPerformed(ActionEvent e) {
	}

	public SWTClient() {
		// add log
		setLogingProperties();
		// close window
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		int x = 10; // 初始x
		int y = 10; // 初始y
		int lines = 5; // 行间距
		int lineh = 25; // 行高
		int lbw = 100; // 标签宽度
		int tfw = 190; // 输入框宽度
		int jbw = 90; // 多选框宽度

		// use setBounds() config location
		this.setLayout(null);

		// 加密key
		lable00 = new JLabel("加密key--------");
		this.add(lable00);
		lable00.setBounds(x, y, 300, lineh);

		// 类别license
		lableType = new JLabel("类别：");
		this.add(lableType);
		String[] type = { "正式版", "测试版" };
		jb_licenseType = new JComboBox(type);
		this.add(jb_licenseType);
		lableType.setBounds(x, y + (lineh + lines) * 2, lbw, lineh);
		jb_licenseType.setBounds(x + lbw, y + (lineh + lines) * 2, tfw, lineh);

		jb_licenseType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String jb_item = jb_licenseType.getSelectedItem().toString();
				if ("正式版".equals(jb_item)) {
					tf_date.setText("-1");
				} else {
					tf_date.setText(CommonTools.getLastMonth());
				}
				log.info("jb_licenseType: " + jb_item);
			}
		});

		lable0 = new JLabel("服务器IP：");
		this.add(lable0);
		tf_ip = new JTextField(18);
		this.add(tf_ip);
		lable0.setBounds(x, y + (lineh + lines) * 3, lbw, lineh);
		tf_ip.setBounds(x + lbw, y + (lineh + lines) * 3, tfw, lineh);

		lable1 = new JLabel("服务器MAC：");
		this.add(lable1);
		tf_mac = new JTextField(18);
		this.add(tf_mac);
		lable1.setBounds(x, y + (lineh + lines) * 4, lbw, lineh);
		tf_mac.setBounds(x + lbw, y + (lineh + lines) * 4, tfw, lineh);

		lable8 = new JLabel("服务器序列号：");
		this.add(lable8);
		tf_code = new JTextField(18);
		this.add(tf_code);
		lable8.setBounds(x, y + (lineh + lines) * 5, lbw, lineh);
		tf_code.setBounds(x + lbw, y + (lineh + lines) * 5, tfw, lineh);

		lable2 = new JLabel("产品名称：");
		this.add(lable2);
		String[] pname = { "EMM", "MAM", "SDK", "MAS", "MCM", "MDM", "MMS",
				"OMM" ,"MEM","MBAAS","APPIN"};
		jb_productname = new JComboBox(pname);
		this.add(jb_productname);
		lable2.setBounds(x, y + (lineh + lines) * 1, lbw, lineh);
		jb_productname.setBounds(x + lbw, y + (lineh + lines) * 1, jbw, lineh);
		jb_productname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String jb_item = jb_productname.getSelectedItem().toString();
				log.info("productname: " + jb_item);
			}
		});

		// 加密license
		lable33 = new JLabel("加密license--------");
		this.add(lable33);
		lable33.setBounds(x, y + (lineh + lines) * 6, 300, lineh);

		lable22 = new JLabel("产品版本：");
		this.add(lable22);
		tf_version = new JTextField(18);
		this.add(tf_version);
		lable22.setBounds(x, y + (lineh + lines) * 7, lbw, lineh);
		tf_version.setBounds(x + lbw, y + (lineh + lines) * 7, tfw, lineh);

		timeLable = new JLabel("起始日期：");
		this.add(timeLable);
		tf_beginDate = new JTextField(18);
		this.add(tf_beginDate);
		timeLable.setBounds(x, y + (lineh + lines) * 9, lbw, lineh);
		tf_beginDate.setBounds(x + lbw, y + (lineh + lines) * 9, tfw, lineh);

		lable3 = new JLabel("失效日期：");
		this.add(lable3);
		tf_date = new JTextField(18);
		this.add(tf_date);
		lable3.setBounds(x, y + (lineh + lines) * 10, lbw, lineh);
		tf_date.setBounds(x + lbw, y + (lineh + lines) * 10, tfw, lineh);

		lable4 = new JLabel("应用最大数：");
		this.add(lable4);
		tf_appcount = new JTextField(18);
		this.add(tf_appcount);
		lable4.setBounds(x, y + (lineh + lines) * 11, lbw, lineh);
		tf_appcount.setBounds(x + lbw, y + (lineh + lines) * 11, tfw, lineh);

		lable5 = new JLabel("终端最大数：");
		this.add(lable5);
		tf_devcount = new JTextField(18);
		this.add(tf_devcount);
		lable5.setBounds(x, y + (lineh + lines) * 12, lbw, lineh);
		tf_devcount.setBounds(x + lbw, y + (lineh + lines) * 12, tfw, lineh);

		lable6 = new JLabel("授权信息：");
		this.add(lable6);
		tf_info = new JTextField(18);
		this.add(tf_info);
		lable6.setBounds(x, y + (lineh + lines) * 13, lbw, lineh);
		tf_info.setBounds(x + lbw, y + (lineh + lines) * 13, tfw, lineh);

		lableEntCount = new JLabel("企业最大数：");
		this.add(lableEntCount);
		tf_entcount = new JTextField(18);
		this.add(tf_entcount);
		lableEntCount.setBounds(x, y + (lineh + lines) * 14, lbw, lineh);
		tf_entcount.setBounds(x + lbw, y + (lineh + lines) * 14, tfw, lineh);

		bt1 = new JButton("生成");
		bt2 = new JButton("重置");
		bt3 = new JButton("导出");
		bt4 = new JButton("复制");
		bt5 = new JButton("导入信息");
//		bt6 = new JButton("加密更新包");
		this.add(bt1);
		this.add(bt2);
		this.add(bt3);
		this.add(bt4);
		this.add(bt5);
//		this.add(bt6);
		bt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("click createLicense()");
				createLicense();
			}
		});
		bt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("click reset()");
				reset();
			}
		});
		bt3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("click importFile()");
				importFile();
			}
		});
		bt4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("click copy()");
				copy();
			}
		});
		bt5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String RC4info = loadFile();
				log.info("RC4 info ==============>" + RC4info);
				// 解密info
				String productname = jb_productname.getSelectedItem()
						.toString();// 获取产品名称
				log.info("product info ==============>" + productname);
				try {
					String info = CommonTools.getInfo(RC4info, productname);
					log.info("info =================>" + info);
					setInfo(info);// 自动获取ip mac code
				} catch (Exception e2) {
					// TODO: handle exception
					showErrorMsg("请导入正确的info文件！");
				}

			}
		});

//		bt6.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
//				try {
//					encryptFile();
//				} catch (Exception e2) {
//					// TODO: handle exception
//					showErrorMsg("请导入正确的资源文件！");
//				}
//
//			}
//		});
		tarea = new JTextArea(6, 16);
		tarea.setEditable(false);
		// tarea.setEnabled(false);
		tarea.setLineWrap(true);// 激活自动换行功能
		tarea.setWrapStyleWord(true);// 激活断行不断字功能
		this.add(tarea);
		bt1.setBounds(x, y + (lineh + lines) * 15, lbw, lineh);
		bt2.setBounds(x, y + (lineh + lines) * 16, lbw, lineh);
		bt3.setBounds(x, y + (lineh + lines) * 17, lbw, lineh);
		bt4.setBounds(x, y + (lineh + lines) * 18, lbw, lineh);
		bt5.setBounds(x + lineh * 12, y + (lineh + lines) * 2, lbw, lineh);
//		bt6.setBounds(x + lineh * 16, y + (lineh + lines) * 2, lbw, lineh);
		tarea.setBounds(x + lbw, y + (lineh + lines) * 15, tfw + 150, 115);

		initDefault();

		this.setBounds(400, 100, 500, 620);
		this.setTitle("AppCan-License-Creator");
		this.setVisible(true);
		this.setResizable(false);
	}
}
