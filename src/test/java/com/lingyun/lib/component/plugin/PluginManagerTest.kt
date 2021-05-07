package com.lingyun.lib.component.plugin

import kotlinx.coroutines.*
import org.junit.Test

/*
 * Created by mc_luo on 5/7/21 9:48 AM.
 * Copyright (c) 2021 The LingYun Authors. All rights reserved.
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
 */
class PluginManagerTest {

    @Test
    fun testPluginManager() = runBlocking {
        val p1 = SamplePlugin("1")
        val p2 = SamplePlugin("2")
        val p3 = SamplePlugin("3")
        val p4 = SamplePlugin("4")
        val p5 = SamplePlugin("5")
        val p6 = SamplePlugin("6")
        val p7 = SamplePlugin("7")
        val p8 = SamplePlugin("8")
        val p9 = SamplePlugin("9")
        val p10 = SamplePlugin("10")

        PluginManager.registerPlugin(p1)
        PluginManager.registerPlugin(p2)
        PluginManager.registerPlugin(p3)
        PluginManager.registerPlugin(p4)
        PluginManager.registerPlugin(p5)
        PluginManager.registerPlugin(p6)
        PluginManager.registerPlugin(p7)
        PluginManager.registerPlugin(p8)
        PluginManager.registerPlugin(p9)
        PluginManager.registerPlugin(p10)

        PluginManager.dependenciesOn(p1, p2)
        PluginManager.dependenciesOn(p1, p3)
        PluginManager.dependenciesOn(p2, p4)
        PluginManager.dependenciesOn(p2, p5)
        PluginManager.dependenciesOn(p6, p7)
        PluginManager.dependenciesOn(p6, p8)
        PluginManager.dependenciesOn(p9, p2)
        PluginManager.dependenciesOn(p10, p9)
//        PluginManager.dependenciesOn(p5, p10)

        PluginManager.checkCloseCircle()
        println(PluginManager.printNode())
        PluginManager.optimizationHierarchy()

        println(PluginManager.printNode())

        PluginManager.startPlugin(PluginContext())

        delay(10 * 1000)
        println("finish")
    }
}

class SamplePlugin(pluginName: String) :
    AbstractPlugin(CoroutineScope(Dispatchers.IO + SupervisorJob()), pluginName) {

    override suspend fun config(context: PluginContext) {
        println("plugin:$pluginName config")
        delay(200)
    }

    override fun executeAsync(context: PluginContext): Deferred<Any> {
        println("plugin:$pluginName executeAsync")
        return async {
            delay(1000)
        }
    }
}