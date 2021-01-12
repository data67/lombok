打包和安装到maven本地库
ant maven; mvn install:install-file -Dfile=dist/lombok-1.18.16.jar -DgroupId=org.projectlombok -DartifactId=lombok -Dversion=6ack-1.18.16-v1-SNAPSHOT -Dpackaging=jar


部署到nexus
1.修改build/pom.xml中的版本信息
2.将build/pom.xml和dist下的lombok-1.18.16.jar、lombok-1.18.16-javadoc.jar、lombok-1.18.16-source.jar上传到nexus