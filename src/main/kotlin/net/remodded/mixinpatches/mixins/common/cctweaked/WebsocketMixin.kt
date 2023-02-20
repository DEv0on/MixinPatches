package net.remodded.mixinpatches.mixins.common.cctweaked

import dan200.computercraft.ComputerCraft
import dan200.computercraft.core.apis.http.HTTPRequestException
import dan200.computercraft.core.apis.http.NetworkUtils
import dan200.computercraft.core.apis.http.Resource
import dan200.computercraft.core.apis.http.websocket.Websocket
import dan200.computercraft.core.apis.http.websocket.WebsocketHandler
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler
import io.netty.handler.proxy.HttpProxyHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.net.InetSocketAddress
import java.net.URI

@Mixin(Websocket::class)
abstract class WebsocketMixin : Resource<Websocket>(null) {

    @Shadow
    private lateinit var connectFuture: ChannelFuture

    @Shadow
    private lateinit var uri: URI

    @Shadow
    private lateinit var headers: HttpHeaders

    @Shadow
    abstract fun failure(cause: String)

    @Overwrite
    private fun doConnect() {
        // If we're cancelled, abort.
        if (isClosed()) return
        try {
            val ssl: Boolean = uri.getScheme().equals("wss", ignoreCase = true)
            val socketAddress = NetworkUtils.getAddress(uri.getHost(), uri.getPort(), ssl)
            val sslContext = if (ssl) NetworkUtils.getSslContext() else null

            // getAddress may have a slight delay, so let's perform another cancellation check.
            if (isClosed()) return
            connectFuture = Bootstrap()
                    .group(NetworkUtils.LOOP_GROUP)
                    .channel(NioSocketChannel::class.java)
                    .handler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            val p = ch.pipeline()
                            if (sslContext != null) {
                                p.addLast(sslContext.newHandler(ch.alloc(), uri.getHost(), socketAddress.port))
                            }
                            val handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, headers,
                                    if (ComputerCraft.httpMaxWebsocketMessage == 0) Websocket.MAX_MESSAGE_SIZE else ComputerCraft.httpMaxWebsocketMessage
                            )
                            p.addLast(
                                    HttpClientCodec(),
                                    HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    WebsocketHandler(this@WebsocketMixin as Websocket, handshaker)
                            )
                            if (Core.httpProxyEnabled) {
                                p.addFirst(HttpProxyHandler(InetSocketAddress(Core.httpProxyAddress, Core.httpProxyPort)))
                            }
                        }
                    })
                    .remoteAddress(socketAddress)
                    .connect()
                    .addListener(GenericFutureListener { c: Future<in Void?> -> if (!c.isSuccess) failure(c.cause().message!!) })

            // Do an additional check for cancellation
            checkClosed()
        } catch (e: HTTPRequestException) {
            failure(e.message!!)
        } catch (e: Exception) {
            failure("Could not connect")
            if (ComputerCraft.logPeripheralErrors) ComputerCraft.log.error("Error in websocket", e)
        }
    }
}