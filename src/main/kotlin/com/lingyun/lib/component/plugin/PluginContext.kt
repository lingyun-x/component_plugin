package com.lingyun.lib.component.plugin

/*
* Created by mc_luo on 5/6/21 4:29 PM.
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
open class PluginContext {

    private val properties = HashMap<String, Any>()

    fun setValue(key: String, value: Any) {
        properties[key] = value
    }

    fun <T> getValue(key: String): T {
        return properties[key] as T
    }
}