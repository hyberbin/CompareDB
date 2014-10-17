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
package com.hyberbin.db;

import com.hyberbin.frame.StartFrame;
import org.jplus.hyb.database.config.DbConfig;
import org.jplus.hyb.database.config.SimpleConfigurator;
import org.jplus.hyb.database.transaction.AutoManager;
import org.jplus.hyb.database.transaction.IDbManager;

/**
 *
 * @author Hyberbin
 */
public class ConnectionUtil {


    public static IDbManager getMainConn() {
        IDbManager manager=new AutoManager("main");
        SimpleConfigurator.addConfigurator(new DbConfig(StartFrame.bzk.getUrl(), StartFrame.bzk.getUser(), StartFrame.bzk.getPassword(),"main"));
        return manager;
    }

    public static IDbManager getCompareConn() {
        IDbManager manager=new AutoManager("compare");
        SimpleConfigurator.addConfigurator(new DbConfig(StartFrame.bjk.getUrl(), StartFrame.bjk.getUser(), StartFrame.bjk.getPassword(),"compare"));
        return manager;
    }
}
