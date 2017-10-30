package org.zywx.cooldev.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.dao.app.AppDao;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.util.RelativeDateFormat;
import org.zywx.cooldev.vo.DiffItem;

/**
 * WebIDE 相关服务
 * 
 * @author yang.li
 * @date 2016-01-26
 *
 */
@Service
public class WebIDEService extends BaseService {
	

	@Autowired
	private AppDao appDao;

	/**
	 * 用户个人仓库根路径<br>
	 * ${webide.personalGitRoot}/userId/xxxxxx.git
	 */
	@Value("${webide.personalGitRoot}")
	private String personalGitRoot;

	@Value("${git.remoteGitRoot}")
	private String remoteGitRoot;
	
	@Value("${shellPath}")
	private String shellPath;
	
	@Value("${gitFactoryAccount}")
    private String gitFactoryAccount;
	
	@Value("${gitFactoryPassword}")
    private String gitFactoryPassword;
	
	@Value("${git.localGitRoot}")
	private String localGitRoot;
	/**
	 * 仓库浏览
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 * @throws GitAPIException 
	 * @throws IOException 
	 */
	public Map<String, Object> browseRepo(long appId, long loginUserId, String relativePath) throws RuntimeException, IOException, GitAPIException {
		
		this.gitClone(appId, loginUserId);
		
		String absDirPath = this.getAbsPath(appId, loginUserId, relativePath);

		File dir  = new File(absDirPath);

		if(!dir.exists()) {
			throw new RuntimeException("文件夹不存在");
		}
		
		if(!dir.isDirectory()) {
			throw new RuntimeException("不是文件夹");
		}
		
		// 获取未提交的文件列表（及状态）
		Map<String, Object> statusObj = this.gitStatus(appId, loginUserId);
		Map<String, String> statusMatchMap = new HashMap<>();
		Iterator<String> keyIterator = statusObj.keySet().iterator();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next();

	        if("new".equals(key) || "modified".equals(key) || "untracked".equals(key) || "conflict".equals(key) || "deleted".equals(key) || "renamed".equals(key)) {	
				List<Map<String, Object>> array = (List<Map<String, Object>>)statusObj.get(key);
				for(Map<String, Object> element : array) {
					String path = (String)element.get("fileName");
					statusMatchMap.put(path, key);
				}
	        
	        }
		}
		String currentBranch = (String)statusObj.get("currentBranch");
		
		Map< String, Map<String, Object> > dirCache = new HashMap<>();
		Map< String, Map<String, Object> > fileCache = new HashMap<>();
		
		File[] fileList = dir.listFiles();
		for(File file : fileList) {
			String fileName = file.getName();
			if(fileName.startsWith(".git")) {
				continue;
			}

			Map<String, Object> item = new HashMap<>();
			
			
			// 文件名，判定编码
			String charset = this.getEncoding(fileName);
			if(charset != null && !"".equals(charset) && !"UTF-8".equals(charset)) {
				try {
					fileName = new String(fileName.getBytes("ISO-8859-1"), charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			item.put("fileName", fileName);
			
			item.put("type", file.isDirectory() ? "DIRECTORY" : "FILE");
			item.put("lastModified", RelativeDateFormat.format(new Date(file.lastModified())));
			
			if("/".equals(relativePath)) {
				item.put("relativePath", "/" + file.getName());
			} else {
				item.put("relativePath", relativePath + "/" + file.getName());
			}
			
			if( file.isFile() ) {
				// 处理文件
				item.put("fileSize", file.length());
				String status = statusMatchMap.get( item.get("relativePath") );
				log.info(String.format("browseRepo relativePath[%s] status[%s]", item.get("relativePath"), status));
				item.put("status", status == null ? "commited" : status);
				fileCache.put((String)item.get("relativePath"), item);

			} else if(file.isDirectory()) {
				// 处理文件夹
				String[] subList = file.list();
				int childrenTotal = 0;
				if(subList != null) {
					childrenTotal = subList.length;
				}
				
				File gitKeep = new File(file.getAbsolutePath() + "/.gitkeep");
				if(gitKeep.exists()) {
					childrenTotal--;
				}
 
				item.put("childrenTotal", childrenTotal);
				String status = null;
				Iterator<String> it = statusMatchMap.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					String speciRelativePath = (String)item.get("relativePath");

					log.info("brwoseRepo dir status -> dir path[" + speciRelativePath + "] file[" + key + "]");
					
					if(key.startsWith(speciRelativePath) ) {
						status = this.getMaxPriotiryStatus(statusMatchMap.get(key), status);
					}
				}
				if(status == null) {
					status = "commited";
				}
				item.put("status", status == null ? "commited" : status);
				dirCache.put((String)item.get("relativePath"), item);
			}
			
		}
		
		Map<String, Object> message = new HashMap<>();
		
		// 排序

		String[] dirKeyList  = new String[dirCache.size()];
		String[] fileKeyList = new String[fileCache.size()];
		dirCache.keySet().toArray(dirKeyList);
		fileCache.keySet().toArray(fileKeyList);
		
		Arrays.sort(dirKeyList);
		Arrays.sort(fileKeyList);
		
		ArrayList<Map<String, Object>> sorted = new ArrayList<>();
		for(String key : dirKeyList) {
			sorted.add(dirCache.get(key));
		}
		for(String key : fileKeyList) {
			sorted.add(fileCache.get(key));
		}
		
		App app=appDao.findOne(appId);
		message.put("object", sorted);
		message.put("currentBranch", currentBranch);
		message.put("appName",app.getName());
		
		return message;
	}

	private String getMaxPriotiryStatus(String match, String current) {
		if( current == null || current.equals("") ) {
			return match;
		}

		// 变更文件夹状态，参照优先级 conflict -> modified -> new -> deleted > renamed -> untracked
		if( "conflict".equals(current) ) {
			return current;
		}
		
		if( "modified".equals(current) ) {
			if( "conflict".equals(match) ) {
				return match;
			}
		}
		
		if( "new".equals(current) ) {
			if( "conflict".equals(match) || "modified".equals(match) ) {
				return match;
			}
		}
		
		if( "delete".equals(current) ) {
			if( "conflict".equals(match) || "modified".equals(match) || "new".equals(match)) {
				return match;
			}
		}
		
		if("renamed".equals(current)) {
			if( !match.equals("untracked") ) {
				return match;
			}
		}
		
		if( "untracked".equals(current) ) {
			return match;
		}
		
		return current;
	}
	
	/**
	 * 文件浏览
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 */
	public File fileRepo(long appId, long loginUserId, String relativePath) throws RuntimeException {
		
		String absDirPath = this.getAbsPath(appId, loginUserId, relativePath);

		File file  = new File(absDirPath);

		if(!file.exists()) {
			throw new RuntimeException("文件不存在");
		}
		
		if(!file.isFile()) {
			throw new RuntimeException("不是文件");
		}

		return file;
		
	}

	/**
	 * 保存文件
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @param content
	 * @return
	 */
	public boolean storeFile(long appId, long loginUserId, String relativePath, String content) {
		String absDirPath = this.getAbsPath(appId, loginUserId, relativePath);

		File file  = new File(absDirPath);

		if(!file.exists()) {
			throw new RuntimeException("文件不存在");
		}
		
		if(!file.isFile()) {
			throw new RuntimeException("不是文件");
		}
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(content.getBytes("UTF-8"));
			fos.flush();
			fos.close();
			// 自动进行git add 
			
			App app = appDao.findOne(appId);
			if(app == null) {
				throw new RuntimeException("指定应用不存在");
			}
			
			String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
			
			File repoRoot = new File(repoPath);
			
			if(!repoRoot.exists()) {
				throw new RuntimeException("指定GIT仓库不存在");

			}
			
			if(relativePath.startsWith("/")) {
				relativePath = relativePath.substring(1);
			}
			
			// 变更文件后，自动添加到暂存区
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, relativePath);
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> gitClone cmd[%s] ret[%s]", cmd, ret));
//			Git git = Git.open(dir);
			
			log.info("------->come into new save file method");
			Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
			Git git = new Git(repo);
			AddCommand ac = git.add();
			ac.addFilepattern(".");
			ac.call();
			return true;
		} catch(Exception e) {
			throw new RuntimeException("保存异常:" + e.getMessage());
		}
	}
	
	/**
	 * 创建文件夹
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 * @throws GitAPIException 
	 * @throws NoFilepatternException 
	 */
 	public boolean makeDirectory(long appId, long loginUserId, String relativePath) throws RuntimeException, NoFilepatternException, GitAPIException {
		
		String absDirPath = this.getAbsPath(appId, loginUserId, relativePath);
		
		File dir  = new File(absDirPath);

		if(dir.exists()) {
			throw new RuntimeException("无法创建目录，文件已存在");
		}
		
		boolean created = dir.mkdirs();
		if(created) {
			
			if(absDirPath.endsWith("/")) {
				absDirPath = absDirPath.substring(0, absDirPath.length() - 1);
			}
			
			File keepFile = new File(absDirPath + "/.gitkeep");
			try {
				keepFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("文件夹已创建，但.gitkeep添加失败");
			}
			
			App app = appDao.findOne(appId);
			if(app == null) {
				throw new RuntimeException("指定应用不存在");
			}
			
			String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
			
			File repoRoot = new File(repoPath);
			
			if(!repoRoot.exists()) {
				throw new RuntimeException("指定GIT仓库不存在");

			}
			
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, absDirPath);
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> renameFile -> add previous file cmd[%s] ret[%s]", cmd, ret));	
			log.info("------->come into make dir method");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
					Git git = new Git(repo);){
				AddCommand ac = git.add();
				ac.addFilepattern(".");
				ac.call();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoFilepatternException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return created;

	}
	
	/**
	 * 重命名文件夹
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @param newName
	 * @return
	 * @throws RuntimeException
	 * @throws GitAPIException 
	 * @throws NoFilepatternException 
	 */
	public boolean renameDirectory(long appId, long loginUserId, String relativePath, String newName) throws RuntimeException, NoFilepatternException, GitAPIException {
		
		String absDirPath = this.getAbsPath(appId, loginUserId, relativePath);
		
		File dir  = new File(absDirPath);
		
		if(!dir.isDirectory()) {
			throw new RuntimeException("指定为文件，请选择文件夹");
		}

		if(!dir.exists()) {
			throw new RuntimeException("指定文件夹不存在");
		}
		
		String destDirPath = dir.getParent() + "/" + newName;
		File destDir = new File(destDirPath);
		
		if(destDir.exists()) {
			throw new RuntimeException("无法重命名目录，文件已存在");
		}
		
		boolean success = dir.renameTo(destDir);
		if(!success) {
			return false;
		}
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定GIT仓库不存在");

		}
		
		// 变更文件后，自动添加到暂存区
//		String cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, absDirPath);
//		String ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> renameDirectory -> add previous file cmd[%s] ret[%s]", cmd, ret));		
//		cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, destDirPath);
//		ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> renameDirectory -> add renamed file cmd[%s] ret[%s]", cmd, ret));

		log.info("------->come into new rename file method");
		try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
				Git git = new Git(repo);){
			AddCommand ac = git.add();
			ac.addFilepattern(".");
			ac.call();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}
	
	/**
	 * 删除文件夹
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 */
	public boolean removeDirectory(long appId, long loginUserId, String relativePath) throws RuntimeException {
		
		String absDirPath = this.getAbsPath(appId, loginUserId, relativePath);
		
		File dir  = new File(absDirPath);
		
		if(!dir.isDirectory()) {
			throw new RuntimeException("指定为文件，请选择文件夹");
		}

		if(!dir.exists()) {
			throw new RuntimeException("指定文件夹不存在");
		}

		return this.deleteFile(dir);

	}

	/**
	 * 创建文件
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 * @throws GitAPIException 
	 * @throws NoFilepatternException 
	 */
	public boolean makeFile(long appId, long loginUserId, String relativePath) throws RuntimeException, NoFilepatternException, GitAPIException {
		
		String absFilePath = this.getAbsPath(appId, loginUserId, relativePath);
		
		File file  = new File(absFilePath);

		if(file.exists()) {
			throw new RuntimeException("无法创建目录，文件(夹)已存在");
		}
		
		try {
			boolean created =  file.createNewFile();
			
			App app = appDao.findOne(appId);
			if(app == null) {
				throw new RuntimeException("指定应用不存在");
			}
			
			String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
			
			File repoRoot = new File(repoPath);
			
			if(!repoRoot.exists()) {
				throw new RuntimeException("指定GIT仓库不存在");

			}
			
			// 创建文件后自动添加到暂存区
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, absFilePath );
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> makeFile cmd[%s] ret[%s]", cmd, ret));
			
			log.info("------->come into new mkdir file");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
					Git git = new Git(repo);) {
				AddCommand ac = git.add();
				ac.addFilepattern(".");
				ac.call();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (NoFilepatternException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return created;
			
		} catch (IOException e) {
			throw new RuntimeException("创建文件失败");
		}
	}
	
	/**
	 * 重命名文件
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @param newName
	 * @return
	 * @throws RuntimeException
	 * @throws GitAPIException 
	 * @throws NoFilepatternException 
	 */
	public boolean renameFile(long appId, long loginUserId, String relativePath, String newName) {
		
		String absFilePath = this.getAbsPath(appId, loginUserId, relativePath);
		
		File file  = new File(absFilePath);
		
		if(!file.isFile()) {
			throw new RuntimeException("请选择文件");
		}

		if(!file.exists()) {
			throw new RuntimeException("选定文件不存在");
		}
		
		String destFilePath = file.getParent() + "/" + newName;
		File destFile = new File(destFilePath);
		
		if(destFile.exists()) {
			throw new RuntimeException("无法重命名目录，文件(夹)已存在");
		}
		
		boolean renamed = file.renameTo(destFile);
		
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定GIT仓库不存在");

		}
		
		// 变更文件后，自动添加到暂存区
//		String cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, absFilePath);
//		String ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> renameFile -> add previous file cmd[%s] ret[%s]", cmd, ret));		
//		cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_add.sh %s %s", repoRoot, destFilePath);
//		ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> renameFile -> add renamed file cmd[%s] ret[%s]", cmd, ret));
		
		log.info("------->come into new rename dir method");
		try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
		Git git = new Git(repo);){
			AddCommand ac = git.add();
			ac.addFilepattern(".");
			ac.call();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return renamed;

	}
	
	/**
	 * 删除文件
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 * @throws GitAPIException 
	 * @throws NoFilepatternException 
	 */
	public boolean removeFile(long appId, long loginUserId, String relativePath) throws RuntimeException, NoFilepatternException, GitAPIException {
		
		String absFilePath = this.getAbsPath(appId, loginUserId, relativePath);
		
		File file  = new File(absFilePath);
		
		if(!file.isFile()) {
			throw new RuntimeException("请选择文件");
		}

		if(!file.exists()) {
			throw new RuntimeException("选定文件不存在");
		}

		boolean deleted = this.deleteFile(file);
		
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定GIT仓库不存在");

		}
		// 变更文件后，自动添加到暂存区
//		String cmd = String.format("sh " + shellPath + "coopdev_git/webide_file_del.sh %s %s", repoRoot, absFilePath);
//		String ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> gitClone cmd[%s] ret[%s]", cmd, ret));
		
		log.info("------->come into new delete file method");
		try (Repository repo = new FileRepositoryBuilder().readEnvironment()
				.findGitDir(repoRoot).build();
		Git git = new Git(repo);){
			git.rm().addFilepattern(absFilePath);
			AddCommand ac = git.add();
			ac.addFilepattern(".");
			ac.call();
		}catch (IOException e) {
			e.printStackTrace();
		}catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return deleted;

	}
	
	/**
	 * 删除文件或文件夹
	 * @param file
	 * @return
	 */
	private boolean deleteFile(File file) {

		if(!file.exists()) {
			return false;
		}
				
		if(file.isFile()) {
			return file.delete();
			
		} else if(file.isDirectory()) {
			File[] subList = file.listFiles();
			if(subList != null && subList.length > 0) {
				for(File sub : subList) {
					boolean delOK = deleteFile(sub);
					if(!delOK) {
						return false;
					}
				}
			}
			return file.delete();
		
		} else {
			return false;

		}
	}

	/**
	 * 获取绝对路径
	 * @param appId
	 * @param loginUserId
	 * @param relativePath
	 * @return
	 * @throws RuntimeException
	 */
	private String getAbsPath(long appId, long loginUserId, String relativePath) throws RuntimeException {
		
		log.info(String.format("getAbsPath -> appId[%s] loginUserId[%s] relativePath[%s]", appId, loginUserId, relativePath));
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		if(relativePath.endsWith("/")) {
			relativePath = relativePath.substring(0, relativePath.length() - 1);
		}
		
		if(!relativePath.startsWith("/")) {
			relativePath = "/" + relativePath;
		}

		String absPath = String.format("%s/%s%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath(), relativePath);
		
		log.info(String.format("getAbsPath -> absPath[%s]", absPath));
		
		return absPath;
	}

	//*****************************************************
	//*  GIT ACTIONS                                      *
	//*****************************************************
	
	/**
	 * 生成个人仓库
	 * @return
	 * @throws InvalidRemoteException 
	 * @throws TransportException 
	 */
	private boolean gitClone(long appId, long loginUserId) throws InvalidRemoteException, TransportException {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		log.info("repoPath:"+repoPath);
		if(repoRoot.exists()) {
			return true;
		} else {
			// clone
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_clone.sh %s %s", loginUserId, app.getRelativeRepoPath());
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> gitClone cmd[%s] ret[%s]", cmd, ret));
			
			log.info("------->come into make personal gitclone");
			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
					gitFactoryAccount, gitFactoryPassword);
			try (Git git = Git.cloneRepository().setURI(remoteGitRoot+app.getRelativeRepoPath())
					.setDirectory(repoRoot).setCredentialsProvider(cp).call();){
			} catch (InvalidRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new InvalidRemoteException("Error gitClone:" + e.getMessage());
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new TransportException("Error gitClone:" + e.getMessage());
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		
	}

	/**
	 * commit 仓库
	 * 
	 * @param appId
	 * @param loginUserId
	 * @param message
	 * @return
	 */
	public String gitCommit(long appId, long loginUserId, String message) {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");

		} else {
			// commit
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_commit.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), message);
//			String ret = this.syncExecShell(cmd);
			String cmdLog1 = "git add --all  ";
			String cmdLog2 = "git commit -m \""+message+"\"";
			String [] sh_command1={"/bin/sh","-c",cmdLog1};
			String [] sh_command2={"/bin/sh","-c",cmdLog2};
			String ret1 = this.syncExecShell(sh_command1,new File(personalGitRoot+"/"+loginUserId+"/"+app.getRelativeRepoPath()));
			String ret2 = this.syncExecShell(sh_command2,new File(personalGitRoot+"/"+loginUserId+"/"+app.getRelativeRepoPath()));
//			log.info(String.format("webide -> gitCommit cmd[%s] ret[%s]", cmd, ret));
			log.info(String.format("webide -> gitCommit  ret[%s]",  ret2));
			
			if(ret2.indexOf("error") != -1 || ret2.indexOf("fatal") != -1) {
				throw new RuntimeException(transfer2Chinese(ret2));
			}
			
			
			return transfer2Chinese(ret2);
		}
		
	}
	
	/**
	 * push 仓库
	 * @param appId
	 * @param loginUserId
	 * @return
	 * @throws GitAPIException 
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 */
	public String gitPush(long appId, long loginUserId, String branch) throws InvalidRemoteException, TransportException, GitAPIException {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");

		} else {
			// push
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_push.sh %s %s", repoPath, branch);
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> gitPush cmd[%s] ret[%s]", cmd, ret));
			
//			if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
//				throw new RuntimeException(transfer2Chinese(ret));
//			}
			log.info("------->come into new push method");
			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
					gitFactoryAccount, gitFactoryPassword);
			try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
					Git git = new Git(repo);){
						PushCommand pc = git.push();
						pc.setCredentialsProvider(cp).setForce(true).setPushAll().call();
						pc.call();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (NoFilepatternException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (GitAPIException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//			return transfer2Chinese(ret);
			return "推送成功";
		}
	}
	
	/**
	 * commit & push
	 * 
	 * @param appId
	 * @param loginUserId
	 * @param message
	 * @return
	 * @throws GitAPIException 
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 */
	public String gitCommitThenPush(long appId, long loginUserId, String message, String branch, List<String> relativePathes) throws InvalidRemoteException, TransportException, GitAPIException {
		
		
		this.gitCommitFiles(appId, loginUserId, message, relativePathes);
		
		return this.gitPush(appId, loginUserId, branch);
		
		
	}
	
	public boolean gitCommitFiles(long appId, long loginUserId, String message, List<String> relativePathes) {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			return false;

		} else {
			// commit
			
			for(String relativePath : relativePathes) {
				
				if(relativePath.startsWith("/")) {
					relativePath = relativePath.substring(1);
				}
				
//				String cmd = String.format("sh " + shellPath + "coopdev_git/webide_commit_files.sh %s %s %s %s",
//						loginUserId, app.getRelativeRepoPath(), message, relativePath);
//				String ret = this.syncExecShell(cmd);
//				log.info(String.format("webide -> gitCommitFiles cmd[%s] ret[%s]", cmd, ret));
				
				String cmdLog = "git commit -m \""+message+"\"  \""+relativePath+"\"";
				String [] sh_command={"/bin/sh","-c",cmdLog};
				String ret = this.syncExecShell(sh_command,new File(personalGitRoot+"/"+loginUserId+"/"+app.getRelativeRepoPath()));
				
				if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
					throw new RuntimeException(transfer2Chinese(ret));
				}
				
			}
			
			
			return true;
		}
	}
	
	/**
	 * pull 仓库
	 * @param appId
	 * @param loginUserId
	 * @return
	 * @throws GitAPIException 
	 * @throws TransportException 
	 * @throws NoHeadException 
	 * @throws RefNotAdvertisedException 
	 * @throws RefNotFoundException 
	 * @throws CanceledException 
	 * @throws InvalidRemoteException 
	 * @throws DetachedHeadException 
	 * @throws InvalidConfigurationException 
	 * @throws WrongRepositoryStateException 
	 * @throws IOException 
	 */
	public String gitPull(long appId, long loginUserId) throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException, IOException {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");

		} else {
			// gitPull
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_pull.sh %s %s", loginUserId, app.getRelativeRepoPath());
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> gitPull cmd[%s] ret[%s]", cmd, ret));
//			
//			if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
//				throw new RuntimeException(transfer2Chinese(ret));
//			}
			log.info("------->come into new pull method");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
					Git git = new Git(repo);){
						CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
								gitFactoryAccount, gitFactoryPassword);
						PullResult result = git.pull().setCredentialsProvider(cp).call();
						return result+"";
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new IOException(e.getMessage());
					}catch (NoFilepatternException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new NoFilepatternException(e.getMessage());
					} catch (GitAPIException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "pull 失败";
					}
		}
	}
	
	/**
	 * 查看远程分支列表
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	public List< Map<String, Object> > getRemoteBranchList(long appId, long loginUserId) {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		log.info("repoPath:"+repoPath);
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			return null;

		} else {
			// git branch -r
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_branch_r.sh %s %s", loginUserId, app.getRelativeRepoPath());
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
//			List< Map<String, Object> > mapList = new ArrayList<>();
//			
//			ret = ret.replaceAll("\r", "");
//			ret = ret.replaceAll(" ", "");
//	        String[] items = ret.split("\n");
//	        if(items.length > 0) {
//	        	for(String item :items) {
//	        		item = item.trim();
//	        		if(item.length() == 0) {
//	        			continue;
//	        		}
//	        		log.info(String.format("item[%s]", item));
//	        		if(item.indexOf("HEAD") != -1) continue;
//	        		
//	        		if(item.startsWith("origin/")) {
//	        			Map<String, Object> branch = new HashMap<>();
//	        			branch.put("branchName", item.substring(7));
//	        			mapList.add(branch);
//	        		}
//	        	}
//	        }
//			return mapList;
			log.info("------->come into new long range branch");
			List< Map<String, Object> > mapList = new ArrayList<>();
			try (Repository repo = new FileRepositoryBuilder().readEnvironment()
					.findGitDir(repoRoot).build();){
				List<Ref> call;
				call = new Git(repo).branchList().setListMode(ListMode.REMOTE).call();
				for (Ref ref : call) {
					if(ref.getName().toLowerCase().endsWith("/HEAD".toLowerCase())){
						continue;
					}
					Map<String, Object> map = new HashMap<>();
					map.put("branchName", ref.getName().substring(20));
					mapList.add(map);
				}
			}catch (GitAPIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return mapList;
		}
	}
	
	/**
	 * 查看本地分支列表
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	public List< Map<String, Object> > getLocalBranchList(long appId, long loginUserId) {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			return null;

		} else {
			// git branch -r
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_branch.sh %s %s", loginUserId, app.getRelativeRepoPath());
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
//			List< Map<String, Object> > mapList = new ArrayList<>();
//			ret = ret.replaceAll("\r", "");
//			ret = ret.replaceAll(" ", "");
//	        String[] items = ret.split("\n");
//	        if(items.length > 0) {
//	        	for(String item :items) {
//	        		item = item.trim();
//	        		if(item.length() == 0) {
//	        			continue;
//	        		}
//	        		
//	        		log.info(String.format("item[%s]", item));
//	        		if(item.startsWith("*")) {
//	        			item = item.substring(1);
//	        		}
//        			Map<String, Object> branch = new HashMap<>();
//        			branch.put("branchName", item);
//        			mapList.add(branch);
//	        	}
//	        }
			log.info("------->come into new local branch");
			List< Map<String, Object> > mapList = new ArrayList<>();
			try (Repository repo = new FileRepositoryBuilder().readEnvironment()
					.findGitDir(repoRoot).build();){
				List<Ref> call;
				call = new Git(repo).branchList().call();
				for (Ref ref : call) {
					if(ref.getName().toLowerCase().endsWith("/HEAD".toLowerCase())){
						continue;
					}
					Map<String, Object> map = new HashMap<>();
					map.put("branchName", ref.getName().substring(11));
					mapList.add(map);
				}
			}catch (GitAPIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return mapList;
		}
		
	}
	
	/**
	 * 检出本地分支
	 * @param appId
	 * @param loginUserId
	 * @param branchName
	 * @return
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 */
	public String checkoutBranch(long appId, long loginUserId, String branchName) throws InvalidRemoteException, TransportException {
		
		
		this.gitClone(appId, loginUserId);
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");

		} else {
			// git branch -r
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_checkout.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), branchName);
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
//			
//			if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
//				throw new RuntimeException(transfer2Chinese(ret));
//			}
//			
//			return transfer2Chinese(ret);
			log.info("------->come into new checkout local branch");
			Ref ref = null;
			try (Repository repo = new FileRepositoryBuilder().readEnvironment()
					.findGitDir(repoRoot).build();
					Git git = new Git(repo);){
				CheckoutCommand checkoutCmd = git.checkout();
				checkoutCmd.setName(branchName);
				ref = checkoutCmd.call();
			} catch (Exception e) {  
	            e.printStackTrace();  
	        }
			return ref.toString();
		}
		
	}
	
	/**
	 * 检出远程分支
	 * @param appId
	 * @param loginUserId
	 * @param branchName
	 * @return
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 */
	@SuppressWarnings("unused")
	public String checkoutBranchR(long appId, long loginUserId, String branchName) throws InvalidRemoteException, TransportException {
		
		
		this.gitClone(appId, loginUserId);
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		log.info("repoPath："+repoPath);
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");
		} else {
			// git branch -r
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_checkout_r.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), branchName);
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
//			
//			if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
//				throw new RuntimeException(transfer2Chinese(ret));
//			}
//			
//			return transfer2Chinese(ret);
			log.info("------->come into new checkout faraway branch");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment()
					.findGitDir(repoRoot).build();
					Git git = new Git(repo);){
				Boolean flag=false; 
				List<Ref> call = new Git(repo).branchList().call();
				for (Ref ref : call) {
					if(ref.getName().toLowerCase().endsWith("/HEAD".toLowerCase())){
						continue;
					}
					if(branchName.equals(ref.getName().substring(11))){
						flag=true;
						break;
					}
				}
				CheckoutCommand checkoutCmd = git.checkout();
				checkoutCmd.setName(branchName);
				if(flag){
					checkoutCmd.setCreateBranch(false);
				}else{
					checkoutCmd.setCreateBranch(true);
				}
				synchronized(repoPath){
					Ref ref = checkoutCmd.call();
				}
			} catch (Exception e) {  
	            e.printStackTrace();  
	        }
			return "检出远程分支成功";
		}
		
	}
	
	/**
	 * 删除本地分支
	 * @param appId
	 * @param loginUserId
	 * @param branchName
	 * @return
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws CannotDeleteCurrentBranchException 
	 * @throws NotMergedException 
	 */
	public String deleteBranch(long appId, long loginUserId, String branchName) throws IOException, NotMergedException, CannotDeleteCurrentBranchException, GitAPIException {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");

		} else {
/*			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_branch_del.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), branchName);
			String ret = this.syncExecShell(cmd);
			log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
			
			
			if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
				throw new RuntimeException(transfer2Chinese(ret));
			}
			
			return transfer2Chinese(ret);*/
			log.info("------->come into new delete local branch");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
					Git git = new Git(repo);){
					git.branchDelete().setBranchNames(branchName).setForce(true).call();
					return "删除本地分支成功";
			} 
		}
		
	}
	
	/**
	 * 创建分支
	 * @param appId
	 * @param loginUserId
	 * @param branchName
	 * @return
	 * @throws GitAPIException 
	 * @throws InvalidRefNameException 
	 * @throws RefNotFoundException 
	 * @throws RefAlreadyExistsException 
	 * @throws IOException 
	 */
	public String createBranch(long appId, long loginUserId, String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException, IOException {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			return null;

		} else {
			// git branch branchName
//			String cmd = String.format("sh " + shellPath + "coopdev_git/webide_branch_add.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), branchName);
//			String ret = this.syncExecShell(cmd);
//			log.info(String.format("webide -> git branch  cmd[%s] ret[%s]", cmd, ret));
//			
//			
//			if(ret.indexOf("error") != -1 || ret.indexOf("fatal") != -1) {
//				throw new RuntimeException(transfer2Chinese(ret));
//			}
//			
//			return transfer2Chinese(ret);
			log.info("------->come into new create branch");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
					Git git = new Git(repo);){
					git.branchCreate().setName(branchName).call();
					return "创建分支成功";
			}
		}
	}
	
	/**
	 * 文件状态
	 * @param appId
	 * @param loginUserId
	 * @return
	 * @throws GitAPIException 
	 * @throws IOException 
	 */
	public Map<String, Object> gitStatus(long appId, long loginUserId) throws IOException, GitAPIException {
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			return null;

		} else {
			return gitStatusIns(loginUserId, app, repoRoot);
	        
		}
		
	}
	
	public String gitDiffFile(long appId, long loginUserId, String relativePath) throws IOException {
		
		App app = appDao.findOne(appId);
		if(app == null) {
			throw new RuntimeException("指定应用不存在");
		}
		
		String absFilePath = this.getAbsPath(appId, loginUserId, relativePath);
		
		String repoPath = String.format("%s/%s%s", personalGitRoot, loginUserId, app.getRelativeRepoPath());
		
		String ret = "";
				
		File repoRoot = new File(repoPath);
		
		if(!repoRoot.exists()) {
			throw new RuntimeException("指定仓库不存在");

		}
		
		if(relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		
//		String cmd = String.format("sh " + shellPath + "coopdev_git/webide_diff_file.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), relativePath);
//		String ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
//		log.info("-------->appId:"+appId+",loginUserId:"+appId+",relativePath:"+relativePath+",repoPath:"+repoPath);
		
		log.info("-------->come into new diff method");
		try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
				Git git = new Git(repo);){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DiffFormatter formatter = new DiffFormatter( out );
			formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
			formatter.setRepository( git.getRepository());
			formatter.setPathFilter(PathFilter.create(relativePath));
			AbstractTreeIterator commitTreeIterator = prepareTreeParser( git.getRepository(),  Constants.HEAD );
			FileTreeIterator workTreeIterator = new FileTreeIterator( git.getRepository() );
			List<DiffEntry> diffEntries = formatter.scan( commitTreeIterator, workTreeIterator );
			formatter.format(diffEntries.get(0));
			ret = out.toString("UTF-8");
		    System.out.println("ret:"+ret);
		    formatter.close();
		    out.reset();
		}
		
		
		File file = new File(absFilePath);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			ArrayList<String> codeLines = new ArrayList<>();		// 右侧文件代码
			String line = null;
			while( (line = in.readLine()) != null ) {
				codeLines.add(line);
			}
			
			in.close();
			
			List<DiffItem> itemList = null;
			try {
				itemList = this.analysisDiff(ret);
				System.out.println("---------->itemList.size():"+itemList.size());
			} catch(Exception e) {
				log.info("analysis EXCEPTION:" +e.getMessage());
				return "";
			}
			
			
			ArrayList<String> resultLines = new ArrayList<>();
			
			
			for(String code : codeLines) {
				log.info("code -> " + code);
			}
			
			int nextAddIndex = 0;
			
			if(itemList.size() > 0) {
				// 替换代码块
				for(DiffItem item : itemList) {
					log.info(item + " current nextAddIndex=" + nextAddIndex);
					
					// 添加相同代码段
					for(; nextAddIndex < item.getStartNumber() - 1; nextAddIndex++) {
						log.info("same code add -> nextAddIndex -> " + nextAddIndex + " -> code -> " + codeLines.get(nextAddIndex));
						resultLines.add( " " + codeLines.get(nextAddIndex) );
					}
					
					// 添加替换代码段
					resultLines.addAll(item.getReplacement());
					log.info("after add code, current nextAddIndex=" + nextAddIndex);
					
					nextAddIndex += item.getLineTotal();
				}
				
				// 添加剩余代码
				int codeCount = codeLines.size();
				for(; nextAddIndex < codeCount; nextAddIndex++) {
					log.info("same code add -> nextAddIndex -> " + nextAddIndex + " -> code -> " + codeLines.get(nextAddIndex));
					resultLines.add( " " + codeLines.get(nextAddIndex) );
				}
				
				String resultStr = "";
				for(String code : resultLines) {
					resultStr += (code += "\r\n");
				}
				log.info("resultStr:"+resultStr);
				return resultStr;

			} else {
				return "";
			}
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("指定文件不存在");
		} catch (IOException e) {
			throw new RuntimeException("文件操作异常");
		}
		
	}
	
	/**
	 * 解析diff结果
	 * @param deffResult
	 * @return
	 */
	private static List<DiffItem> analysisDiff(String deffResult) {
		
		List<DiffItem> itemList = new ArrayList<>();
//		String[] diffLines = deffResult.split("\n\r");	// git diff 结果
		deffResult = deffResult.replaceAll("\r", "");
		String[] diffLines = deffResult.split("\n");	// git diff 结果
		
		String diffAbstract = null;

		ArrayList<String> cacheLines = new ArrayList<>();	// 一个diff点
		int extra = 0;
		
		// 解析diff结果
		for(int i = 0; i < diffLines.length; i++) {
			if(i <= 3) {
				continue; // 略过前四行
			}
			String line = diffLines[i];
			if(line.startsWith("@@")) {
				// 存在以往的变化
				if(diffAbstract != null && cacheLines.size() > 0) {
					// 添加变化
					//@@ -1,16 +1 @@
					int rightIdx = diffAbstract.indexOf("+") + 1;
					int nextBlankIdx = diffAbstract.indexOf(" ", rightIdx);
					String info = diffAbstract.substring(rightIdx, nextBlankIdx);
					String[] numStrs = info.split(",");
					
					int startNumber = 0;
					int lineTotal  = 0;
					if(numStrs.length == 2) {
						startNumber = Integer.parseInt(numStrs[0]);
						lineTotal   = Integer.parseInt(numStrs[1]);
					} else {
						startNumber = Integer.parseInt(info);
						lineTotal = 1;
					}
					
				
					ArrayList<String> recLines = new ArrayList<>();
					for(int j = 0; j < lineTotal + extra; j++) {
						recLines.add(cacheLines.get(j));
					}
					DiffItem item = new DiffItem();
					item.setLineTotal(lineTotal);
					item.setStartNumber(startNumber);
					item.setReplacement(recLines);
					
					itemList.add(item);

				}
				
				// 更新状态
				diffAbstract = line;
				cacheLines.clear();
				extra = 0;
				int idx = line.lastIndexOf("@@");
				String tail = line.substring(idx + 2);
				if(tail.trim().length() > 0) {
					cacheLines.add(tail);
				}
				
			} else {
				if(line.startsWith("-")) {
					extra++;
				}

				cacheLines.add(line);
			
			}
			
		}
		
		if(diffAbstract != null && cacheLines.size() > 0) {
			// 添加变化
			int rightIdx = diffAbstract.indexOf("+") + 1;
			int nextBlankIdx = diffAbstract.indexOf(" ", rightIdx);
			String info = diffAbstract.substring(rightIdx, nextBlankIdx);
			String[] numStrs = info.split(",");
			int startNumber = 0;
			int lineTotal  = 0;
			if(numStrs.length == 2) {
				startNumber = Integer.parseInt(numStrs[0]);
				lineTotal   = Integer.parseInt(numStrs[1]);
			} else {
				startNumber = Integer.parseInt(info);
				lineTotal = 1;
			}
			
		
			DiffItem item = new DiffItem();
			item.setLineTotal(lineTotal);
			item.setStartNumber(startNumber);
			item.setReplacement(cacheLines);
			
			itemList.add(item);
		}
		
		return itemList;
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException,
		MissingObjectException,IncorrectObjectTypeException {
		// from the commit we can build the tree which allows us to construct the TreeParser
		Ref head = repository.getRef(ref);
		try (RevWalk walk = new RevWalk(repository)) {
		    RevCommit commit = walk.parseCommit(head.getObjectId());
		    RevTree tree = walk.parseTree(commit.getTree().getId());
		
		    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		    try (ObjectReader oldReader = repository.newObjectReader()) {
		        oldTreeParser.reset(oldReader, tree.getId());
		    }
		
		    walk.dispose();
		
		    return oldTreeParser;
		}
	}
	
	private Map<String, Object> gitStatusIns(long loginUserId, App app ,File repoRoot) throws IOException, GitAPIException{
//		String cmd = String.format("sh " + shellPath + "coopdev_git/webide_status.sh %s %s", loginUserId, app.getRelativeRepoPath());
//		String ret = this.syncExecShell(cmd);
//		log.info(String.format("webide -> git branch -r cmd[%s] ret[%s]", cmd, ret));
		
		String ret="";
		Status ret1=null;
		String branchName = null;
		log.info("---------->come into new gitStatusIns method");
		log.info("--------->repoRoot:"+repoRoot.toString());
		try (Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repoRoot).build();
				Git git = new Git(repo);){
				ret1=git.status().call();
				branchName=git.status().getRepository().getBranch();
				log.info("branchName:"+branchName);
//				log.info("ret1.getAdded():"+ret1.getAdded());
//				log.info("ret1.getChanged():"+ret1.getChanged());
//				log.info("ret1.getMissing():"+ret1.getMissing());
//				log.info("ret1.getModified():"+ret1.getModified());
//				log.info("ret1.getRemoved():"+ret1.getRemoved());
//				log.info("ret1.getUncommittedChanges():"+ret1.getUncommittedChanges());
//				log.info("ret1.getUntracked():"+ret1.getUntracked());
		}catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ret = ret.replaceAll("\r", "");
		ret = ret.replaceAll(" ", "");
		
//        String[] items = ret.split("\n");
        
        
        List<Map<String, Object>> newFileList       = new ArrayList<>();
        List<Map<String, Object>> modifiedFileList  = new ArrayList<>();
        List<Map<String, Object>> untrackedFileList = new ArrayList<>();
        List<Map<String, Object>> conflictFileList  = new ArrayList<>();
        List<Map<String, Object>> deletedFileList   = new ArrayList<>();
        List<Map<String, Object>> renamedFileList   = new ArrayList<>();
        
        if(!ret1.getAdded().isEmpty()){
        	for(String str:ret1.getAdded()){
        		Map<String, Object> fileMap = new HashMap<>();
        		fileMap.put("fileName", System.getProperty("file.separator")+str);
        		newFileList.add(fileMap);
        	}
        }
        if(!ret1.getChanged().isEmpty()){
        	for(String str:ret1.getChanged()){
        		Map<String, Object> fileMap = new HashMap<>();
        		fileMap.put("fileName", System.getProperty("file.separator")+str);
        		modifiedFileList.add(fileMap);
        	}
        }
        if(!ret1.getUntracked().isEmpty()){
        	for(String str:ret1.getUntracked()){
        		Map<String, Object> fileMap = new HashMap<>();
        		fileMap.put("fileName", System.getProperty("file.separator")+str);
        		untrackedFileList.add(fileMap);
        	}
        }
        if(!ret1.getConflicting().isEmpty()){
        	for(String str:ret1.getConflicting()){
        		Map<String, Object> fileMap = new HashMap<>();
        		fileMap.put("fileName", System.getProperty("file.separator")+str);
        		conflictFileList.add(fileMap);
        	}
        }
        if(!ret1.getRemoved().isEmpty()){
        	for(String str:ret1.getRemoved()){
        		Map<String, Object> fileMap = new HashMap<>();
        		fileMap.put("fileName", System.getProperty("file.separator")+str);
        		deletedFileList.add(fileMap);
        	}
        }
        if(!ret1.getMissing().isEmpty()){
        	for(String str:ret1.getMissing()){
        		Map<String, Object> fileMap = new HashMap<>();
        		fileMap.put("fileName", System.getProperty("file.separator")+str);
        		renamedFileList.add(fileMap);
        	}
        }
//        String status = null;
//        
//        int length = items.length;
//        
//        if(items.length > 0) {
//        	for(int i = 0; i < length; i++) {
//        		String line = items[i];
//        		log.info("gitStatusIns line[" + line + "]");
//        		
//        		if(i == 0) {
//        			status = "HEAD";
//        			if(line.startsWith("#Onbranch")) {
//	        			// 获取分支信息
//	        			branchName = line.substring(9).trim();
//	        		}
//        			continue;
//        			
//        		} else if(line.equals("# Untracked files:")) {
//        			status = "UNTRACKED";
//        			continue;
//        			
//        		}
//        		
//        		if(line.startsWith("#\tnewfile:") ) {
//        			log.info("gitStatusIns add 2 newFileList");
//        			Map<String, Object> fileMap = new HashMap<>();
//        			fileMap.put("fileName", "/" + line.substring(10).trim());
//        			newFileList.add(fileMap);
//        		
//        		} else if(line.startsWith("#\tmodified:") ) {
//        			log.info("gitStatusIns add 2 modifiedFileList");
//        			Map<String, Object> fileMap = new HashMap<>();
//        			fileMap.put("fileName", "/" + line.substring(11).trim());
//        			log.info("modifiedFileList:"+line.substring(11).trim());
//        			modifiedFileList.add(fileMap);
//        		
//        		} else if(line.startsWith("#\tbothmodified:")) {
//        			log.info("gitStatusIns add 2 conflictFileList");
//        			Map<String, Object> fileMap = new HashMap<>();
//        			fileMap.put("fileName", "/" + line.substring(15).trim());
//        			conflictFileList.add(fileMap);
//
//        		} else if(line.startsWith("#\tdeleted:")) {
//        			log.info("gitStatusIns add 2 deletedFileList");
//        			Map<String, Object> fileMap = new HashMap<>();
//        			fileMap.put("fileName", "/" + line.substring(10).trim());
//        			deletedFileList.add(fileMap);
//
//        		} else if(line.startsWith("#\trenamed:")) {
//        			log.info("gitStatusIns add 2 renamedFileList");
//        			Map<String, Object> fileMap = new HashMap<>();
//        			 int idx = line.indexOf("->");
//        			fileMap.put("fileName", "/" + line.substring(idx + 2).trim());
//        			renamedFileList.add(fileMap);
//        			
//        		} else if("UNTRACKED".equals(status) && line.startsWith("#\t") ) {
//        			log.info("gitStatusIns add 2 untrackedFileList");
//        			Map<String, Object> fileMap = new HashMap<>();
//        			fileMap.put("fileName", "/" + line.substring(2).trim());
//        			untrackedFileList.add(fileMap);
//
//        		} else {
//        			log.info("gitStatusIns add 2 nowhere");
//        		}
//        		
//
//        	}
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("currentBranch", branchName);
        map.put("new", newFileList);
        map.put("modified", modifiedFileList);
        map.put("untracked", untrackedFileList);
        map.put("conflict",conflictFileList);
        map.put("deleted", deletedFileList);
        map.put("renamed", renamedFileList);
        return map;		
	}
	
	public static void main22(String[] args) {

		String str = "diff --git a/t2 b/t2\n\rindex ea17f91..d612ccb 100644\n\r--- a/t2\n\r+++ b/t2\n\r@@ -1,4 +1 @@\n\r-abcfff\n\r-1\n\r-2\n\r-3\n\r\\ No newline at end of file\n\r+abcfff\n\r\\ No newline at end of file";
		List<DiffItem> list = analysisDiff(str);
		
		System.out.println(list.get(0));
	}


	private String transfer2Chinese(String original) {
		if(original == null || "".equals(original)) {
			return original;
		}
		
		original = original.replace("appcanGitAdminResu", "******");//
		if(original.startsWith("fatal: A branch named") && original.endsWith("already exists.")) {
			return "错误：分支已经存在";
		}
		
		if(original.startsWith("Fetching origin Already up-to-date.")) {
			return "拉取成功";
		}
		
		if(original.startsWith("Fetching origin Auto-merging") && original.endsWith("fix conflicts and then commit the result.")) {
			return "已进行自动合并，和本地代码产生冲突，请您查看并解决";
		}
		
		if(original.startsWith("Fetching origin Updateing")) {
			return "拉取成功";
		}
		
		if(original.indexOf("Updates were rejected") != -1 && original.indexOf("current branch is behind") != -1) {
			return "您当前的分支落后于远程分支，请先进行拉取（pull），然后再进行推送";
		}
		
		if(original.indexOf("commit your changes or stash them before you can switch branches") != -1) {
			return "请您先提交后再切换分支，否则未提交或暂存的文件将可能被覆盖";
		}
		
		if(original.indexOf("Cannot delete the branch") != -1 && original.endsWith("which you are currently on.")) {
			return "需要删除的分支，您正在编辑，请切换到其他分支再进行删除动作";
		}
		
		if(original.startsWith("Branch") && original.indexOf("set up to track remote branch") != -1) {
			return "推送成功";
		}

		return original;
	}



	public String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}
	public static void main1(String[] args) {
//		downloadFromUrl("http://zymobitest.appcan.cn/zymobiResource/aaa.txt","C://aaaa//");
//			File src = new File("/home/gitRepo/011/587/629/x60eb1.git");
			File src = new File("D://gitTest/xda7ce");
			try (Repository repo = new FileRepositoryBuilder().readEnvironment()
					.findGitDir(src).build();
			Git git = new Git(repo);){
				
				// git checkout branchName1
				CheckoutCommand checkoutCmd = git.checkout();
				checkoutCmd.setName("master");
				checkoutCmd.setCreateBranch(false);
//				checkoutCmd
//						.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK);
//				checkoutCmd.setStartPoint("origin/" + branchName);
				synchronized("D://gitTest/xda7ce"){
					Ref ref = checkoutCmd.call();
				}
				// git pull
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(
						"haijun.cheng@zymobi.com", "123456");
				PullResult result = git.pull().setCredentialsProvider(cp)
						.call();
				System.out.println(result.isSuccessful());
				
				Iterable<RevCommit> log = git.log().call();
				for(RevCommit str: log){//iteratorSequence.iterator()返回的是一个Iterable<T>实例，支持foreach循环
					String intToS = String.valueOf(str.getCommitTime());
					Long sToL = Long.parseLong(intToS);
					Date date = new Date(sToL*1000);
			        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.getDefault());
			        System.out.println(format.format(date)+","+str.getFullMessage());
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
	}
	
	public static void main(String[] args) {
		File src = new File("D://gitTest/xbde29");
		try (Repository repository = new FileRepositoryBuilder().readEnvironment()
				.findGitDir(src).build();
		Git git = new Git(repository);){
			List<DiffEntry> diff = git.diff().setPathFilter(PathFilterGroup.createFromStrings("phone/index.m")).setShowNameAndStatusOnly(true).call();
			ByteArrayOutputStream out = new ByteArrayOutputStream();    
            DiffFormatter df = new DiffFormatter(out);   
            //设置比较器为忽略空白字符对比（Ignores all whitespace）  
            df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);  
            df.setRepository(git.getRepository());   
	            System.out.println("------------------------------start-----------------------------");  
	            //每一个diffEntry都是第个文件版本之间的变动差异  
	            for (DiffEntry diffEntry : diff) {   
	            	System.out.println(diffEntry.getNewPath());
	            	System.out.println(diffEntry.getChangeType());
	            	System.out.println(diffEntry.getNewId());
	                //打印文件差异具体内容  
//	                df.format(diffEntry);    
	                String diffText = out.toString("UTF-8");    
	                System.out.println(">>>>>>>>>    "+diffText);    
//	                if(StringUtils.isBlank(diffText)) continue;
	                  
	                //获取文件差异位置，从而统计差异的行数，如增加行数，减少行数  
	                FileHeader fileHeader = df.toFileHeader(diffEntry);  
	                List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();  
	                int addSize = 0;  
	                int subSize = 0;  
	                for(HunkHeader hunkHeader:hunks){  
	                    EditList editList = hunkHeader.toEditList();  
	                    for(Edit edit : editList){  
	                        subSize += edit.getEndA()-edit.getBeginA();  
	                        addSize += edit.getEndB()-edit.getBeginB();  
	                          
	                    }  
	                }  
	                System.out.println("addSize="+addSize);  
	                System.out.println("subSize="+subSize);  
	                System.out.println("------------------------------end-----------------------------");  
	                out.reset();    
	           }   
	              
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	public static AbstractTreeIterator prepareTreeParser(RevCommit commit,Repository repository){  
        System.out.println(commit.getId());  
        try (RevWalk walk = new RevWalk(repository)) {  
            System.out.println(commit.getTree().getId());  
            RevTree tree = walk.parseTree(commit.getTree().getId());  
  
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();  
            try (ObjectReader oldReader = repository.newObjectReader()) {  
                oldTreeParser.reset(oldReader, tree.getId());  
            }  
  
            walk.dispose();  
  
            return oldTreeParser;  
    }catch (Exception e) {  
        // TODO: handle exception  
    }  
        return null;  
        }
}
