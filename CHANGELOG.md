All notable changes to this project's current version will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Added

- Add a new command, `category`, that displays the list of categories and their associated labels (#39).
- Support a main label for categories (#39).
- Support blockquotes (#38).
  - Brackets (`&lt;` and `&gt;`) are now unescaped in the messages in order to be properly interpreted in the final
    markdown document.
  - New lines are now retained if they appear in the first message of a thread. New lines in replies are still deleted:
    this is required because replies must be displayed in a markdown list.

### Changed

- All commands associated keywords are now displayed in the help message (#48).
- Update categories order and labels (#39).

### Fixed

### Deprecated

### Removed

### Dependencies

- Upgrade spotless from 6.8.0 to
  [6.10.0](https://github.com/diffplug/spotless/blob/main/plugin-gradle/CHANGES.md#6100---2022-08-23) (#34).
- Upgrade quarkus from 2.10.2 to [2.11.2](https://quarkus.io/blog/quarkus-2-11-2-final-released/) (#35, #47).
- Upgrade java version to [temurin-17.0.4+8](https://www.oracle.com/java/technologies/javase/17-0-4-relnotes.html)
  (#36).
- Upgrade gradle version from 7.4.2 to [7.5.1](https://docs.gradle.org/7.5/release-notes.html) (#36, #46).

### Internal
