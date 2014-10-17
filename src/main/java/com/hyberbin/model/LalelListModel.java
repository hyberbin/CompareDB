/*
 * Copyright 2014 Hyberbin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Email:hyberbin@qq.com
 */
package com.hyberbin.model;

import com.hyberbin.bean.DbLinkBean;
import com.hyberbin.db.SqliteDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import org.jplus.hyb.log.Logger;
import org.jplus.hyb.log.LoggerManager;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public final class LalelListModel extends DefaultComboBoxModel {

    protected static Logger log = LoggerManager.getLogger(LalelListModel.class);
    private final Map<String, DbLinkBean> map = new HashMap();

    public LalelListModel() {
        getList();
    }

    public void getList() {
        List<DbLinkBean> dblinkInfo = SqliteDao.getDblinkInfo();
        addElement("请选择");
        if (ObjectHelper.isNotEmpty(dblinkInfo)) {
            map.clear();
            log.debug("dblinkInfo size:" + dblinkInfo.size());
            for (DbLinkBean bean : dblinkInfo) {
                addElement(bean.getLable());
                log.debug("add " + bean.getLable());
                map.put(bean.getLable(), bean);
            }
        }
    }

    public DbLinkBean getDbLinkBean(String lable) {
        DbLinkBean get = map.get(lable);
        return get == null ? new DbLinkBean("", "", "", "") : get;
    }

}
