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

package moe.codeest.rxsocketclient

import io.reactivex.functions.Consumer
import moe.codeest.rxsocketclient.meta.DataWrapper
import moe.codeest.rxsocketclient.meta.SocketState


/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/9
 * @description:
 */

abstract class SocketSubscriber : Consumer<DataWrapper> {

    override fun accept(t: DataWrapper) {
        when (t.state) {
            SocketState.CONNECTING -> onResponse(t.data)
            SocketState.OPEN -> onConnected()
            SocketState.CLOSE -> onDisconnected()
        }

    }

    abstract fun onConnected()

    abstract fun onDisconnected()

    abstract fun onResponse(data: ByteArray)
}