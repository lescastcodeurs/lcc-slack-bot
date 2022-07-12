# Contributing guide

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

## Setup

If you have not done so, you need to:

1. install Git and configure your GitHub access
2. install a JDK (for example [Eclipse Temurin](https://projects.eclipse.org/projects/adoptium.temurin)),
3. [install Gradle](https://gradle.org/install/),
4. [install Quarkus CLI](https://quarkus.io/guides/cli-tooling).

Please refer to [.tool-versions](/.tool-versions) for the exact versions. If you are using [asdf](https://asdf-vm.com/),
then you can install the required dependencies (except git) using the following commands :

```shell
asdf plugin-add java
asdf plugin-add gradle
asdf plugin-add quarkus
asdf install
```

## Running the application in dev mode

You can run the application in dev mode that enables live coding using:

```shell
quarkus dev
```

## Packaging and running the application

The application can be packaged using:

```shell
quarkus build
```

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

For more information have a look at [Building Quarkus apps with Gradle](https://quarkus.io/guides/gradle-tooling).

## Formatting the code

This project automatically format source code during build to comply with
[Google Java Style](https://google.github.io/styleguide/javaguide.html) and make use of
[google-java-format](https://github.com/google/google-java-format) for that. You can manually trigger code formatting
using:

```shell
gradle spotlessApply
```

You are strongly encouraged to install a plugin in your IDE if one is available. Take a look at
[the google-java-format README](https://github.com/google/google-java-format) to check.
