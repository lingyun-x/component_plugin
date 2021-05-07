package com.lingyun.lib.component.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

/*
* Created by mc_luo on 4/8/21 2:37 PM.
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
interface IPlugin : CoroutineScope {
    val pluginName: String
    fun startAsync(context: PluginContext): Deferred<Any>

    fun cancelPlugin()
    suspend fun config(context: PluginContext)
    fun executeAsync(context: PluginContext): Deferred<Any>
    fun restartAsync(context: PluginContext): Deferred<Any>

    fun <T> registerService(clazz: Class<T>, service: T)
    fun <T> getConfigService(clazz: Class<T>): T
    fun <T> getServiceAsync(clazz: Class<T>): Deferred<T>

    fun awaitExecuteCompleteAsync(): Deferred<Any>
}