package com.lingyun.lib.component.plugin.tree

import com.lingyun.lib.component.plugin.NodeNotFindException


/*
* Created by mc_luo on 4/8/21 2:45 PM.
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

open class Tree<T> {
    val root: Node<T> = Node("root")

    fun removeNode(node: Node<T>) {
        root.removeDependencyChild(node)
    }

    fun removeNode(nodeName: String) {
        root.removeDependencyChild(nodeName)
    }

    fun addNode(node: Node<T>, parentName: String?) {
        val targetNode = if (parentName == null) root else root.findNodeByName(parentName)

        if (targetNode == null) {
            throw NodeNotFindException("Node: $parentName not find")
        }
        targetNode.addChild(node)
    }

    fun findNodeByName(nodeName: String): Node<T>? {
        return root.findNodeByName(nodeName)
    }

    fun print(): String {
        val strs = root.printAll()
        return strs.joinToString("\r\n")
    }

    fun findCloseCycle(): CircleNodeResult<T>? {
        if (root.childSize() == 0) return null
        val fastPoints = ArrayList<Node<T>>()
        val slowPoints = ArrayList<Node<T>>()
        val tempPoints = ArrayList<Node<T>>()


        slowPoints.add(root)
        fastPoints.add(root)

        var step = 0

        while (fastPoints.isNotEmpty()) {
            //slow point move 1
            for (node in slowPoints) {
                tempPoints.addAll(node.childs())
            }
            slowPoints.clear()
            slowPoints.addAll(tempPoints)
            tempPoints.clear()


            //fast point move 2
            for (i in 0..1) {
                for (node in fastPoints) {
                    tempPoints.addAll(node.childs())
                }
                fastPoints.clear()
                fastPoints.addAll(tempPoints)
                tempPoints.clear()
            }

//            println("===  step:${step++}  ===")
//            println("slowPoints: ${slowPoints.map { it.nodeName }.joinToString("  ")}")
//            println("fastPoints: ${fastPoints.map { it.nodeName }.joinToString("  ")}")

            //check close circle
            for (node in slowPoints) {
                if (fastPoints.contains(node)) {
                    //to find close circle start node
                    val result = findCloseCircleStart(root, node)
                    if (result != null) return result
                }
            }

        }

        return null
    }


    private fun findCloseCircleStart(
        rootNode: Node<T>,
        meetNode: Node<T>
    ): CircleNodeResult<T>? {
//        println("==== findCloseCircleStart meetNode:${meetNode.nodeName}====")
        val points1 = mutableListOf<Node<T>>(rootNode)
        val points2 = mutableListOf<Node<T>>(meetNode)
        val tempPoints = ArrayList<Node<T>>()


        var step = 0
        var startPoint: Node<T>? = null
        while (startPoint == null && points1.isNotEmpty() && points2.isNotEmpty()) {
            for (point in points1) {
                if (points2.contains(point)) {
                    startPoint = point
                    break
                }

                tempPoints.addAll(point.childs())
            }
            points1.clear()
            points1.addAll(tempPoints)
            tempPoints.clear()

            for (point in points2) {
                tempPoints.addAll(point.childs())
            }
            points2.clear()
            points2.addAll(tempPoints)
            tempPoints.clear()

//            println("==== findCloseCircleStart step:${step++}====")
//            println("findCloseCircleStart point1 ${points1.map { it.nodeName }.joinToString(" ")}")
//            println("findCloseCircleStart point2 ${points2.map { it.nodeName }.joinToString(" ")}")
        }

        if (startPoint != null) {
            points1.clear()
            points1.add(startPoint)
            var endPoint: Node<T>? = null

            tempPoints.clear()
            while (endPoint == null && points1.isNotEmpty()) {
                for (point in points1) {
                    if (point.childs().contains(startPoint)) {
                        endPoint = point
                        break
                    }
                    tempPoints.addAll(point.childs())
                }

                points1.clear()
                points1.addAll(tempPoints)
                tempPoints.clear()
            }

            if (endPoint != null) {
                return CircleNodeResult(startPoint, endPoint)
            }

        }

        return null
    }

    data class CircleNodeResult<T>(val circleStartNode: Node<T>, val circleEndNode: Node<T>)
}