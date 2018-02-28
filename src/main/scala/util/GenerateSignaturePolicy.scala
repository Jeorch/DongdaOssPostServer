package util

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.sql.Date
import java.util

import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.model.{MatchMode, PolicyConditions}
import org.json.JSONObject

/**
  * Created by jeorch on 18-2-27.
  */
class GenerateSignaturePolicy extends HttpServlet{

    override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        val endpoint = "oss-cn-beijing.aliyuncs.com"
        val accessId = "lC2a3NXist8peEDm"
        val accessKey = "WP59s7IZkHBqIWWs57Ho0yx9B28S9m"
        val bucket = "blackmirror"
        val dir = "test/"
        val host = "http://" + bucket + "." + endpoint
        val client = new OSSClient(endpoint, accessId, accessKey)
        try {
//            val expireTime = 30
            val expireTime = 60 * 60    //Policy过期时间1小时
            val expireEndTime = System.currentTimeMillis + expireTime * 1000
            val expiration = new Date(expireEndTime)
            val policyConds = new PolicyConditions()
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000)
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir)
            val postPolicy = client.generatePostPolicy(expiration, policyConds)
            val binaryData = postPolicy.getBytes("utf-8")
            val encodedPolicy = BinaryUtil.toBase64String(binaryData)
            val postSignature = client.calculatePostSignature(postPolicy)
            val respMap = new util.LinkedHashMap[String, String]
            respMap.put("accessid", accessId)
            respMap.put("util", encodedPolicy)
            respMap.put("signature", postSignature)
            //respMap.put("expire", formatISO8601Date(expiration));
            respMap.put("dir", dir)
            respMap.put("host", host)
            respMap.put("expire", String.valueOf(expireEndTime / 1000))
            val ja1 = new JSONObject(respMap)
            println(ja1.toString)
            response.setHeader("Access-Control-Allow-Origin", "*")
            response.setHeader("Access-Control-Allow-Methods", "GET, POST")
            do_response(request, response, ja1.toString)
        } catch {
            case e: Exception =>
                e.printStackTrace()
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
