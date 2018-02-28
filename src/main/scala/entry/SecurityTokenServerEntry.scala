package entry

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.profile.IClientProfile
import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.http.MethodType
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse
import org.eclipse.jetty.server.handler.{DefaultHandler, HandlerList}
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.server.{Connector, Handler, Server, ServerConnector}
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import util.{GenerateSecurityToken, GenerateSignaturePolicy}


/**
  * Created by jeorch on 18-2-28.
  */
object SecurityTokenServerEntry extends App {

//    val endpoint = "sts.cn-beijing.aliyuncs.com"
//    val accessKeyId = "LTAIocDzvBsFxylm"
//    val accessKeySecret = "tzE4WoCYSxCU97K1M0PAa8paOQEYo2"
//    val roleArn = "acs:ram::1696525727043311:role/aliyunosstokengeneratorrole"
//    val roleSessionName = "alfred"
////    val policy = "{\n" + "    \"Version\": \"1\", \n" + "    \"Statement\": [\n" + "        {\n" + "            \"Action\": [\n" + "                \"oss:*\"\n" + "            ], \n" + "            \"Resource\": [\n" + "                \"acs:oss:*:*:*\" \n" + "            ], \n" + "            \"Effect\": \"Allow\"\n" + "        }\n" + "    ]\n" + "}"
//    try { // Init ACS Client
//        DefaultProfile.addEndpoint("", "", "Sts", endpoint)
//        val profile: IClientProfile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret)
//        val client = new DefaultAcsClient(profile)
//        val request = new AssumeRoleRequest()
//        request.setMethod(MethodType.POST)
//        request.setRoleArn(roleArn)
//        request.setRoleSessionName(roleSessionName)
////        request.setPolicy(policy) // Optional
//
//        val response:AssumeRoleResponse = client.getAcsResponse(request)
//        System.out.println("Expiration: " + response.getCredentials.getExpiration)
//        System.out.println("Access Key Id: " + response.getCredentials.getAccessKeyId)
//        System.out.println("Access Key Secret: " + response.getCredentials.getAccessKeySecret)
//        System.out.println("Security Token: " + response.getCredentials.getSecurityToken)
//        System.out.println("RequestId: " + response.getRequestId)
//    } catch {
//        case e: ClientException =>
//            System.out.println("Failed：")
//            System.out.println("Error code: " + e.getErrCode)
//            System.out.println("Error message: " + e.getErrMsg)
//            System.out.println("RequestId: " + e.getRequestId)
//    }

    val server = new Server()
    var port = 7091  //设置获取SecurityToken端口是：7091

    val connector = new ServerConnector(server)
    connector.setPort(port)
    println("server run on port:" + port)

    server.setConnectors(Array[Connector](connector))
    //设置handler根路径
    val webApiContext = new ServletContextHandler()
    webApiContext.setContextPath("/")
    webApiContext.addServlet(new ServletHolder(classOf[GenerateSecurityToken]), "/*")
    webApiContext.setSessionHandler(new SessionHandler())

    val handlers = new HandlerList()
    handlers.setHandlers(Array[Handler](webApiContext, new DefaultHandler()))
    //增加handlers并启动server
    server.setHandler(handlers)
    server.start
}
