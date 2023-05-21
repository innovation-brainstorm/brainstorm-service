# 该镜像需要依赖的基础镜像
FROM java:8u20
VOLUME /tmp
# 将当前目录下的jar包复制到docker容器的/目录下
COPY target/*.jar /BrainStorm-1.0-SNAPSHOT.jar
# 运行过程中创建一个mall-tiny-docker-file.jar文件
#RUN bash -c 'touch /BrainStorm-1.0-SNAPSHOT.jar'
# 声明服务运行在8080端口
EXPOSE 8080
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/BrainStorm-1.0-SNAPSHOT.jar"]
