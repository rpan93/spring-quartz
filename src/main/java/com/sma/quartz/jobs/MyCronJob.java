package com.sma.quartz.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.*;

@Slf4j
@DisallowConcurrentExecution
public class MyCronJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("MyCronJob Start................");
        log.info("Executing bash script..........");

        final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        final String homeDirectory = System.getProperty("user.home");

        if (isWindows) {
           log.info("=============WindowOS================" + homeDirectory);
        } else {
           log.info("=============LinuxOS================" + homeDirectory);
        }

        // Run a shell script
        final ClassLoader classLoader = MyCronJob.class.getClassLoader();
        final InputStream in = classLoader.getResourceAsStream("test.sh");

        final File workspace = new File(FileUtils.getTempDirectory(), System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10));
        if (!workspace.mkdirs()) {
            if (!workspace.exists() || !workspace.isDirectory()) {
                throw new RuntimeException( "could not read input stream");
            }
        }
        //copy to tmp and execute
        final File tmpFile = new File(workspace, "test.sh");
        try (OutputStream stream = new FileOutputStream(tmpFile)) {
            IOUtils.copy(in, stream);
        } catch (IOException e) {
            throw new RuntimeException("could not read input stream");
        }
        //FileUtils.copyInputStreamToFile(in, tmpFile);

        //check mod +x
        // command("chmod +x " + TMP_FILE, isDebug);
        //execute it
        Helper.command(String.format("%s version-1", tmpFile.getAbsolutePath()),true);

        log.info("=============COMPLETED================");
        log.info("MyCronJob End..................");
    }


}
