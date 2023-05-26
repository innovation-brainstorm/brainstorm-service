# 该镜像需要依赖的基础镜像
FROM --platform=linux/amd64 openjdk:8
USER root
WORKDIR /root/
ARG JAR_FILE=/target/\*.jar
COPY ${JAR_FILE} /root/BrainStorm-1.0-SNAPSHOT.jar
# 将当前目录下的jar包复制到docker容器的/目录下
# 运行过程中创建一个mall-tiny-docker-file.jar文件
#RUN bash -c 'touch /BrainStorm-1.0-SNAPSHOT.jar'
# 声明服务运行在8080端口
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/root/BrainStorm-1.0-SNAPSHOT.jar"]
