# HoraOS reproducible build image — same JDK/SDK/AGP everywhere.
# Build:  docker build -t wispos .
# Run:    docker run --rm -v "%CD%:/wispos" wispos
FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
    PATH=$PATH:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools

RUN apt-get update && apt-get install -y --no-install-recommends \
      openjdk-17-jdk-headless unzip wget git locales \
 && rm -rf /var/lib/apt/lists/* \
 && locale-gen en_US.UTF-8

ARG CMDLINE_TOOLS=11076708_latest
RUN mkdir -p /opt/android-sdk/cmdline-tools \
 && wget -q "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS}.zip" -O /tmp/cmdtools.zip \
 && unzip -q /tmp/cmdtools.zip -d /opt/android-sdk/cmdline-tools \
 && mv /opt/android-sdk/cmdline-tools/cmdline-tools /opt/android-sdk/cmdline-tools/latest \
 && rm /tmp/cmdtools.zip \
 && yes | sdkmanager --licenses >/dev/null \
 && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /wispos
CMD ["bash", "-c", "cd WATCHOSS-aosp/launcher && ./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest"]
