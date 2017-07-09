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

package moe.codeest.rxsocketclient.meta

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/8
 * @description:
 */

class SocketOption(
        val mHeartBeatConfig: HeartBeatConfig?,
        val mHead: ByteArray?,
        val mTail: ByteArray?
) {

    private constructor(builder: Builder) : this(builder.mHeartBeatConfig, builder.mHead, builder.mTail)

    class Builder {
        var mHeartBeatConfig: HeartBeatConfig? = null
            private set

        var mHead: ByteArray? = null
            private set

        var mTail: ByteArray? = null
            private set

        fun setHeartBeat(data: ByteArray, interval: Long) = apply { this.mHeartBeatConfig = HeartBeatConfig(data, interval) }

        fun setHead(head: ByteArray) = apply { this.mHead = head }

        fun setTail(tail: ByteArray) = apply { this.mTail = tail }

        fun build() = SocketOption(this)
    }

    class HeartBeatConfig(var data: ByteArray?, var interval: Long)
}