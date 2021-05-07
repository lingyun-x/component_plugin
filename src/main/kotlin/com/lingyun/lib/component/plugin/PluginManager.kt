package com.lingyun.lib.component.plugin

import com.lingyun.lib.component.plugin.tree.Tree
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.jvm.Throws

/*
* Created by mc_luo on 5/6/21 5:28 PM.
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
object PluginManager {

    private val pluginTree = Tree<IPlugin>()

    private var pluginJob: Job? = null

    fun registerPlugin(plugin: IPlugin) {
        pluginTree.addNode(PluginNode(plugin), null)
    }

    fun dependenciesOn(plugin: IPlugin, dependency: IPlugin) {
        val oldDependencyNode = pluginTree.findNodeByName(dependency.pluginName)

        println("dependenciesOn plugin:${plugin.pluginName} dependency:${dependency.pluginName} oldDependencyNode exist?:${oldDependencyNode != null}")

        if (oldDependencyNode != null) {
            if (oldDependencyNode.value != dependency) {
                throw IllegalArgumentException("Can not register plugin by same name:${dependency.pluginName}")
            }
        }

        val oldPluginNode = pluginTree.findNodeByName(plugin.pluginName)
        val pluginNode = oldPluginNode ?: PluginNode(plugin)

        if (oldPluginNode != null) {
            if (oldPluginNode.value != plugin) {
                throw IllegalArgumentException("Can not register plugin by same name:${plugin.pluginName}")
            }
        }

        var dependencyFind = false
        for (child in pluginNode.childs()) {
            if (child.value == dependency) {
                dependencyFind = true
                break
            }
        }

        if (!dependencyFind) {
            val dependencyNode = oldDependencyNode ?: PluginNode(dependency)
            pluginNode.addChild(dependencyNode)
        }
    }

    private fun _optimizationRootHierarchy(pluginNode: PluginNode) {
        val pluginName = pluginNode.plugin.pluginName

        val otherPlugin =
            pluginTree.root.childs().map { it as PluginNode }.filter { it != pluginNode }
        val otherExist = findChildPlugin(pluginName, otherPlugin) != null
        if (otherExist) {
            pluginTree.root.removeChild(pluginNode)
        }
    }

    fun optimizationHierarchy() {
        val optimizationChilds = pluginTree.root.childs().map { it as PluginNode }

        optimizationChilds.forEach { pluginNode ->
            _optimizationRootHierarchy(pluginNode)
        }
    }

    private fun findChildPlugin(pluginName: String, pluginNodes: List<PluginNode>): PluginNode? {
        pluginNodes.forEach {
            it.childs().forEach {
                val result = it.findNodeByName(pluginName) as PluginNode?
                if (result != null) return result
            }
        }

        return null
    }

    fun getPlugin(pluginName: String): IPlugin? {
        if (pluginName == "root") return pluginTree.root.value
        return (pluginTree.findNodeByName(pluginName) as PluginNode).plugin
    }

    fun getPluginNode(pluginName: String): PluginNode {
        return pluginTree.findNodeByName(pluginName) as PluginNode
    }

    fun startPlugin(context: PluginContext) {
        pluginJob = GlobalScope.launch {
            for (node in pluginTree.root.childs()) {
                launch {
                    (node as PluginNode).startAsync(context).await()
                }
            }
        }
    }

    fun restart(context: PluginContext) {
        pluginJob?.cancel()
        for (node in pluginTree.root.childs()) {
            (node as PluginNode).cancelPlugin()
        }
        startPlugin(context)
    }

    fun printNode(): String {
        return pluginTree.print()
    }

    @Throws(TreeException::class)
    fun checkCloseCircle() {
        val result = pluginTree.findCloseCycle()

        if (result != null) {
            throw TreeCloseCircleException("Tree close circle start:${result.circleStartNode.nodeName} end:${result.circleEndNode.nodeName}")
        }
    }

}