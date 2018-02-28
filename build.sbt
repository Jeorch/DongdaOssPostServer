name := "DongdaOssPostServer"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
    "org.eclipse.jetty" % "jetty-servlet" % "9.4.8.v20171121",
    "org.eclipse.jetty" % "jetty-server" % "9.4.8.v20171121",
    "com.aliyun.oss" % "aliyun-sdk-oss" % "3.0.0",
    "com.aliyun" % "aliyun-java-sdk-sts" % "3.0.0",
    "com.aliyun" % "aliyun-java-sdk-core" % "3.5.0",
    "javax.servlet" % "javax.servlet-api" % "4.0.0" % "provided"
)