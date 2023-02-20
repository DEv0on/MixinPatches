package net.remodded.mixinpatches.mixins.common.cctweaked

import dan200.computercraft.ComputerCraft
import dan200.computercraft.core.apis.IAPIEnvironment
import dan200.computercraft.core.apis.http.HTTPRequestException
import dan200.computercraft.core.apis.http.NetworkUtils
import dan200.computercraft.core.apis.http.Resource
import dan200.computercraft.core.apis.http.request.HttpRequest
import dan200.computercraft.core.apis.http.request.HttpRequestHandler
import dan200.computercraft.core.tracking.TrackingField
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpContentDecompressor
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.proxy.HttpProxyHandler
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.TimeUnit

@Mixin(HttpRequest::class)
abstract class HttpRequestMixin : Resource<HttpRequest>(null) {

    @Shadow
    private lateinit var environment: IAPIEnvironment

    @Shadow
    private lateinit var headers: HttpHeaders

    @Shadow
    private lateinit var postBuffer: ByteBuf

    @Shadow
    private lateinit var currentRequest: HttpRequestHandler

    @Shadow
    private lateinit var connectFuture: ChannelFuture

    @Shadow
    abstract fun failure(cause: Throwable)

    @Shadow
    abstract fun failure(cause: String)

    @Overwrite
    private fun doRequest(uri: URI, method: HttpMethod) {
        // If we're cancelled, abort.
        if (isClosed()) return
        try {
            val ssl = uri.scheme.equals("https", ignoreCase = true)
            val socketAddress = NetworkUtils.getAddress(uri.host, uri.port, ssl)
            val sslContext = if (ssl) NetworkUtils.getSslContext() else null

            // getAddress may have a slight delay, so let's perform another cancellation check.
            if (isClosed()) return

            // Add request size to the tracker before opening the connection
            environment.addTrackingChange(TrackingField.HTTP_REQUESTS, 1)
            environment.addTrackingChange(TrackingField.HTTP_UPLOAD, HttpRequest.getHeaderSize(headers) + postBuffer.capacity())

            val handler = HttpRequestHandler::class.java.declaredConstructors.first().newInstance(this as HttpRequest, uri, method) as HttpRequestHandler
            currentRequest = handler

            connectFuture = Bootstrap()
                    .group(NetworkUtils.LOOP_GROUP)
                    .channelFactory(object : ChannelFactory<NioSocketChannel> {
                        override fun newChannel(): NioSocketChannel {
                            return NioSocketChannel()
                        }
                    })
                    .handler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            if (ComputerCraft.httpTimeout > 0) {
                                ch.config().connectTimeoutMillis = ComputerCraft.httpTimeout
                            }
                            val p = ch.pipeline()
                            if (sslContext != null) {
                                p.addLast(sslContext.newHandler(ch.alloc(), uri.host, socketAddress.port))
                            }
                            if (ComputerCraft.httpTimeout > 0) {
                                p.addLast(ReadTimeoutHandler(ComputerCraft.httpTimeout.toLong(), TimeUnit.MILLISECONDS))
                            }
                            p.addLast(
                                    HttpClientCodec(),
                                    HttpContentDecompressor(),
                                    handler
                            )
                            if (Core.httpProxyEnabled) {
                                p.addFirst(HttpProxyHandler(InetSocketAddress(Core.httpProxyAddress, Core.httpProxyPort)))
                            }
                        }
                    })
                    .remoteAddress(socketAddress)
                    .connect()
                    .addListener(GenericFutureListener { c: Future<in Void?> -> if (!c.isSuccess) failure(c.cause()) })

            // Do an additional check for cancellation

            checkClosed()
        } catch (e: HTTPRequestException) {
            failure(e.message!!)
        } catch (e: Exception) {
            failure("Could not connect")
            if (ComputerCraft.logPeripheralErrors) ComputerCraft.log.error("Error in HTTP request", e)
        }
    }
}