All notable changes to this project's current version will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Added

- Automatically fill the _Conférences_ section using french conferences found on
  [scraly/developers-conferences-agenda](https://github.com/scraly/developers-conferences-agenda) (#74).
- Add record date detection (#75). Date of messages containing a configured string (typically a link to
  `recording.zencastr.com`) will be used as record date in the show notes.

### Changed

### Fixed

### Deprecated

### Removed

### Internal

- Configure dependabot to check for update of GitHub actions versions (#72).
- Bump tyrus-standalone-client from 1.19 to 1.20 (#77).
- Bump bolt-socket-mode from 1.25.1 to 1.26.1 (#79).
- Bump com.diffplug.spotless from 6.10.0 to 6.11.0 (#76).
- Bump quarkus from 2.12.0 to 2.13.1 (#80).
