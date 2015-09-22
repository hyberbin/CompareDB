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
        String url=StartFrame.bzk.getUrl();
        String driver=null;
        if(url.contains("mysql")){
            url+="&characterEncoding=UTF-8&generateSimpleParameterMetadata=true&useOldAliasMetadataBehavior=true&UseOldSyntax=true";
            driver=DbConfig.DRIVER_MYSQL;
        }else{
            driver=DbConfig.DRIVER_ORACLE;
        }
        DbConfig main = new DbConfig(driver,url, StartFrame.bzk.getUser(), StartFrame.bzk.getPassword(), "main");
        SimpleConfigurator.addConfigurator(main);
        return new AutoManager("main");
    }

    public static IDbManager getCompareConn() {
        String url=StartFrame.bjk.getUrl();
        String driver=null;
        if(url.contains("mysql")){
            url+="&characterEncoding=UTF-8&generateSimpleParameterMetadata=true&useOldAliasMetadataBehavior=true&UseOldSyntax=true";
            driver=DbConfig.DRIVER_MYSQL;
        }else{
            driver=DbConfig.DRIVER_ORACLE;
        }
        SimpleConfigurator.addConfigurator(new DbConfig(driver,url, StartFrame.bjk.getUser(), StartFrame.bjk.getPassword(),"compare"));
        return new AutoManager("compare");
    }
}
