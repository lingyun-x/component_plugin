package com.lingyun.lib.component.plugin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.util.concurrent.ConcurrentHashMap

/*
* Created by mc_luo on 5/6/21 4:32 PM.
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
abstract class AbstractPlugin(coroutineScope: CoroutineScope, override val pluginName: String) :
    IPlugin,
    CoroutineScope by coroutineScope {

    private val services = ConcurrentHashMap<String, Any>()

    private val stateFlow = MutableSharedFlow<PluginState>(replay = 1, 16)
    private var state: PluginState = PluginState.IDLE


    @Volatile
    private var executeJob: Job? = null

    override fun startAsync(context: PluginContext): Deferred<Any> {
        val job = Job()
        executeJob = job
        return async(job, start = CoroutineStart.UNDISPATCHED) {
            state = PluginState.BEFORE_CONFIG
            stateFlow.tryEmit(PluginState.BEFORE_CONFIG)

            config(context)

            state = PluginState.BEFORE_EXECUTE
            stateFlow.tryEmit(PluginState.BEFORE_EXECUTE)

            executeAsync(context).await()

            state = PluginState.AFTER_EXECUTE
            stateFlow.tryEmit(PluginState.AFTER_EXECUTE)

        }
    }

    override fun restartAsync(context: PluginContext): Deferred<Any> {
        services.clear()
        return startAsync(context)
    }

    override fun <T> registerService(clazz: Class<T>, service: T) {
        services[clazz.simpleName] = service!!
    }

    override fun <T> getConfigService(clazz: Class<T>): T {
        return services[clazz.simpleName] as T
    }

    override fun <T> getServiceAsync(clazz: Class<T>): Deferred<T> {
        return async {
            stateFlow.first { it == PluginState.AFTER_EXECUTE }
            services[clazz.simpleName] as T
        }
    }

    override fun awaitExecuteCompleteAsync(): Deferred<Any> {
        return async {
            stateFlow.first { it == PluginState.AFTER_EXECUTE }
        }
    }

    override fun cancelPlugin() {
        services.clear()
        executeJob?.cancel()
    }

    override fun getAllServices(): Map<String, Any> = services
}