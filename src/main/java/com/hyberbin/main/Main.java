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
package com.hyberbin.main;

import com.hyberbin.frame.StartFrame;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jplus.hyb.database.config.ConfigCenter;
import org.jplus.hyb.log.LocalLogger;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //设置本属性将改变窗口边框样式定义
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        if(ObjectHelper.isNotEmpty(args)){
            for(String arg:args){
                arg=arg.toLowerCase();
                if(arg.startsWith("-d")){
                    LocalLogger.setLevel(LocalLogger.DEBUG);
                }else if(arg.startsWith("-t")){
                    LocalLogger.setLevel(LocalLogger.TRACE);
                }else if(arg.startsWith("-i")){
                    LocalLogger.setLevel(LocalLogger.INFO);
                }else if(arg.startsWith("-e")){
                    LocalLogger.setLevel(LocalLogger.ERROR);
                }
            }
        }
        ConfigCenter.INSTANCE.getSqlout().setSqlout(true);
        new StartFrame().setVisible(true);
    }
}
