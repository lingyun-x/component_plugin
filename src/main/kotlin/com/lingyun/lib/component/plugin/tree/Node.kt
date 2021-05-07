package com.lingyun.lib.component.plugin.tree

import com.lingyun.lib.component.plugin.TreeException


/*
* Created by mc_luo on 4/8/21 2:38 PM.
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
open class Node<T>(val nodeName: String, val value: T? = null) {
//    var preNode: Node<T>? = null

    private val childs: ArrayList<Node<T>> = ArrayList()

    fun childSize() = childs.size

    fun addChild(node: Node<T>) {
        if (childs.contains(node)) {
            throw TreeException("can not add child twice")
        }

        if (childs.filter { it.nodeName == node.nodeName }.isNotEmpty()) {
            throw TreeException("can not add the same name node")
        }
        childs.add(node)
    }

    fun removeChild(node: Node<T>) {
        childs.remove(node)
    }

    fun removeChild(nodeName: String) {
        val toremove = childs.filter { it.nodeName == nodeName }
        childs.removeAll(toremove)
    }

    fun removeDependencyChild(node: Node<T>) {
        childs.remove(node)
        childs.forEach { it.removeChild(node) }
    }

    fun removeDependencyChild(nodeName: String) {
        val toremove = childs.filter { it.nodeName == nodeName }
        childs.removeAll(toremove)
        childs.forEach { it.removeChild(nodeName) }
    }


    fun childs(): List<Node<T>> {
        return childs
    }

    fun printAll(): List<String> {
        val strs = ArrayList<String>()
        strs.add("+--- $nodeName")
        for (child in childs) {
            val childStrs = child.printAll()

            for (str in childStrs) {
                strs.add("|    $str")
            }
        }
        return strs
    }

    fun findNodeByName(nodeName: String): Node<T>? {
        if (this.nodeName == nodeName) {
            return this
        }
        for (child in childs) {
            val find = child.findNodeByName(nodeName)
            if (find != null) {
                return find
            }
        }
        return null
    }
}