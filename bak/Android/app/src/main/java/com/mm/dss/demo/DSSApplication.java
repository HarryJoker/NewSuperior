/*
 * Copyright 2015 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mm.dss.demo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.android.business.adapter.DataAdapterImpl;
import com.android.business.exception.BusinessException;
import com.mm.dss.demo.push.DSSPush;

import gov.android.com.superior.BuildConfig;

public class DSSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initDSSSdk();
    }

    private void initDSSSdk() {
        try {
            DataAdapterImpl.getInstance().createDataAdapter(BuildConfig.sdkPackage);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        loadLibrary();
//        try {
//            init();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void loadLibrary(){
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("CommonSDK");
        System.loadLibrary("Encrypt");
        if(BuildConfig.isVCloud) {
            System.loadLibrary("DSSCloudStream");
        } else if(BuildConfig.isExpress){
            System.loadLibrary("DPRTSPSDK");
            System.loadLibrary("DPExpressStream");
            System.loadLibrary("dsl");
            System.loadLibrary("dslalien");
        } else {
            System.loadLibrary("DPSStream");
            System.loadLibrary("dsl");
            System.loadLibrary("dslalien");
            System.loadLibrary("PlatformSDK");
            System.loadLibrary("DPRTSPSDK");
        }
    }



    private void init() throws Exception{
        DSSPush mDSSPush = new DSSPush();
        boolean bDSSOk = mDSSPush.init(this.getApplicationContext());
        EnvironmentHelper.getInstance().initEnvironment(this.getApplicationContext());
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    //限制字体尺寸，避免受系统字体大小影响导致界面不适配
    // limit the font size to avoid the interface mismatch caused by system font size.
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        return resources;
    }


    @Override
    public String getPackageName() {
        return super.getPackageName();
    }
}
