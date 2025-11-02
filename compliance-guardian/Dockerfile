FROM jdk21:1.0
MAINTAINER liujinhui <jhliuc@isoftstone.com>
RUN mkdir /home/release
RUN mkdir /home/release/compliance
RUN mkdir /home/release/compliance/log
RUN mkdir /home/release/compliance/file
ARG JAR_FILE=target/*.jar
COPY $JAR_FILE /home/release/compliance/app.jar
# 运行jar包
RUN bash -c 'touch /home/release/compliance/app.jar'
ENTRYPOINT ["java","-jar","/home/release/compliance/app.jar"]
# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone