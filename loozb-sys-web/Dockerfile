FROM loozb/tomcat:7-jre8
MAINTAINER Long Zhao Bi "714037058@qq.com"

RUN rm -r /usr/local/tomcat/webapps/ROOT

ADD ./target/loozb-sys-web.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080