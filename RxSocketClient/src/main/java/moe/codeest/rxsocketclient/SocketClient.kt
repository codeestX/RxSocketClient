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

import io.reactivex.Observable
import moe.codeest.rxsocketclient.meta.DataWrapper
import moe.codeest.rxsocketclient.meta.SocketConfig
import moe.codeest.rxsocketclient.meta.SocketOption
import moe.codeest.rxsocketclient.meta.ThreadStrategy
import moe.codeest.rxsocketclient.post.AsyncIPoster
import moe.codeest.rxsocketclient.post.IPoster
import moe.codeest.rxsocketclient.post.SyncIPoster
import java.net.Socket
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/9
 * @description:
 */

class SocketClient(val mConfig: SocketConfig) {

    var mSocket: Socket = Socket()
    var mOption: SocketOption? = null
    lateinit var mObservable: Observable<DataWrapper>
    lateinit var mIPoster: IPoster
    var mExecutor: Executor = Executors.newCachedThreadPool()

    fun option(option: SocketOption): SocketClient {
        mOption = option
        return this
    }

    fun connect(): Observable<DataWrapper> {
        mObservable = SocketObservable(mConfig, mSocket)
        mIPoster = if (mConfig.mThreadStrategy == ThreadStrategy.ASYNC) AsyncIPoster(this, mExecutor) else SyncIPoster(this, mExecutor)
        initHeartBeat()
        return mObservable
    }

    fun disconnect() {
        if (mObservable is SocketObservable) {
            (mObservable as SocketObservable).close()
        }
    }

    private fun initHeartBeat() {
        mOption?.apply {
            if (mHeartBeatConfig != null) {
            val disposable = Observable.interval(mHeartBeatConfig.interval, TimeUnit.MILLISECONDS)
                        .subscribe({
                            sendData(mHeartBeatConfig.data?: ByteArray(0))
                        })
                if (mObservable is SocketObservable) {
                    (mObservable as SocketObservable).setHeartBeatRef(disposable)
                }
            }
        }
    }

    fun sendData(data: ByteArray) {
        mOption?.apply {
            if (mHead != null || mTail != null) {
                var result: String = data.toString()
                mHead?.let {
                    if (mHead.isNotEmpty()) {
                        mHead.toString().plus(result)
                    }
                }
                mTail?.let {
                    if (mTail.isNotEmpty()) {
                        result.plus(mTail.toString())
                    }
                }
                mIPoster.enqueue(result.toByteArray(charset = mConfig.mCharset))
                    return@sendData
            }
        }
        mIPoster.enqueue(data)
    }

    fun sendData(string: String) {
        sendData(string.toByteArray(charset = mConfig.mCharset))
    }
}