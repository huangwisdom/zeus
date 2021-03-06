package com.ctrip.zeus.nginx.impl;

import com.ctrip.zeus.nginx.LocalValidate;
import com.ctrip.zeus.nginx.entity.NginxResponse;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by fanqq on 2015/6/25.
 */
@Component("localValidate")
public class LocalValidateImpl implements LocalValidate{



    private Logger LOGGER = LoggerFactory.getLogger(LocalValidateImpl.class);
    @Override
    public boolean pathExistValidate(String path , boolean isDirs) throws Exception {
        File pathFile = new File(path);
        if (pathFile.isDirectory()==isDirs)
        {
            LOGGER.info("path:"+path+" is exists :"+pathFile.exists());
            return pathFile.exists();
        }else
        {
            LOGGER.warn("path:"+path+"is directory :"+pathFile.isDirectory()+";isDirs="+isDirs);
            return false;
        }
    }

    @Override
    public NginxResponse nginxIsUp(String nginxBinPath) throws Exception {
        File pidFile = new File(nginxBinPath+"/../logs/nginx.pid");
        String nginxPid = null;
        NginxResponse response = new NginxResponse();
        if (pidFile.exists()&&pidFile.isFile())
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(pidFile));
                nginxPid = br.readLine();
            }catch (IOException e)
            {
                LOGGER.error("Read nginx.pid file error!Validate Fail!");
            }
            if (nginxPid==null){
                response.setSucceed(false).setOutMsg("nginx.pid file is empty!");
                return response;
            }
            String command = "ps "+nginxPid;
            LOGGER.info("command:"+command);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            CommandLine commandline = CommandLine.parse(command);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
            exec.setStreamHandler(streamHandler);
            int exitVal = exec.execute(commandline);
            String out = outputStream.toString("UTF-8");
            String error = errorStream.toString("UTF-8");

            response.setOutMsg(out);
            response.setErrMsg(error);
            response.setSucceed(0==exitVal);
            LOGGER.info(response.toString());
            return response;
        }else {
            LOGGER.error("Not found nginx.pid file");
            response.setSucceed(false).setOutMsg("Not found nginx.pid file!");
            return response;
        }
    }
}
