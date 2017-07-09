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

class PendingPost private constructor(
        var data: ByteArray?,
        var next: PendingPost?)
{
    companion object {
        private val pendingPostPool = mutableListOf<PendingPost>()

        fun obtainPendingPost(data: ByteArray): PendingPost {
            synchronized(pendingPostPool) {
                val size = pendingPostPool.size
                if (size > 0) {
                    val pendingPost = pendingPostPool.removeAt(size - 1)
                    pendingPost.data = data
                    pendingPost.next = null
                    return pendingPost
                }
            }
            return PendingPost(data, null)
        }

        fun releasePendingPost(pendingPost: PendingPost) {
            pendingPost.data = null
            pendingPost.next = null
            synchronized(pendingPostPool) {
                if (pendingPostPool.size < 10000) {
                    pendingPostPool.add(pendingPost)
                }
            }
        }
    }

}