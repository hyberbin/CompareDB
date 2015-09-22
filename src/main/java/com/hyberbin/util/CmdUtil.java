/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyberbin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import org.jplus.hyb.log.Logger;
import org.jplus.hyb.log.LoggerManager;
import org.jplus.util.FileUtils;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class CmdUtil {

    private final static Logger log = LoggerManager.getLogger(CmdUtil.class);
    private final static boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

    private final String mainFile;
    private final String[] argsLine;
    private final StringBuilder errors = new StringBuilder();

    public CmdUtil(String mainFile, String[] argsLine) {
        this.mainFile = mainFile;
        this.argsLine = argsLine;
        exe();
    }

    private void exe() {
        try {
            Process process = IS_WINDOWS ? exeWindows() : exeLinux();
            if (argsLine.length > 1) {
                OutputStream os = process.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os);
                //命令1和命令2要放在一起执行  
                StringBuilder cmd = new StringBuilder();
                for (int i = 1; i < argsLine.length; i++) {
                    if (i != 1) {
                        cmd.append("\r\n");
                    }
                    cmd.append(argsLine[i]);
                }
                log.debug("write to cmd:{}",cmd);
                writer.write(cmd.toString());
                writer.flush();
                writer.close();
                os.close();
            }
            InputStream errorStream = process.getErrorStream();
            List<String> readLines = FileUtils.readLines(errorStream);
            if (ObjectHelper.isNotEmpty(readLines)) {
                for (String string : readLines) {
                    errors.append(string);
                }
            }
        } catch (Exception ex) {
            errors.append(ex.getMessage());
            log.error("执行命令错误！", ex);
        }

    }

    private Process exeWindows() throws Exception {
        String cmd = "\"cmd\" /c \"\"" + mainFile + "\" " + argsLine[0] + "\"";
        log.debug("in exeWindows,cmdline:{}", cmd);
        return Runtime.getRuntime().exec(cmd);

    }

    private Process exeLinux() throws IOException {
        String cmd = mainFile + " " + argsLine[0];
        log.debug("in exeLinux,cmdline:{}", cmd);
        return Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
    }

    public String getErrors() {
        return errors.toString();
    }

}
