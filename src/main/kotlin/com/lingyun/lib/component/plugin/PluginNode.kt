package com.lingyun.lib.component.plugin

import com.lingyun.lib.component.plugin.tree.Node
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*
* Created by mc_luo on 5/6/21 5:26 PM.
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
class PluginNode(val plugin: IPlugin) : Node<IPlugin>(plugin.pluginName, plugin) {

    @Volatile
    private var executeJob: Job? = null

    fun startAsync(context: PluginContext): Deferred<Any> {
        val job = Job()
        executeJob = job
        return plugin.async(job) {
            val cj = launch {
                for (child in childs()) {
                    launch {
                        (child as PluginNode).startAsync(context).await()
                    }
                }
            }

            cj.join()
            plugin.startAsync(context).await()
        }
    }

    fun cancelPlugin() {
        executeJob?.cancel()
        for (child in childs()) {
            (child as PluginNode).cancelPlugin()
        }
        plugin.cancelPlugin()
    }
}