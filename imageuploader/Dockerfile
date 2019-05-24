FROM centos:7

RUN set -e && curl --silent --location https://rpm.nodesource.com/setup_6.x | bash -
RUN curl https://bintray.com/sbt/rpm/rpm > /etc/yum.repos.d/bintray-sbt-rpm.repo

RUN yum clean all && yum -y update && \
    yum install -y iproute which vim java-1.8.0-openjdk.x86_64 epel-release

RUN mkdir -p /opt/src
ADD start-dashboards.sh /opt/src
ADD PhxqaCertRoot.crt /opt/src
RUN keytool -import -keystore /etc/pki/java/cacerts -storepass changeit -file /opt/src/PhxqaCertRoot.crt -alias qaca -noprompt -v

RUN mkdir -p /opt/src/target/scala-2.11
ADD target/scala-2.11/imageuploader-assembly-0.1.jar /opt/src/target/scala-2.11

WORKDIR /opt/src

CMD ["bash", "/opt/src/start-service.sh"]