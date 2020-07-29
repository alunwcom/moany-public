#
# build image
#

FROM openjdk:11-jdk-alpine as build
WORKDIR /workspace
COPY . /workspace
RUN echo "hello"
VOLUME /root/.gradle
RUN sh gradlew build

#
# deployment image
#

FROM openjdk:11-jre-alpine

RUN mkdir -p /opt/software/
COPY --from=build /workspace/build/libs/moany-SNAPSHOT.war /opt/software/moany.war
ENV spring_profiles_active=docker
CMD ["java","-jar","/opt/software/moany.war"]