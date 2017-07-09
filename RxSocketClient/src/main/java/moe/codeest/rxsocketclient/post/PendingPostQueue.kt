/*
 * Copyright (C) 2017 codeestX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package moe.codeest.rxsocketclient.post

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/9
 * @description:
 */

class PendingPostQueue {
    private var head: PendingPost? = null
    private var tail: PendingPost? = null
    private val lock = java.lang.Object()


    fun enqueue(pendingPost: PendingPost) = synchronized(lock) {
        if (tail != null) {
            tail!!.next = pendingPost
            tail = pendingPost
        } else if (head == null) {
            tail = pendingPost
            head = tail
        } else {
            throw IllegalStateException("Head present, but no tail")
        }
        lock.notifyAll()
    }

    @Synchronized fun poll(): PendingPost? {
        val pendingPost = head
        head?.let {
            head = head!!.next
            if (head == null) {
                tail = null
            }
        }
        return pendingPost
    }

    @Throws(InterruptedException::class)
    fun poll(maxMillisToWait: Int): PendingPost? = synchronized(lock){
        if (head == null) {
            lock.wait(maxMillisToWait.toLong())
        }
        return poll()
    }

}