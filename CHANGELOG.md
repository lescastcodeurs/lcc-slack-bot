All notable changes to this project's current version will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Added

### Changed

- Improve help when an error occurs, such as a timeout (#97).

### Fixed

- `GenerateShowNotesHandler` and `GenerateShowNotesSummaryHandler` are now executed in a worker thread (#96).

### Deprecated

### Removed

### Internal

- Bump bolt-socket-mode from 1.26.1 to 1.27.2 (#93, #95, #103).
- Bump quarkus from 2.13.4.Final to 2.14.1.Final (#94, #99).
- Bump spotless from 6.11.0 to 6.12.0 (#102).
