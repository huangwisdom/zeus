package com.ctrip.zeus.service.activate.impl;

import com.ctrip.zeus.dal.core.*;
import com.ctrip.zeus.service.activate.ActiveConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanqq on 2015/3/30.
 */
@Component("activeConfService")
public class ActiveConfServiceImpl implements ActiveConfService {
    @Resource
    private ConfGroupActiveDao confGroupActiveDao;
    @Resource
    private ConfSlbActiveDao confSlbActiveDao;

    private Logger logger = LoggerFactory.getLogger(ActiveConfServiceImpl.class);

    @Override
    public List<String> getConfGroupActiveContentByGroupIds(Long[] groupIds) throws Exception {


        List<ConfGroupActiveDo> l = confGroupActiveDao.findAllByGroupIds(groupIds, ConfGroupActiveEntity.READSET_FULL);

        List<String> res = new ArrayList<>();

        if (l==null)
        {
            logger.warn("No ConfAppActive for apps:"+groupIds.toString());
            return res;
        }

        for (ConfGroupActiveDo a : l)
        {
            res.add(a.getContent());
        }
        return res;
    }

    @Override
    public String getConfSlbActiveContentBySlbId(Long slbId) throws Exception {
         ConfSlbActiveDo d = confSlbActiveDao.findBySlbId(slbId, ConfSlbActiveEntity.READSET_FULL);
        if (d==null)
        {
            logger.warn("No conf slb active for SlbID: "+slbId);
            return null;
        }

        return d.getContent();
    }
}
