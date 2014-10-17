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
package com.hyberbin.bean;

import javax.persistence.Table;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
@Table(name = "dblist")

public class DbLinkBean {
    private Integer id;
    private String lable;
    private String user;
    private String url;
    private String password;

    public DbLinkBean() {
    }

    public DbLinkBean(String lable, String user, String url,String password) {
        this.lable = lable;
        this.user = user;
        this.url = url;        
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getHost() {
        if(ObjectHelper.isNullOrEmptyString(url))return "";
        String host=url.toLowerCase().replaceFirst("jdbc:mysql://", "");
        host=host.substring(0, host.indexOf("/"));
        if(host.contains(",")){
            host=host.split(",")[1];
        }
        if(host.contains(":")){
            host=host.substring(0,host.indexOf(":"));
        }
        return host;
    }
    
    public String getHostAndPort() {
        if(ObjectHelper.isNullOrEmptyString(url))return "";
        String host=url.toLowerCase().replaceFirst("jdbc:mysql://", "");
        host=host.substring(0, host.indexOf("/"));
        if(host.contains(",")){
            host=host.split(",")[1];
        }
        return host;
    }
    
    public String getPort() {
        String host=getHostAndPort();
        return host.contains(":")?host.split(":")[1]:"3306";
    }
    
}


