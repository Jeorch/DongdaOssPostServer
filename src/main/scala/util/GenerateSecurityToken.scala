package util

import java.sql.Date
import java.util
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.model.{MatchMode, PolicyConditions}
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.{DefaultProfile, IClientProfile}
import com.aliyuncs.sts.model.v20150401.{AssumeRoleRequest, AssumeRoleResponse}
import entry.SecurityTokenServerEntry._
import org.json.JSONObject

/**
  * Created by jeorch on 18-2-28.
  */
class GenerateSecurityToken  extends HttpServlet{

    override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        val endpoint = "*"
        val accessKeyId = "*"
        val accessKeySecret = "*"
        val roleArn = "*"
        val roleSessionName = "*"
//    val policy = "{\n" + "    \"Version\": \"1\", \n" + "    \"Statement\": [\n" + "        {\n" + "            \"Action\": [\n" + "                \"oss:*\"\n" + "            ], \n" + "            \"Resource\": [\n" + "                \"acs:oss:*:*:*\" \n" + "            ], \n" + "            \"Effect\": \"Allow\"\n" + "        }\n" + "    ]\n" + "}"
        try {
            DefaultProfile.addEndpoint("", "", "Sts", endpoint)
            val profile: IClientProfile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret)
            val client = new DefaultAcsClient(profile)
            val assumeRoleRequest = new AssumeRoleRequest()
            assumeRoleRequest.setMethod(MethodType.POST)
            assumeRoleRequest.setRoleArn(roleArn)
            assumeRoleRequest.setRoleSessionName(roleSessionName)
//        request.setPolicy(policy) // Optional

            val assumeRoleResponse:AssumeRoleResponse = client.getAcsResponse(assumeRoleRequest)

            val respMap = new util.LinkedHashMap[String, String]
            respMap.put("Expiration", assumeRoleResponse.getCredentials.getExpiration)
            respMap.put("accessKeyId", assumeRoleResponse.getCredentials.getAccessKeyId)
            respMap.put("accessKeySecret", assumeRoleResponse.getCredentials.getAccessKeySecret)
            respMap.put("SecurityToken", assumeRoleResponse.getCredentials.getSecurityToken)
            respMap.put("RequestId", assumeRoleResponse.getRequestId)
            //respMap.put("expire", formatISO8601Date(expiration));
            val ja1 = new JSONObject(respMap)
            println(ja1.toString)
            response.setHeader("Access-Control-Allow-Origin", "*")
            response.setHeader("Access-Control-Allow-Methods", "GET, POST")
            do_response(request, response, ja1.toString)
        } catch {
            case e: ClientException =>
                System.out.println("Failed：")
                System.out.println("Error code: " + e.getErrCode)
                System.out.println("Error message: " + e.getErrMsg)
                System.out.println("RequestId: " + e.getRequestId)
        }
    }

    private def do_response(request: HttpServletRequest, response: HttpServletResponse, results: String): Unit = {
        val callbackFunName = request.getParameter("callback")
        if (callbackFunName == null || callbackFunName.equalsIgnoreCase("")) response.getWriter.println(results)
        else response.getWriter.println(callbackFunName + "( " + results + " )")
        response.setStatus(HttpServletResponse.SC_OK)
        response.flushBuffer()
    }

    override protected def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        doGet(request, response)
    }

}
