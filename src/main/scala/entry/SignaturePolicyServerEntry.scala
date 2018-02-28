package entry

import org.eclipse.jetty.server.{Connector, Handler, Server, ServerConnector}
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import util.GenerateSignaturePolicy


/**
  * Created by jeorch on 18-2-27.
  */
object SignaturePolicyServerEntry extends App {

    val server = new Server()
    var port = 7090  //设置获取Policy端口是：7090

    val connector = new ServerConnector(server)
    connector.setPort(port)
    println("server run on port:" + port)

    server.setConnectors(Array[Connector](connector))
    //设置handler根路径
    val webApiContext = new ServletContextHandler()
    webApiContext.setContextPath("/")
    webApiContext.addServlet(new ServletHolder(classOf[GenerateSignaturePolicy]), "/*")
    webApiContext.setSessionHandler(new SessionHandler())

    val handlers = new HandlerList()
    handlers.setHandlers(Array[Handler](webApiContext, new DefaultHandler()))
    //增加handlers并启动server
    server.setHandler(handlers)
    server.start

}
