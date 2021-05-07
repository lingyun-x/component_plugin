package com.lingyun.lib.component.plugin.tree

import org.junit.Test

import org.junit.Assert.*

/*
 * Created by mc_luo on 5/6/21 3:56 PM.
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
class TreeTest {

    @Test
    fun testTree() {

        val tree = Tree<String>()

        val node0 = Node<String>("node0", "0")
        val node1 = Node<String>("node1", "1")
        val node2 = Node<String>("node2", "2")
        val node3 = Node<String>("node3", "3")
        val node4 = Node<String>("node4", "4")
        val node5 = Node<String>("node5", "5")
        val node6 = Node<String>("node6", "6")
        val node7 = Node<String>("node7", "7")
        val node8 = Node<String>("node8", "8")
        val node9 = Node<String>("node9", "9")

        tree.addNode(node0, null)
        tree.addNode(node1, node0.nodeName)
        tree.addNode(node2, node1.nodeName)
        tree.addNode(node3, node2.nodeName)
        tree.addNode(node4, node1.nodeName)
        tree.addNode(node5, node4.nodeName)
        tree.addNode(node6, node4.nodeName)
        tree.addNode(node7, node6.nodeName)
        tree.addNode(node8, node6.nodeName)
        tree.addNode(node9, node8.nodeName)
        tree.addNode(node4,node9.nodeName)

        val closeresult = tree.findCloseCycle()

        println(closeresult)
        println(tree.print())
        assertEquals(true, closeresult != null)


    }
}