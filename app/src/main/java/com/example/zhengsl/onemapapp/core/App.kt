package com.example.zhengsl.onemapapp.core

import android.app.Application
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        //ArcGIS授权
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud8786962025,none,HC5X0H4AH540J9HSX159")
    }
}