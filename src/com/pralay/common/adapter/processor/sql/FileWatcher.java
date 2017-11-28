package com.pralay.common.adapter.processor.sql;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPStatement;
import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.common.adapter.processor.sql.CorrelateEvent.CEPListener;
import com.pralay.common.adapter.processor.sql.UptimeMgmt.CEPListenerUptime;
import com.pralay.common.adapter.processor.sql.UptimeMgmt.CEPListenerUptimePlatformEvents;
import com.pralay.configuration.model.ServerData;

import difflib.DiffUtils;

public class FileWatcher {
	
	private static ServerData serverData;
	private Path watchDir;
    private Path shadowDir;
    //private int watchInterval;
    //private WatchService watchService;
    private static String ruleStmt;
    private static String ruleStmtId;
    private static EPAdministrator cepAdm=null;
    public static EPStatement allRuleStmt=null;
    public static EPStatement allRuleStmtexisting=null;
    private static final Logger LOGGING = LoggerFactory.getLogger(FileWatcher.class);
    
	//public FileWatcher(Path watchDir, Path shadowDir, int watchInterval) throws IOException {
	public FileWatcher(Path watchDir, Path shadowDir, ServerData serverData) throws IOException {
        this.watchDir = watchDir;
        this.shadowDir = shadowDir;
        //this.watchInterval = watchInterval;
        //watchService = FileSystems.getDefault().newWatchService();
        cepAdm = CommonAdapterBuilder.cepAdm;
        allRuleStmt = CommonAdapterBuilder.allRuleStmt;
        this.serverData = serverData;
    }
	
	public void firstrun() throws InterruptedException, IOException {
		LOGGING.info("<-- Starting of file changes check for 1st run -->");
		recursiveDeleteDir(shadowDir);
		recursiveCopyDir(watchDir, shadowDir);
        //prepareShadowDir();
		//recursiveDeleteDir(shadowDir);
		//watchDir.register(watchService, ENTRY_MODIFY);
            //WatchKey watchKey = watchService.poll(10,TimeUnit.SECONDS);
            //if(watchKey!=null){
            //for (WatchEvent<?> event : watchKey.pollEvents()) {
                //Path oldFile = shadowDir.resolve((Path) event.context());
                //Path newFile = watchDir.resolve((Path) event.context());
                Path oldFile = shadowDir;
                Path newFile = watchDir;
                List<String> oldContent;
                List<String> newContent;
                //WatchEvent.Kind<?> eventType = event.kind();
                //if (!(Files.isDirectory(newFile) || Files.isDirectory(oldFile))) {
                    //if (eventType == ENTRY_MODIFY) {
                        //Thread.sleep(200);
                		//System.out.println("shadow loc: "+oldFile);
                		//System.out.println("watch loc: "+newFile);
                        oldContent = fileToLines(oldFile);
                        newContent = fileToLines(newFile);
                        //System.out.println("shadow: "+oldContent.size());
                        //System.out.println("watch: "+newContent.size());
                        ChangeRuleApply(newFile, oldFile, oldContent, newContent);
                        /*try {
                            Files.copy(newFile, oldFile, StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    //} 
                //}
            //}
          //}else LOGGING.info("<-- No Change in rule -->");
    }
	
	public void restrun() throws InterruptedException, IOException {
		LOGGING.info("<-- Starting of file changes check for rest run -->");
        //watchDir.register(watchService, ENTRY_MODIFY);
            //WatchKey watchKey = watchService.poll(10,TimeUnit.SECONDS);
            //if(watchKey!=null){
            //for (WatchEvent<?> event : watchKey.pollEvents()) {
                //Path oldFile = shadowDir.resolve((Path) event.context());
                //Path newFile = watchDir.resolve((Path) event.context());
                Path oldFile = shadowDir;
                Path newFile = watchDir;
                List<String> oldContent;
                List<String> newContent;
                //WatchEvent.Kind<?> eventType = event.kind();
                //if (!(Files.isDirectory(newFile) || Files.isDirectory(oldFile))) {
                    //if (eventType == ENTRY_MODIFY) {
                        //Thread.sleep(200);
                        //System.out.println("shadow loc: "+oldFile);
                        //System.out.println("watch loc: "+newFile);
                        oldContent = fileToLines(oldFile);
                        newContent = fileToLines(newFile);
                        //System.out.println("shadow: "+oldContent.size());
                        //System.out.println("watch: "+newContent.size());
                        ChangeRuleApply(newFile, oldFile, oldContent, newContent);
                    //} 
                //}
            //}
          //}else LOGGING.info("<-- No Change in rule -->");
    }
	
    /*private void prepareShadowDir() throws IOException {
        recursiveDeleteDir(shadowDir);
        Runtime.getRuntime().addShutdownHook(
            new Thread() {
                @Override
                public void run() {
                    try {
                        LOGGING.info("Cleaning up shadow directory " + shadowDir);
                        recursiveDeleteDir(shadowDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
        recursiveCopyDir(watchDir, shadowDir);
    }*/

    public static void recursiveDeleteDir(Path directory) throws IOException {
        if (!directory.toFile().exists())
            return;
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void recursiveCopyDir(final Path sourceDir, final Path targetDir) throws IOException {
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, Paths.get(file.toString().replace(sourceDir.toString(), targetDir.toString())));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(Paths.get(dir.toString().replace(sourceDir.toString(), targetDir.toString())));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static List<String> fileToLines(Path path) throws IOException {
        List<String> lines = new LinkedList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            while ((line = reader.readLine()) != null)
                lines.add(line);
        }
        catch (Exception e) {}
        //System.out.println("Hello lines :"+lines);
        return lines;
    }

    private static void ChangeRuleApply(Path oldPath, Path newPath, List<String> oldContent, List<String> newContent) {
        List<String> diffLines = DiffUtils.generateUnifiedDiff(
            newPath.toString(),
            oldPath.toString(),
            oldContent,
            DiffUtils.diff(oldContent, newContent),
            0
        );
        LOGGING.info("If there is any change: "+diffLines.size());
        if(diffLines.size()>0){
        	LOGGING.info("<-- Rule Changes Exist -->");
	        for (String diffLine : diffLines){
	        	if(diffLine.startsWith("+<")){
	        		LOGGING.info(diffLine);
	        		
	        		ruleStmt = diffLine.substring(diffLine.indexOf(">")+1, diffLine.lastIndexOf("</"));
	        		ruleStmtId = diffLine.substring(diffLine.indexOf("<")+1, diffLine.indexOf(">"));
	        		LOGGING.info("Rule: "+ruleStmt);
	        		LOGGING.info("Rule Id: "+ruleStmtId);
	        		LOGGING.info("<-- Applying the changes from filewatcher to esper -->");
	        		allRuleStmtexisting = cepAdm.getStatement(ruleStmtId);
	        		if(allRuleStmtexisting!=null){
	        			
	        			
	        			try {
							LOGGING.info("There is a change in existing rule :'"+allRuleStmtexisting.getText()
							+"'.New Rule will be applied is ->"+ruleStmt);
							if(allRuleStmtexisting.isStarted()){
								allRuleStmtexisting.destroy();
								allRuleStmt = cepAdm.createEPL(replaceXmlSplChar(ruleStmt));
								if(ruleStmt.contains(serverData.getDowntimejoincondition())){
		        	            	LOGGING.info("You are in Downtime Listener 03072016 "+ruleStmt);
		        	            	allRuleStmt.addListener(new CEPListener());
		        	            }else if(ruleStmt.contains(serverData.getUptimeAlarmAgentPA())){
		        	            	LOGGING.info("You are in Uptime1 Listener 03072016 "+ruleStmt);
		        	            	allRuleStmt.addListener(new CEPListenerUptimePlatformEvents());
		        	            }else if(ruleStmt.contains(serverData.getUptimeruleuniqueidentifier())){
		        	            	LOGGING.info("You are in Uptime2 Listener 03072016 "+ruleStmt);
		        	            	allRuleStmt.addListener(new CEPListenerUptime());
		        	            }else{
		        	            	LOGGING.info("You are in defaul Listener 03072016 "+ruleStmt);
		        	            	allRuleStmt.addListener(new CEPListener());
		        	            }
							}
						} catch (EPException e) {
							// TODO Auto-generated catch block
							LOGGING.info("There are some syntax errors in the rule, please modify it: "+ruleStmt);
							e.printStackTrace();
							
						}  
	        		}else{
	        			try {
							LOGGING.info("There are no changes in existing rule. New Rule, will be applied, is ->"+ruleStmt);
							allRuleStmt = cepAdm.createEPL(replaceXmlSplChar(ruleStmt));
							if(ruleStmt.contains(serverData.getDowntimejoincondition())){
	        	            	LOGGING.info("You are in Downtime Listener 03072016 "+ruleStmt);
	        	            	allRuleStmt.addListener(new CEPListener());
	        	            }else if(ruleStmt.contains(serverData.getUptimeAlarmAgentPA())){
	        	            	LOGGING.info("You are in Uptime1 Listener 03072016 "+ruleStmt);
	        	            	allRuleStmt.addListener(new CEPListenerUptimePlatformEvents());
	        	            }else if(ruleStmt.contains(serverData.getUptimeruleuniqueidentifier())){
	        	            	LOGGING.info("You are in Uptime2 Listener 03072016 "+ruleStmt);
	        	            	allRuleStmt.addListener(new CEPListenerUptime());
	        	            }else{
	        	            	LOGGING.info("You are in defaul Listener 03072016 "+ruleStmt);
	        	            	allRuleStmt.addListener(new CEPListener());
	        	            }
						} catch (EPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							LOGGING.info("There are some syntax errors in the rule, please modify it: "+ruleStmt);
						}
	        		}
	        	}
	        }
	        try {
	            Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }else LOGGING.info("<-- Rule changes didn't exist -->");
    }
    
    private static String replaceXmlSplChar(String ruletext){
		if(ruletext.contains("&gt;"))
			ruletext = ruletext.replace("&gt;", ">");
		else if(ruletext.contains("&lt;"))
			ruletext = ruletext.replace("&lt;", "<");
		return ruletext;
	}
}
